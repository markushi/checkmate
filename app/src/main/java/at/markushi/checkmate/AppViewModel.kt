package at.markushi.checkmate

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.markushi.checkmate.model.Goal
import at.markushi.checkmate.model.GoalType
import at.markushi.checkmate.model.MonthlyProgress
import at.markushi.checkmate.model.Progress
import at.markushi.checkmate.model.RepoFactory
import at.markushi.checkmate.model.dayIdentifier
import at.markushi.checkmate.ui.AppColors
import io.sentry.Sentry
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

sealed class Screen(val name: String) {
    data object Onboarding : Screen("Onboarding")
    data object Today : Screen("Today")
    data object ThisMonth : Screen("ThisMonth")
    data object ThisYear : Screen("ThisYear")
    data object Settings : Screen("Settings")
    data object GoalSettings : Screen("GoalDetail")
    data object Unknown : Screen("Unknown")
}

sealed class NavigationAction {
    data object Back : NavigationAction()
    class ToScreen(val screen: Screen, val pushOntoStack: Boolean = false) : NavigationAction()
}

class AppViewModel : ViewModel() {

    private val repo = RepoFactory.getRepo()

    private val todayDateFlow = MutableStateFlow(LocalDate.now())
    private val goalsFlow = MutableStateFlow<List<Goal>>(emptyList())
    private val progressesFlow = MutableStateFlow<List<Progress>>(emptyList())

    val todayData = mutableStateOf(TodayData(LocalDate.now(), "", emptyList()))
    val monthData = mutableStateOf<MonthData>(MonthData.Empty)

    private val currentGoalFlow = MutableStateFlow<Goal?>(null)

    val navActionFlow = MutableSharedFlow<NavigationAction>()

    val goals = mutableStateListOf<Goal>()

    val currentGoal = mutableStateOf<Goal?>(null)

    val settingsGoal = mutableStateOf<Goal?>(null)
    val settingsGoalShowDeletePrompt = mutableStateOf(false)
    val settingsGoalShowDeleteOption = mutableStateOf(false)

    init {
        viewModelScope.launch {
            repo.getOnboardingShown().collect { shown ->
                val screen = if (shown) Screen.Today else Screen.Onboarding
                navActionFlow.emit(NavigationAction.ToScreen(screen))
            }
        }

        viewModelScope.launch {
            val span = Sentry.getSpan()?.startChild("get_goals")
            repo.getGoals().collect { goalList ->
                goalsFlow.emit(goalList)

                goals.clear()
                goals.addAll(goalList)

                span?.finish()

                if (currentGoalFlow.value == null) {
                    currentGoalFlow.emit(goalList.firstOrNull())
                }
            }
        }

        viewModelScope.launch {
            repo.getAllProgresses().collect { progresses ->
                progressesFlow.emit(progresses)
            }
        }

        viewModelScope.launch {
            combine(todayDateFlow, goalsFlow, progressesFlow) { todayDate, goals, progresses ->
                val todayDateIdentifier = dayIdentifier(todayDate)
                val todayGoals = goals.map { goal ->
                    val todayProgress =
                        progresses.lastOrNull {
                            it.goalId == goal.id && dayIdentifier(it) == todayDateIdentifier
                        }
                    GoalProgress(goal, todayProgress)
                }
                val dateLabel = todayDate.format(DateTimeFormatter.ofPattern("EEEE, dd", Locale.US))

                TodayData(todayDate, dateLabel, todayGoals)
            }.collect { data ->
                todayData.value = data
            }
        }

        viewModelScope.launch {
            currentGoalFlow.collect { goal ->
                currentGoal.value = goal
            }
        }

        viewModelScope.launch {
            combine(todayDateFlow, currentGoalFlow, progressesFlow) { todayDate, goal, progresses ->
                if (goal == null) {
                    null
                } else {
                    MonthlyProgress.create(goal, todayDate, progresses)
                }
            }.map { monthlyProgress ->
                monthlyProgress?.let { createMonthData(it) }
            }.collect { newMonthlyData ->
                newMonthlyData?.let { monthData.value = it }
            }
        }
    }

    private fun createMonthData(monthlyProgress: MonthlyProgress): MonthData.Data {
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val weeks = monthlyProgress.days.chunked(7)
        val streaks = monthlyProgress.streaks

        val todayStreakCount =
            monthlyProgress.days.firstOrNull() { it.date == today }?.streakCount ?: 0
        monthlyProgress.days.firstOrNull { it.date == yesterday }?.streakCount ?: 0

        val maxStreakCount = monthlyProgress.streaks.maxByOrNull { it.duration() }?.duration() ?: 0
        val daysInMonth = monthlyProgress.days.count { it.date.month == monthlyProgress.month }
        val daysWithProgress =
            monthlyProgress.days.count { it.date.month == monthlyProgress.month && it.checkedIn }

        val accomplishedPercentage = if (daysWithProgress > 0) {
            (daysWithProgress.toFloat() / daysInMonth.toFloat() * 100f).roundToInt()
        } else 0

        return MonthData.Data(
            monthlyProgress.goal,
            today.month,
            today,
            weeks,
            streaks,
            todayStreakCount,
            maxStreakCount,
            accomplishedPercentage
        )
    }

    fun onGoalSelected(goal: Goal) {
        currentGoalFlow.tryEmit(goal)
    }

    fun onGoalAtDayChecked(date: LocalDate, goal: Goal) {
        viewModelScope.launch {
            val progress = repo.getProgressForGoalAtDay(goal, date).firstOrNull()
            if (progress == null) {
                val newProgress = Progress("", goal.id, date.dayOfMonth, date.monthValue, date.year)
                repo.saveProgress(newProgress)
            } else {
                repo.deleteProgress(progress)
            }
        }
    }

    fun onTodayClicked() {
        viewModelScope.launch {
            navActionFlow.emit(NavigationAction.ToScreen(Screen.Today))
        }
    }

    fun onThisMonthClicked() {
        viewModelScope.launch {
            navActionFlow.emit(NavigationAction.ToScreen(Screen.ThisMonth))
        }
    }

    fun onThisYearClicked() {
        viewModelScope.launch {
            navActionFlow.emit(NavigationAction.ToScreen(Screen.ThisYear))
        }
    }

    fun onSettingsClicked() {
        viewModelScope.launch {
            navActionFlow.emit(NavigationAction.ToScreen(Screen.Settings))
        }
    }

    fun onSaveGoalSettingsClicked(goal: Goal) {
        viewModelScope.launch {
            repo.saveGoal(goal)
            navActionFlow.emit(NavigationAction.Back)
        }
    }

    fun onGoalSettingsClicked(goal: Goal) {
        settingsGoalShowDeleteOption.value = goal.id.isNotEmpty()
        settingsGoal.value = goal
        viewModelScope.launch {
            navActionFlow.emit(NavigationAction.ToScreen(Screen.GoalSettings, true))
        }
    }

    fun onCreateGoalClicked() {
        val goal =
            Goal(
                id = "", name = "", description = "",
                0,
                AppColors.Goals.random().toArgb(),
                "",
                GoalType.Daily
            )

        onGoalSettingsClicked(goal)
    }

    fun onDeleteGoalSettingsClicked() {
        settingsGoalShowDeletePrompt.value = true
    }

    fun onDeleteGoalSettingsConfirmedClicked(goal: Goal) {
        GlobalScope.launch {
            repo.deleteGoal(goal)
        }
        viewModelScope.launch {
            navActionFlow.emit(NavigationAction.Back)
            settingsGoalShowDeletePrompt.value = false
            settingsGoal.value = null
        }
    }

    fun onDeleteGoalDismissed() {
        settingsGoalShowDeletePrompt.value = false
    }

    fun onOnboardingStartClicked(goals: List<Goal>) {
        GlobalScope.launch {
            for (goal in goals) {
                repo.saveGoal(goal)
            }
            repo.setOnboardingShown(true)
        }
    }
}

data class GoalProgress(
    val goal: Goal,
    val progress: Progress?
)

data class TodayData(
    val date: LocalDate = LocalDate.now(),
    val dateLabel: String,
    val goals: List<GoalProgress> = emptyList()
)

sealed class MonthData {
    data object Empty : MonthData()
    class Data(
        val goal: Goal,
        val month: Month,
        val today: LocalDate,
        val weeks: List<List<MonthlyProgress.DailyProgress>>,
        val streaks: List<MonthlyProgress.Streak>,
        val todayStreakCount: Int,
        val longestStreakCount: Int,
        val accomplishedPercentage: Int,
    ) : MonthData()
}
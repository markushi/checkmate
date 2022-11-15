package at.markushi.checkmate.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.markushi.checkmate.GoalProgress
import at.markushi.checkmate.R
import at.markushi.checkmate.TodayData
import at.markushi.checkmate.model.Goal
import at.markushi.checkmate.model.GoalType
import at.markushi.checkmate.model.Progress
import org.threeten.bp.LocalDate

@Composable
fun TodayScreen(
    todayData: TodayData,
    onGoalClicked: (date: LocalDate, goal: GoalProgress) -> Unit
) {

    LazyColumn(content = {
        this.item(key = "today-title") {
            Column(
                modifier = Modifier.padding(start = 24.dp, top = 48.dp),
            ) {
                Text(
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    text = stringResource(R.string.today_title)
                )
                Text(
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 24.dp),
                    text = todayData.dateLabel
                )
            }
        }

        this.items(todayData.goals, itemContent = { goal ->
            val goalAchieved = goal.progress != null
            Box(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                GoalPill(
                    name = goal.goal.name,
                    color = Color(goal.goal.color),
                    checked = goalAchieved,
                    showCheckBox = true,
                    compact = false,
                ) {
                    onGoalClicked(todayData.date, goal)
                }
            }
        })
    })
}


@Composable
@Preview(backgroundColor = 0xFF222222)
fun TodayScreenPreview() {

    val data = remember {
        mutableStateOf(
            TodayData(
                LocalDate.now(),
                "",
                listOf(
                    GoalProgress(
                        Goal("1", "Workout", "", 0, 0xFF3a3a3a.toInt(), "", GoalType.Daily),
                        null
                    ),
                    GoalProgress(
                        Goal(
                            "2",
                            "Floss like there's no tomorrow",
                            "",
                            0,
                            0xFFFFAEFF.toInt(),
                            "",
                            GoalType.Daily
                        ),
                        Progress("", "", 1, 1, 1)
                    ),
                    GoalProgress(
                        Goal("3", "Code something", "", 0, 0xFF33AECC.toInt(), "", GoalType.Daily),
                        null
                    )
                )
            )
        )
    }

    MaterialTheme(colorScheme = darkColorScheme()) {
        Surface {
            TodayScreen(todayData = data.value, onGoalClicked = { date, goal ->
                val newGoals = data.value.goals.map {
                    if (it == goal) {
                        it
                    } else {
                        it.copy(
                            progress =
                            if (it.progress == null) Progress("", "", 1, 1, 1) else null
                        )
                    }
                }
                data.value = data.value.copy(goals = newGoals)
            })
        }
    }
}
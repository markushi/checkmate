package at.markushi.checkmate.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import at.markushi.checkmate.model.RepoImpl.Preferences.OnboardingShown
import at.markushi.checkmate.model.RepoImpl.Preferences.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.threeten.bp.LocalDate
import java.util.UUID


class RepoImpl(private val context: Context) : Repo {

    object Preferences {
        val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
            name = "settings"
        )
        val OnboardingShown = booleanPreferencesKey("onboarding_shown")
    }

    private val db by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database-1"
        ).build()
    }

    override suspend fun getGoals(): Flow<List<Goal>> = db.progressDao().getGoals()

    override suspend fun getProgressesForGoal(goal: Goal): Flow<List<Progress>> =
        db.progressDao().getGoalProgresses(goal.id)

    override suspend fun getProgressForGoalAtDay(goal: Goal, date: LocalDate): Flow<Progress> {
        val dayIdentifier = dayIdentifier(date)
        return db.progressDao().getProgressForGoalAtDate(goal.id, dayIdentifier)
    }

    override suspend fun getAllProgresses(): Flow<List<Progress>> =
        db.progressDao().getAllProgresses()

    override suspend fun saveProgress(progress: Progress): Progress {
        return if (progress.id.isEmpty()) {
            val id = UUID.randomUUID().toString()
            val dbProgress = progress.copy(id = id)
            db.progressDao().insertProgress(dbProgress)
            dbProgress
        } else {
            db.progressDao().updateProgress(progress)
            progress
        }
    }

    override suspend fun deleteProgress(progress: Progress) {
        db.progressDao().deleteProgress(progress)
    }

    override suspend fun deleteGoal(goal: Goal) {
        if (goal.id != "") {
            db.progressDao().deleteGoal(goal)
        }
    }

    override suspend fun saveGoal(goal: Goal): Goal {
        return if (goal.id.isEmpty()) {
            val dbGoal = goal.copy(id = UUID.randomUUID().toString())
            db.progressDao().insertGoal(dbGoal)
            dbGoal
        } else {
            db.progressDao().updateGoal(goal)
            goal
        }
    }

    override suspend fun getOnboardingShown(): Flow<Boolean> {
        return context.dataStore.data.map {
            it[OnboardingShown] ?: false
        }
    }

    override suspend fun setOnboardingShown(shown: Boolean) {
        context.dataStore.edit {
            it[OnboardingShown] = shown
        }
    }
}
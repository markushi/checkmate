package at.markushi.checkmate.model

import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

interface Repo {
    suspend fun getGoals(): Flow<List<Goal>>
    suspend fun deleteGoal(goal: Goal)
    suspend fun saveGoal(goal: Goal): Goal

    suspend fun getProgressesForGoal(goal: Goal): Flow<List<Progress>>
    suspend fun getProgressForGoalAtDay(goal: Goal, date: LocalDate): Flow<Progress>
    suspend fun getAllProgresses(): Flow<List<Progress>>
    suspend fun deleteProgress(progress: Progress)
    suspend fun saveProgress(progress: Progress): Progress

    suspend fun getOnboardingShown(): Flow<Boolean>
    suspend fun setOnboardingShown(shown: Boolean)
}
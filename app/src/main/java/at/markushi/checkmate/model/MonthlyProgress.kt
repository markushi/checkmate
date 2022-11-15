package at.markushi.checkmate.model

import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.temporal.ChronoUnit
import kotlin.math.absoluteValue

class MonthlyProgress(
    val goal: Goal,
    val month: Month,
    val days: List<DailyProgress> = emptyList(),
    val streaks: List<Streak> = emptyList()
) {

    companion object {
        fun create(goal: Goal, dayInMonth: LocalDate, items: List<Progress>): MonthlyProgress {
            val days = assembleDays(dayInMonth)

            val progressSet = items
                .filter { it.goalId == goal.id }
                .map { progress -> getLocalDateOf(progress) }.toSet()

            val dailyProgressList = mutableListOf<DailyProgress>()
            var lastDailyProgress: DailyProgress? = null

            var rowIdx = 0
            var colIdx = 0

            for (day in days) {
                val checkedIn = progressSet.contains(day)
                var streakCount = if (checkedIn) 1 else 0
                if (checkedIn) {
                    streakCount += lastDailyProgress?.streakCount ?: 0
                }
                val progress =
                    DailyProgress(day, progressSet.contains(day), streakCount, rowIdx, colIdx)
                dailyProgressList.add(progress)

                lastDailyProgress = progress

                colIdx++
                if (colIdx == 7) {
                    colIdx = 0
                    rowIdx++
                }
            }

            val streaks = mutableListOf<Streak>()
            var lastStreakStartIdx: Int = -1
            for (i in 0 until dailyProgressList.size) {
                val progress = dailyProgressList[i]
                if (progress.date.month.value < dayInMonth.month.value) {
                    continue
                }
                // a streak always end with a no-progress day
                if (progress.checkedIn && lastStreakStartIdx == -1) {
                    lastStreakStartIdx = i
                } else if (!progress.checkedIn && lastStreakStartIdx != -1) {
                    streaks.add(
                        Streak(
                            dailyProgressList[lastStreakStartIdx],
                            dailyProgressList[i - 1]
                        )
                    )
                    lastStreakStartIdx = -1
                }
            }
            if (lastStreakStartIdx != -1) {
                streaks.add(Streak(dailyProgressList[lastStreakStartIdx], dailyProgressList.last()))
            }

            return MonthlyProgress(goal, dayInMonth.month, dailyProgressList, streaks)
        }

        private fun assembleDays(today: LocalDate): List<LocalDate> {
            val days = mutableListOf<LocalDate>()

            var start: LocalDate = today.withDayOfMonth(1)
            var end: LocalDate = today.withDayOfMonth(today.lengthOfMonth())

            while (start.dayOfWeek != DayOfWeek.MONDAY) {
                start = start.minusDays(1)
            }
            while (end.dayOfWeek != DayOfWeek.SUNDAY) {
                end = end.plusDays(1)
            }

            while (start.isBefore(end)) {
                days.add(start)
                start = start.plusDays(1)
            }
            days.add(end)
            return days
        }

        private fun getLocalDateOf(item: Progress) =
            LocalDate.of(item.year, item.month, item.day)

    }

    class DailyProgress(
        val date: LocalDate,
        val checkedIn: Boolean,
        val streakCount: Int,
        val rowIdx: Int,
        val columnIdx: Int,
    )

    data class Streak(
        val start: DailyProgress,
        val end: DailyProgress,
    ) {
        fun duration(): Int =
            ChronoUnit.DAYS.between(end.date, start.date).toInt().absoluteValue + 1
    }
}
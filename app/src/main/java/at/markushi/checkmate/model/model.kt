package at.markushi.checkmate.model

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import org.threeten.bp.LocalDate

fun dayIdentifier(year: Int, monthOfYear: Int, dayOfMonth: Int) =
    year * 10000 + monthOfYear * 100 + dayOfMonth

fun dayIdentifier(progress: Progress): Int =
    dayIdentifier(progress.year, progress.month, progress.day)

fun dayIdentifier(date: LocalDate): Int =
    dayIdentifier(date.year, date.month.value, date.dayOfMonth)

enum class GoalType(val value: Int) {
    Daily(1),
    Weekly(2),
}

class Converters {

    @TypeConverter
    fun toGoalType(itemValue: Int): GoalType = when (itemValue) {
        GoalType.Daily.value -> GoalType.Daily
        GoalType.Weekly.value -> GoalType.Weekly
        else -> throw IllegalArgumentException("Invalid Goal Type $itemValue")
    }

    @TypeConverter
    fun fromGoalType(item: GoalType) = item.value
}

@Entity
@TypeConverters(Converters::class)
data class Goal(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",

    val name: String,
    val description: String,
    val itemOrder: Int,
    val color: Int,
    val icon: String,
    val goalType: GoalType,
)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Goal::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("goalId"),
        onDelete = ForeignKey.CASCADE
    )]
)

data class Progress(
    @PrimaryKey(autoGenerate = false)
    val id: String = "",

    @ColumnInfo(index = true)
    val goalId: String,

    val day: Int,
    val month: Int,
    val year: Int,
    val identifier: Int = dayIdentifier(year, month, day)
)

@Dao
interface ProgressDao {

    @Query("SELECT * FROM Goal ORDER BY itemOrder ASC")
    fun getGoals(): Flow<List<Goal>>

    @Query("SELECT * FROM Progress WHERE goalId = :goalId ORDER BY identifier ASC")
    fun getGoalProgresses(goalId: String): Flow<List<Progress>>

    @Query("SELECT * FROM Progress ORDER BY identifier ASC")
    fun getAllProgresses(): Flow<List<Progress>>

    @Query("SELECT * FROM Progress WHERE goalId = :goalId AND identifier = :dateIdentifier")
    fun getProgressForGoalAtDate(goalId: String, dateIdentifier: Int): Flow<Progress>

    @Insert
    suspend fun insertProgress(progress: Progress): Long

    @Insert
    suspend fun insertGoal(goal: Goal): Long

    @Delete
    suspend fun deleteProgress(progress: Progress): Void

    @Update
    fun updateProgress(progress: Progress): Void

    @Delete
    suspend fun deleteGoal(goal: Goal): Void

    @Update
    suspend fun updateGoal(goal: Goal): Void

    @Query("DELETE FROM Progress WHERE identifier = :identifier")
    suspend fun deleteProgressByIdentifier(identifier: Int): Int
}

@Database(entities = [Goal::class, Progress::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun progressDao(): ProgressDao
}


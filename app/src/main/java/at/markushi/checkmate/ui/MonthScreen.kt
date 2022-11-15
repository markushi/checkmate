package at.markushi.checkmate.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import at.markushi.checkmate.MonthData
import at.markushi.checkmate.model.Goal
import at.markushi.checkmate.model.MonthlyProgress
import org.threeten.bp.LocalDate
import org.threeten.bp.format.TextStyle
import java.util.Locale

@Composable
fun MonthScreen(
    monthData: MonthData,
    goals: List<Goal>,
    onGoalSelected: (Goal) -> Unit,
    onDayChecked: (date: LocalDate, goal: Goal) -> Unit
) {
    if (monthData is MonthData.Empty) {
        return
    }

    if (monthData is MonthData.Data) {
        val goalColor = monthData.goal.color.asColor()

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 48.dp,
                        start = 16.dp,
                        end = 16.dp
                    ),
            ) {
                Text(
                    text = monthData.month.getDisplayName(
                        TextStyle.FULL,
                        Locale.US,
                    ),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                GoalPicker(goals, monthData.goal, onGoalSelected)
            }

            val days = listOf(
                "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun",
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 2.dp, end = 2.dp, top = 24.dp)
                    .height(40.dp)
            ) {
                for (day in days) {
                    Text(
                        modifier = Modifier.weight(1.0f),
                        fontWeight = FontWeight.Bold,
                        text = day,
                        textAlign = TextAlign.Center
                    )
                }
            }
            MonthLayout(streaks = monthData.streaks, color = goalColor) {
                for (week in monthData.weeks) {
                    for (day in week) {
                        val thisMonth = day.date.month == monthData.month
                        val isToday = day.date == monthData.today
                        DayOfMonth(Modifier.weight(1.0f), day, thisMonth, isToday) {
                            onDayChecked(day.date, monthData.goal)
                        }
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                StatView(
                    "Current Streak: ${monthData.todayStreakCount}", null
                )
                Spacer(modifier = Modifier.size(16.dp))
                StatView(
                    "Best Streak: ${monthData.longestStreakCount}", null
                )
                Spacer(modifier = Modifier.size(16.dp))
                StatView(
                    "Accomplishment Rate: ${monthData.accomplishedPercentage}%", null
                )
            }
        }
    }
}

@Composable
private fun StatView(title: String, icon: ImageVector?) {
    Row(
        modifier = Modifier
            .animateContentSize()
            .height(40.dp)
            .border(2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(30))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.let {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = it, tint = AppColors.Star, contentDescription = "Streak"
            )
            Spacer(modifier = Modifier.size(4.dp))
        }
        Text(
            color = MaterialTheme.colorScheme.outline,
            text = title
        )
    }
}

@Composable
private fun MonthLayout(
    streaks: List<MonthlyProgress.Streak>,
    color: Color,
    content: @Composable () -> Unit
) {

    val cols = 7
    val rows = 6

    val activeColor = color.copy(alpha = 0.56f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(cols.toFloat() / rows.toFloat())
    ) {
        Canvas(modifier = Modifier.fillMaxSize(), onDraw = {

            val outerPadding = 2.dp.roundToPx().toFloat()
            val itemPadding = 4.dp.roundToPx().toFloat()

            val childSize =
                ((size.width - outerPadding - outerPadding) / cols).toInt().toFloat()
            val innerChildHeight = childSize - itemPadding - itemPadding

            val cornerRadius = CornerRadius(childSize / 2f)

            for (streak in streaks) {
                val startRow = streak.start.rowIdx
                val endRow = streak.end.rowIdx
                val x0 = streak.start.columnIdx
                val x1 = streak.end.columnIdx

                if (startRow == endRow) {
                    drawRoundRectWithStroke(
                        activeColor,
                        Offset(
                            outerPadding + itemPadding + (childSize * x0),
                            outerPadding + itemPadding + (childSize * startRow)
                        ),
                        Size(
                            (x1 - x0 + 1) * childSize - itemPadding - itemPadding,
                            innerChildHeight
                        ),
                        cornerRadius
                    )
                } else {
                    // start
                    drawRoundRectWithStroke(
                        activeColor,
                        Offset(
                            outerPadding + itemPadding + (childSize * x0),
                            outerPadding + itemPadding + (childSize * startRow)
                        ),
                        Size(
                            size.width - (childSize * x0.toFloat() + outerPadding) + childSize,
                            innerChildHeight
                        ),
                        cornerRadius
                    )

                    // middle
                    for (i in startRow + 1 until endRow) {
                        drawRoundRectWithStroke(
                            activeColor,
                            Offset(0f, outerPadding + itemPadding + (childSize * i)),
                            Size(size.width, innerChildHeight)
                        )
                    }

                    // end
                    drawRoundRectWithStroke(
                        activeColor,
                        Offset(
                            -childSize,
                            outerPadding + itemPadding + (childSize * endRow)
                        ),
                        Size(
                            outerPadding - itemPadding + (childSize * (x1 + 2)),
                            innerChildHeight
                        ),
                        cornerRadius
                    )
                }

            }
        })
        Layout(modifier = Modifier.fillMaxSize(),
            content = content,
            measurePolicy = { measurables, constraints ->
                val padding = 2.dp.roundToPx()
                val childSize = (constraints.maxWidth - padding - padding) / cols

                val childConstraint = Constraints.fixed(childSize, childSize)
                val placeables = measurables.map { measurable ->
                    measurable.measure(childConstraint)
                }

                // Set the size of the layout as big as it can
                layout(constraints.maxWidth, constraints.maxHeight) {
                    // Place children in the parent layout
                    placeables.forEachIndexed { idx, placeable ->
                        val x = padding + (idx % cols) * childSize
                        val y = padding + (idx / cols) * childSize
                        placeable.placeRelative(x = x, y = y)
                    }
                }
            })
    }
}

@Composable
private fun DayOfMonth(
    modifier: Modifier,
    day: MonthlyProgress.DailyProgress,
    isCurrentMonth: Boolean,
    isToday: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val todayColor = MaterialTheme.colorScheme.primary

    var m = modifier
        .fillMaxWidth()
        .aspectRatio(1f, false)
        .padding(4.dp)

    if (!day.checkedIn) {
        // m = m.border(2.dp, borderColor, CircleShape)
    }
    Box(
        modifier = m
            .clip(CircleShape)
            .clickable {
                onDayClick(day.date)
            },
        contentAlignment = Alignment.Center
    ) {
        val textColor =
            if (isCurrentMonth) MaterialTheme.typography.bodyMedium.color
            else borderColor
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor
        )
        if (isToday) {
            Box(
                modifier = Modifier
                    .padding(bottom = 6.dp)
                    .size(4.dp)
                    .align(Alignment.BottomCenter)
                    .background(todayColor, CircleShape)
            )
        }

    }
}

fun DrawScope.drawRoundRectWithStroke(
    color: Color,
    topLeft: Offset,
    size: Size,
    cornerRadius: CornerRadius = CornerRadius.Zero,
) {
    drawRoundRect(
        color, topLeft, size, cornerRadius
    )
}


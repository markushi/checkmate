package at.markushi.checkmate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.markushi.checkmate.model.Goal

@Composable
fun GoalPicker(goals: List<Goal>, currentGoal: Goal?, onGoalSelected: (Goal) -> Unit) {
    val expanded = remember { mutableStateOf(false) }
    currentGoal?.let { activeGoal ->

        val activeColor =
            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(activeColor)
                .clickable(onClick = {
                    expanded.value = true
                })
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = activeGoal.color.asColor(),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = activeGoal.name,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.size(2.dp))
            Icon(
                Icons.Filled.ArrowDropDown,
                contentDescription = "DropDown"
            )
            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
            ) {
                for (goal in goals) {
                    DropdownMenuItem(
                        text = {
                            Text(text = goal.name)
                        },
                        onClick = {
                            expanded.value = false
                            onGoalSelected(goal)
                        })
                }
            }
        }
    }
}
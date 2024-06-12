@file:OptIn(ExperimentalMaterial3Api::class)

package at.markushi.checkmate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import at.markushi.checkmate.AppViewModel
import at.markushi.checkmate.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalSettingsScreen(appViewModel: AppViewModel) {
    appViewModel.settingsGoal.value?.let { goal ->
        val goalName = remember {
            mutableStateOf(TextFieldValue(goal.name))
        }
        val goalColor = remember {
            mutableStateOf(goal.color.asColor())
        }

        val onBackClicked: () -> Unit = {
            val newGoal = goal
                .copy(name = goalName.value.text, color = goalColor.value.toArgb())
            appViewModel.onSaveGoalSettingsClicked(newGoal)
        }

        Column(modifier = Modifier.padding(vertical = 24.dp)) {
            NavHeading(
                title = stringResource(id = R.string.goal_details_edit),
                onClick = onBackClicked
            )

            Spacer(modifier = Modifier.size(24.dp))
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {

                Text(
                    text = stringResource(R.string.goal_settings_label_name),
                    style = MaterialTheme.typography.labelMedium
                )
                OutlinedTextField(value = goalName.value,
                    placeholder = {
                        Text(text = stringResource(R.string.goal_detail_goal_name_hint))
                    },
                    onValueChange = {
                        goalName.value = it
                    })
                Spacer(modifier = Modifier.size(24.dp))
                Text(
                    text = stringResource(R.string.goal_settings_label_color),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.size(8.dp))
                Box(modifier = Modifier
                    .size(60.dp)
                    .background(color = goalColor.value, shape = CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        var idx = AppColors.Goals.indexOf(goalColor.value)
                        idx = (idx + 1) % AppColors.Goals.size
                        goalColor.value = AppColors.Goals[idx]
                    })

                Spacer(modifier = Modifier.size(24.dp))

                Row() {
                    Button(onClick = {
                        val newGoal =
                            goal.copy(name = goalName.value.text, color = goalColor.value.toArgb())
                        appViewModel.onSaveGoalSettingsClicked(newGoal)
                    }) {
                        Text(text = stringResource(R.string.goal_settings_button_save))
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    if (appViewModel.settingsGoalShowDeleteOption.value) {
                        DangerousButton(
                            onClick = {
                                appViewModel.onDeleteGoalSettingsClicked()
                            }) {
                            Text(text = stringResource(R.string.goal_settings_button_delete))
                        }
                    }
                }
            }
        }
        if (appViewModel.settingsGoalShowDeletePrompt.value) {
            AlertDialog(onDismissRequest = {
                appViewModel.onDeleteGoalDismissed()
            }, confirmButton = {
                Button(onClick = { appViewModel.onDeleteGoalSettingsConfirmedClicked(goal) }) {
                    Text(text = stringResource(R.string.goal_settings_button_delete))
                }
            }, dismissButton = {
                NeutralButton(
                    onClick = { appViewModel.onDeleteGoalDismissed() }) {
                    Text(text = stringResource(R.string.goal_settings_delete_dialog_cancel))
                }
            }, title = {
                Text(text = stringResource(R.string.goal_settings_delete_dialog_title))
            }, text = {
                Text(text = stringResource(R.string.goal_settings_delete_dialog_description))
            })
        }
    }
}
package at.markushi.checkmate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import at.markushi.checkmate.AppViewModel
import at.markushi.checkmate.R

@Composable
fun SettingsScreen(appViewModel: AppViewModel) {
    Column {
        Text(
            modifier = Modifier.padding(
                top = 48.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            text = stringResource(R.string.settings_title),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(R.string.settings_goal_header)
        )
        for (goal in appViewModel.goals) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clickable {
                        appViewModel.onGoalSettingsClicked(goal)
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(color = goal.color.asColor(), shape = CircleShape)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(text = goal.name)
            }
        }
        Button(
            modifier = Modifier.padding(16.dp),
            onClick = {
                appViewModel.onCreateGoalClicked()
            }) {
            Text(text = stringResource(R.string.settings_new_goal))
        }
    }
}
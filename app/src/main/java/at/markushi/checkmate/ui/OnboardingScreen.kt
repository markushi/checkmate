package at.markushi.checkmate.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.markushi.checkmate.R
import at.markushi.checkmate.model.Goal
import at.markushi.checkmate.model.GoalType

@Composable
fun OnboardingScreen(onFinishOnboardingClicked: (List<Goal>) -> Unit) {
    val exampleGoals = listOf(
        Goal(
            "",
            stringResource(R.string.onboarding_goal_0),
            "",
            0,
            AppColors.Goals[0].toArgb(),
            "",
            GoalType.Daily
        ),
        Goal(
            "",
            stringResource(R.string.onboarding_goal_1),
            "",
            0,
            AppColors.Goals[1].toArgb(),
            "",
            GoalType.Daily
        ),
        Goal(
            "",
            stringResource(R.string.onboarding_goal_2),
            "",
            0,
            AppColors.Goals[2].toArgb(),
            "",
            GoalType.Daily
        ),
        Goal(
            "",
            stringResource(R.string.onboarding_goal_3),
            "",
            0,
            AppColors.Goals[3].toArgb(),
            "",
            GoalType.Daily
        ),
    )
    val checkedGoals = remember { mutableStateListOf<Goal>() }

    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        Text(
            modifier = Modifier.padding(
                top = 24.dp,
                bottom = 16.dp,
            ),
            text = stringResource(R.string.onboarding_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(text = stringResource(R.string.onboarding_subtitle_1))
        Text(text = stringResource(R.string.onboarding_subtitle_2))

        Spacer(modifier = Modifier.size(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (i in 0 until exampleGoals.size / 2) {
                    GoalPill(
                        name = exampleGoals[i].name, AppColors.Goals[i],
                        checked = checkedGoals.contains(exampleGoals[i]),
                        compact = true,
                        showCheckBox = false,
                    ) {
                        if (!checkedGoals.remove(exampleGoals[i])) {
                            checkedGoals.add(exampleGoals[i])
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                for (i in (exampleGoals.size / 2) until exampleGoals.size) {
                    GoalPill(
                        name = exampleGoals[i].name, AppColors.Goals[i],
                        checked = checkedGoals.contains(exampleGoals[i]),
                        compact = true,
                        showCheckBox = false,
                    ) {
                        if (!checkedGoals.remove(exampleGoals[i])) {
                            checkedGoals.add(exampleGoals[i])
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    onFinishOnboardingClicked(checkedGoals)
                }) {
                Text(
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 32.dp),
                    text = stringResource(R.string.onboarding_action_start)
                )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun OnboardingScreenPreview() {
    MaterialTheme(darkColorScheme()) {
        Scaffold {
            OnboardingScreen() {}
        }
    }
}
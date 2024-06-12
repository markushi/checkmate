package at.markushi.checkmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import at.markushi.checkmate.ui.AppColors
import at.markushi.checkmate.ui.BottomAppBarButton
import at.markushi.checkmate.ui.GoalSettingsScreen
import at.markushi.checkmate.ui.MonthScreen
import at.markushi.checkmate.ui.OnboardingScreen
import at.markushi.checkmate.ui.SettingsScreen
import at.markushi.checkmate.ui.TodayScreen

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val model: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val currentBackStackEntry = navController.currentBackStackEntryAsState()

            LaunchedEffect(Unit) {
                model.navActionFlow.collect { action ->
                    when (action) {
                        is NavigationAction.Back -> {
                            navController.popBackStack()
                        }

                        is NavigationAction.ToScreen -> {
                            if (!action.pushOntoStack) {
                                navController.clearBackStack(Screen.Today.name)
                            }
                            navController.navigate(
                                action.screen.name,
                                navOptions {
                                    launchSingleTop = true
                                })
                        }
                    }
                }
            }


            MaterialTheme(colorScheme = AppColors.ColorScheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            when (currentBackStackEntry.value?.destination?.route) {
                                Screen.Today.name, Screen.ThisMonth.name, Screen.Settings.name -> BottomNavigation(
                                    currentBackStackEntry.value?.destination?.route
                                )

                                else -> {}
                            }
                        },
                        content = { padding ->
                            NavHost(
                                modifier = Modifier.padding(padding),
                                navController = navController,
                                startDestination = Screen.Today.name
                            ) {
                                composable(Screen.Onboarding.name) {
                                    OnboardingScreen { goals ->
                                        model.onOnboardingStartClicked(goals)
                                    }
                                }
                                composable(Screen.Today.name) {
                                    TodayScreen(model.todayData.value) { date, goalWithState ->
                                        model.onGoalAtDayChecked(date, goalWithState.goal)
                                    }
                                }
                                composable(Screen.ThisMonth.name) {
                                    MonthScreen(
                                        model.monthData.value,
                                        model.goals,
                                        model::onGoalSelected,
                                        model::onGoalAtDayChecked
                                    )
                                }
                                composable(Screen.Settings.name) {
                                    SettingsScreen(appViewModel = model)
                                }
                                composable(Screen.GoalSettings.name) {
                                    GoalSettingsScreen(appViewModel = model)
                                }
                                composable(Screen.Unknown.name) {

                                }
                            }
                        }
                    )
                }
            }
        }
    }


    @Composable
    private fun BottomNavigation(currentRoute: String?) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp))
        ) {
            Row(
                modifier = Modifier
                    .height(64.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BottomAppBarButton(
                    title = stringResource(R.string.home_tab_today),
                    active = currentRoute == Screen.Today.name
                ) {
                    model.onTodayClicked()
                }
                BottomAppBarButton(
                    title = stringResource(R.string.home_tab_this_month),
                    active = currentRoute == Screen.ThisMonth.name
                ) {
                    model.onThisMonthClicked()
                }
                Spacer(modifier = Modifier.weight(1.0f))
                BottomAppBarButton(
                    icon = Icons.Outlined.Settings,
                    active = currentRoute == Screen.Settings.name
                ) {
                    model.onSettingsClicked()
                }
            }
        }
    }
}
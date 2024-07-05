package ru.pseudonimb.clickergame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.pseudonimb.clickergame.ui.screens.GameOfSnakes
import ru.pseudonimb.clickergame.ui.screens.MainScreen
import ru.pseudonimb.clickergame.ui.screens.Player
import ru.pseudonimb.clickergame.ui.theme.ClickergameTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()

            ClickergameTheme {
                NavHost(
                    navController = navController,
                    startDestination = "main_screen"
                ) {
                    composable("main_screen") {
                        MainScreen {
                            navController.navigate("game_screen")
                        }
                    }
                    composable("game_screen") {
                        Player(
                            {
                                navController.navigate("game_screen") {
                                    popUpTo("main_screen") {
                                        inclusive = true
                                    }
                                }
                            },
                            GameOfSnakes(
                                scope = lifecycleScope
                            ) {
                                navController.navigate("main_screen") {
                                    popUpTo("main_screen") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
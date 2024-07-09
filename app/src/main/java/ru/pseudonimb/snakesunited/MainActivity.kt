package ru.pseudonimb.snakesunited

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.pseudonimb.snakesunited.ui.screens.GameOfSnakes
import ru.pseudonimb.snakesunited.ui.screens.MainScreen
import ru.pseudonimb.snakesunited.ui.screens.Player
import ru.pseudonimb.snakesunited.ui.screens.RecordsScreen
import ru.pseudonimb.snakesunited.ui.theme.MainTheme
import ru.pseudonimb.snakesunited.utils.DataStoreManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataStoreManager = DataStoreManager(this)
        setContent {
            val navController = rememberNavController()
            MainTheme {
                NavHost(
                    navController = navController,
                    startDestination = "main_screen"
                ) {
                    composable("main_screen") {
                        MainScreen (
                            { navController.navigate("game_screen") },
                            { navController.navigate("records_screen") }
                        )
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
                                scope = lifecycleScope,
                                {
                                    navController.navigate("main_screen") {
                                        popUpTo("main_screen") {
                                            inclusive = true
                                        }
                                    }
                                },
                                dataStoreManager = dataStoreManager
                            )
                        )
                    }
                    composable("records_screen") {
                        RecordsScreen {
                            navController.navigate("main_screen"){
                                popUpTo("main_screen") {
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
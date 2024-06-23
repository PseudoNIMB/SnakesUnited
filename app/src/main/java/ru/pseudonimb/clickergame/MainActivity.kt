package ru.pseudonimb.clickergame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import ru.pseudonimb.clickergame.ui.screens.GameOfSnakes
import ru.pseudonimb.clickergame.ui.screens.Player
import ru.pseudonimb.clickergame.ui.theme.ClickergameTheme
import ru.pseudonimb.clickergame.utils.DataStoreManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val game = GameOfSnakes(lifecycleScope)
        val dataStoreManager = DataStoreManager(this)

        setContent {
            ClickergameTheme {
                val highScoreState = remember {
                    mutableStateOf(0)
                }
                //Корутина для получения данных при запуске приложения
                LaunchedEffect(key1 = true){
                    dataStoreManager.getSettings().collect{settings ->
                        highScoreState.value = settings.highScore
                    }
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
//                    MainScreen()
                    Player(dataStoreManager, highScoreState, game)
                }
            }
        }
    }
}
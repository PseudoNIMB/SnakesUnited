package ru.pseudonimb.snakesunited.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.pseudonimb.snakesunited.R
import ru.pseudonimb.snakesunited.ui.data.GameData
import ru.pseudonimb.snakesunited.ui.data.RecordData
import ru.pseudonimb.snakesunited.utils.DataStoreManager
import ru.pseudonimb.snakesunited.utils.SettingsData
import java.util.*

class GameOfSnakes(
    private val scope: CoroutineScope,
    val navigateMainMenu: () -> Unit,
    val dataStoreManager: DataStoreManager
) {
    private val mutex = Mutex()
    private val mutableGameData = MutableStateFlow(GameData(food = Pair(5, 5), snake = listOf(Pair(7, 7))))
    val gameData: Flow<GameData> = mutableGameData
    val dialogState = mutableStateOf(false)
    var snakeLength = SNAKE_SIZE
    val fbs = Firebase.firestore

    var move = Pair(1, 0)
        set(value) {
            scope.launch {
                mutex.withLock {
                    field = value
                }
            }
        }

    init {
        scope.launch {
            while (true) {
                //Скорость змейки (чем ближе к нулю тем быстрее)
                delay(200)
                mutableGameData.update {
                    val newPosition = it.snake.first().let { position ->
                        mutex.withLock {
                            Pair(
                                (position.first + move.first + BOARD_SIZE) % BOARD_SIZE,
                                (position.second + move.second + BOARD_SIZE) % BOARD_SIZE
                            )
                        }
                    }

                    //Если змея съёла еду
                    if (newPosition == it.food) {
                        snakeLength++
                    }

                    //Если змея врезалась в себя
                    if (it.snake.contains(newPosition)) {
                        if (!dialogState.value) {
                            val snakeHighScore = if (snakeLength > SNAKE_SIZE) {
                                (snakeLength - SNAKE_SIZE)
                            } else 0

                            val dataStoreHighScore = dataStoreManager.getSettings().first().sharedHighScore

                            if (snakeHighScore > dataStoreHighScore) {
                                dataStoreManager.saveSettings(
                                    SettingsData(
                                        snakeHighScore
                                    )
                                )
                                if (auth.currentUser != null) {
                                    fbs.collection("Records")
                                        .document(auth.currentUser?.email?.substringBefore("@").toString()).set(
                                            RecordData(
                                                auth.currentUser?.email?.substringBefore("@").toString(),
                                                snakeHighScore
                                            )
                                        )
                                }
                            }
                        }
                        dialogState.value = true

                        snakeLength = SNAKE_SIZE
                    }

                    it.copy(
                        food = if (newPosition == it.food) Pair(
                            Random().nextInt(BOARD_SIZE),
                            Random().nextInt(BOARD_SIZE)
                        ) else it.food,
                        snake = listOf(newPosition) + it.snake.take(snakeLength - 1)
                    )
                }
            }
        }
    }

    companion object {
        const val BOARD_SIZE = 16
        const val SNAKE_SIZE = 4
    }
}

//Весь экран
@Composable
fun Player(
    onClick: () -> Unit,
    game: GameOfSnakes
) {
    val state = game.gameData.collectAsState(initial = null)
    val dataState = game.dataStoreManager.getSettings().collectAsState(initial = SettingsData())

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.padding(0.dp, 16.dp).fillMaxSize()) {
        Text(
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 40.dp),
            color = Color.DarkGray,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            text = stringResource(id = R.string.highscore_uppercase) + dataState.value.sharedHighScore
        )
        Text(
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 40.dp),
            color = Color.DarkGray,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            text = stringResource(id = R.string.score_uppercase) + (game.snakeLength - 4)
        )
        state.value?.let {
            Board(it)
        }
        if (game.dialogState.value) {
            DialogCollision(game.dialogState, game.navigateMainMenu, game)
        }
        Buttons {
            game.move = it
        }
    }

}

//Кнопки управления
@Composable
fun Buttons(onDirectionChange: (Pair<Int, Int>) -> Unit) {
    val buttonSize = Modifier.size(96.dp)
    val buttonShape = RoundedCornerShape(16.dp)

    //Булеан для отключения кнопок текущего и реверсивного направления, чтобы не дать змее жрать себя
    var movement by remember {
        mutableStateOf(false)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
        OutlinedButton(
            onClick = {
                onDirectionChange(Pair(0, -1))
                movement = true
            },
            enabled = !movement,
            modifier = buttonSize,
            shape = buttonShape
        ) {
            Icon(Icons.Default.KeyboardArrowUp, "Up",
                modifier = Modifier.size(32.dp))
        }
        Row {
            OutlinedButton(
                onClick = {
                    onDirectionChange(Pair(-1, 0))
                    movement = false
                },
                enabled = movement,
                modifier = buttonSize,
                shape = buttonShape
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, "Left",
                    modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = buttonSize)
            OutlinedButton(
                onClick = {
                    onDirectionChange(Pair(1, 0))
                    movement = false
                },
                enabled = movement,
                modifier = buttonSize,
                shape = buttonShape
            ) {
                Icon(Icons.Default.KeyboardArrowRight, "Right",
                    modifier = Modifier.size(32.dp))
            }
        }
        OutlinedButton(
            onClick = {
                onDirectionChange(Pair(0, 1))
                movement = true
            },
            enabled = !movement,
            modifier = buttonSize,
            shape = buttonShape
        ) {
            Icon(Icons.Default.KeyboardArrowDown, "Down",
                modifier = Modifier.size(32.dp))
        }
    }
}

//Игровое поле
@Composable
fun Board(gameData: GameData) {
    BoxWithConstraints(Modifier.padding(20.dp)) {
        val tileSize = maxWidth / GameOfSnakes.BOARD_SIZE

        //Граница игрового поля
        Box(
            Modifier
                .size(maxWidth)
                .border(2.dp, Color.DarkGray)
        )

        //Еда
        Box(
            Modifier
                .offset(x = tileSize * gameData.food.first, y = tileSize * gameData.food.second)
                .size(tileSize)
                .background(
                    Color.Gray, CircleShape
                )
        )

        //Змея
        gameData.snake.forEach {
            Box(
                modifier = Modifier
                    .padding(1.dp)
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize * 0.9f)
                    .background(
                        Color.DarkGray, shape = RoundedCornerShape(16)
                    )
            )
        }
    }
}

@Composable
fun DialogCollision(dialogState: MutableState<Boolean>, navigateMainMenu: () -> Unit, game: GameOfSnakes) {
    var finalHighScore =
        game.dataStoreManager.getSettings().collectAsState(initial = SettingsData()).value.sharedHighScore
    AlertDialog(onDismissRequest = {
        dialogState.value = false
    }, confirmButton = {
        TextButton(onClick = {
            dialogState.value = false
        }) {
            Text(text = stringResource(id = R.string.try_again))
        }
    }, dismissButton = {
        TextButton(onClick = {
            dialogState.value = false
            navigateMainMenu.invoke()
        }) {
            Text(text = stringResource(id = R.string.main_menu))
        }
    }, title = {
        Text(text = stringResource(id = R.string.game_over) + finalHighScore)
    })
}
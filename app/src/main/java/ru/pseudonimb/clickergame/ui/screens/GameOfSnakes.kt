package ru.pseudonimb.clickergame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.pseudonimb.clickergame.utils.DataStoreManager
import ru.pseudonimb.clickergame.utils.SettingsData
import java.util.*

data class State(val food: Pair<Int, Int>, val snake: List<Pair<Int, Int>>)

var highScore = 0
var score = 0

class GameOfSnakes(private val scope: CoroutineScope) {
    private val mutex = Mutex()
    private val mutableState = MutableStateFlow(State(food = Pair(5, 5), snake = listOf(Pair(7, 7))))
    val state: Flow<State> = mutableState

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
            var snakeLength = 4
            highScore = 0
            score = 0

            while (true) {
                //Скорость змейки (чем ближе к нулю тем быстрее)
                delay(200)
                mutableState.update {
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
                        score++
                    }

                    //Если змея врезалась в себя
                    if (it.snake.contains(newPosition)) {
                        if (highScore<snakeLength) highScore = (snakeLength - 4)
                        //TODO вставлять тут хайскор и диалоговое окно

                        snakeLength = 4
                        score = 0
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
    }
}

//Весь экран
@Composable
fun Player(
    onClick: () -> Unit,
    game: GameOfSnakes
) {
    val state = game.state.collectAsState(initial = null)

    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text(
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 40.dp),
            color = Color.DarkGray,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            text = "HIGHSCORE: " + highScore
        )
        Text(
            modifier = Modifier
                .align(Alignment.End)
                .padding(horizontal = 40.dp),
            color = Color.DarkGray,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            text = "SCORE: " + score
        )
        state.value?.let {
            Board(it)
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
        Button(
            onClick = {
                onDirectionChange(Pair(0, -1))
                movement = true
            },
            enabled = !movement,
            modifier = buttonSize,
            colors = ButtonDefaults.buttonColors(Color.Gray),
            shape = buttonShape
        ) {
            Icon(Icons.Default.KeyboardArrowUp, "Up")
        }
        Row {
            Button(
                onClick = {
                    onDirectionChange(Pair(-1, 0))
                    movement = false
                },
                enabled = movement,
                modifier = buttonSize,
                colors = ButtonDefaults.buttonColors(Color.Gray),
                shape = buttonShape
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, "Left")
            }
            Spacer(modifier = buttonSize)
            Button(
                onClick = {
                    onDirectionChange(Pair(1, 0))
                    movement = false
                },
                enabled = movement,
                modifier = buttonSize,
                colors = ButtonDefaults.buttonColors(Color.Gray),
                shape = buttonShape
            ) {
                Icon(Icons.Default.KeyboardArrowRight, "Right")
            }
        }
        Button(
            onClick = {
                onDirectionChange(Pair(0, 1))
                movement = true
            },
            enabled = !movement,
            modifier = buttonSize,
            colors = ButtonDefaults.buttonColors(Color.Gray),
            shape = buttonShape
        ) {
            Icon(Icons.Default.KeyboardArrowDown, "Down")
        }
    }
}

//Игровое поле
@Composable
fun Board(state: State) {
    BoxWithConstraints(Modifier.padding(16.dp)) {
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
                .offset(x = tileSize * state.food.first, y = tileSize * state.food.second)
                .size(tileSize)
                .background(
                    Color.Gray, CircleShape
                )
        )

        //Змея
        state.snake.forEach {
            Box(
                modifier = Modifier
                    .offset(x = tileSize * it.first, y = tileSize * it.second)
                    .size(tileSize)
                    .background(
                        Color.DarkGray, shape = RoundedCornerShape(16)
                    )
            )
        }
    }
}
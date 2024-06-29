package ru.pseudonimb.clickergame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.dataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.MainScope

@Composable
fun MainScreen(onClick: () -> Unit) {

    val buttonShape = RoundedCornerShape(16.dp)
    val buttonSize = Modifier.height(96.dp).fillMaxWidth().padding(16.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(24.dp).fillMaxSize()
    ) {
        Button(
            onClick = {
                onClick()
            },
            modifier = buttonSize,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(Color.Gray)
        ) {
            Text(text = "Начать игру")
        }
        Button(
            onClick = {  },
            modifier = buttonSize,
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(Color.Gray)
        ) {
            Text(text = "Настройки")
        }
    }
}
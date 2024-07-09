package ru.pseudonimb.snakesunited.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainScreen(navigateToGame: () -> Unit, navigateToRecords: () -> Unit) {

    val buttonShape = RoundedCornerShape(16.dp)
    val buttonSize = Modifier.height(96.dp).fillMaxWidth().padding(16.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(24.dp).fillMaxSize()
    ) {
        OutlinedButton(
            onClick = {
                navigateToGame.invoke()
            },
            modifier = buttonSize,
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = buttonShape
        ) {
            Text(text = "New game", fontSize = 16.sp)
        }
        OutlinedButton(
            onClick = {
                navigateToRecords.invoke()
            },
            modifier = buttonSize,
            shape = buttonShape
        ) {
            Text(text = "Leaderboards", fontSize = 16.sp)
        }
    }
}
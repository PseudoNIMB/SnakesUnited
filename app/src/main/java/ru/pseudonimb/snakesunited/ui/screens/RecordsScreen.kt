package ru.pseudonimb.snakesunited.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecordsScreen (navigateToMainMenu: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier
            .padding(0.dp, 16.dp)
            .fillMaxSize()) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(horizontal = 40.dp, vertical = 20.dp),
                color = Color.DarkGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                text = "Online leaderboard:"
            )
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 40.dp, vertical = 4.dp),
                color = Color.DarkGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                text = "Record 1"
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .size(2.dp)
                    .border(2.dp, Color.DarkGray)
            )
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 40.dp, vertical = 4.dp),
                color = Color.DarkGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                text = "Record 2"
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .size(2.dp)
                    .border(2.dp, Color.DarkGray)
            )
            Text(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(horizontal = 40.dp, vertical = 4.dp),
                color = Color.DarkGray,
                fontFamily = FontFamily.Monospace,
                fontSize = 24.sp,
                text = "Record 3"
            )
        }
    }
}
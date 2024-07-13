package ru.pseudonimb.snakesunited.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.pseudonimb.snakesunited.R
import ru.pseudonimb.snakesunited.ui.data.RecordData

@Composable
fun RecordsScreen(navigateToMainMenu: () -> Unit) {
    val localUsername = auth.currentUser?.email?.substringBefore("@")
    val context = LocalContext.current
    val listOfRecords = remember {
        mutableStateOf(emptyList<RecordData>())
    }
    val fbs = Firebase.firestore

    LaunchedEffect(Unit) {
        if (auth.currentUser != null) Toast.makeText(context, context.getString(R.string.hello_word) + ", $localUsername", Toast.LENGTH_SHORT).show()
        fbs.collection("Records").get().addOnCompleteListener {
            if (it.isSuccessful) {
                listOfRecords.value = it.result.toObjects(RecordData::class.java)
            } else {
                it.exception?.printStackTrace()
            }
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 40.dp, vertical = 20.dp),
            color = Color.DarkGray,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            text = stringResource(id = R.string.online_leaderboard_title)
        )
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .padding(0.dp, 16.dp)
                .fillMaxSize()
        ) {
            items(listOfRecords.value.sortedByDescending { it.highestScore }) { records ->
                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth()
                            .padding(16.dp),
                        text = records.username.toUpperCase() + " " + records.highestScore
                    )
                }
            }
        }
    }
}
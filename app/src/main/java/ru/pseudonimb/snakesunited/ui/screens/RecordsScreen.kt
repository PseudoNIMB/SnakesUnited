package ru.pseudonimb.snakesunited.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import ru.pseudonimb.snakesunited.R
import ru.pseudonimb.snakesunited.ui.data.RecordData
import ru.pseudonimb.snakesunited.ui.theme.AppTypography
import ru.pseudonimb.snakesunited.ui.theme.MainTheme

@Composable
fun RecordsScreen(navigateToMainMenu: () -> Unit) {
    MainTheme {
        Surface(color = colorScheme.surfaceVariant) {
            val localUsername = auth.currentUser?.email?.substringBefore("@")
            val context = LocalContext.current
            val listOfRecords = remember {
                mutableStateOf(emptyList<RecordData>())
            }
            val fbs = Firebase.firestore

            LaunchedEffect(Unit) {
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
                IconButton(
                    onClick = {
                        if (auth.currentUser != null) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.successful_logout) + "$localUsername" + ".",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        Firebase.auth.signOut()
                    },
                    enabled = auth.currentUser != null,
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Icon(
                        Icons.Default.ExitToApp, "Logout",
                        modifier = Modifier.size(32.dp)
                    )
                }
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(20.dp),
                    color = colorScheme.secondary,
                    fontFamily = AppTypography.displaySmall.fontFamily,
                    fontSize = AppTypography.displaySmall.fontSize,
                    text = stringResource(id = R.string.online_leaderboard_title)
                )
                LazyColumn(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .padding(0.dp, 16.dp)
                        .fillMaxSize()
                ) {
                    itemsIndexed(listOfRecords.value.sortedByDescending { it.highestScore }) { index, records ->
                        OutlinedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            colors = CardDefaults.cardColors(containerColor = colorScheme.secondaryContainer)
                        ) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth()
                                    .padding(16.dp),
                                text = records.username.toUpperCase() + " " + records.highestScore,
                                fontFamily = AppTypography.bodyLarge.fontFamily,
                                fontSize = AppTypography.bodyLarge.fontSize,
                                color = colorScheme.primary
                            )
                            if (records.username.contains(localUsername.toString())) {
                                if (auth.currentUser != null) Toast.makeText(
                                    context,
                                    context.getString(R.string.hello_word) + ", $localUsername," + context.getString(R.string.you_got) + (index + 1).toString() + context.getString(
                                        R.string.place
                                    ),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
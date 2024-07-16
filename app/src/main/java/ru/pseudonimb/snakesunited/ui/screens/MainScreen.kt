package ru.pseudonimb.snakesunited.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.datastore.dataStore
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.pseudonimb.snakesunited.R
import ru.pseudonimb.snakesunited.utils.DataStoreManager
import ru.pseudonimb.snakesunited.utils.SettingsData

val auth = Firebase.auth
val authDialogState = mutableStateOf(false)

@Composable
fun MainScreen(navigateToGame: () -> Unit, navigateToRecords: () -> Unit) {
    val buttonShape = RoundedCornerShape(16.dp)
    val buttonSize = Modifier
        .height(96.dp)
        .fillMaxWidth()
        .padding(16.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier
            .padding(24.dp)
            .fillMaxSize()
    ) {
        OutlinedButton(
            onClick = {
                navigateToGame.invoke()
            },
            modifier = buttonSize,
            colors = ButtonDefaults.outlinedButtonColors(),
            shape = buttonShape
        ) {
            Text(text = stringResource(id = R.string.new_game), fontSize = 16.sp)
        }

        if (authDialogState.value) {
            DialogAuth(dialogState = authDialogState, navigateToRecords = navigateToRecords)
        }

        OutlinedButton(
            onClick = {
                if (auth.currentUser != null) {
                    navigateToRecords.invoke()
                } else {
                    authDialogState.value = true
                }
            },
            modifier = buttonSize,
            shape = buttonShape
        ) {
            Text(text = stringResource(id = R.string.leaderboards), fontSize = 16.sp)
        }
    }
}

@Composable
fun DialogAuth(dialogState: MutableState<Boolean>, navigateToRecords: () -> Unit) {
    val username = remember {
        mutableStateOf("")
    }
    val password = remember {
        mutableStateOf("")
    }
    Dialog(onDismissRequest = {
        dialogState.value = false
    }) {
        Column (
            modifier = Modifier.fillMaxSize().padding(0.dp, 120.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            //TODO Необходимо пароль отображать ещё раз, с понтом "сохраните его чтобы продолжить игру на других устройствах, и увидите вы его только единожды"
            Text(text = stringResource(R.string.if_you_want_to_see) + "\n" + stringResource(R.string.if_it_s_your_first_time), modifier = Modifier.padding(24.dp, 0.dp), color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = username.value, singleLine = true, label = {Text(text = stringResource(id = R.string.username))}, onValueChange = {
                username.value = it
            })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = password.value, singleLine = true, label = {Text(text = stringResource(id = R.string.password))}, onValueChange = {
                password.value = it
            })
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                OutlinedButton(onClick = {
                    signIn(auth, username.value, password.value, navigateToRecords)
                    authDialogState.value = false
                }) {
                    Text(text = stringResource(id = R.string.sign_in))
                }
                Spacer(modifier = Modifier.width(24.dp))
                OutlinedButton(onClick = {
                    navigateToRecords.invoke()
                    authDialogState.value = false
                }) {
                    Text(text = stringResource(id = R.string.take_a_look))
                }
            }
        }
    }
}

//TODO Сразу после логина подтягивать хайскор из облачной базы

private fun signIn(auth: FirebaseAuth, username: String, password: String, navigateToRecords: () -> Unit) {
    //fake username registration with @email.com postfix for Google
    val finalUsername = username + "@snakesunited.com"
    auth.signInWithEmailAndPassword(finalUsername, password).addOnCompleteListener {
        if (it.isSuccessful) {
            navigateToRecords.invoke()
            Log.e("TAG", "signIn is successful")
        } else {
            Log.e("TAG", "signIn error, trying to create an account")
            auth.createUserWithEmailAndPassword(finalUsername, password).addOnCompleteListener{
                if (it.isSuccessful) {
                    auth.signInWithEmailAndPassword(finalUsername, password).addOnCompleteListener {
                        navigateToRecords.invoke()
                        Log.e("TAG", "signIn after account creation is successful")
                    }
                } else {
                    Log.e("TAG", "signIn after account creation error")
                }
            }
        }
    }
}
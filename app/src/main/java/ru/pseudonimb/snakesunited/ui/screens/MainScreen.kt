package ru.pseudonimb.snakesunited.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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
import ru.pseudonimb.snakesunited.ui.theme.AppTypography
import ru.pseudonimb.snakesunited.ui.theme.MainTheme
import ru.pseudonimb.snakesunited.utils.DataStoreManager
import ru.pseudonimb.snakesunited.utils.SettingsData

val auth = Firebase.auth
val authDialogState = mutableStateOf(false)

@Composable
fun MainScreen(navigateToGame: () -> Unit, navigateToRecords: () -> Unit) {
    MainTheme {
        Surface(color = colorScheme.surfaceVariant) {
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
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colorScheme.secondaryContainer,
                        contentColor = colorScheme.primary
                    ),
                    shape = buttonShape
                ) {
                    Text(
                        text = stringResource(id = R.string.new_game),
                        fontFamily = AppTypography.titleLarge.fontFamily,
                        fontSize = AppTypography.titleLarge.fontSize,
                        color = colorScheme.primary
                    )
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
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = colorScheme.secondaryContainer,
                        contentColor = colorScheme.primary
                    ),
                    shape = buttonShape
                ) {
                    Text(
                        text = stringResource(id = R.string.leaderboards),
                        fontFamily = AppTypography.titleLarge.fontFamily,
                        fontSize = AppTypography.titleLarge.fontSize,
                        color = colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun DialogAuth(dialogState: MutableState<Boolean>, navigateToRecords: () -> Unit) {
    MainTheme {
        val username = remember {
            mutableStateOf("")
        }
        val password = remember {
            mutableStateOf("")
        }
        var loginError by remember { mutableStateOf(false) }
        var passwordError by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf("") }

        val pattern = remember { Regex("[a-z0-9]*") }

        Dialog(onDismissRequest = {
            dialogState.value = false
        }) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp, 120.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                //TODO Необходимо пароль отображать ещё раз, с понтом "сохраните его чтобы продолжить игру на других устройствах, и увидите вы его только единожды"
                Text(
                    text = stringResource(R.string.if_you_want_to_see) + "\n" + stringResource(R.string.if_it_s_your_first_time),
                    modifier = Modifier.padding(24.dp, 0.dp),
                    fontFamily = AppTypography.bodyMedium.fontFamily,
                    fontSize = AppTypography.bodyLarge.fontSize,
                    color = colorScheme.inversePrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    value = username.value,
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(id = R.string.username),
                            fontFamily = AppTypography.labelLarge.fontFamily,
                            fontSize = AppTypography.labelLarge.fontSize,
                            color = colorScheme.inversePrimary
                        )
                    },
                    onValueChange = {
                        if (it.matches(pattern)) username.value = it
                        if (loginError) {
                            errorMessage = ""
                            loginError = false
                        }
                    },
                    isError = loginError,
                    supportingText = {
                        if (loginError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.must_have_at_least_3_symbols),
                                fontFamily = AppTypography.labelLarge.fontFamily,
                                fontSize = AppTypography.labelLarge.fontSize,
                                color = colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect = true,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    value = password.value,
                    singleLine = true,
                    label = {
                        Text(
                            text = stringResource(id = R.string.password),
                            fontFamily = AppTypography.labelLarge.fontFamily,
                            fontSize = AppTypography.labelLarge.fontSize,
                            color = colorScheme.inversePrimary
                        )
                    },
                    onValueChange = {
                        password.value = it.filter { it.isDigit() }.ifBlank { "" }
                        if (passwordError) {
                            errorMessage = ""
                            passwordError = false
                        }
                    },
                    isError = passwordError,
                    supportingText = {
                        if (passwordError) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.must_have_at_least_8_symbols),
                                fontFamily = AppTypography.labelLarge.fontFamily,
                                fontSize = AppTypography.labelLarge.fontSize,
                                color = colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedButton(onClick = {
                        if (username.value.length < 3) {
                            errorMessage = "Must have at least 3 symbols"
                            loginError = true
                            return@OutlinedButton
                        }
                        if (password.value.length < 8) {
                            errorMessage = "Must have at least 8 symbols"
                            passwordError = true
                            return@OutlinedButton
                        }
                        signIn(auth, username.value, password.value, navigateToRecords)
                        authDialogState.value = false
                    }, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.sign_in),
                            fontFamily = AppTypography.bodyLarge.fontFamily,
                            fontSize = AppTypography.bodyLarge.fontSize,
                            color = colorScheme.inversePrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(24.dp))
                    OutlinedButton(onClick = {
                        navigateToRecords.invoke()
                        authDialogState.value = false
                    }, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.take_a_look),
                            fontFamily = AppTypography.bodyLarge.fontFamily,
                            fontSize = AppTypography.bodyLarge.fontSize,
                            color = colorScheme.inversePrimary
                        )
                    }
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
            auth.createUserWithEmailAndPassword(finalUsername, password).addOnCompleteListener {
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
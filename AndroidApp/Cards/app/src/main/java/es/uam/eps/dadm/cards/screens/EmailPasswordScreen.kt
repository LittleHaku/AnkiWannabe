package es.uam.eps.dadm.cards.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.SettingsActivity


@Composable
fun EmailPasswordScaffold(
    navController: NavHostController,
    viewModel: CardViewModel
) {
    val cards by viewModel.cards.observeAsState()
    val decks by viewModel.decks.observeAsState()

    Scaffold(
        content = { paddingValues ->
            Column(
                Modifier.padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                EmailPasswordScreen(
                    navController = navController,
                    viewModel = viewModel
                )

            }
        })
}

@Composable
fun EmailPasswordScreen(
    viewModel: CardViewModel, navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailPassword(navController = navController, viewModel = viewModel)
    }
}

@Composable
fun EmailPassword(
    navController: NavController, viewModel: CardViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val onEmailChanged = { value: String -> email = value }
    val onPasswordChanged = { value: String -> password = value }
    val baseContext = LocalContext.current

    val failedAuth = stringResource(id = R.string.failed_auth)
    val onSignedIn: () -> Unit = {
        viewModel.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(baseContext as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success,
                    // Update UI with the signed-in user's
                    // information
                    // val user = viewModel.auth.currentUser
                    // Navigate to deck list
                    viewModel.userId = Firebase.auth.currentUser?.uid ?: "unknown user"
                    SettingsActivity.setLoggedIn(baseContext, true)
                    navController.navigate(NavRoutes.Decks.route)
                } else {
                    // If sign in fails,
                    // display a message to the user
                    Toast.makeText(
                        baseContext,
                        failedAuth,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    val onSignedUp: () -> Unit = {
        viewModel.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(baseContext as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success,
                    // update UI with the signed-in
                    // user's information
                    viewModel.userId = Firebase.auth.currentUser?.uid ?: "unknown user"
                    // Navigate to list of decks
                    navController.navigate(NavRoutes.Decks.route)
                } else {
                    // If sign in fails,
                    // display a message to the user.
                    Toast.makeText(
                        baseContext,
                        failedAuth,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    OutlinedTextField(value = email,
        onValueChange = onEmailChanged,
        label = { Text(text = stringResource(id = R.string.email_field)) })

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(value = password,
        onValueChange = onPasswordChanged,
        label = { Text(stringResource(id = R.string.password_field)) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        })

    Row {
        Button(
            onClick = {
                onSignedUp()
            }, modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }

        Button(
            onClick = {
                onSignedIn()
            }, modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
    }

}



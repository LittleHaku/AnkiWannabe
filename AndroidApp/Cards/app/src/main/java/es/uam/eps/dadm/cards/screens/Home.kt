package es.uam.eps.dadm.cards.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.google.firebase.Firebase
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R

@Composable
fun Home(navController: NavController, viewModel: CardViewModel) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                if (viewModel.auth.currentUser != null) {
                    navController.navigate(NavRoutes.Decks.route)
                } else
                    navController.navigate(NavRoutes.Login.route)

            }
        ) {
            Text(stringResource(id = R.string.app_name))
        }
    }
}
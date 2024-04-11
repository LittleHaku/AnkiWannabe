package es.uam.eps.dadm.cards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.EmailPassword


@OptIn(ExperimentalMaterial3Api::class)
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


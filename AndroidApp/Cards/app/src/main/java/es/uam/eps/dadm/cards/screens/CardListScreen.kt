package es.uam.eps.dadm.cards.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import es.uam.eps.dadm.cards.CardList
import es.uam.eps.dadm.cards.CardViewModel

@Composable
fun CardListScreen(
    viewModel: CardViewModel, navController: NavController, deckId: String
) {
    CardList(
        viewModel = viewModel, navController, deckId = deckId
    )
}
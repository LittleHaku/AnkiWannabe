package es.uam.eps.dadm.cards.screens


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.navigation.NavController
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.DeckList


@Composable
fun DeckListScreen(
    viewModel: CardViewModel, navController: NavController
) {
    //val cards by viewModel.cards.observeAsState(emptyList())
    //val decks by viewModel.decks.observeAsState(emptyList())
    val userId = viewModel.userId
    val cards by viewModel.getCardsFromUser(userId).observeAsState(
        initial = emptyList()
    )
    val decks by viewModel.getDecksFromUser(userId).observeAsState(
        initial = emptyList()
    )
    DeckList(cards = cards, decks = decks, navController = navController, viewModel)
}
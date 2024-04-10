package es.uam.eps.dadm.cards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import es.uam.eps.dadm.cards.CardEditor
import es.uam.eps.dadm.cards.CardViewModel

@Composable
fun CardEditorScreen(
    viewModel: CardViewModel, navController: NavController, cardId: String, deckId: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardEditor(
            viewModel, navController = navController, cardId = cardId, deckId = deckId
        )
    }
}
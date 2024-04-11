package es.uam.eps.dadm.cards.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.Study

@Composable
fun StudyScreen(viewModel: CardViewModel) {
    /*val card by viewModel.dueCard.observeAsState()
    val nCards by viewModel.nDueCards.observeAsState(initial = 0)*/
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Study(viewModel = viewModel)

    }
}
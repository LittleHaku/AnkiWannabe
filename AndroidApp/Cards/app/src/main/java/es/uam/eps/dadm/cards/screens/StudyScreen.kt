package es.uam.eps.dadm.cards.screens

import androidx.compose.runtime.Composable
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.Study

@Composable
fun StudyScreen(viewModel: CardViewModel) {
    Study(viewModel = viewModel)
}
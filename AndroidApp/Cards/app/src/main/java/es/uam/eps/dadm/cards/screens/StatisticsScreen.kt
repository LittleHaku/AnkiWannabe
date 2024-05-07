package es.uam.eps.dadm.cards.screens

import androidx.compose.runtime.Composable
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.Statistics

@Composable
fun StatisticsScreen(viewModel: CardViewModel) {
    Statistics(viewModel = viewModel)
}
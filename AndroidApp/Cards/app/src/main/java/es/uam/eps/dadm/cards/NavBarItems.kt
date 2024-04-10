package es.uam.eps.dadm.cards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.PlayArrow

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Decks", image = Icons.AutoMirrored.Filled.List, route = "decks"
        ), BarItem(
            title = "Study", image = Icons.Filled.PlayArrow, route = "study"
        )
    )
}
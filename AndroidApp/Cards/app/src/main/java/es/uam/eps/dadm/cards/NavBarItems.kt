package es.uam.eps.dadm.cards

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Decks",
            image = Icons.Filled.List,
            route = "decks"
        ),
        BarItem(
            title = "Cards",
            image = Icons.Filled.List,
            route = "cards"
        ),
        BarItem(
            title = "Study",
            image = Icons.Filled.PlayArrow,
            route = "study"
        )
    )
}
package es.uam.eps.dadm.cards

sealed class NavRoutes(val route: String) {
    data object Home : NavRoutes("home")
    data object Cards : NavRoutes("cards")
    data object Decks : NavRoutes("decks")
    data object Statistics : NavRoutes("statistics")

    data object CardEditor : NavRoutes("cardEditor")
    data object DeckEditor : NavRoutes("deckEditor")
    data object Study : NavRoutes("study")

    data object Login : NavRoutes("login")
}
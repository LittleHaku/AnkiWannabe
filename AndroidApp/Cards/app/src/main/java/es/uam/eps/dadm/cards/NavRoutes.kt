package es.uam.eps.dadm.cards

sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Cards : NavRoutes("cards")
    object Decks : NavRoutes("decks")

    object CardEditor : NavRoutes("cardEditor")
}
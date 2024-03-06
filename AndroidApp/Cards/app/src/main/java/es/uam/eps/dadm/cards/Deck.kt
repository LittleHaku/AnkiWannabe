package es.uam.eps.dadm.cards

import java.util.UUID

class Deck(
    val deckId: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String
)
package es.uam.eps.dadm.cards

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "decks_table")
open class Deck(
    @PrimaryKey val deckId: String = UUID.randomUUID().toString(),
    var name: String,
    var description: String = "",
    var userId: String

) {
    constructor() : this(name = "", description = "", userId = "")
}
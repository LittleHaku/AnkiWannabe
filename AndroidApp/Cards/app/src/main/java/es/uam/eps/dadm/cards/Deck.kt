package es.uam.eps.dadm.cards

import androidx.room.ColumnInfo
import java.util.UUID
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName= "decks_table")
open class Deck(
    @PrimaryKey
    val deckId: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = ""
)
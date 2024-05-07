package es.uam.eps.dadm.cards

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "reviews_table")
data class Review (
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var reviewDate: String,
    var nextReviewDate: String,
    var cardId: String,
    var deckId: String,
    var userId: String
)
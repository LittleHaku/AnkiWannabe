package es.uam.eps.dadm.cards.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import es.uam.eps.dadm.cards.Card


@Dao
interface CardDao {
    @Query("SELECT * FROM cards_table")
    fun getCards(): LiveData<List<Card>>

    @Query("SELECT * FROM cards_table WHERE id = :cardId")
    fun getCard(cardId: String) : Card

    @Insert
    suspend fun addCard(card: Card)

    @Query("DELETE FROM cards_table")
    suspend fun deleteCards()

    @Update
    suspend fun updateCard(card: Card)

}
package es.uam.eps.dadm.cards.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.Deck


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

    @Query("SELECT * FROM cards_table WHERE deckId = :id")
    fun getCardsOfDeck(id: String): LiveData<List<Card>>

    @Insert
    suspend fun addDeck(deck: Deck)
    @Query("DELETE FROM decks_table")
    suspend fun deleteDecks()

    @Query("SELECT * FROM decks_table")
    fun getDecks(): LiveData<List<Deck>>

    @Query("SELECT * FROM cards_table " +
            "INNER JOIN decks_table ON decks_table.deckId = cards_table.deckId " +
            "WHERE decks_table.name LIKE :deckName")
    fun getCardsByDeckName(deckName: String): LiveData<List<Card>>
}
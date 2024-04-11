package es.uam.eps.dadm.cards

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.database.CardDao
import es.uam.eps.dadm.cards.database.CardDatabase
import kotlinx.coroutines.launch
import java.time.LocalDateTime.now

class CardViewModel(application: Application) : ViewModel() {
    val cards: LiveData<List<Card>>
    val decks: LiveData<List<Deck>>
    val dueCard: LiveData<Card?>
    val nDueCards: LiveData<Int>
    private val cardDao: CardDao
    var auth = Firebase.auth
    var userId = Firebase.auth.currentUser?.uid ?: "unknown user"


    init {
        cardDao = CardDatabase.getInstance(application.applicationContext).cardDao
        cards = cardDao.getCards()
        decks = cardDao.getDecks()


        dueCard = cards.map {
            it.filter { card -> (card.isDue(now()) && (card.userId == Firebase.auth.currentUser?.uid)) }
                .run {
                    if (any()) random() else null
                }
        }

        nDueCards = cards.map {
            it.filter { card -> (card.isDue(now()) && (card.userId == Firebase.auth.currentUser?.uid)) }.size
        }
    }

    fun getUserDueCard(userId: String): LiveData<Card?> {
        return cardDao.getCardsFromUser(userId).map {
            it.filter { card -> (card.isDue(now())) }.run {
                if (any()) first() else null
            }
        }
    }

    fun getUserNumDueCards(userId: String): LiveData<Int> {
        return cardDao.getCardsFromUser(userId).map {
            it.filter { card -> (card.isDue(now()) && (card.userId == Firebase.auth.currentUser?.uid)) }.size
        }
    }

    fun addCard(card: Card) = viewModelScope.launch {
        cardDao.addCard(card)
    }

    private fun deleteCards() = viewModelScope.launch {
        cardDao.deleteCards()
    }

    fun getCard(cardId: String): LiveData<Card> = cardDao.getCard(cardId)

    fun updateCard(card: Card) = viewModelScope.launch {
        cardDao.updateCard(card)
    }

    fun updateDeck(deck: Deck) = viewModelScope.launch {
        cardDao.updateDeck(deck)
    }

    fun addDeck(deck: Deck) = viewModelScope.launch {
        cardDao.addDeck(deck)
    }

    private fun deleteDecks() = viewModelScope.launch {
        cardDao.deleteDecks()
    }

    fun getCardsByDeckId(deckId: String) = cardDao.getCardsByDeckId(deckId)

    @Suppress("unused")
    fun getAllCards() = cardDao.getCards()

    @Suppress("unused")
    fun getCardsAndDecks() = cardDao.getCardsAndDecks()
    fun getDeckById(deckId: String) = cardDao.getDeckById(deckId)

    fun deleteDeckById(deckId: String) = viewModelScope.launch {
        cardDao.deleteDeckId(deckId)
        cardDao.deleteCardsInDeck(deckId)
    }

    fun deleteCardById(cardId: String) = viewModelScope.launch {
        cardDao.deleteCardById(cardId)
    }

    fun getCardsFromUser(userId: String) = cardDao.getCardsFromUser(userId)

    fun getDecksFromUser(userId: String) = cardDao.getDecksFromUser(userId)

    fun getCardsFromDeckAndUser(userId: String, deckId: String) =
        cardDao.getCardsFromDeckAndUser(userId, deckId)

    fun uploadToFirebase(cards: List<Card>, decks: List<Deck>) {
        val decksReference = FirebaseDatabase.getInstance().getReference("decks")
        decksReference.setValue(null)
        decks.forEach { deck ->
            decksReference.child(deck.deckId).setValue(deck)
        }

        val cardsReference = FirebaseDatabase.getInstance().getReference("cards")
        cardsReference.setValue(null)
        cards.forEach { card -> cardsReference.child(card.id).setValue(card) }
    }

    fun downloadFromFirebase() {
        val decksReference = FirebaseDatabase.getInstance().getReference("decks")
        decksReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val decks = mutableListOf<Deck>()
                snapshot.children.forEach {
                    it.getValue(Deck::class.java)?.let {
                        decks.add(it)
                    }
                }

                deleteDecks()
                decks.forEach { deck -> addDeck(deck) }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })
        val cardsReference = FirebaseDatabase.getInstance().getReference("cards")
        cardsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cards = mutableListOf<Card>()
                snapshot.children.forEach {
                    it.getValue(Card::class.java)?.let {
                        cards.add(it)
                    }
                }

                deleteCards()
                cards.forEach { card ->
                    addCard(card)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

@Suppress("UNCHECKED_CAST")
class CardViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardViewModel(application) as T
    }
}
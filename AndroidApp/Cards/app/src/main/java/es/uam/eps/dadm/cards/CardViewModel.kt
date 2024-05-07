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
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.LocalDateTime.parse

class CardViewModel(application: Application) : ViewModel() {
    val cards: LiveData<List<Card>>
    val decks: LiveData<List<Deck>>
    val reviews: LiveData<List<Review>>
    val dueCard: LiveData<Card?>
    val nDueCards: LiveData<Int>
    private val cardDao: CardDao
    var auth = Firebase.auth
    var userId = Firebase.auth.currentUser?.uid ?: "unknown user"


    init {
        cardDao = CardDatabase.getInstance(application.applicationContext).cardDao
        cards = cardDao.getCards()
        decks = cardDao.getDecks()
        reviews = cardDao.getReviews()


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

    fun addReview(review: Review) = viewModelScope.launch {
        cardDao.addReview(review)
    }

    fun addCard(card: Card) = viewModelScope.launch {
        cardDao.addCard(card)
    }

    private fun deleteCards() = viewModelScope.launch {
        cardDao.deleteCards()
    }

    private fun deleteReviews() = viewModelScope.launch {
        cardDao.deleteReviews()
    }

    // Shouldn't this also have a userId??
    fun fromReviewsToMap(
        reviews: List<Review>
    ): Map<String, Int> {
        val map = mutableMapOf<String, Int>()

        reviews.forEach { review ->
            // get the previous number of reviews and add 1

            // parse it because if not it takes the date and TIME and we don't need time
            // because if not the mapping doesn't group them
            val date = parse(review.reviewDate).toLocalDate().toString()
            //val date = review.reviewDate
            map[date] = map.getOrDefault(date, 0) + 1
        }

        return map
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


    fun uploadToFirebase(cards: List<Card>, decks: List<Deck>, reviews: List<Review>) {
        val decksReference = FirebaseDatabase.getInstance().getReference("decks")
        decksReference.setValue(null)
        decks.forEach { deck ->
            decksReference.child(deck.deckId).setValue(deck)
        }

        val cardsReference = FirebaseDatabase.getInstance().getReference("cards")
        cardsReference.setValue(null)
        cards.forEach { card -> cardsReference.child(card.id).setValue(card) }

        val reviewsReference = FirebaseDatabase.getInstance().getReference("reviews")
        reviewsReference.setValue(null)
        reviews.forEach {
            review -> reviewsReference.child(review.id).setValue(review)
        }
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

        val reviewsReference = FirebaseDatabase.getInstance().getReference("reviews")
        reviewsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviews = mutableListOf<Review>()
                snapshot.children.forEach {
                    it.getValue(Review::class.java)?.let {
                        reviews.add(it)
                    }
                }

                deleteReviews()
                reviews.forEach { review ->
                    addReview(review)
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
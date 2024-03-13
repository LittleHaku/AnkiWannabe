package es.uam.eps.dadm.cards

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import es.uam.eps.dadm.cards.database.CardDao
import es.uam.eps.dadm.cards.database.CardDatabase
import kotlinx.coroutines.launch
import java.time.LocalDateTime.now

class CardViewModel(application: Application) : ViewModel() {
    val cards: LiveData<List<Card>>
    val dueCard: LiveData<Card?>
    val nDueCards: LiveData<Int>
    private val cardDao: CardDao

    init {
        cardDao = CardDatabase.getInstance(application.applicationContext).cardDao
        cards = cardDao.getCards()

        deleteCards()
        addCard(Card("To wake up", "Despertarse"))
        addCard(Card("To slow down", "Ralentizar"))
        addCard(Card("To give up", "Rendirse"))
        addCard(Card("To come up", "Acercarse"))

        dueCard = cards.map {
            it.filter { card -> card.isDue(now()) }.run {
                if (any()) random() else null
            }
        }

        nDueCards = cards.map {
            it.filter { card -> card.isDue(now()) }.size
        }
    }

    fun addCard(card: Card) = viewModelScope.launch {
        cardDao.addCard(card)
    }

    fun deleteCards() = viewModelScope.launch {
        cardDao.deleteCards()
    }

    fun getCard(cardId: String) = cardDao.getCard(cardId)

    fun updateCard(card: Card) = viewModelScope.launch {
        cardDao.updateCard(card)
    }

    fun update(card: Card, quality: Int) {
        val updateCard = cardDao.getCard(card.id)
        //updateCard.quality = quality
        //updateCard.update(now())

        //cardDao.updateCard(updateCard)
    }

}

class CardViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardViewModel(application) as T
    }
}
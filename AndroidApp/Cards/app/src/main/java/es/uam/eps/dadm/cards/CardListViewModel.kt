package es.uam.eps.dadm.cards

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import es.uam.eps.dadm.cards.database.CardDao
import es.uam.eps.dadm.cards.database.CardDatabase
import kotlinx.coroutines.launch

class CardViewModel(application: Application) : ViewModel() {
    val cards: LiveData<List<Card>>
    private val cardDao: CardDao

    init {
        cardDao = CardDatabase.getInstance(application.applicationContext).cardDao

        addCard(Card("To wake up", "Despertarse"))
        addCard(Card("To slow down", "Ralentizar"))
        addCard(Card("To give up", "Rendirse"))
        addCard(Card("To come up", "Acercarse"))

        cards = cardDao.getCards()
    }

    fun addCard(card: Card) = viewModelScope.launch {
        cardDao.addCard(card)
    }
}

class CardViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CardViewModel(application) as T
    }
}
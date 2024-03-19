package es.uam.eps.dadm.cards

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import es.uam.eps.dadm.cards.ui.theme.CardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: CardViewModel = viewModel(
                            it,
                            "CardViewModel",
                            CardViewModelFactory(LocalContext.current.applicationContext as Application)
                        )

                        //CardList(viewModel)
                        //Study(viewModel)
                        //DeckList(viewModel)

                        /*var deck = Deck(name = "English", description = "Se enseña mal")
                        viewModel.addDeck(deck = deck)
                        DeckEditor(viewModel = viewModel, deck = deck)*/

                        //DeckCreator(viewModel = viewModel)
                        NavComposable(viewModel)
                    }
                }
            }
        }
    }
}
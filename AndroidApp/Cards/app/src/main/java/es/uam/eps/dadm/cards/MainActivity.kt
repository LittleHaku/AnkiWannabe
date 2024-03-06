package es.uam.eps.dadm.cards

import Card
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uam.eps.dadm.cards.ui.theme.CardsTheme
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val decks = mutableListOf<Deck>()
                    val english =
                        Deck(
                            name = "English",
                            description = "English phrasal verbs"
                        )
                    val cards = mutableListOf<Card>()
                    cards += Card("To wake up", "Despertarse", deckId = english.deckId)
                    cards += Card("To slow down", "Ralentizar", deckId = english.deckId)
                    cards += Card("To give up", "Rendirse", deckId = english.deckId)
                    cards += Card("To come up", "Acercarse", deckId = english.deckId)

                    val french = Deck(
                        name = "French",
                        description = "French verbs"
                    )
                    cards += Card("Se réveiller", "Despertarse", deckId = french.deckId)
                    cards += Card("Ralentir", "Ralentizar", deckId = french.deckId)
                    cards += Card("Abandonner", "Rendirse", deckId = french.deckId)
                    cards += Card("Approcher", "Acercarse", deckId = french.deckId)

                    decks += english
                    decks += french

                    DeckList(cards, decks)
                }
            }
        }
    }
}

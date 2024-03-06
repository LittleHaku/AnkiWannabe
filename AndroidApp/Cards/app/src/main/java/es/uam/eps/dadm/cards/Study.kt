package es.uam.eps.dadm.cards

import Card
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import es.uam.eps.dadm.cards.ui.theme.CardsTheme
import java.time.LocalDateTime
import java.time.LocalDateTime.now

val RED = Color(0xfffc496d)
val YELLOW = Color(0xfff7e848)
val GREEN = Color(0xff40de68)
val PASTEL_GREEN = Color(0xFF9BDEAC)
val BLACK = Color(0xFF121212)

@Composable
fun CardView(cards: List<Card>) {


    var answered by remember { mutableStateOf(false) }

    val card = getCard(cards)

    val onAnswered = { value: Boolean ->
        answered = value
    }

    card?.let {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CardData(card, answered, onAnswered)
        }
    }
}

@Composable
private fun getCard(cards: List<Card>) = cards.filter {
    LocalDateTime.parse(it.nextPracticeDate) <= now()
}.getOrNull(0)

@Composable
fun CardData(card: Card, answered: Boolean, onAnswered: (Boolean) -> Unit) {
    val onDifficultyChecked = { value: Int ->
        card.quality = value
        card.update(now())
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(card.question)

        Spacer(modifier = Modifier.height(16.dp))

        if (answered) {
            Text(card.answer)
            DifficultyButtons(onAnswered, onDifficultyChecked)
        } else {
            ViewAnswerButton(onAnswered)
        }
    }
}

@Composable
fun CardList(cards: List<Card>) {
    val context = LocalContext.current
    val onItemClick = { card: Card ->
        Toast.makeText(
            context,
            card.question,
            Toast.LENGTH_SHORT
        ).show()
    }

    LazyColumn() {
        item {
            Text(
                "LIST OF CARDS",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge
            )
        }
        items(cards) { card ->
            CardItem(card, onItemClick)
        }
    }
}

@Composable
fun CardItem(
    card: Card,
    onItemClick: (Card) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier
            .fillMaxWidth()
            .padding(all = 5.dp)
            .clickable { onItemClick(card) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        var switchState: Boolean by remember { mutableStateOf(false) }
        val onSwitchState = { value: Boolean ->
            switchState = value
        }
        Column {
            SwitchIcon(switchState, onSwitchState)
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                card.question,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(card.answer, modifier, style = MaterialTheme.typography.bodyMedium)
            if (switchState) {
                Text(
                    "Quality = ${card.quality}\nEasiness = ${card.easiness}" +
                            "\nRepetitions = ${card.repetitions}",
                    modifier,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Column(modifier = Modifier.padding(end = 10.dp), horizontalAlignment = Alignment.End) {
            Text(card.date.toString().substring(0..9))
        }
    }
}


@Composable
fun SwitchIcon(switchState: Boolean, onSwitchChange: (Boolean) -> Unit) {
    val drawableResource = if (switchState) R.drawable.rounded_arrow_drop_down_24
    else R.drawable.rounded_arrow_right_24

    Icon(
        painter = painterResource(id = drawableResource),
        contentDescription = "contentDescription",
        modifier = Modifier
            .clickable { onSwitchChange(!switchState) }
    )
}

@Composable
fun ViewAnswerButton(onAnswered: (Boolean) -> Unit) {
    Button(
        onClick = { onAnswered(true) },
        colors = ButtonDefaults.buttonColors(containerColor = GREEN)
    ) {
        Text(text = "View Answer", color = BLACK)
    }
}

@Composable
fun DifficultyButtons(
    onAnswered: (Boolean) -> Unit,
    onDifficultyChecked: (Int) -> Unit
) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                onDifficultyChecked(0)
                onAnswered(false)
            },
            colors = ButtonDefaults.buttonColors(containerColor = GREEN)
        ) {
            Text("Easy", color = BLACK)
        }
        Button(
            onClick = {
                onDifficultyChecked(3)
                onAnswered(false)
            },
            colors = ButtonDefaults.buttonColors(containerColor = YELLOW)
        ) {
            Text("Doubt", color = BLACK)
        }
        Button(
            onClick = {
                onDifficultyChecked(5)
                onAnswered(false)
            },
            colors = ButtonDefaults.buttonColors(containerColor = RED)
        ) {
            Text("Hard", color = BLACK)
        }
    }
}

@Composable
fun DeckList(cards: List<Card>, decks: List<Deck>) {
    val context = LocalContext.current
    val onItemClick = { deck: Deck ->
        Toast.makeText(
            context,
            "${deck.name} selected",
            Toast.LENGTH_SHORT
        ).show()
    }

    LazyColumn() {
        item {
            Text(
                "LIST OF DECKS",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall
            )
        }
        items(decks) { deck ->
            DeckItem(deck, cards, onItemClick)

        }
    }
}

@Composable
fun DeckItem(
    deck: Deck,
    cards: List<Card>,
    onItemClick: (Deck) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Row(
        modifier
            .fillMaxWidth()
            .padding(all = 5.dp)
            .clickable { onItemClick(deck) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        var switchState: Boolean by remember { mutableStateOf(false) }
        val onSwitchState = { value: Boolean ->
            switchState = value
        }

        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                deck.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(deck.description, modifier, style = MaterialTheme.typography.bodyMedium)

        }
        Column(modifier = Modifier.padding(end = 10.dp), horizontalAlignment = Alignment.End) {
            val numberOfCardsInDeck = cards.filter {it.deckId == deck.deckId }.size
            Text("$numberOfCardsInDeck cards",
                style=MaterialTheme.typography.bodyMedium)
        }
    }


}


@Preview(showBackground = true)
@Composable
fun Screen() {
    CardsTheme {
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
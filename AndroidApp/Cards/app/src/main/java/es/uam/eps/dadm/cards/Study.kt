package es.uam.eps.dadm.cards

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import es.uam.eps.dadm.cards.ui.theme.CardsTheme
import java.time.LocalDateTime
import java.time.LocalDateTime.now

val RED = Color(0xfffc496d)
val YELLOW = Color(0xfff7e848)
val GREEN = Color(0xff40de68)
val PASTEL_GREEN = Color(0xFF9BDEAC)
val BLACK = Color(0xFF121212)


@Composable
fun NavComposable(viewModel: CardViewModel) {
    var showStudy by remember { mutableStateOf(false) }
    var showDeckList by remember { mutableStateOf(false) }
    var showCardList by remember { mutableStateOf(false) }
    var showDeckCreator by remember { mutableStateOf(false) }

    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = PASTEL_GREEN
    )


    val buttonModifier = Modifier.padding(8.dp)
    val buttonColors = ButtonDefaults.buttonColors(PASTEL_GREEN)


    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(10.dp)
        ) {
            Button(
                onClick = {
                    showStudy = true
                    showDeckList = false
                    showCardList = false
                    showDeckCreator = false
                }, modifier = buttonModifier, colors = buttonColors
            ) {
                Text(stringResource(id = R.string.study_menu))
            }
            Button(
                onClick = {
                    showStudy = false
                    showDeckList = true
                    showCardList = false
                    showDeckCreator = false
                }, modifier = buttonModifier, colors = buttonColors
            ) {
                Text(stringResource(id = R.string.deck_list_menu))
            }
            Button(
                onClick = {
                    showStudy = false
                    showDeckList = false
                    showCardList = true
                    showDeckCreator = false
                }, modifier = buttonModifier, colors = buttonColors
            ) {
                Text(stringResource(id = R.string.card_list_menu))
            }
            Button(
                onClick = {
                    showStudy = false
                    showDeckList = false
                    showCardList = false
                    showDeckCreator = true
                }, modifier = buttonModifier, colors = buttonColors
            ) {
                Text(stringResource(id = R.string.create_deck_menu))
            }
            Button(
                onClick = { viewModel.populateDB() },
                modifier = buttonModifier,
                colors = buttonColors
            ) {
                Text(stringResource(id = R.string.reset_db_menu))
            }
        }

        if (showStudy) {
            Study(viewModel)
        } else if (showDeckList) {
            DeckList(viewModel)
        } else if (showCardList) {
            CardList(viewModel)
        } else if (showDeckCreator) {
            DeckCreator(viewModel)
        }
    }
}


@Composable
fun Study(viewModel: CardViewModel) {
    val card by viewModel.dueCard.observeAsState()
    val nCards by viewModel.nDueCards.observeAsState(initial = 0)
    card?.let {
        CardView(viewModel = viewModel, it, nCards)
    } ?: Toast.makeText(
        LocalContext.current, stringResource(id = R.string.no_more_cards), Toast.LENGTH_SHORT
    ).show()

}

@Composable
fun CardView(viewModel: CardViewModel, card: Card, nCards: Int) {


    var answered by remember { mutableStateOf(false) }

    //val card = getCard(cards)

    val onAnswered = { value: Boolean ->
        answered = value
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            stringResource(id = R.string.remaining_cards) + ": $nCards",
            modifier = Modifier.padding(top = 50.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CardData(card, answered, onAnswered, viewModel)
        }
    }


}

@Composable
private fun getCard(cards: List<Card>) = cards.filter {
    LocalDateTime.parse(it.nextPracticeDate) <= now()
}.getOrNull(0)

@Composable
fun CardData(
    card: Card, answered: Boolean, onAnswered: (Boolean) -> Unit, viewModel: CardViewModel
) {
    val onDifficultyChecked = { value: Int ->
        card.quality = value
        card.update(now())
        viewModel.updateCard(card)
        // Done again because lambda can't finish with the viewModel and it must be done after
        card.quality = value
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(card.question)

        Spacer(modifier = Modifier.height(16.dp))

        if (answered) {
            Text(card.answer)
            val intervals = card.possibleNextPractice(now())
            DifficultyButtons(onAnswered, onDifficultyChecked, intervals)
        } else {
            ViewAnswerButton(onAnswered)
        }
    }
}

@Composable
fun CardList(viewModel: CardViewModel) {
    var selectedDeck = "English"
    val cards by viewModel.getCardsByDeckName(selectedDeck).observeAsState(listOf())

    val all by viewModel.getCardsAndDecks().observeAsState()

    all?.let {
        it.forEach { deck, cards ->
            println(deck.name)
            cards.forEach { println(it.question) }
        }
    }

    val context = LocalContext.current
    val onItemClick = { card: Card ->
        Toast.makeText(
            context, card.question, Toast.LENGTH_SHORT
        ).show()
    }

    LazyColumn() {
        item {
            Text(
                stringResource(id = R.string.decks_cards) + ": $selectedDeck",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge
            )
        }
        cards?.let {
            items(it) { card ->
                CardItem(card, onItemClick)
            }
        }
    }
}

@Composable
fun CardItem(
    card: Card, onItemClick: (Card) -> Unit, modifier: Modifier = Modifier
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
                    stringResource(id = R.string.quality) + " = ${card.quality}\n" + stringResource(
                        id = R.string.easiness
                    ) + " = ${card.easiness}\n" + stringResource(id = R.string.repetitions) + " = ${card.repetitions}\n" + stringResource(
                        id = R.string.next_practice
                    ) + " = ${card.nextPracticeDate.substring(0..9)}",
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

    Icon(painter = painterResource(id = drawableResource),
        contentDescription = "contentDescription",
        modifier = Modifier.clickable { onSwitchChange(!switchState) })
}

@Composable
fun ViewAnswerButton(onAnswered: (Boolean) -> Unit) {
    Button(
        onClick = { onAnswered(true) }, colors = ButtonDefaults.buttonColors(containerColor = GREEN)
    ) {
        Text(text = stringResource(id = R.string.view_answer), color = BLACK)
    }
}

@Composable
fun DifficultyButtons(
    onAnswered: (Boolean) -> Unit, onDifficultyChecked: (Int) -> Unit, intervals: Map<Int, Long>
) {
    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    onDifficultyChecked(0)
                    onAnswered(false)
                }, colors = ButtonDefaults.buttonColors(containerColor = RED)
            ) {
                Text(stringResource(id = R.string.hard), color = BLACK)
            }
            Text(text = intervals[0].toString() + " " + stringResource(id = R.string.days))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Button(
                onClick = {
                    onDifficultyChecked(3)
                    onAnswered(false)
                }, colors = ButtonDefaults.buttonColors(containerColor = YELLOW)
            ) {
                Text(stringResource(id = R.string.doubt), color = BLACK)
            }
            Text(text = intervals[3].toString() + " " + stringResource(id = R.string.days))

        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Button(
                onClick = {
                    onDifficultyChecked(5)
                    onAnswered(false)
                }, colors = ButtonDefaults.buttonColors(containerColor = GREEN)
            ) {
                Text(stringResource(id = R.string.easy), color = BLACK)
            }
            Text(text = intervals[5].toString() + " " + stringResource(id = R.string.days))

        }
    }
}

@Composable
fun DeckList(viewModel: CardViewModel) {
    val cards by viewModel.cards.observeAsState(emptyList())
    val decks by viewModel.decks.observeAsState(emptyList())
    val context = LocalContext.current
    // done this way because it doesn't work inside Toast
    val selectedString = stringResource(id = R.string.selected)
    val onItemClick = { deck: Deck ->
        Toast.makeText(
            context, "${deck.name} $selectedString", Toast.LENGTH_SHORT
        ).show()
    }

    LazyColumn() {
        item {
            Text(
                stringResource(id = R.string.list_of_decks),
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
    deck: Deck, cards: List<Card>, onItemClick: (Deck) -> Unit, modifier: Modifier = Modifier
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
                deck.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge
            )
            Text(deck.description, modifier, style = MaterialTheme.typography.bodyMedium)

        }
        Column(modifier = Modifier.padding(end = 10.dp), horizontalAlignment = Alignment.End) {
            val numberOfCardsInDeck = cards.filter { it.deckId == deck.deckId }.size
            Text(
                "$numberOfCardsInDeck " + stringResource(id = R.string.cards),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }


}

@Composable
fun DeckEditor(viewModel: CardViewModel, deck: Deck) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        var name by remember { mutableStateOf(deck.name) }
        var description by remember {
            mutableStateOf(deck.description)
        }
        val onNameChanged = { value: String -> name = value }
        val onDescriptionChanged = { value: String -> description = value }

        val context = LocalContext.current

        OutlinedTextField(value = name,
            onValueChange = onNameChanged,
            label = { Text(text = stringResource(id = R.string.deck_name)) })

        OutlinedTextField(value = description,
            onValueChange = onDescriptionChanged,
            label = { Text(stringResource(id = R.string.deck_description)) })

        Row() {


            Button(
                onClick = { },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RED)
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
            val editedString = stringResource(id = R.string.edited_succ)
            val introduceString = stringResource(id = R.string.introduce_some_values)
            Button(
                onClick = {
                    if (name.isNotEmpty() && description.isNotEmpty()) {
                        deck.name = name
                        deck.description = description
                        viewModel.updateDeck(deck)
                        Toast.makeText(context, editedString, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, introduceString, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GREEN)
            ) {
                Text(text = stringResource(id = R.string.accept))
            }
        }

    }
}

@Composable
fun DeckCreator(viewModel: CardViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        var name by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        val onNameChanged = { value: String -> name = value }
        val onDescriptionChanged = { value: String -> description = value }

        val context = LocalContext.current

        OutlinedTextField(value = name,
            onValueChange = onNameChanged,
            label = { Text(text = stringResource(id = R.string.deck_name)) })

        OutlinedTextField(value = description,
            onValueChange = onDescriptionChanged,
            label = { Text(stringResource(id = R.string.deck_description)) })

        Row() {


            Button(
                onClick = { },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RED)
            ) {
                Text(text = stringResource(id = R.string.cancel))
            }
            val createdString = stringResource(id = R.string.created_succ)
            val introduceString = stringResource(id = R.string.introduce_some_values)
            Button(
                onClick = {
                    if (name.isNotEmpty() && description.isNotEmpty()) {
                        val deck = Deck(name = name, description = description)
                        viewModel.addDeck(deck)
                        Toast.makeText(context, createdString, Toast.LENGTH_SHORT)
                            .show()

                    } else {
                        Toast.makeText(context, introduceString, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GREEN)
            ) {
                Text(text = stringResource(id = R.string.accept))
            }
        }

    }
}


@Preview(showBackground = true)
@Composable
fun Screen() {
    CardsTheme {
        val decks = mutableListOf<Deck>()
        val english = Deck(
            name = "English", description = "English phrasal verbs"
        )
        val cards = mutableListOf<Card>()
        cards += Card("To wake up", "Despertarse", deckId = english.deckId)
        cards += Card("To slow down", "Ralentizar", deckId = english.deckId)
        cards += Card("To give up", "Rendirse", deckId = english.deckId)
        cards += Card("To come up", "Acercarse", deckId = english.deckId)

        val french = Deck(
            name = "French", description = "French verbs"
        )
        cards += Card("Se réveiller", "Despertarse", deckId = french.deckId)
        cards += Card("Ralentir", "Ralentizar", deckId = french.deckId)
        cards += Card("Abandonner", "Rendirse", deckId = french.deckId)
        cards += Card("Approcher", "Acercarse", deckId = french.deckId)

        decks += english
        decks += french
        //DeckList(cards, decks)
    }
}
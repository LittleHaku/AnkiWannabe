package es.uam.eps.dadm.cards

import android.app.Activity
import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay
import java.time.LocalDateTime.now

val RED = Color(0xfffc496d)
val YELLOW = Color(0xfff7e848)
val GREEN = Color(0xff40de68)

@Suppress("unused")
val PASTEL_GREEN = Color(0xFF9BDEAC)
val BLACK = Color(0xFF121212)


@Composable
fun Study(viewModel: CardViewModel) {
    val uid = Firebase.auth.currentUser?.uid ?: ""
    val card by viewModel.getUserDueCard(uid).observeAsState()
    val nCards by viewModel.nDueCards.observeAsState(initial = 0)
    val noMoreCards = stringResource(id = R.string.no_more_cards)
    if (card?.userId == Firebase.auth.currentUser?.uid) {
        card?.let { CardView(viewModel = viewModel, it, nCards) }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = noMoreCards, style = MaterialTheme.typography.titleLarge)
        }
    }

}

@Composable
fun CardView(viewModel: CardViewModel, card: Card, nCards: Int) {


    var answered by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val noMoreCardsString = stringResource(id = R.string.no_more_cards)
    val onAnswered = { value: Boolean ->
        answered = value
        if (nCards == 0) {
            Toast.makeText(
                context, noMoreCardsString, Toast.LENGTH_SHORT
            ).show()
        }
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
fun CardData(
    card: Card, answered: Boolean, onAnswered: (Boolean) -> Unit, viewModel: CardViewModel
) {
    val onDifficultyChecked = { value: Int ->
        card.quality = value
        card.update(now())
        viewModel.updateCard(card)
        val review = Review(
            reviewDate = now().toString(),
            nextReviewDate = card.nextPracticeDate,
            cardId = card.id,
            deckId = card.deckId,
            userId = card.userId
        )
        viewModel.addReview(review)
        // Done again because lambda can't finish with the viewModel and it must be done after
        card.quality = value
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Text(stringResource(id = R.string.card_question)+":")
        Text(
            card.question,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (answered) {
            Text(stringResource(id = R.string.card_answer)+":")
            Text(
                card.answer,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(id = R.string.select_difficulty)+":", modifier = Modifier.padding(8.dp))

            val intervals = card.possibleNextPractice()
            DifficultyButtons(onAnswered, onDifficultyChecked, intervals)
        } else {
            ViewAnswerButton(onAnswered)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardList(viewModel: CardViewModel, navController: NavController, deckId: String = "") {
    val cards by viewModel.getCardsByDeckId(deckId).observeAsState(listOf())
    val deck by viewModel.getDeckById(deckId).observeAsState()
    val selectedDeck = deck?.name

    val onItemClick = { card: Card ->
        navController.navigate(NavRoutes.CardEditor.route + "/${card.id}" + "/${card.deckId}")
    }

    LazyColumn {
        item {
            Text(
                stringResource(id = R.string.decks_cards) + ": $selectedDeck",
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge
            )
        }
        items(cards) { card ->
            SwipeToDeleteCard(viewModel, card, onItemClick, navController)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SwipeToDeleteCard(
    viewModel: CardViewModel, card: Card, onItemClick: (Card) -> Unit, navController: NavController
) {
    var removed by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val deletedString = stringResource(id = R.string.deleted)
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        when (it) {
            SwipeToDismissBoxValue.EndToStart -> {
                Toast.makeText(context, deletedString, Toast.LENGTH_SHORT).show()
                removed = true
            }

            else -> Unit
        }
        false
    })
    LaunchedEffect(removed) {
        if (removed) {
            delay(100L) // delay in milliseconds
            viewModel.deleteCardById(card.id)
            // IMPORTANT
            // Solves the problem of deleting, then syncing and the thing getting deleted again
            navController.navigate(NavRoutes.Cards.route + "/${card.deckId}")
        }
    }
    SwipeToDismissBox(state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true, // only this direction
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                label = ""
            )

            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.LightGray
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> Color.LightGray
                }, label = ""
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color), contentAlignment = alignment
            ) {
                Image(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = "Delete the deck",
                    modifier = Modifier
                        .padding(8.dp)
                        .scale(scale),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
        }) {
        OutlinedCard(shape = RectangleShape) {
            CardItem(card, onItemClick)
        }
    }
}

@Composable
fun CardItem(
    card: Card, onItemClick: (Card) -> Unit, modifier: Modifier = Modifier
) {
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
            Text(card.date.substring(0..9))
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckList(
    cards: List<Card>, decks: List<Deck>, navController: NavController, viewModel: CardViewModel
) {
    val onItemClick = { deck: Deck ->
        navController.navigate(NavRoutes.DeckEditor.route + "/${deck.deckId}")
    }
    LazyColumn {
        item {
            Text(
                stringResource(id = R.string.list_of_decks),
                Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall
            )
        }
        items(decks) { deck ->
            SwipeToDeleteDeck(viewModel, deck, cards, onItemClick, navController)
        }

    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SwipeToDeleteDeck(
    viewModel: CardViewModel,
    deck: Deck,
    cards: List<Card>,
    onItemClick: (Deck) -> Unit,
    navController: NavController
) {
    var removed by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current
    val deletedString = stringResource(id = R.string.deleted)
    val dismissState = rememberSwipeToDismissBoxState(confirmValueChange = {
        when (it) {
            SwipeToDismissBoxValue.EndToStart -> {
                Toast.makeText(context, deletedString, Toast.LENGTH_SHORT).show()
                removed = true
            }

            else -> Unit
        }
        false
    })
    LaunchedEffect(removed) {
        if (removed) {
            delay(100L) // delay in milliseconds
            viewModel.deleteDeckById(deck.deckId)
            // IMPORTANT
            // Solves the problem of deleting, then syncing and the thing getting deleted again
            navController.navigate(NavRoutes.Decks.route)
        }
    }
    SwipeToDismissBox(state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true, // only this direction
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val scale by animateFloatAsState(
                if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
                label = ""
            )

            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                else -> Alignment.Center
            }
            val color by animateColorAsState(
                when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.Settled -> Color.LightGray
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    else -> Color.LightGray
                }, label = ""
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(color), contentAlignment = alignment
            ) {
                Image(
                    painter = painterResource(R.drawable.baseline_delete_24),
                    contentDescription = "Delete the deck",
                    modifier = Modifier
                        .padding(8.dp)
                        .scale(scale),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
            }
        }) {
        OutlinedCard(shape = RectangleShape) {
            DeckItem(deck, cards, onItemClick, navController)
        }
    }
}


@Composable
fun DeckItem(
    deck: Deck,
    cards: List<Card>,
    onItemClick: (Deck) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(all = 5.dp)
            .clickable { onItemClick(deck) },
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
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
            Image(painter = painterResource(R.drawable.baseline_library_books_24),
                contentDescription = "Access this Deck's Cards",
                modifier = Modifier
                    .clickable { navController.navigate(NavRoutes.Cards.route + "/${deck.deckId}") }
                    .padding(8.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface))
        }
    }
}

@Composable
fun CardEditor(
    viewModel: CardViewModel, cardId: String, navController: NavController, deckId: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Top
    ) {
        if (cardId == "adding_card") {
            InnerCardEditor(
                navController = navController,
                viewModel = viewModel,
                card = Card("", "", id = "adding_card", deckId = deckId, userId = viewModel.userId)
            )
        } else {
            val card by viewModel.getCard(cardId).observeAsState(null)
            card?.let {
                InnerCardEditor(
                    navController = navController, viewModel = viewModel, card = it
                )
            }
        }
    }
}

@Composable
fun InnerCardEditor(navController: NavController, viewModel: CardViewModel, card: Card) {
    var question by remember { mutableStateOf(card.question) }
    var answer by remember { mutableStateOf(card.answer) }
    val onQuestionChanged = { value: String -> question = value }

    OutlinedTextField(value = question,
        onValueChange = onQuestionChanged,
        label = { Text(stringResource(id = R.string.card_question)) })
    val onAnswerChanged = { value: String -> answer = value }
    OutlinedTextField(value = answer,
        onValueChange = onAnswerChanged,
        label = { Text(stringResource(id = R.string.card_answer)) })

    val context = LocalContext.current
    Row {

        Button(
            onClick = {
                navController.navigate(NavRoutes.Cards.route + "/${card.deckId}") {
                    popUpTo(NavRoutes.Home.route)
                }
            },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RED)
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }
        val editedString = stringResource(id = R.string.edited_succ)
        val createdString = stringResource(id = R.string.created_succ)
        val introduceString = stringResource(id = R.string.introduce_some_values)
        Button(
            onClick = {
                if (answer.isNotEmpty() && question.isNotEmpty()) {
                    if (card.id == "adding_card") {
                        val newCard =
                            Card(question, answer, deckId = card.deckId, userId = viewModel.userId)
                        viewModel.addCard(newCard)
                        Toast.makeText(context, createdString, Toast.LENGTH_SHORT).show()

                    } else {
                        card.question = question
                        card.answer = answer
                        viewModel.updateCard(card)
                        Toast.makeText(context, editedString, Toast.LENGTH_SHORT).show()
                    }
                    navController.navigate(NavRoutes.Cards.route + "/${card.deckId}") {
                        popUpTo(NavRoutes.Home.route)
                    }
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

@Composable
fun DeckEditor(viewModel: CardViewModel, navController: NavController, deckId: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        if (deckId == "adding_deck") {
            InnerDeckEditor(
                navController = navController,
                viewModel = viewModel,
                deck = Deck("adding_deck", "", "", userId = viewModel.userId)
            )
        } else {
            val deck by viewModel.getDeckById(deckId).observeAsState(null)
            deck?.let {
                InnerDeckEditor(
                    navController = navController, viewModel = viewModel, deck = it
                )
            }
        }
    }

}

@Composable
fun InnerDeckEditor(navController: NavController, viewModel: CardViewModel, deck: Deck) {
    var name by remember { mutableStateOf(deck.name) }
    var description by remember { mutableStateOf(deck.description) }

    val onNameChanged = { value: String -> name = value }
    val onDescriptionChanged = { value: String -> description = value }

    val context = LocalContext.current

    OutlinedTextField(value = name,
        onValueChange = onNameChanged,
        label = { Text(text = stringResource(id = R.string.deck_name)) })

    OutlinedTextField(value = description,
        onValueChange = onDescriptionChanged,
        label = { Text(stringResource(id = R.string.deck_description)) })

    Row {
        Button(
            onClick = {
                navController.navigate(NavRoutes.Decks.route) {
                    popUpTo(NavRoutes.Home.route)
                }
            },
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RED)
        ) {
            Text(text = stringResource(id = R.string.cancel))
        }
        val editedString = stringResource(id = R.string.edited_succ)
        val introduceString = stringResource(id = R.string.introduce_some_values)
        val createdString = stringResource(id = R.string.created_succ)

        Button(
            onClick = {
                if (name.isNotEmpty() && description.isNotEmpty()) {
                    if (deck.deckId == "adding_deck") {
                        val newDeck =
                            Deck(name = name, description = description, userId = viewModel.userId)
                        viewModel.addDeck(newDeck)
                        Toast.makeText(context, createdString, Toast.LENGTH_SHORT).show()

                    } else {
                        deck.name = name
                        deck.description = description
                        viewModel.updateDeck(deck)
                        Toast.makeText(context, editedString, Toast.LENGTH_SHORT).show()

                    }
                    navController.navigate(NavRoutes.Decks.route) {
                        popUpTo(NavRoutes.Home.route)
                    }
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

@Composable
fun EmailPassword(
    navController: NavController, viewModel: CardViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val onEmailChanged = { value: String -> email = value }
    val onPasswordChanged = { value: String -> password = value }
    val baseContext = LocalContext.current

    val failedAuth = stringResource(id = R.string.failed_auth)
    val onSignedIn: () -> Unit = {
        viewModel.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(baseContext as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success,
                    // Update UI with the signed-in user's
                    // information
                    // val user = viewModel.auth.currentUser
                    // Navigate to deck list
                    viewModel.userId = Firebase.auth.currentUser?.uid ?: "unknown user"
                    navController.navigate(NavRoutes.Decks.route)
                } else {
                    // If sign in fails,
                    // display a message to the user
                    Toast.makeText(
                        baseContext,
                        failedAuth,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    val onSignedUp: () -> Unit = {
        viewModel.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(baseContext as Activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success,
                    // update UI with the signed-in
                    // user's information
                    viewModel.userId = Firebase.auth.currentUser?.uid ?: "unknown user"
                    // Navigate to list of decks
                    navController.navigate(NavRoutes.Decks.route)
                } else {
                    // If sign in fails,
                    // display a message to the user.
                    Toast.makeText(
                        baseContext,
                        failedAuth,
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    OutlinedTextField(value = email,
        onValueChange = onEmailChanged,
        label = { Text(text = stringResource(id = R.string.email_field)) })

    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(value = password,
        onValueChange = onPasswordChanged,
        label = { Text(stringResource(id = R.string.password_field)) },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        })

    Row {
        Button(
            onClick = {
                onSignedUp()
            }, modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.sign_up))
        }

        Button(
            onClick = {
                onSignedIn()
            }, modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(id = R.string.log_in))
        }
    }

}



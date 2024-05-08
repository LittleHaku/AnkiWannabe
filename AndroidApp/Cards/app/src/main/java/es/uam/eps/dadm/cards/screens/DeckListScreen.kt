package es.uam.eps.dadm.cards.screens


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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.Deck
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R
import kotlinx.coroutines.delay


@Composable
fun DeckListScreen(
    viewModel: CardViewModel, navController: NavController
) {
    //val cards by viewModel.cards.observeAsState(emptyList())
    //val decks by viewModel.decks.observeAsState(emptyList())
    val userId = viewModel.userId
    val context = LocalContext.current
    val cards by viewModel.getCardsFromUser(userId).observeAsState(
        initial = emptyList()
    )
    val decks by viewModel.getDecksFromUser(userId).observeAsState(
        initial = emptyList()
    )

    //Toast.makeText(context, decks.size.toString(), Toast.LENGTH_SHORT).show()

    DeckList(cards = cards, decks = decks, navController = navController, viewModel)
}


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
                text = stringResource(id = R.string.list_of_decks),
                textAlign = TextAlign.Center,
                modifier = Modifier.run {
                    fillMaxWidth()
                                .padding(vertical = 16.dp) // Add some vertical padding (optional)
                                .fillMaxHeight()
                }, // Restrict height to content
                style = MaterialTheme.typography.headlineSmall
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

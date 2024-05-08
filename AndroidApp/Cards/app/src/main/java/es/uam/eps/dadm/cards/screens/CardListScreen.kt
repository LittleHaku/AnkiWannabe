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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.SettingsActivity
import kotlinx.coroutines.delay

@Composable
fun CardListScreen(
    viewModel: CardViewModel, navController: NavController, deckId: String
) {
    CardList(
        viewModel = viewModel, navController, deckId = deckId
    )
}


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
            val context = LocalContext.current
            val showCards = SettingsActivity.getShowAnswers(context)
            if (showCards) {
                Text(card.answer, modifier, style = MaterialTheme.typography.bodyMedium)
            }
            else {
                Text(stringResource(id = R.string.show_answers), modifier, style = MaterialTheme.typography.bodyMedium)
                
            }
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

package es.uam.eps.dadm.cards.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.Deck
import es.uam.eps.dadm.cards.GREEN
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.RED

@Composable
fun DeckEditorScreen(
    viewModel: CardViewModel, navController: NavController, deckId: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DeckEditor(
            viewModel = viewModel, navController = navController, deckId = deckId
        )
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


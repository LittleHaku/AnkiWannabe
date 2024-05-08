package es.uam.eps.dadm.cards.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.GREEN
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.RED

@Composable
fun CardEditorScreen(
    viewModel: CardViewModel, navController: NavController, cardId: String, deckId: String
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardEditor(
            viewModel, navController = navController, cardId = cardId, deckId = deckId
        )
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
    val onAnswerChanged = { value: String -> answer = value }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = question,
                onValueChange = onQuestionChanged,
                label = { Text(stringResource(id = R.string.card_question)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            OutlinedTextField(
                value = answer,
                onValueChange = onAnswerChanged,
                label = { Text(stringResource(id = R.string.card_answer)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
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


package es.uam.eps.dadm.cards.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import es.uam.eps.dadm.cards.BLACK
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.GREEN
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.RED
import es.uam.eps.dadm.cards.Review
import es.uam.eps.dadm.cards.SettingsActivity
import es.uam.eps.dadm.cards.YELLOW
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun StudyScreen(viewModel: CardViewModel) {/*val card by viewModel.dueCard.observeAsState()
    val nCards by viewModel.nDueCards.observeAsState(initial = 0)*/
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center
    ) {
        Study(viewModel = viewModel)

    }
}


@Composable
fun Study(viewModel: CardViewModel) {
    val uid = Firebase.auth.currentUser?.uid ?: ""
    val card by viewModel.getUserDueCard(uid).observeAsState()
    val nCards by viewModel.nDueCards.observeAsState(0)
    val noMoreCards = stringResource(id = R.string.no_more_cards)
    val cardLimit = stringResource(id = R.string.card_limit)
    val context = LocalContext.current
    val maxCards = SettingsActivity.getMaximumNumberOfCards(context)
    val userReviews = viewModel.getUserReviews(viewModel.userId).observeAsState(listOf()).value
    val cardsStudiedToday = viewModel.fromReviewsToMap(userReviews)[LocalDate.now().toString()] ?: 0

    // Check the user is the real one and the limit of cards
    if (card?.userId == Firebase.auth.currentUser?.uid && (cardsStudiedToday < maxCards)) {
        card?.let { CardView(viewModel = viewModel, it, nCards, maxCards, cardsStudiedToday) }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // cardsStudiedToday > maxCards is worse because maybe there are no more cards left
            if (nCards == 0) {
                Text(
                    text = noMoreCards,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {

                Text(
                    text = cardLimit,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )

            }
        }
    }

}

@Composable
fun CardView(
    viewModel: CardViewModel,
    card: Card,
    nCards: Int,
    maxCards: Int,
    cardsStudiedToday: Int?
) {


    var answered by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val noMoreCardsString = stringResource(id = R.string.no_more_cards)
    val maxCardsString = stringResource(id = R.string.max_number_cards_short)
    val cardsStudiedTodayString = stringResource(id = R.string.cards_studied_today)
    val remainingCardsString = stringResource(id = R.string.remaining_cards)

    val onAnswered = { value: Boolean ->
        answered = value
        if (nCards == 0) {
            Toast.makeText(
                context, noMoreCardsString, Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxHeight()
    ) {
        //Text(text = stringResource(id = R.string.card_from_deck) +": " + )
        Spacer(modifier = Modifier.weight(1f)) // Pushes the Box to the center

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            CardData(card, answered, onAnswered, viewModel)
        }

        Spacer(modifier = Modifier.weight(1f)) // Pushes the Text to the bottom

        Text(
            text = "$remainingCardsString: $nCards\n" +
                    "$maxCardsString: $maxCards\n" +
                    "$cardsStudiedTodayString: $cardsStudiedToday",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 50.dp), // Add padding to the bottom
            style = MaterialTheme.typography.bodyLarge
        )
    }




}

@Composable
fun CardData(
    card: Card, answered: Boolean, onAnswered: (Boolean) -> Unit, viewModel: CardViewModel
) {
    val onDifficultyChecked = { value: Int ->
        card.quality = value
        card.update(LocalDateTime.now())
        viewModel.updateCard(card)
        val review = Review(
            reviewDate = LocalDateTime.now().toString(),
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

        Text(stringResource(id = R.string.card_question) + ":")
        Text(
            card.question,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (answered) {
            Text(stringResource(id = R.string.card_answer) + ":")
            Text(
                card.answer,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                stringResource(id = R.string.select_difficulty) + ":",
                modifier = Modifier.padding(8.dp)
            )

            val intervals = card.possibleNextPractice()
            DifficultyButtons(onAnswered, onDifficultyChecked, intervals)
        } else {
            ViewAnswerButton(onAnswered)
        }
    }
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

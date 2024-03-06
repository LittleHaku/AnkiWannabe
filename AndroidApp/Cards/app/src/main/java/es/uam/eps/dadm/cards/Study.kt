package es.uam.eps.dadm.cards

import Card
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDateTime
import java.time.LocalDateTime.now


@Composable
fun CardView(cards: List<Card>) {

    var answered by remember { mutableStateOf(false) }

    val card = cards.filter {
        LocalDateTime.parse(it.nextPracticeDate) <= now()
    }.let{
        if (it.any()) it.random() else null
    }

    val onAnswered = { value: Boolean ->
        answered = value
    }

    card?.let {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {

            CardData(card, answered, onAnswered)
        }
    }
}

@Composable
fun CardData(card: Card, answered: Boolean, onAnswered: (Boolean) -> Unit) {
    val onDifficultyChecked = { value: Int ->
        card.quality = value
        card.update(now())
    }
    Column {
        Row {
            Text(card.question)
        }

        Column {
            if (answered) {
                Text(card.answer)
                DifficultyButtons(onAnswered, onDifficultyChecked)

            } else {
                ViewAnswerButton(onAnswered)
            }
        }
    }

}

@Composable
fun ViewAnswerButton(onAnswered: (Boolean) -> Unit) {
    Button(onClick = { onAnswered(true) }) {
        Text(text = "View Answer")

    }
}

@Composable
fun DifficultyButtons(
    onAnswered: (Boolean) -> Unit,
    onDifficultyChecked: (Int) -> Unit
) {
    Row {
        Button(onClick = {
            onDifficultyChecked(0)
            onAnswered(false)
        }) {
            Text("Easy")
        }
        Button(onClick = {
            onDifficultyChecked(3)
            onAnswered(false)
        }) {
            Text("Doubt")
        }
        Button(onClick = {
            onDifficultyChecked(5)
            onAnswered(false)
        }) {
            Text("Hard")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun Screen() {
    Column {
        CardView(listOf(Card("To wake up", "Despertarse")))
        //CardItem(Card("To slow down", "Ralentizar"))
    }

}
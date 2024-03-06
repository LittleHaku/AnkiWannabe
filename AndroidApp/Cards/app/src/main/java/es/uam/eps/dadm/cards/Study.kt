package es.uam.eps.dadm.cards

import Card
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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

@Preview(showBackground = true)
@Composable
fun Screen() {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        CardView(
            listOf(
                Card("To wake up", "Despertarse"),
                Card("To slow down", "Ralentizar")
            )
        )
    }
}

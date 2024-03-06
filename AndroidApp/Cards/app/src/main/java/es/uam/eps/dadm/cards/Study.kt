package es.uam.eps.dadm.cards

import Card
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
fun CardItem(
    card: Card,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        var switchState: Boolean by remember { mutableStateOf(false) }
        val onSwitchState = { value: Boolean ->
            switchState = value
        }
        Column {
            SwitchIcon(switchState, onSwitchState)

        }

        Column {
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

        Column() {
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

@Preview(showBackground = true)
@Composable
fun Screen() {
    CardsTheme {
        /*val cards = mutableListOf<Card>()
        cards += Card("To wake up", "Despertarse")
        cards += Card("To slow down", "Ralentizar")
        cards += Card("To give up", "Rendirse")
        cards += Card("To come up", "Acercarse")
        CardList(cards)*/

        CardItem(card = Card("To slow down", "Ralentizar"))
    }
}
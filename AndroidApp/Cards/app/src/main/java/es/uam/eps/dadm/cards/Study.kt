package es.uam.eps.dadm.cards

import Card
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import java.time.LocalDateTime


@Composable
fun CardItem(
    card: Card,
    switchState: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    modifier: Modifier
) {

    var switchState by remember { mutableStateOf(false) }
    val onSwitchChange = { it: Boolean -> switchState = it }


    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {

        CardData(card = card, switchState, onSwitchChange)
    }
}

@Composable
fun CardView(cards: List<Card>) {

    var answered by remember { mutableStateOf(false) }

    val card = cards.filter {
        LocalDateTime.parse(it.nextPracticeDate) <= LocalDateTime.now()
    }.random()

    val onAnswered = { value: Boolean ->
        answered = value
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {

        CardData(card, answered, onAnswered)
    }

}

@Composable
fun CardData(card: Card, answered: Boolean, onAnswered: (Boolean) -> Unit) {

    Column {
        Row {
            Text(card.question)
        }

        Row {
            if (answered) {
                Text(card.answer)

            } else {
                ViewAnswerButton(answered, onAnswered)
            }
        }
    }

}

@Composable
fun ViewAnswerButton(answered: Boolean, onValueChange: (Boolean) -> Unit) {
    Button(onClick = { onValueChange(!answered) }) {
        Text(text = "View Answer")

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

@Composable
fun SwitchICon(switchState: Boolean, onSwitchChange: (Boolean) -> Unit) {
    val drawableResource = if (switchState) R.drawable.rounded_arrow_drop_up_24
    else R.drawable.rounded_arrow_right_24

    Icon(
        painter = painterResource(id = drawableResource),
        contentDescription = "contentDescription",
        modifier = Modifier
            .clickable { onSwitchChange(!switchState) }
    )
}

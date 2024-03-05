package es.uam.eps.dadm.cards

import Card
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CardItem(Card("To wake up", "Despertarse"))
                    //CardItem(Card("To slow down", "Ralentizar"))
                }
            }
        }
    }
}

@Composable
fun CardItem(
    card: Card,
    modifier: Modifier = Modifier
) {
    var switchState by remember { mutableStateOf(false) }
    val onSwitchChange = { it: Boolean -> switchState = it }


    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {

        CardData(card = card, switchState, onSwitchChange, modifier)
    }
}

@Composable
fun CardData(card: Card, switchState: Boolean, onSwitchChange: (Boolean) -> Unit, modifier: Modifier) {

    Column {
        SwitchICon(switchState = switchState, onSwitchChange = onSwitchChange)
    }

    Column() {
        Text(card.question, modifier)
        Text(card.answer, modifier)
        if (switchState) {
            Text("  Quality = " + card.quality.toString())
            Text("  Easiness = " + card.easiness.toString())
            Text("  Repetitions = " + card.repetitions.toString())
        }
    }

    Column() {
        Text(card.date.toString().substring(0..9))
    }
}

@Preview(showBackground = true)
@Composable
fun Screen() {
    CardItem(Card("To wake up", "Despertarse")
    )
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

package es.uam.eps.dadm.cards.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.CardItem
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R

@Composable
fun Home(navController: NavController) {
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = {
                navController.navigate(NavRoutes.Cards.route)
            }
        ) {
            Text(stringResource(id = R.string.cards))
        }
    }
}
package es.uam.eps.dadm.cards.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import es.uam.eps.dadm.cards.Card
import es.uam.eps.dadm.cards.CardList
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.Deck
import es.uam.eps.dadm.cards.DeckItem
import es.uam.eps.dadm.cards.DeckList
import es.uam.eps.dadm.cards.NavBarItems
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckScaffold(navController: NavHostController, viewModel: CardViewModel) {
    Scaffold(
        content = { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                DeckListScreen(viewModel = viewModel, navController = navController)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.list_of_decks),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ), actions = {
                Image(
                    painter = painterResource(R.drawable.baseline_cloud_upload_24),
                    contentDescription = "Que no me des errores pesao",
                    modifier = Modifier
                        .clickable {}
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(Color.White)
                )
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(NavRoutes.DeckEditor.route)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add card"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = { CardBottomNavigationBar(navController) }
    )
}

@Composable
fun DeckListScreen(
    viewModel: CardViewModel,
    navController: NavController
) {
    val cards by viewModel.cards.observeAsState(emptyList())
    val decks by viewModel.decks.observeAsState(emptyList())
    DeckList(cards = cards, decks = decks)
}
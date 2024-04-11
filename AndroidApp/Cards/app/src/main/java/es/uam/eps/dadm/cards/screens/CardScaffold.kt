package es.uam.eps.dadm.cards.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.EmailPassword
import es.uam.eps.dadm.cards.NavBarItems
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScaffold(
    navController: NavHostController,
    viewModel: CardViewModel,
    deckId: String = "",
    cardId: String = "",
    contentRoute: String
) {
    val cards by viewModel.cards.observeAsState()
    val decks by viewModel.decks.observeAsState()

    Scaffold(content = { paddingValues ->
        Column(
            Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            when (contentRoute) {
                NavRoutes.Cards.route -> CardListScreen(
                    viewModel = viewModel, navController, deckId = deckId
                )

                NavRoutes.CardEditor.route -> CardEditorScreen(
                    viewModel, navController = navController, cardId = cardId, deckId = deckId
                )


                NavRoutes.Decks.route -> DeckListScreen(
                    viewModel = viewModel, navController = navController
                )

                NavRoutes.DeckEditor.route -> DeckEditorScreen(
                    viewModel = viewModel, navController = navController, deckId = deckId
                )

                NavRoutes.Study.route -> StudyScreen(viewModel = viewModel)
            }
        }
    },
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(id = R.string.app_name),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                )
            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ), actions = {
                Image(painter = painterResource(R.drawable.baseline_cloud_upload_24),
                    contentDescription = "Upload to cloud",
                    modifier = Modifier
                        .clickable {
                            decks?.let { decks ->
                                cards?.let { cards ->
                                    viewModel.uploadToFirebase(
                                        cards, decks
                                    )
                                }
                            }
                        }
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(Color.White))
                Image(painter = painterResource(R.drawable.baseline_cloud_download_24),
                    contentDescription = "Download from cloud",
                    modifier = Modifier
                        .clickable { viewModel.downloadFromFirebase() }
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(Color.White))
                Image(painter = painterResource(R.drawable.baseline_logout_24),
                    contentDescription = "Log Out",
                    modifier = Modifier
                        .clickable {
                            viewModel.auth.signOut()
                            navController.navigate(NavRoutes.Login.route)

                        }
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(Color.White))
            })
        },
        floatingActionButton = {
            when (contentRoute) {
                NavRoutes.Cards.route -> {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(NavRoutes.CardEditor.route + "/adding_card" + "/$deckId")
                        }, containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add, contentDescription = "Add card"
                        )
                    }
                }

                NavRoutes.Decks.route -> {
                    FloatingActionButton(
                        onClick = {
                            navController.navigate(NavRoutes.DeckEditor.route + "/adding_deck")
                        }, containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add, contentDescription = "Add Deck"
                        )
                    }
                }

            }
        },

        floatingActionButtonPosition = FabPosition.End,
        bottomBar = { CardBottomNavigationBar(navController) })
}

@Composable
fun CardBottomNavigationBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(selected = currentRoute == navItem.route, onClick = {
                navController.navigate(navItem.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }, icon = {
                Icon(
                    imageVector = navItem.image, contentDescription = navItem.title
                )
            }, label = { Text(text = navItem.title) })
        }
    }
}
package es.uam.eps.dadm.cards.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import es.uam.eps.dadm.cards.CardViewModel
import es.uam.eps.dadm.cards.NavBarItems
import es.uam.eps.dadm.cards.NavRoutes
import es.uam.eps.dadm.cards.R
import es.uam.eps.dadm.cards.SettingsActivity

//import kotlin.coroutines.jvm.internal.CompletedContinuation.context

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
    val reviews by viewModel.reviews.observeAsState()
    val context = LocalContext.current

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

                NavRoutes.Statistics.route -> StatisticsScreen(viewModel = viewModel)
            }
        }
    },
        topBar = {
            CenterAlignedTopAppBar(title = {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                    Firebase.auth.currentUser?.email?.let {
                        Text(
                            it,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                }

            }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary
            ), actions = {
                Image(painter = painterResource(R.drawable.baseline_cloud_upload_24),
                    contentDescription = "Upload to cloud",
                    modifier = Modifier
                        .clickable {
                            decks?.let { decks ->
                                cards?.let { cards ->
                                    reviews?.let { reviews ->
                                        viewModel.uploadToFirebase(
                                            cards, decks, reviews
                                        )
                                    }
                                }
                            }
                        }
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary))
                Image(painter = painterResource(R.drawable.baseline_cloud_download_24),
                    contentDescription = "Download from cloud",
                    modifier = Modifier
                        .clickable { viewModel.downloadFromFirebase() }
                        .padding(8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary))
            }, navigationIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.baseline_logout_24),
                        contentDescription = "Log Out",
                        modifier = Modifier
                            .clickable {
                                Firebase.auth.signOut()
                                viewModel.userId = "unknown user"
                                SettingsActivity.setLoggedIn(context, false)
                                navController.navigate(NavRoutes.Login.route)

                            }
                            .padding(8.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary))
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        contentDescription = "Settings",

                        modifier = Modifier
                            .clickable {
                                // Start Preference activity here
                                context.startActivity(
                                    Intent(
                                        context,
                                        SettingsActivity::class.java
                                    )
                                )
                            }
                            .padding(8.dp)
                    )
                }
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
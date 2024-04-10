package es.uam.eps.dadm.cards

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import es.uam.eps.dadm.cards.screens.CardScaffold
import es.uam.eps.dadm.cards.screens.Home
import es.uam.eps.dadm.cards.ui.theme.CardsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CardsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: CardViewModel = viewModel(
                            it,
                            "CardViewModel",
                            CardViewModelFactory(LocalContext.current.applicationContext as Application)
                        )


                        //NavComposable(viewModel)
                        //CardScaffold(navController, viewModel)
                        MainScreen(viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: CardViewModel) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        composable(NavRoutes.Home.route) {
            Home(navController)
        }
        composable(NavRoutes.Cards.route + "/{deckId}") { backEntry ->
            val deckId = backEntry.arguments?.getString("deckId")
            deckId?.let {
                CardScaffold(
                    navController,
                    viewModel,
                    deckId = deckId,
                    contentRoute = NavRoutes.Cards.route
                )
            }
        }
        composable(NavRoutes.Decks.route) {
            //DeckScaffold(navController, viewModel)
            CardScaffold(
                navController = navController,
                viewModel = viewModel,
                contentRoute = NavRoutes.Decks.route
            )
        }
        composable(NavRoutes.CardEditor.route + "/{cardId}" + "/{deckId}") { backEntry ->
            val cardId = backEntry.arguments?.getString("cardId")
            val deckId = backEntry.arguments?.getString("deckId")
            cardId?.let {
                deckId?.let {
                    //CardEditorScaffold(navController, viewModel, cardId = cardId, deckId = deckId)
                    CardScaffold(
                        navController = navController,
                        viewModel = viewModel,
                        contentRoute = NavRoutes.CardEditor.route,
                        deckId = deckId,
                        cardId = cardId
                    )
                }
            }
        }
        composable(NavRoutes.DeckEditor.route + "/{deckId}") { backEntry ->
            //DeckEditorScaffold(navController, viewModel)
            val deckId = backEntry.arguments?.getString("deckId")
            deckId?.let {
                CardScaffold(
                    navController = navController,
                    viewModel = viewModel,
                    contentRoute = NavRoutes.DeckEditor.route,
                    deckId = deckId
                )
            }
        }
        composable(NavRoutes.Study.route) {
            //StudyScaffold(navController, viewModel)
            CardScaffold(
                navController = navController,
                viewModel = viewModel,
                contentRoute = NavRoutes.Study.route
            )
        }
    }
}
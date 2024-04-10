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
import es.uam.eps.dadm.cards.screens.CardEditorScaffold
import es.uam.eps.dadm.cards.screens.CardScaffold
import es.uam.eps.dadm.cards.screens.DeckEditorScaffold
import es.uam.eps.dadm.cards.screens.DeckListScreen
import es.uam.eps.dadm.cards.screens.DeckScaffold
import es.uam.eps.dadm.cards.screens.Home
import es.uam.eps.dadm.cards.screens.StudyScaffold
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
        composable(NavRoutes.Cards.route) {
            CardScaffold(navController, viewModel)
        }
        composable(NavRoutes.Decks.route) {
            DeckScaffold(navController, viewModel)
            //DeckListScreen(viewModel, navController)
        }
        composable(NavRoutes.CardEditor.route + "/{cardId}") { backEntry ->
            val id = backEntry.arguments?.getString("cardId")
            id?.let {
                CardEditorScaffold(navController, viewModel, cardId = id)
            }
        }
        composable(NavRoutes.DeckEditor.route) {
            DeckEditorScaffold(navController, viewModel)
        }
        composable(NavRoutes.Study.route) {
            StudyScaffold(navController, viewModel)
        }
    }
}
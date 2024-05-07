package es.uam.eps.dadm.cards

import android.app.Application
import android.os.Bundle
import android.widget.Toast
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
import androidx.preference.PreferenceManager
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.uam.eps.dadm.cards.screens.CardScaffold
import es.uam.eps.dadm.cards.screens.EmailPasswordScaffold
import es.uam.eps.dadm.cards.screens.Home
import es.uam.eps.dadm.cards.ui.theme.CardsTheme

class MainActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val reference = database.getReference("message")
        reference.setValue("Hello from Cards")
        auth = Firebase.auth

        PreferenceManager.setDefaultValues(
            this,
            R.xml.root_preferences,
            false
        )

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Toast.makeText(
                    applicationContext, snapshot.value.toString(), Toast.LENGTH_SHORT
                ).show()
            }

            override fun onCancelled(error: DatabaseError) {}

        })


        setContent {
            CardsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: CardViewModel = viewModel(
                            it,
                            "CardViewModel",
                            CardViewModelFactory(LocalContext.current.applicationContext as Application)
                        )

                        MainScreen(viewModel = viewModel)
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
        navController = navController, startDestination = NavRoutes.Home.route
    ) {
        composable(NavRoutes.Home.route) {
            Home(navController = navController, viewModel = viewModel)
        }
        composable(NavRoutes.Cards.route + "/{deckId}") { backEntry ->
            val deckId = backEntry.arguments?.getString("deckId")
            deckId?.let {
                CardScaffold(
                    navController, viewModel, deckId = deckId, contentRoute = NavRoutes.Cards.route
                )
            }
        }
        composable(NavRoutes.Decks.route) {
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
            CardScaffold(
                navController = navController,
                viewModel = viewModel,
                contentRoute = NavRoutes.Study.route
            )
        }

        composable(NavRoutes.Statistics.route) {
            CardScaffold(navController = navController,
                viewModel = viewModel, contentRoute = NavRoutes.Statistics.route)
        }
        
        composable(NavRoutes.Login.route) {
            EmailPasswordScaffold(navController, viewModel)
        }
        
    }
}
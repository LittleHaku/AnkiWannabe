package es.uam.eps.dadm.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScaffold(viewModel: CardViewModel) {
    Scaffold(
        content = { paddingValues ->
            Column(Modifier.padding(paddingValues)) {
                CardList(viewModel = viewModel)
            }
        },
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(
                    text = "Cards",
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
                    val card = Card("did it create", "it did")
                    viewModel.addCard(card)
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
        bottomBar = { CardBottomNavigationBar() }
    )
}

@Composable
fun CardBottomNavigationBar() {
    NavigationBar {
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = true,
                onClick = { /*TODO*/ },
                icon = {
                    Icon(
                        imageVector = navItem.image,
                        contentDescription = navItem.title
                    )
                },
                label = { Text(text = navItem.title) }
            )
        }
    }
}
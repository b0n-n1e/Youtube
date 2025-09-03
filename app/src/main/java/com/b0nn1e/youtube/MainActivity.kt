package com.b0nn1e.youtube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.b0nn1e.youtube.home.HomeScreen
import com.b0nn1e.youtube.home.HomeViewModel
import com.b0nn1e.youtube.mine.MineScreen
import com.b0nn1e.youtube.search.SearchScreen
import com.b0nn1e.youtube.search.SearchViewModel
import com.b0nn1e.youtube.ui.theme.YoutubeTheme
import com.b0nn1e.youtube.ui.widget.BottomBarWidget
import com.b0nn1e.youtube.ui.widget.Screen


class MainActivity : ComponentActivity() {
    private val searchViewModel by lazy { ViewModelProvider(this)[SearchViewModel::class] }
    private val homeViewModel by lazy { ViewModelProvider(this)[HomeViewModel::class] }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CenterScreen(
                homeViewModel,
                searchViewModel
            )
        }
    }
}

@Composable
fun CenterScreen(
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Home, Screen.Search, Screen.Mine)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomBarWidget(
                navController = navController,
                items = items
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(viewModel = homeViewModel, onVideoClick = {} ) }
            composable(Screen.Search.route) { SearchScreen(viewModel = searchViewModel) }
            composable(Screen.Mine.route) { MineScreen() }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun CenterScreenPreLight() {
//    YoutubeTheme(YoutubeTheme.Theme.Light) {
//        CenterScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun CenterScreenPreDark() {
//    YoutubeTheme(YoutubeTheme.Theme.Dark) {
//        CenterScreen()
//    }
//}


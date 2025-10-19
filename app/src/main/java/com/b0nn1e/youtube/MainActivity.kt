package com.b0nn1e.youtube

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.b0nn1e.youtube.home.HomeScreen
import com.b0nn1e.youtube.home.HomeViewModel
import com.b0nn1e.youtube.mine.MineScreen
import com.b0nn1e.youtube.search.SearchScreen
import com.b0nn1e.youtube.search.SearchViewModel
import com.b0nn1e.youtube.ui.widget.BottomBarWidget
import com.b0nn1e.youtube.ui.widget.Screen
import com.b0nn1e.youtube.ui.widget.list
import com.b0nn1e.youtube.video.VideoPlayerScreen


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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun CenterScreen(
    homeViewModel: HomeViewModel,
    searchViewModel: SearchViewModel
) {
    val navController = rememberNavController()
    val items = list

    //获取当前路由状态，用于控制 TopAppBar 的内容
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "YouTube") },
                //在 actions slot 中添加菜单按钮
                actions = {
                    if (currentRoute == Screen.Home.route) HomeRefreshMenu(homeViewModel)
                    if (currentRoute == Screen.Search.route) HomeRefreshMenu(searchViewModel)

                }
            )
        },
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
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onVideoClick = {videoId ->
                        navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                    }
                ) }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = searchViewModel,
                    onItemClick = {videoId->
                        navController.navigate(Screen.VideoPlayer.createRoute(videoId))
                    }
                )
            }
            composable(Screen.Mine.route) { MineScreen() }
            composable(
                route = Screen.VideoPlayer.route,
                arguments = listOf(navArgument("videoId") { type = NavType.StringType }),
                deepLinks = listOf(
                    navDeepLink {
                        // 匹配标准长链接
                        uriPattern = "https://www.youtube.com/watch?v={videoId}"
                        action = Intent.ACTION_VIEW
                    },
                    navDeepLink {
                        uriPattern = "http://www.youtube.com/watch?v={videoId}"
                        action = Intent.ACTION_VIEW
                    })
            ) {navBackStateEntry ->
                val videoId = navBackStateEntry.arguments?.getString("videoId")?:""
                VideoPlayerScreen(videoId)
            }
        }
    }
}

/**
 * 用于 Home 界面的刷新菜单 Composable
 */
@Composable
fun HomeRefreshMenu(viewModel: ViewModel) {
    var isMenuExpanded by remember { mutableStateOf(false) }

    IconButton(onClick = { isMenuExpanded = true }) {
        Icon(Icons.Filled.MoreVert, contentDescription = "更多选项")
    }

    DropdownMenu(
        expanded = isMenuExpanded,
        onDismissRequest = { isMenuExpanded = false }
    ) {
        DropdownMenuItem(
            text = { Text("刷新") },
            leadingIcon = {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "刷新图标"
                )
            },
            onClick = {
                isMenuExpanded = false
                if(viewModel is HomeViewModel){
                    viewModel.getPopularVideos()
                }
            }
        )
        // 可以在这里添加其他菜单项，例如：设置
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


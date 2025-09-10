package com.b0nn1e.youtube.ui.widget

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Mine : Screen("mine")
    object VideoPlayer : Screen("video_player/{videoId}") {
        fun createRoute(videoId: String) = "video_player/$videoId"
    }
}

val list = listOf<Screen>(Screen.Home, Screen.Search, Screen.Mine)
package com.b0nn1e.youtube.video

import androidx.lifecycle.ViewModel
import com.b0nn1e.youtube.player.YouTubePlayer
import com.b0nn1e.youtube.player.listeners.AbstractYouTubePlayerListener
import com.b0nn1e.youtube.player.views.YouTubePlayerView


class VideoPlayerViewModel: ViewModel() {

    val model by lazy { VideoPlayerModel() }

    //由viewModel来控制播放器的行为
    private var youTubePlayer : YouTubePlayer? = null


    val playerListener = object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            this@VideoPlayerViewModel.youTubePlayer = youTubePlayer
        }
    }

    fun initPlayer(
        youTubePlayerView: YouTubePlayerView,
        videoId : String
    ){
        youTubePlayerView.enableAutomaticInitialization = false

    }
}
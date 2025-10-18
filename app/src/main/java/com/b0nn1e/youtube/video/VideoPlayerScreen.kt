package com.b0nn1e.youtube.video


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.b0nn1e.youtube.player.PlayerConstants.PlayerError
import com.b0nn1e.youtube.player.YouTubePlayer
import com.b0nn1e.youtube.player.listeners.AbstractYouTubePlayerListener
import com.b0nn1e.youtube.player.views.YouTubePlayerView

@Composable
fun VideoPlayerScreen(
    videoId : String
) {

    var youTubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    Column {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp), // 设置固定高度
            factory = { context ->
                YouTubePlayerView(context).apply {
                    (context as? LifecycleOwner)?.lifecycle?.addObserver(this)
                    //取消自动初始化，以供动态加载指定的videoId
                    enableAutomaticInitialization = false
                    initialize(object : AbstractYouTubePlayerListener() {
                        override fun onReady(player: YouTubePlayer) {
                            youTubePlayer = player
                            player.loadVideo(videoId, 0f)
                        }

                        override fun onError(player: YouTubePlayer, error: PlayerError) {
                            Log.e("YouTubePlayer", "Error: $error")
                        }
                    })
                }
            },
            update = { youTubePlayerView ->

            }
        )
    }
}

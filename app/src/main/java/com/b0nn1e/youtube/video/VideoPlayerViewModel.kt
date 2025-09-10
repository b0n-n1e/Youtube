package com.b0nn1e.youtube.video

import androidx.lifecycle.ViewModel

class VideoPlayerViewModel: ViewModel() {

    val model by lazy { VideoPlayerModel() }
}
package com.b0nn1e.youtube.player.listeners

import android.view.View

/**
 * 用于监听 YouTube 播放器全屏事件的接口。
 * 实现此接口以处理播放器进入和退出全屏模式的事件。
 */
interface FullscreenListener {
    /**
     * 通知宿主应用播放器已进入全屏模式（用户点击了播放器 UI 的全屏按钮）。
     * 调用此方法后，视频将不再在 [YouTubePlayerView] 中渲染，而是切换到 [fullscreenView]。
     * 宿主应用需将此 View 添加到全屏容器中以实现视频全屏显示。
     *
     * 应用可通过调用 [exitFullscreen] 主动退出全屏模式（例如用户按下返回键）。
     * 播放器自身也提供退出全屏的 UI。无论通过何种方式退出全屏，都会调用 [onExitFullscreen]，
     * 通知宿主应用移除自定义 View。
     *
     * @param fullscreenView 用于渲染全屏视频的视图
     * @param exitFullscreen 主动退出全屏模式的回调函数
     */
    fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit)

    /**
     * 通知宿主应用播放器已退出全屏模式。
     * 宿主应用需隐藏之前传入 [onEnterFullscreen] 的自定义 View，
     * 视频将重新在 [YouTubePlayerView] 中渲染。
     */
    fun onExitFullscreen()
}
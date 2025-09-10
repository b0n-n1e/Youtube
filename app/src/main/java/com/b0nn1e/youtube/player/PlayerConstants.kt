package com.b0nn1e.youtube.player

/**
 * 定义 YouTube 播放器相关的枚举常量，与 IFrame Player API 的状态、质量、错误和速率对应。
 */
class PlayerConstants {

    /**
     * 播放器状态枚举，表示视频播放的不同状态。
     */
    enum class PlayerState {
        UNKNOWN,        // 未知状态
        UNSTARTED,      // 未开始
        ENDED,          // 已结束
        PLAYING,        // 播放中
        PAUSED,         // 已暂停
        BUFFERING,      // 缓冲中
        VIDEO_CUED      // 视频已准备
    }

    /**
     * 播放质量枚举，表示视频的清晰度选项。
     */
    enum class PlaybackQuality {
        UNKNOWN,        // 未知质量
        SMALL,          // 小分辨率
        MEDIUM,         // 中等分辨率
        LARGE,          // 大分辨率
        HD720,          // 720p 高清
        HD1080,         // 1080p 高清
        HIGH_RES,       // 高分辨率
        DEFAULT         // 默认质量
    }

    /**
     * 播放器错误枚举，表示播放器可能遇到的错误类型。
     */
    enum class PlayerError {
        UNKNOWN,                        // 未知错误
        INVALID_PARAMETER_IN_REQUEST,   // 请求参数无效
        HTML_5_PLAYER,                  // HTML5 播放器错误
        VIDEO_NOT_FOUND,                // 视频未找到
        VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER, // 视频无法在嵌入播放器中播放
        REQUEST_MISSING_HTTP_REFERER    // 请求缺少 HTTP Referer
    }

    /**
     * 播放速率枚举，表示视频的播放速度选项。
     */
    enum class PlaybackRate {
        UNKNOWN,        // 未知速率
        RATE_0_25,      // 0.25 倍速
        RATE_0_5,       // 0.5 倍速
        RATE_0_75,      // 0.75 倍速
        RATE_1,         // 1 倍速（正常）
        RATE_1_25,      // 1.25 倍速
        RATE_1_5,       // 1.5 倍速
        RATE_1_75,      // 1.75 倍速
        RATE_2          // 2 倍速
    }
}

/**
 * 将播放速率转换为浮点数值，用于 IFrame Player API。
 * @return 对应的浮点速率，UNKNOWN 默认返回 1f（正常速率）
 */
fun PlayerConstants.PlaybackRate.toFloat(): Float {
    return when (this) {
        PlayerConstants.PlaybackRate.UNKNOWN -> 1f
        PlayerConstants.PlaybackRate.RATE_0_25 -> 0.25f
        PlayerConstants.PlaybackRate.RATE_0_5 -> 0.5f
        PlayerConstants.PlaybackRate.RATE_0_75 -> 0.75f
        PlayerConstants.PlaybackRate.RATE_1 -> 1f
        PlayerConstants.PlaybackRate.RATE_1_25 -> 1.25f
        PlayerConstants.PlaybackRate.RATE_1_5 -> 1.5f
        PlayerConstants.PlaybackRate.RATE_1_75 -> 1.75f
        PlayerConstants.PlaybackRate.RATE_2 -> 2f
    }
}
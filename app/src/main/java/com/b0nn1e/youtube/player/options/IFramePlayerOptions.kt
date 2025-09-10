package com.b0nn1e.youtube.player.options

import org.json.JSONException
import org.json.JSONObject

/**
 * 用于配置 IFrame 播放器的选项。所有可用参数见：
 * [IFrame 播放器参数](https://developers.google.com/youtube/player_parameters#Parameters)
 * 使用 Builder 模式创建配置，确保参数正确设置。
 */
class IFramePlayerOptions private constructor(private val playerOptions: JSONObject) {
    companion object {
        /**
         * 默认配置，启用播放器控制条（controls=1）。
         */
        val default = Builder().controls(1).build()
    }

    override fun toString(): String {
        return playerOptions.toString()
    }

    /**
     * 获取播放器的来源域名（origin）。
     * @return 配置中的 origin 参数值
     */
    internal fun getOrigin(): String {
        return playerOptions.getString(Builder.ORIGIN)
    }

    class Builder {
        companion object {
            private const val AUTO_PLAY = "autoplay"
            private const val MUTE = "mute"
            private const val CONTROLS = "controls"
            private const val ENABLE_JS_API = "enablejsapi"
            private const val FS = "fs"
            internal const val ORIGIN = "origin"
            private const val REL = "rel"
            private const val IV_LOAD_POLICY = "iv_load_policy"
            private const val CC_LOAD_POLICY = "cc_load_policy"
            private const val CC_LANG_PREF = "cc_lang_pref"
            private const val LIST = "list"
            private const val LIST_TYPE = "listType"
            private const val START = "start"
            private const val END = "end"
        }

        private val builderOptions = JSONObject()

        init {
            /**
             * 初始化默认参数：
             * - 禁用自动播放、静音、控制条、全屏、相关视频、字幕
             * - 启用 JS API
             * - 设置默认域名和视频注解策略
             */
            addInt(AUTO_PLAY, 0)
            addInt(MUTE, 0)
            addInt(CONTROLS, 0)
            addInt(ENABLE_JS_API, 1)
            addInt(FS, 0)
            addString(ORIGIN, "https://www.youtube.com")
            addInt(REL, 0)
            addInt(IV_LOAD_POLICY, 3)
            addInt(CC_LOAD_POLICY, 0)
        }

        /**
         * 构建 IFramePlayerOptions 实例。
         * @return 配置好的播放器选项
         */
        fun build(): IFramePlayerOptions {
            return IFramePlayerOptions(builderOptions)
        }

        /**
         * 控制是否使用 IFrame 播放器的网页界面。
         * @param controls 0：不使用网页界面；1：使用网页界面
         */
        fun controls(controls: Int): Builder {
            addInt(CONTROLS, controls)
            return this
        }

        /**
         * 控制播放器初始化后是否自动播放视频。
         * @param autoplay 0：不自动播放；1：自动播放
         */
        fun autoplay(autoplay: Int): Builder {
            addInt(AUTO_PLAY, autoplay)
            return this
        }

        /**
         * 控制播放器初始化时是否静音。
         * @param mute 0：不静音，获取音频焦点；1：静音，不获取音频焦点
         */
        fun mute(mute: Int): Builder {
            addInt(MUTE, mute)
            return this
        }

        /**
         * 控制视频结束时显示的相关视频。
         * @param rel 0：显示同一频道的相关视频；1：显示多个频道的相关视频
         */
        fun rel(rel: Int): Builder {
            addInt(REL, rel)
            return this
        }

        /**
         * 控制视频注解显示。
         * @param ivLoadPolicy 1：显示视频注解；3：不显示视频注解
         */
        fun ivLoadPolicy(ivLoadPolicy: Int): Builder {
            addInt(IV_LOAD_POLICY, ivLoadPolicy)
            return this
        }

        /**
         * 指定播放器显示字幕的默认语言。
         * 若同时设置 ccLoadPolicy 为 1，播放器加载时将显示指定语言的字幕。
         * 若未设置 ccLoadPolicy，用户需手动启用字幕。
         * @param languageCode ISO 639-1 两字母语言代码（如 "zh" 表示中文）
         */
        fun langPref(languageCode: String): Builder {
            addString(CC_LANG_PREF, languageCode)
            return this
        }

        /**
         * 控制视频字幕显示（不适用于自动生成字幕）。
         * @param ccLoadPolicy 0：不显示字幕；1：显示字幕
         */
        fun ccLoadPolicy(ccLoadPolicy: Int): Builder {
            addInt(CC_LOAD_POLICY, ccLoadPolicy)
            return this
        }

        /**
         * 指定播放器运行的域名。
         * 默认值为 "https://www.youtube.com"，建议保留以确保某些功能可用。
         * @param origin 运行播放器的域名
         */
        fun origin(origin: String): Builder {
            addString(ORIGIN, origin)
            return this
        }

        /**
         * 指定播放的播放列表 ID，需配合 listType 参数使用。
         * 若 listType 为 "playlist"，则需提供播放列表 ID（需以 "PL" 开头，如 PL1234）。
         * @param list 播放列表 ID
         */
        fun list(list: String): Builder {
            addString(LIST, list)
            return this
        }

        /**
         * 控制播放器播放视频 ID 或播放列表 ID。
         * 若设置为 "playlist"，需通过 list 参数设置播放列表 ID。
         * 详见文档：https://developers.google.com/youtube/player_parameters#Selecting_Content_to_Play
         * @param listType 设置为 "playlist" 以播放播放列表
         */
        fun listType(listType: String): Builder {
            addString(LIST_TYPE, listType)
            return this
        }

        /**
         * 控制全屏按钮是否显示。
         * 详见文档：https://developers.google.com/youtube/player_parameters#Parameters
         * @param fs 0：不显示全屏按钮；1：显示全屏按钮
         */
        fun fullscreen(fs: Int): Builder {
            addInt(FS, fs)
            return this
        }

        /**
         * 指定视频播放的起始秒数。
         * @param startSeconds 正整数，视频起始播放的秒数
         */
        fun start(startSeconds: Int): Builder {
            addInt(START, startSeconds)
            return this
        }

        /**
         * 指定视频播放的结束秒数。
         * @param endSeconds 正整数，视频停止播放的秒数
         */
        fun end(endSeconds: Int): Builder {
            addInt(END, endSeconds)
            return this
        }

        /**
         * modestbranding 参数已废弃，无效。
         * 播放器现根据播放器大小、其他 API 参数（如 controls）等因素自动确定品牌展示。
         * 详见 2023 年 8 月 15 日废弃公告：https://developers.google.com/youtube/player_parameters#release_notes_08_15_2023
         */
        @Deprecated("已废弃，无效")
        fun modestBranding(modestBranding: Int): Builder {
            return this
        }

        /**
         * 向配置中添加字符串类型的键值对。
         * @param key 参数键
         * @param value 参数值
         * @throws RuntimeException 如果 JSON 操作失败
         */
        private fun addString(key: String, value: String) {
            try {
                builderOptions.put(key, value)
            } catch (e: JSONException) {
                throw RuntimeException("非法 JSON 值 $key: $value")
            }
        }

        /**
         * 向配置中添加整数类型的键值对。
         * @param key 参数键
         * @param value 参数值
         * @throws RuntimeException 如果 JSON 操作失败
         */
        private fun addInt(key: String, value: Int) {
            try {
                builderOptions.put(key, value)
            } catch (e: JSONException) {
                throw RuntimeException("非法 JSON 值 $key: $value")
            }
        }
    }
}
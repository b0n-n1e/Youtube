package com.b0nn1e.youtube.player.utils

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresPermission

/**
 * 用于观察网络状态变化的类。
 * 支持监听网络连接和断开事件，兼容现代（API 24+）和旧版（API < 24）系统。
 * 常用于确保 YouTube 播放器在网络可用时正常工作。
 */
internal class NetworkObserver(private val context: Context) {

    /**
     * 网络状态变化监听器接口。
     */
    interface Listener {
        /**
         * 当网络可用时调用。
         */
        fun onNetworkAvailable()

        /**
         * 当网络不可用时调用。
         */
        fun onNetworkUnavailable()
    }

    /**
     * 存储注册的监听器列表。
     */
    val listeners = mutableListOf<Listener>()

    /**
     * 旧版 API 的广播接收器，用于监听网络变化。
     */
    private var networkBroadcastReceiver: NetworkBroadcastReceiver? = null

    /**
     * 现代 API 的网络回调，用于监听网络变化。
     */
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    /**
     * 开始观察网络状态变化。
     * 需要 ACCESS_NETWORK_STATE 权限。
     * 调用后会触发 [Listener.onNetworkAvailable] 或 [Listener.onNetworkUnavailable]。
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun observeNetwork() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            doObserveNetwork(context)
        } else {
            doObserveNetworkLegacy(context)
        }
    }

    /**
     * 停止观察网络状态并清理资源。
     * 包括注销网络回调和广播接收器，清除监听器列表。
     */
    fun destroy() {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback?.let {
            connectivityManager.unregisterNetworkCallback(it)
            networkCallback = null
        }
        networkBroadcastReceiver?.let {
            context.unregisterReceiver(it)
            networkBroadcastReceiver = null
        }
        listeners.clear()
    }

    /**
     * 使用现代 API（API 24+）观察网络状态变化。
     * 回调在非主线程，通过 Handler 切换到主线程通知监听器。
     */
    private fun doObserveNetwork(context: Context) {
        val callback = object : ConnectivityManager.NetworkCallback() {
            private val mainThreadHandler = Handler(Looper.getMainLooper())
            override fun onAvailable(network: Network) {
                mainThreadHandler.post {
                    listeners.forEach { it.onNetworkAvailable() }
                }
            }

            override fun onLost(network: Network) {
                mainThreadHandler.post {
                    listeners.forEach { it.onNetworkUnavailable() }
                }
            }
        }
        networkCallback = callback
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    /**
     * 使用旧版 API（API < 24）观察网络状态变化。
     * 通过广播接收器监听网络变化事件。
     */
    private fun doObserveNetworkLegacy(context: Context) {
        networkBroadcastReceiver = NetworkBroadcastReceiver(
            onNetworkAvailable = { listeners.forEach { it.onNetworkAvailable() } },
            onNetworkUnavailable = { listeners.forEach { it.onNetworkUnavailable() } },
        )
        context.registerReceiver(networkBroadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }
}

/**
 * 用于响应网络连接变化的广播接收器。
 * 仅用于旧版 API（API < 24）。
 */
private class NetworkBroadcastReceiver(
    /**
     * 网络可用时的回调。
     */
    private val onNetworkAvailable: () -> Unit,
    /**
     * 网络不可用时的回调。
     */
    private val onNetworkUnavailable: () -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (isConnectedToInternet(context)) {
            onNetworkAvailable()
        } else {
            onNetworkUnavailable()
        }
    }
}

/**
 * 检查设备是否连接到网络。
 * 兼容现代（API 24+）和旧版（API < 24）系统。
 * @param context 上下文
 * @return true 表示网络可用，false 表示网络不可用
 */
private fun isConnectedToInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
        networkCapabilities.isConnectedToInternet()
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        networkInfo != null && networkInfo.isConnected
    }
}

/**
 * 检查网络是否为 Wi-Fi、蜂窝或以太网连接。
 * @return true 表示连接到网络，false 表示未连接
 */
private fun NetworkCapabilities.isConnectedToInternet(): Boolean {
    return (hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
}
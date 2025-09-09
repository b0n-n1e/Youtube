package com.b0nn1e.youtube.player

import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/** Returns true if the player is muted, false otherwise. */
suspend fun YouTubePlayer.isMuted(): Boolean = suspendCoroutine { continuation ->
    isMutedAsync { isMuted -> continuation.resume(isMuted) }
}
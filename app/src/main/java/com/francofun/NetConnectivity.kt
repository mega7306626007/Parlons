package com.francofun

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Thin online-state wrapper (§9.4). Exposes a one-shot [isOnline] plus a
 * [online] flow so the UI can react if connectivity changes mid-session.
 *
 * Engine choice is cached per session: a scenario started offline must not
 * jankily switch voices mid-conversation, so callers should resolve
 * [engineForSession] once (e.g. in MainActivity) and pass it down.
 */
enum class SpeechEngine { SYSTEM, OFFLINE }

class NetConnectivity(ctx: Context) {
    private val cm = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val _online = MutableStateFlow(isOnline())
    val online: StateFlow<Boolean> = _online

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) { _online.value = isOnline() }
        override fun onLost(network: Network) { _online.value = isOnline() }
        override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
            _online.value = isOnline()
        }
    }

    init {
        // Live updates so the UI can react mid-session (§9.4); engine choice
        // itself stays session-pinned and never flips mid-conversation.
        runCatching { cm.registerDefaultNetworkCallback(callback) }
    }

    /** Pinned at session start — never re-resolve mid-conversation. */
    val engineForSession: SpeechEngine by mutableStateOf(
        if (isOnline()) SpeechEngine.SYSTEM else SpeechEngine.OFFLINE
    )

    fun isOnline(): Boolean {
        val net: Network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    fun refresh() {
        _online.value = isOnline()
    }
}

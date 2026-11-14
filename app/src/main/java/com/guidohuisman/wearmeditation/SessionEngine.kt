package com.guidohuisman.wearmeditation

import android.content.Context
import android.content.Intent
import android.os.SystemClock
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** Everything the screen needs to draw a session. */
data class SessionState(
    val active: Boolean = false,
    val paused: Boolean = false,
    val totalMillis: Long = 0L,
    val remainingMillis: Long = 0L,
    val intervalMillis: Long = 0L,
    val soundEnabled: Boolean = true,
)

/**
 * The single source of truth for the running session.
 *
 * The countdown runs here, not in the screen, and [TimerService] keeps the process alive while a
 * session is active so the cues also fire with the screen off.
 */
object SessionEngine {

    private const val TICK_MILLIS = 500L

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state.asStateFlow()

    private var context: Context? = null
    private var tickJob: Job? = null
    private var deadline = 0L
    private var bellsFired = 0

    fun attach(context: Context) {
        this.context = context.applicationContext
    }

    fun start(totalMinutes: Int, intervalMinutes: Int, soundEnabled: Boolean) {
        val ctx = context ?: return
        tickJob?.cancel()
        bellsFired = 0
        val total = totalMinutes.coerceAtLeast(1) * 60_000L
        val interval = intervalMinutes.coerceAtLeast(0) * 60_000L
        deadline = SystemClock.elapsedRealtime() + total
        _state.value = SessionState(
            active = true,
            paused = false,
            totalMillis = total,
            remainingMillis = total,
            intervalMillis = interval,
            soundEnabled = soundEnabled,
        )
        Cues.startCue(ctx, soundEnabled)
        startTimerService(ctx)
        startTicking(ctx)
    }

    fun pause() {
        val current = _state.value
        if (!current.active || current.paused) return
        tickJob?.cancel()
        tickJob = null
        val remaining = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        _state.value = current.copy(paused = true, remainingMillis = remaining)
        context?.let { stopTimerService(it) }
    }

    fun resume() {
        val current = _state.value
        val ctx = context ?: return
        if (!current.active || !current.paused) return
        deadline = SystemClock.elapsedRealtime() + current.remainingMillis
        _state.value = current.copy(paused = false)
        startTimerService(ctx)
        startTicking(ctx)
    }

    /** Ends the session. Pass [playEndCue] = true when the session ran to its end. */
    fun stop(playEndCue: Boolean = false) {
        val current = _state.value
        tickJob?.cancel()
        tickJob = null
        bellsFired = 0
        _state.value = SessionState(soundEnabled = current.soundEnabled)
        context?.let { ctx ->
            if (playEndCue && current.active) Cues.endCue(ctx, current.soundEnabled)
            stopTimerService(ctx)
        }
    }

    private fun startTicking(ctx: Context) {
        tickJob?.cancel()
        tickJob = scope.launch {
            while (isActive) {
                delay(TICK_MILLIS)
                val current = _state.value
                val remaining = (deadline - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
                val elapsed = (current.totalMillis - remaining).coerceAtLeast(0L)

                if (current.intervalMillis > 0L && remaining > 0L) {
                    val nextBell = (bellsFired + 1) * current.intervalMillis
                    if (elapsed >= nextBell) {
                        bellsFired++
                        Cues.intervalCue(ctx, current.soundEnabled)
                    }
                }

                if (remaining <= 0L) {
                    tickJob = null
                    bellsFired = 0
                    _state.value = current.copy(active = false, paused = false, remainingMillis = 0L)
                    Cues.endCue(ctx, current.soundEnabled)
                    stopTimerService(ctx)
                    return@launch
                }

                _state.value = current.copy(remainingMillis = remaining)
            }
        }
    }

    private fun startTimerService(ctx: Context) {
        ContextCompat.startForegroundService(ctx, Intent(ctx, TimerService::class.java))
    }

    private fun stopTimerService(ctx: Context) {
        ctx.stopService(Intent(ctx, TimerService::class.java))
    }
}

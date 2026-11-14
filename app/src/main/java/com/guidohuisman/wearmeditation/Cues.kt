package com.guidohuisman.wearmeditation

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * The start cue, the interval bell and the end cue.
 *
 * The patterns are plain "pause, buzz, pause, buzz" millisecond arrays, so they can be made
 * user editable later without changing how they are played.
 */
object Cues {

    private val startPattern = longArrayOf(0, 250)
    private val bellPattern = longArrayOf(0, 150, 150, 150)
    private val endPattern = longArrayOf(0, 500, 200, 500, 200, 800)

    private var toneGenerator: ToneGenerator? = null

    fun startCue(context: Context, sound: Boolean) =
        playCue(context, startPattern, ToneGenerator.TONE_PROP_ACK, sound)

    fun intervalCue(context: Context, sound: Boolean) =
        playCue(context, bellPattern, ToneGenerator.TONE_PROP_PROMPT, sound)

    fun endCue(context: Context, sound: Boolean) =
        playCue(context, endPattern, ToneGenerator.TONE_PROP_ACK, sound)

    private fun playCue(context: Context, pattern: LongArray, tone: Int, sound: Boolean) {
        vibrate(context, pattern)
        if (sound) playTone(tone)
    }

    private fun vibrate(context: Context, pattern: LongArray) {
        val vibrator = vibrator(context) ?: return
        if (!vibrator.hasVibrator()) return
        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
    }

    private fun playTone(tone: Int) {
        val generator = toneGenerator
            ?: runCatching { ToneGenerator(AudioManager.STREAM_MUSIC, VOLUME_PERCENT) }
                .getOrNull()
                ?.also { toneGenerator = it }
            ?: return
        generator.startTone(tone, TONE_LENGTH_MILLIS)
    }

    private fun vibrator(context: Context): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    private const val VOLUME_PERCENT = 80
    private const val TONE_LENGTH_MILLIS = 600
}

package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.sin

class SoundEffectsHelper(private val context: Context) : TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null
  private var isTtsReady = false
  private var isArabicAvailable = false
  private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
    vibratorManager?.defaultVibrator
  } else {
    @Suppress("DEPRECATION")
    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
  }

  private val coroutineScope = CoroutineScope(Dispatchers.Default)

  init {
    try {
      tts = TextToSpeech(context.applicationContext, this)
    } catch (_: Exception) {
      // Graceful fallback
    }
  }

  override fun onInit(status: Int) {
    if (status == TextToSpeech.SUCCESS) {
      isTtsReady = true
      try {
        val result = tts?.setLanguage(Locale("ar"))
        isArabicAvailable = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED)
        tts?.setSpeechRate(0.85f) // Slightly slower for preschool kids
        tts?.setPitch(1.1f) // Pleasant friendly pitch
      } catch (_: Exception) {
        // Fallback
      }
    }
  }

  fun speakArabic(text: String) {
    if (!isTtsReady) return
    try {
      if (isArabicAvailable) {
        tts?.setLanguage(Locale("ar"))
      }
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AR_SPEECH_ID")
    } catch (_: Exception) {
      // Ignored
    }
  }

  fun speakMalay(text: String) {
    if (!isTtsReady) return
    try {
      tts?.setLanguage(Locale("ms", "MY"))
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MS_SPEECH_ID")
    } catch (_: Exception) {
      // Fallback to default
      tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MS_SPEECH_ID")
    }
  }

  fun playSuccess() {
    vibrateGentle()
    coroutineScope.launch {
      // Cheerful rising major arpeggio: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
      playArpeggio(listOf(523.0, 659.0, 784.0, 1046.0), noteDurationMs = 80)
    }
  }

  fun playStarEarned() {
    vibrateGentle()
    coroutineScope.launch {
      // Sparkling star twinkle: E6 (1318Hz), G#6 (1661Hz), B6 (1975Hz), E7 (2637Hz)
      playArpeggio(listOf(1318.0, 1661.0, 1975.0, 2637.0), noteDurationMs = 70)
    }
  }

  fun playWrong() {
    coroutineScope.launch {
      // Gentle soft warm descending tone (never harsh for children)
      playChirp(330.0, 220.0, 180)
    }
  }

  fun playPop() {
    coroutineScope.launch {
      // Quick bubble pop
      playTone(880.0, 45)
    }
  }

  fun playFanfare() {
    vibrateCelebration()
    coroutineScope.launch {
      // Victory fanfare melody
      val notes = listOf(
        Pair(523.25, 100), // C5
        Pair(659.25, 100), // E5
        Pair(783.99, 100), // G5
        Pair(1046.50, 250), // C6
        Pair(880.00, 100), // A5
        Pair(1046.50, 400) // C6 hold
      )
      for ((freq, dur) in notes) {
        playTone(freq, dur)
      }
    }
  }

  private fun vibrateGentle() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(50)
      }
    } catch (_: Exception) {}
  }

  private fun vibrateCelebration() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val pattern = longArrayOf(0, 80, 80, 150)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(200)
      }
    } catch (_: Exception) {}
  }

  private fun playArpeggio(frequencies: List<Double>, noteDurationMs: Int) {
    for (freq in frequencies) {
      playTone(freq, noteDurationMs)
    }
  }

  private fun playTone(freqHz: Double, durationMs: Int) {
    val sampleRate = 44100
    val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
    if (numSamples <= 0) return
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val t = i.toDouble() / sampleRate
      // Smooth attack and release envelope to avoid clicking
      val envelope = when {
        i < numSamples * 0.15 -> i / (numSamples * 0.15)
        i > numSamples * 0.75 -> (numSamples - i) / (numSamples * 0.25)
        else -> 1.0
      }
      val sample = (sin(2.0 * Math.PI * freqHz * t) * envelope * 0.65 * Short.MAX_VALUE).toInt()
      buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }

    writeToAudioTrack(buffer, sampleRate)
  }

  private fun playChirp(startFreq: Double, endFreq: Double, durationMs: Int) {
    val sampleRate = 44100
    val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
    if (numSamples <= 0) return
    val buffer = ShortArray(numSamples)

    for (i in 0 until numSamples) {
      val progress = i.toDouble() / numSamples
      val currentFreq = startFreq + (endFreq - startFreq) * progress
      val t = i.toDouble() / sampleRate
      val envelope = sin(Math.PI * progress) // Half-sine window
      val sample = (sin(2.0 * Math.PI * currentFreq * t) * envelope * 0.45 * Short.MAX_VALUE).toInt()
      buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
    }

    writeToAudioTrack(buffer, sampleRate)
  }

  private fun writeToAudioTrack(buffer: ShortArray, sampleRate: Int) {
    try {
      val minBufferSize = AudioTrack.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_OUT_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )
      val track = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(maxOf(minBufferSize, buffer.size * 2))
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      track.write(buffer, 0, buffer.size)
      track.play()
      // Release track after playback completes
      Thread.sleep((buffer.size * 1000L / sampleRate) + 20)
      track.stop()
      track.release()
    } catch (_: Exception) {
      // Audio error suppressed gracefully
    }
  }

  fun shutdown() {
    try {
      tts?.stop()
      tts?.shutdown()
    } catch (_: Exception) {}
  }
}

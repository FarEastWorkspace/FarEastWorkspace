package com.example.viewmodel

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEffectsHelper
import com.example.data.db.StudentRecordEntity
import com.example.data.model.ArabicNumber
import com.example.data.model.ArabicNumbersData
import com.example.data.repository.StudentRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class Screen {
  HOME,
  LEARN,
  QUIZ,
  SCOREBOARD,
  TEACHER_RECORDS
}

enum class GameMode(val titleMs: String, val icon: String, val descMs: String) {
  MATCH_NUMBER("Kuiz Padanan Nombor", "🎯", "Padankan perkataan Arab & Rumi dengan nombor Arab."),
  COUNT_OBJECTS("Kira Objek Ceria", "🎈", "Kira bilangan objek comel dan pilih nombor Arab."),
  LISTEN_AND_GUESS("Dengar & Teka", "🔊", "Dengar sebutan audio dan pilih nombor yang betul.")
}

data class QuizQuestion(
  val questionNumber: Int,
  val promptTitle: String,
  val targetNumber: ArabicNumber,
  val visualObjects: List<String> = emptyList(),
  val options: List<ArabicNumber>,
  val audioToPlay: String? = null
)

data class GameUiState(
  val currentScreen: Screen = Screen.HOME,
  val studentName: String = "",
  val studentYear: String = "Prasekolah (6 Tahun)",
  val activeMode: GameMode = GameMode.MATCH_NUMBER,
  val currentQuestionIndex: Int = 0,
  val questions: List<QuizQuestion> = emptyList(),
  val currentQuestion: QuizQuestion? = null,
  val score: Int = 0,
  val streak: Int = 0,
  val correctCount: Int = 0,
  val totalQuestions: Int = 5,
  val selectedAnswer: ArabicNumber? = null,
  val isAnswerCorrect: Boolean? = null,
  val isRoundFinished: Boolean = false,
  val starsEarned: Int = 0,
  val lastSavedRecordId: Long? = null,
  val showProfileDialog: Boolean = false,
  val showSheetsConfigDialog: Boolean = false,
  val sheetsWebhookUrl: String = "",
  val autoSyncSheets: Boolean = true,
  val isSyncingSheets: Boolean = false,
  val syncStatusMessage: String? = null
)

class GameViewModel(
  private val repository: StudentRepository,
  private val soundHelper: SoundEffectsHelper
) : ViewModel() {

  private val _uiState = MutableStateFlow(
    GameUiState(
      studentName = repository.getSavedStudentName().ifBlank { "Murid Pintar" },
      studentYear = repository.getSavedStudentYear(),
      sheetsWebhookUrl = repository.getSheetsWebhookUrl(),
      autoSyncSheets = repository.isAutoSyncEnabled()
    )
  )
  val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

  val allRecords: StateFlow<List<StudentRecordEntity>> = repository.getAllRecords()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val leaderboard: StateFlow<List<StudentRecordEntity>> = repository.getLeaderboard()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun navigateTo(screen: Screen) {
    soundHelper.playPop()
    _uiState.update { it.copy(currentScreen = screen) }
  }

  fun showProfileDialog(show: Boolean) {
    soundHelper.playPop()
    _uiState.update { it.copy(showProfileDialog = show) }
  }

  fun showSheetsConfigDialog(show: Boolean) {
    soundHelper.playPop()
    _uiState.update { it.copy(showSheetsConfigDialog = show) }
  }

  fun updateStudentProfile(name: String, year: String) {
    val trimmedName = name.trim().ifBlank { "Murid Pintar" }
    val trimmedYear = year.trim().ifBlank { "Prasekolah (6 Tahun)" }
    repository.saveStudentName(trimmedName)
    repository.saveStudentYear(trimmedYear)
    soundHelper.playSuccess()
    _uiState.update {
      it.copy(
        studentName = trimmedName,
        studentYear = trimmedYear,
        showProfileDialog = false
      )
    }
  }

  fun updateSheetsConfig(webhookUrl: String, autoSync: Boolean) {
    val cleanUrl = webhookUrl.trim()
    repository.saveSheetsWebhookUrl(cleanUrl)
    repository.setAutoSyncEnabled(autoSync)
    soundHelper.playSuccess()
    _uiState.update {
      it.copy(
        sheetsWebhookUrl = cleanUrl,
        autoSyncSheets = autoSync,
        showSheetsConfigDialog = false
      )
    }
  }

  fun playNumberAudio(number: ArabicNumber) {
    soundHelper.speakArabic(number.arabicWord)
  }

  fun startQuiz(mode: GameMode, totalQuestions: Int = 5) {
    val questions = generateQuestions(mode, totalQuestions)
    _uiState.update {
      it.copy(
        activeMode = mode,
        currentScreen = Screen.QUIZ,
        currentQuestionIndex = 0,
        questions = questions,
        currentQuestion = questions.firstOrNull(),
        score = 0,
        streak = 0,
        correctCount = 0,
        totalQuestions = totalQuestions,
        selectedAnswer = null,
        isAnswerCorrect = null,
        isRoundFinished = false,
        starsEarned = 0,
        lastSavedRecordId = null
      )
    }
    soundHelper.playPop()
    // If it's listen and guess mode, automatically play audio for first question
    if (mode == GameMode.LISTEN_AND_GUESS) {
      questions.firstOrNull()?.targetNumber?.let { num ->
        viewModelScope.launch {
          delay(400)
          soundHelper.speakArabic(num.arabicWord)
        }
      }
    }
  }

  private fun generateQuestions(mode: GameMode, total: Int): List<QuizQuestion> {
    val allNumbers = ArabicNumbersData.numbers.shuffled()
    val pickedTargets = (allNumbers + allNumbers.shuffled()).take(total)

    return pickedTargets.mapIndexed { index, target ->
      val otherOptions = ArabicNumbersData.numbers
        .filter { it.number != target.number }
        .shuffled()
        .take(3)
      val options = (otherOptions + target).shuffled()

      when (mode) {
        GameMode.MATCH_NUMBER -> {
          val style = index % 2
          val prompt = if (style == 0) {
            "Manakah nombor Arab bagi \"${target.malayMeaning} (${target.transliteration})\"?"
          } else {
            "Cari nombor bagi sebutan: ${target.arabicWord} (${target.transliteration})"
          }
          QuizQuestion(
            questionNumber = index + 1,
            promptTitle = prompt,
            targetNumber = target,
            visualObjects = List(target.number) { target.emoji },
            options = options,
            audioToPlay = target.arabicWord
          )
        }
        GameMode.COUNT_OBJECTS -> {
          QuizQuestion(
            questionNumber = index + 1,
            promptTitle = "Kira objek comel di bawah dan pilih nombor Arab yang betul:",
            targetNumber = target,
            visualObjects = List(target.number) { target.emoji },
            options = options,
            audioToPlay = target.arabicWord
          )
        }
        GameMode.LISTEN_AND_GUESS -> {
          QuizQuestion(
            questionNumber = index + 1,
            promptTitle = "Dengar sebutan audio dengan teliti dan pilih kad nombor yang sepadan:",
            targetNumber = target,
            visualObjects = emptyList(),
            options = options,
            audioToPlay = target.arabicWord
          )
        }
      }
    }
  }

  fun submitAnswer(chosen: ArabicNumber) {
    val currentState = _uiState.value
    if (currentState.selectedAnswer != null || currentState.isRoundFinished) return

    val target = currentState.currentQuestion?.targetNumber ?: return
    val isCorrect = (chosen.number == target.number)

    val newScore = if (isCorrect) {
      val streakBonus = (currentState.streak * 2).coerceAtMost(10)
      currentState.score + 20 + streakBonus
    } else {
      currentState.score
    }
    val newStreak = if (isCorrect) currentState.streak + 1 else 0
    val newCorrect = if (isCorrect) currentState.correctCount + 1 else currentState.correctCount

    if (isCorrect) {
      soundHelper.playSuccess()
    } else {
      soundHelper.playWrong()
    }

    _uiState.update {
      it.copy(
        selectedAnswer = chosen,
        isAnswerCorrect = isCorrect,
        score = newScore,
        streak = newStreak,
        correctCount = newCorrect
      )
    }

    viewModelScope.launch {
      delay(1200)
      advanceQuestion()
    }
  }

  private fun advanceQuestion() {
    val currentState = _uiState.value
    val nextIndex = currentState.currentQuestionIndex + 1

    if (nextIndex < currentState.questions.size) {
      val nextQuestion = currentState.questions[nextIndex]
      _uiState.update {
        it.copy(
          currentQuestionIndex = nextIndex,
          currentQuestion = nextQuestion,
          selectedAnswer = null,
          isAnswerCorrect = null
        )
      }
      if (currentState.activeMode == GameMode.LISTEN_AND_GUESS) {
        soundHelper.speakArabic(nextQuestion.targetNumber.arabicWord)
      }
    } else {
      finishRound()
    }
  }

  private fun finishRound() {
    val currentState = _uiState.value
    val total = currentState.totalQuestions
    val correct = currentState.correctCount
    val percentage = if (total > 0) (correct.toFloat() / total * 100).toInt() else 0

    val stars = when {
      percentage >= 80 -> 3
      percentage >= 50 -> 2
      correct > 0 -> 1
      else -> 0
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date())

    val record = StudentRecordEntity(
      studentName = currentState.studentName.ifBlank { "Murid Pintar" },
      studentYear = currentState.studentYear,
      score = currentState.score,
      maxScore = total * 30,
      starsEarned = stars,
      gameMode = currentState.activeMode.titleMs,
      totalQuestions = total,
      correctCount = correct,
      formattedDate = formattedDate
    )

    if (stars > 0) {
      soundHelper.playFanfare()
    } else {
      soundHelper.playPop()
    }

    viewModelScope.launch {
      val id = repository.insertRecord(record)
      _uiState.update {
        it.copy(
          isRoundFinished = true,
          starsEarned = stars,
          lastSavedRecordId = id,
          selectedAnswer = null,
          isAnswerCorrect = null
        )
      }
    }
  }

  fun replayCurrentMode() {
    startQuiz(_uiState.value.activeMode, _uiState.value.totalQuestions)
  }

  fun syncRecordNow(record: StudentRecordEntity) {
    viewModelScope.launch {
      _uiState.update { it.copy(isSyncingSheets = true, syncStatusMessage = "Menghantar ke Google Sheets...") }
      val success = repository.syncSingleRecord(record)
      _uiState.update {
        it.copy(
          isSyncingSheets = false,
          syncStatusMessage = if (success) "Berjaya dihantar ke Google Sheets!" else "Gagal menyambung ke Webhook Google Sheets."
        )
      }
      delay(3000)
      _uiState.update { it.copy(syncStatusMessage = null) }
    }
  }

  fun exportCsvAndShare(context: Context) {
    val records = allRecords.value
    val csv = repository.generateCsvContent(records)
    soundHelper.playPop()

    val sendIntent = Intent().apply {
      action = Intent.ACTION_SEND
      putExtra(Intent.EXTRA_TEXT, csv)
      putExtra(Intent.EXTRA_TITLE, "Data Prestasi Murid (Google Sheets)")
      type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Buka atau Hantar Data ke Google Sheets")
    context.startActivity(shareIntent)
  }

  fun copyCsvToClipboard(context: Context) {
    val records = allRecords.value
    val csv = repository.generateCsvContent(records)
    soundHelper.playPop()

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
    val clip = ClipData.newPlainText("Google Sheets Data", csv)
    clipboard?.setPrimaryClip(clip)
    Toast.makeText(context, "Data berformat 'nama murid,tahun,markah' disalin!", Toast.LENGTH_SHORT).show()
  }

  fun deleteRecord(id: Long) {
    viewModelScope.launch {
      repository.deleteRecord(id)
    }
  }

  fun clearAllRecords() {
    viewModelScope.launch {
      repository.clearAllRecords()
    }
  }

  override fun onCleared() {
    super.onCleared()
    soundHelper.shutdown()
  }
}

class GameViewModelFactory(
  private val repository: StudentRepository,
  private val soundHelper: SoundEffectsHelper
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
      return GameViewModel(repository, soundHelper) as T
    }
    throw IllegalArgumentException("Unknown ViewModel class")
  }
}

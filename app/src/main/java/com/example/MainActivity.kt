package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.audio.SoundEffectsHelper
import com.example.data.db.AppDatabase
import com.example.data.db.StudentRecordEntity
import com.example.data.repository.StudentRepository
import com.example.ui.components.GoogleSheetsConfigDialog
import com.example.ui.components.ProfileEditDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.ScoreboardScreen
import com.example.ui.screens.TeacherRecordsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameMode
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel
import com.example.viewmodel.GameViewModelFactory
import com.example.viewmodel.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

  private var soundHelper: SoundEffectsHelper? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = AppDatabase.getDatabase(this)
    val repository = StudentRepository(database.studentRecordDao(), this)
    val soundHelperInstance = SoundEffectsHelper(this).also { soundHelper = it }

    setContent {
      MyApplicationTheme {
        val viewModel: GameViewModel = viewModel(
          factory = GameViewModelFactory(repository, soundHelperInstance)
        )

        MainAppContent(
          viewModel = viewModel,
          repository = repository
        )
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    soundHelper?.shutdown()
  }
}

@Composable
fun MainAppContent(
  viewModel: GameViewModel,
  repository: StudentRepository
) {
  val context = LocalContext.current
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val records by viewModel.allRecords.collectAsStateWithLifecycle()
  val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()

  // Seed sample classmate scores for healthy competition if empty
  LaunchedEffect(Unit) {
    withContext(Dispatchers.IO) {
      val existing = repository.getAllRecords()
      if (repository.getSavedStudentName().isBlank()) {
        repository.saveStudentName("Ahmad Rayyan")
      }
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    contentWindowInsets = WindowInsets.safeDrawing
  ) { innerPadding ->
    val contentModifier = Modifier.padding(innerPadding)

    when (uiState.currentScreen) {
      Screen.HOME -> {
        HomeScreen(
          uiState = uiState,
          onOpenProfile = { viewModel.showProfileDialog(true) },
          onOpenLearn = { viewModel.navigateTo(Screen.LEARN) },
          onStartQuiz = { mode -> viewModel.startQuiz(mode) },
          onOpenScoreboard = { viewModel.navigateTo(Screen.SCOREBOARD) },
          onOpenTeacherRecords = { viewModel.navigateTo(Screen.TEACHER_RECORDS) },
          modifier = contentModifier
        )
      }

      Screen.LEARN -> {
        LearnScreen(
          onBack = { viewModel.navigateTo(Screen.HOME) },
          onPlayAudio = { number -> viewModel.playNumberAudio(number) },
          modifier = contentModifier
        )
      }

      Screen.QUIZ -> {
        QuizScreen(
          uiState = uiState,
          onBack = { viewModel.navigateTo(Screen.HOME) },
          onAnswerSelected = { chosen -> viewModel.submitAnswer(chosen) },
          onReplayAudio = { num -> viewModel.playNumberAudio(num) },
          onPlayAgain = { viewModel.replayCurrentMode() },
          onViewScoreboard = { viewModel.navigateTo(Screen.SCOREBOARD) },
          onShareToSheets = { viewModel.exportCsvAndShare(context) },
          modifier = contentModifier
        )
      }

      Screen.SCOREBOARD -> {
        ScoreboardScreen(
          records = leaderboard,
          onBack = { viewModel.navigateTo(Screen.HOME) },
          modifier = contentModifier
        )
      }

      Screen.TEACHER_RECORDS -> {
        TeacherRecordsScreen(
          records = records,
          webhookUrl = uiState.sheetsWebhookUrl,
          autoSync = uiState.autoSyncSheets,
          isSyncing = uiState.isSyncingSheets,
          syncStatusMessage = uiState.syncStatusMessage,
          onBack = { viewModel.navigateTo(Screen.HOME) },
          onOpenSheetsConfig = { viewModel.showSheetsConfigDialog(true) },
          onCopyCsv = { viewModel.copyCsvToClipboard(context) },
          onExportCsv = { viewModel.exportCsvAndShare(context) },
          onSyncRecord = { record -> viewModel.syncRecordNow(record) },
          onDeleteRecord = { id -> viewModel.deleteRecord(id) },
          onClearAll = { viewModel.clearAllRecords() },
          modifier = contentModifier
        )
      }
    }

    // Profile Dialog
    if (uiState.showProfileDialog) {
      ProfileEditDialog(
        currentName = uiState.studentName,
        currentYear = uiState.studentYear,
        onDismiss = { viewModel.showProfileDialog(false) },
        onSave = { name, year -> viewModel.updateStudentProfile(name, year) }
      )
    }

    // Google Sheets Config Dialog
    if (uiState.showSheetsConfigDialog) {
      GoogleSheetsConfigDialog(
        currentUrl = uiState.sheetsWebhookUrl,
        currentAutoSync = uiState.autoSyncSheets,
        onDismiss = { viewModel.showSheetsConfigDialog(false) },
        onSave = { url, autoSync -> viewModel.updateSheetsConfig(url, autoSync) }
      )
    }
  }
}

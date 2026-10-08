package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ArabicNumber
import com.example.ui.components.CelebrationResultDialog
import com.example.ui.components.StarRewardBar
import com.example.ui.theme.CardPastelBlue
import com.example.ui.theme.CoralRed
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryOrange
import com.example.viewmodel.GameMode
import com.example.viewmodel.GameUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuizScreen(
  uiState: GameUiState,
  onBack: () -> Unit,
  onAnswerSelected: (ArabicNumber) -> Unit,
  onReplayAudio: (ArabicNumber) -> Unit,
  onPlayAgain: () -> Unit,
  onViewScoreboard: () -> Unit,
  onShareToSheets: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val question = uiState.currentQuestion
  val targetNumber = question?.targetNumber

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.testTag("quiz_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Kembali ke Menu",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = uiState.activeMode.titleMs,
          fontSize = 16.sp,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Murid: ${uiState.studentName} (${uiState.studentYear})",
          fontSize = 11.sp,
          color = PrimaryBlue,
          fontWeight = FontWeight.SemiBold
        )
      }

      // Live Score Badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(16.dp))
          .background(SecondaryAmber)
          .padding(horizontal = 12.dp, vertical = 6.dp)
          .testTag("quiz_live_score_badge"),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "${uiState.score} pt",
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 13.sp
        )
      }
    }

    // Progress Bar
    val progress = if (uiState.totalQuestions > 0) {
      (uiState.currentQuestionIndex + 1).toFloat() / uiState.totalQuestions
    } else 0f

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Soalan ${uiState.currentQuestionIndex + 1} / ${uiState.totalQuestions}",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = Color.Gray
        )

        if (uiState.streak > 1) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Whatshot,
              contentDescription = null,
              tint = SecondaryOrange,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
              text = "Streak x${uiState.streak}!",
              fontSize = 12.sp,
              fontWeight = FontWeight.ExtraBold,
              color = SecondaryOrange
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = PrimaryBlue,
        trackColor = Color(0xFFE2E8F0)
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Main Question Card & Options
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if (question != null && targetNumber != null) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("quiz_question_card"),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = question.promptTitle,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold,
              textAlign = TextAlign.Center,
              color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(14.dp))

            when (uiState.activeMode) {
              GameMode.COUNT_OBJECTS -> {
                // Visual counting items box
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardPastelBlue)
                    .padding(16.dp),
                  contentAlignment = Alignment.Center
                ) {
                  FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    repeat(targetNumber.number) {
                      Box(
                        modifier = Modifier
                          .size(46.dp)
                          .clip(CircleShape)
                          .background(Color.White)
                          .border(1.dp, PrimaryBlue.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(text = targetNumber.emoji, fontSize = 24.sp)
                      }
                    }
                  }
                }
              }

              GameMode.LISTEN_AND_GUESS -> {
                // Audio prompt speaker
                Box(
                  modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3E5F5))
                    .border(3.dp, Color(0xFF8E24AA), CircleShape)
                    .clickable { onReplayAudio(targetNumber) }
                    .testTag("quiz_audio_repeat_btn"),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Main Semula Suara",
                    tint = Color(0xFF8E24AA),
                    modifier = Modifier.size(52.dp)
                  )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "Tekan pembesar suara untuk dengar lagi",
                  fontSize = 12.sp,
                  color = Color.Gray
                )
              }

              GameMode.MATCH_NUMBER -> {
                // Large Arabic word or Roman hint
                Box(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFFFF3E0))
                    .padding(14.dp),
                  contentAlignment = Alignment.Center
                ) {
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = targetNumber.arabicWord,
                      fontSize = 32.sp,
                      fontWeight = FontWeight.Black,
                      color = SecondaryOrange
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = "(${targetNumber.transliteration} = ${targetNumber.malayMeaning})",
                      fontSize = 15.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = Color.DarkGray
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4 Options Grid
        Text(
          text = "Pilih Nombor Arab yang Betul:",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          question.options.chunked(2).forEach { pair ->
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              pair.forEach { option ->
                val isSelected = (uiState.selectedAnswer?.number == option.number)
                val isCorrectTarget = (option.number == targetNumber.number)
                val isEvaluated = (uiState.selectedAnswer != null)

                val backgroundColor by animateColorAsState(
                  targetValue = when {
                    isEvaluated && isCorrectTarget -> Color(0xFFE8F5E9)
                    isEvaluated && isSelected && !isCorrectTarget -> Color(0xFFFFEBEE)
                    else -> Color.White
                  },
                  animationSpec = tween(200),
                  label = "option_bg_anim"
                )

                val borderColor by animateColorAsState(
                  targetValue = when {
                    isEvaluated && isCorrectTarget -> FreshGreen
                    isEvaluated && isSelected && !isCorrectTarget -> CoralRed
                    else -> Color(0xFFCBD5E1)
                  },
                  animationSpec = tween(200),
                  label = "option_border_anim"
                )

                Card(
                  modifier = Modifier
                    .weight(1f)
                    .height(96.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(2.dp, borderColor, RoundedCornerShape(18.dp))
                    .clickable(enabled = uiState.selectedAnswer == null) {
                      onAnswerSelected(option)
                    }
                    .testTag("option_${option.number}"),
                  shape = RoundedCornerShape(18.dp),
                  colors = CardDefaults.cardColors(containerColor = backgroundColor),
                  elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                  Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                  ) {
                    Column(
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement = Arrangement.Center
                    ) {
                      Text(
                        text = option.arabicDigit,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        color = when {
                          isEvaluated && isCorrectTarget -> FreshGreen
                          isEvaluated && isSelected && !isCorrectTarget -> CoralRed
                          else -> PrimaryBlue
                        }
                      )
                      Text(
                        text = "${option.transliteration} (${option.number})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.DarkGray
                      )
                    }

                    // Status check/cross icon
                    if (isEvaluated && isCorrectTarget) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Betul",
                        tint = FreshGreen,
                        modifier = Modifier
                          .align(Alignment.TopEnd)
                          .padding(8.dp)
                          .size(20.dp)
                      )
                    } else if (isEvaluated && isSelected && !isCorrectTarget) {
                      Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Salah",
                        tint = CoralRed,
                        modifier = Modifier
                          .align(Alignment.TopEnd)
                          .padding(8.dp)
                          .size(20.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Celebration Result Dialog
  if (uiState.isRoundFinished) {
    CelebrationResultDialog(
      studentName = uiState.studentName,
      studentYear = uiState.studentYear,
      score = uiState.score,
      starsEarned = uiState.starsEarned,
      correctCount = uiState.correctCount,
      totalQuestions = uiState.totalQuestions,
      onPlayAgain = onPlayAgain,
      onViewScoreboard = onViewScoreboard,
      onShareToSheets = onShareToSheets,
      onDismiss = onBack
    )
  }
}

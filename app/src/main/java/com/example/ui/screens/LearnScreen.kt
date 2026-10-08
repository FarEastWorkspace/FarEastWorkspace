package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.ArabicNumbersData
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LearnScreen(
  onBack: () -> Unit,
  onPlayAudio: (ArabicNumber) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var selectedIndex by remember { mutableIntStateOf(0) }
  val currentNumber = ArabicNumbersData.numbers[selectedIndex]
  var tapCount by remember { mutableIntStateOf(0) }

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
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = onBack,
        modifier = Modifier.testTag("learn_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Kembali",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
      Column {
        Text(
          text = "Mari Kenal Nombor (١ - ١٠)",
          fontSize = 18.sp,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Tekan pembesar suara untuk mendengar sebutan",
          fontSize = 12.sp,
          color = Color.Gray
        )
      }
    }

    // Number Tabs Bar (1 to 10)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      ArabicNumbersData.numbers.forEachIndexed { idx, item ->
        val isSelected = (idx == selectedIndex)
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) PrimaryBlue else Color.White)
            .border(
              width = 1.dp,
              color = if (isSelected) PrimaryBlue else Color(0xFFCBD5E1),
              shape = RoundedCornerShape(12.dp)
            )
            .clickable {
              selectedIndex = idx
              tapCount = 0
              onPlayAudio(item)
            }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("tab_number_${item.number}"),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = item.arabicDigit,
              fontSize = 18.sp,
              fontWeight = FontWeight.Black,
              color = if (isSelected) Color.White else PrimaryBlue
            )
            Text(
              text = "${item.number}",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = if (isSelected) Color.White.copy(alpha = 0.85f) else Color.Gray
            )
          }
        }
      }
    }

    // Scrollable Number Content
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      AnimatedContent(
        targetState = currentNumber,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "number_card_anim"
      ) { targetNum ->
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("featured_number_card"),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = targetNum.cardColor),
          elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Arabic Digit Badge
            Box(
              modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(4.dp, targetNum.accentColor, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = targetNum.arabicDigit,
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
                color = targetNum.accentColor,
                textAlign = TextAlign.Center
              )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Arabic Word with Harakat
            Text(
              text = targetNum.arabicWord,
              fontSize = 32.sp,
              fontWeight = FontWeight.Black,
              color = Color(0xFF1E293B),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Transliteration & Malay Meaning
            Text(
              text = "${targetNum.transliteration}  •  ${targetNum.malayMeaning} (${targetNum.number})",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              color = targetNum.accentColor,
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Audio Button
            Button(
              onClick = { onPlayAudio(targetNum) },
              colors = ButtonDefaults.buttonColors(containerColor = targetNum.accentColor),
              shape = RoundedCornerShape(16.dp),
              modifier = Modifier.testTag("listen_audio_btn")
            ) {
              Icon(
                imageVector = Icons.Default.VolumeUp,
                contentDescription = "Dengar Sebutan",
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Dengar Sebutan (Suara Arab)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Counting Objects Section
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = Color.White),
              shape = RoundedCornerShape(16.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "Mari Mengira Bersama! (${targetNum.objectNameMs})",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.DarkGray
                )
                Text(
                  text = "Tekan objek untuk membilang:",
                  fontSize = 11.sp,
                  color = Color.Gray
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.Center,
                  verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  for (i in 1..targetNum.number) {
                    val isTapped = i <= tapCount
                    Box(
                      modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isTapped) targetNum.cardColor else Color(0xFFF1F5F9))
                        .border(
                          width = if (isTapped) 2.dp else 1.dp,
                          color = if (isTapped) targetNum.accentColor else Color(0xFFE2E8F0),
                          shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                          tapCount = i
                          onPlayAudio(targetNum)
                        }
                        .padding(4.dp),
                      contentAlignment = Alignment.Center
                    ) {
                      Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = targetNum.emoji, fontSize = 20.sp)
                        Text(
                          text = "$i",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = if (isTapped) targetNum.accentColor else Color.Gray
                        )
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Navigation Prev / Next
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        OutlinedButton(
          onClick = {
            if (selectedIndex > 0) {
              selectedIndex--
              tapCount = 0
              onPlayAudio(ArabicNumbersData.numbers[selectedIndex])
            }
          },
          enabled = selectedIndex > 0,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("prev_number_btn")
        ) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Sebelum")
        }

        OutlinedButton(
          onClick = {
            if (selectedIndex < ArabicNumbersData.numbers.lastIndex) {
              selectedIndex++
              tapCount = 0
              onPlayAudio(ArabicNumbersData.numbers[selectedIndex])
            }
          },
          enabled = selectedIndex < ArabicNumbersData.numbers.lastIndex,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("next_number_btn")
        ) {
          Text("Seterusnya")
          Spacer(modifier = Modifier.width(4.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
      }

      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}

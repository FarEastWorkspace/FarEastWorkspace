package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.StudentProfilePill
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.CardPastelBlue
import com.example.ui.theme.CardPastelGreen
import com.example.ui.theme.CardPastelOrange
import com.example.ui.theme.CardPastelPurple
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryOrange
import com.example.ui.theme.TertiaryPink
import com.example.viewmodel.GameMode
import com.example.viewmodel.GameUiState
import com.example.viewmodel.Screen

@Composable
fun HomeScreen(
  uiState: GameUiState,
  onOpenProfile: () -> Unit,
  onOpenLearn: () -> Unit,
  onStartQuiz: (GameMode) -> Unit,
  onOpenScoreboard: () -> Unit,
  onOpenTeacherRecords: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Bar with Student Profile & Quick Actions
    item {
      Spacer(modifier = Modifier.height(8.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        StudentProfilePill(
          studentName = uiState.studentName,
          studentYear = uiState.studentYear,
          onClick = onOpenProfile
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onOpenScoreboard,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(0xFFFFF8E1))
              .testTag("nav_scoreboard_btn")
          ) {
            Icon(
              imageVector = Icons.Default.EmojiEvents,
              contentDescription = "Papan Markah",
              tint = SecondaryAmber
            )
          }

          IconButton(
            onClick = onOpenTeacherRecords,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color(0xFFE8F5E9))
              .testTag("nav_teacher_records_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Assessment,
              contentDescription = "Rekod Guru & Google Sheets",
              tint = FreshGreen
            )
          }
        }
      }
    }

    // Hero Banner
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_banner_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.banner_hero),
              contentDescription = "Nombor Arab Ceria",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xAA0D47A1))
                  )
                )
            )
            Row(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "١  ٢  ٣  ٤  ٥  ٦  ٧  ٨  ٩  ١٠",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 20.sp
              )
            }
          }

          Column(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.linearGradient(
                  colors = listOf(Color(0xFF1E88E5), Color(0xFF1565C0))
                )
              )
              .padding(16.dp)
          ) {
            Text(
              text = "Jom Belajar Nombor Arab! 🌟",
              fontSize = 20.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Mari kenal nombor 1 hingga 10 dalam bahasa Arab dengan sebutan audio, kuiz ceria, dan kumpul bintang!",
              fontSize = 13.sp,
              color = Color(0xFFE3F2FD)
            )
          }
        }
      }
    }

    // Section Title
    item {
      Text(
        text = "Pilih Aktiviti Pembelajaran & Permainan:",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    // Activity 1: Mari Belajar (Flashcards & Pronunciation)
    item {
      ActivityCard(
        title = "1. Mari Kenal Nombor (١ - ١٠)",
        subtitle = "Belajar sebutan Arab yang fasih, harakat & kira objek comel bersama.",
        badge = "MOD BELAJAR",
        icon = "📚",
        backgroundColor = CardPastelBlue,
        accentColor = PrimaryBlue,
        testTag = "learn_mode_btn",
        onClick = onOpenLearn
      )
    }

    // Activity 2: Kuiz Padanan Nombor
    item {
      ActivityCard(
        title = "2. Kuiz Padanan Nombor",
        subtitle = "Pilih nombor Arab yang betul berdasarkan ejaan dan nama nombor.",
        badge = "KUIZ CERIA",
        icon = "🎯",
        backgroundColor = CardPastelOrange,
        accentColor = SecondaryOrange,
        testTag = "quiz_match_btn",
        onClick = { onStartQuiz(GameMode.MATCH_NUMBER) }
      )
    }

    // Activity 3: Kira Objek Ceria
    item {
      ActivityCard(
        title = "3. Kira Objek Ceria",
        subtitle = "Kira belon, epal & bintang yang comel pada skrin, pilih angka Arab!",
        badge = "KIRA & TEKA",
        icon = "🎈",
        backgroundColor = CardPastelGreen,
        accentColor = FreshGreen,
        testTag = "quiz_count_btn",
        onClick = { onStartQuiz(GameMode.COUNT_OBJECTS) }
      )
    }

    // Activity 4: Dengar & Teka
    item {
      ActivityCard(
        title = "4. Dengar & Teka (Audio Arab)",
        subtitle = "Dengar sebutan audio Arab dan pilih kad angka yang sepadan.",
        badge = "LATIHAN AUDIO",
        icon = "🔊",
        backgroundColor = CardPastelPurple,
        accentColor = Color(0xFF8E24AA),
        testTag = "quiz_listen_btn",
        onClick = { onStartQuiz(GameMode.LISTEN_AND_GUESS) }
      )
    }

    // Bottom Quick Shortcuts
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { onOpenScoreboard() }
            .testTag("home_scoreboard_shortcut"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFF8E1)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = SecondaryAmber
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Scoreboard",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Markah Tertinggi",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }
        }

        Card(
          modifier = Modifier
            .weight(1f)
            .clickable { onOpenTeacherRecords() }
            .testTag("home_teacher_shortcut"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                Icons.Default.Assessment,
                contentDescription = null,
                tint = FreshGreen
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Rekod Guru",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = "Google Sheets",
                fontSize = 11.sp,
                color = Color.Gray
              )
            }
          }
        }
      }
      Spacer(modifier = Modifier.height(16.dp))
    }
  }
}

@Composable
fun ActivityCard(
  title: String,
  subtitle: String,
  badge: String,
  icon: String,
  backgroundColor: Color,
  accentColor: Color,
  testTag: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag(testTag),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = backgroundColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(54.dp)
          .clip(RoundedCornerShape(14.dp))
          .background(Color.White)
          .border(2.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
      ) {
        Text(text = icon, fontSize = 28.sp)
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(accentColor.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = badge,
            color = accentColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = title,
          fontWeight = FontWeight.ExtraBold,
          fontSize = 16.sp,
          color = Color(0xFF1E293B)
        )
        Text(
          text = subtitle,
          fontSize = 12.sp,
          color = Color(0xFF475569),
          lineHeight = 16.sp
        )
      }

      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(accentColor),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "Mula",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

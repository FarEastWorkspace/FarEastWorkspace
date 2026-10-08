package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.StudentRecordEntity
import com.example.ui.components.StarRewardBar
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryOrange

@Composable
fun ScoreboardScreen(
  records: List<StudentRecordEntity>,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var searchQuery by remember { mutableStateOf("") }

  val filteredRecords = records.filter {
    it.studentName.contains(searchQuery, ignoreCase = true) ||
      it.studentYear.contains(searchQuery, ignoreCase = true)
  }

  val topThree = filteredRecords.take(3)
  val remaining = if (filteredRecords.size > 3) filteredRecords.drop(3) else emptyList()

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
        modifier = Modifier.testTag("scoreboard_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Kembali ke Menu",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
      Column {
        Text(
          text = "Papan Markah Tertinggi 🏆",
          fontSize = 18.sp,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Persaingan sihat antara rakan sekelas",
          fontSize = 12.sp,
          color = Color.Gray
        )
      }
    }

    // Search bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = { searchQuery = it },
      placeholder = { Text("Cari nama murid atau tahun/kelas...") },
      leadingIcon = {
        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
      },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .testTag("scoreboard_search_input"),
      shape = RoundedCornerShape(14.dp)
    )

    if (filteredRecords.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.EmojiEvents,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(72.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Belum ada rekod markah",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
          )
          Text(
            text = "Main mana-mana kuiz sekarang untuk kumpul markah dan bintang tertinggi!",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Podium Header for Top 3
        item {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Juara Teratas (Top 3):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
          )
          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
          ) {
            // Rank 2 (Silver)
            if (topThree.size >= 2) {
              PodiumCard(
                rank = 2,
                record = topThree[1],
                color = Color(0xFFCFD8DC),
                badgeColor = Color(0xFF78909C),
                modifier = Modifier.weight(1f)
              )
            } else {
              Spacer(modifier = Modifier.weight(1f))
            }

            // Rank 1 (Gold)
            if (topThree.isNotEmpty()) {
              PodiumCard(
                rank = 1,
                record = topThree[0],
                color = Color(0xFFFFF8E1),
                badgeColor = Color(0xFFFFB300),
                modifier = Modifier.weight(1.15f)
              )
            }

            // Rank 3 (Bronze)
            if (topThree.size >= 3) {
              PodiumCard(
                rank = 3,
                record = topThree[2],
                color = Color(0xFFFFE0B2),
                badgeColor = Color(0xFFD84315),
                modifier = Modifier.weight(1f)
              )
            } else {
              Spacer(modifier = Modifier.weight(1f))
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
        }

        // Leaderboard List
        item {
          Text(
            text = "Senarai Kedudukan Penuh:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
          )
        }

        itemsIndexed(filteredRecords) { index, item ->
          LeaderboardItemRow(rank = index + 1, record = item)
        }

        item {
          Spacer(modifier = Modifier.height(20.dp))
        }
      }
    }
  }
}

@Composable
fun PodiumCard(
  rank: Int,
  record: StudentRecordEntity,
  color: Color,
  badgeColor: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = color),
    elevation = CardDefaults.cardElevation(defaultElevation = if (rank == 1) 4.dp else 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = if (rank == 1) 16.dp else 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Box(
        modifier = Modifier
          .size(if (rank == 1) 44.dp else 36.dp)
          .clip(CircleShape)
          .background(badgeColor),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = if (rank == 1) "🥇" else if (rank == 2) "🥈" else "🥉",
          fontSize = if (rank == 1) 22.sp else 18.sp
        )
      }
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = record.studentName,
        fontWeight = FontWeight.ExtraBold,
        fontSize = if (rank == 1) 14.sp else 12.sp,
        maxLines = 1,
        textAlign = TextAlign.Center,
        color = Color(0xFF1E293B)
      )
      Text(
        text = record.studentYear,
        fontSize = 10.sp,
        color = Color.DarkGray,
        maxLines = 1,
        textAlign = TextAlign.Center
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "${record.score} pt",
        fontWeight = FontWeight.Black,
        fontSize = if (rank == 1) 16.sp else 14.sp,
        color = if (rank == 1) SecondaryOrange else PrimaryBlue
      )
      Row {
        repeat(record.starsEarned) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = BrightYellow,
            modifier = Modifier.size(12.dp)
          )
        }
      }
    }
  }
}

@Composable
fun LeaderboardItemRow(
  rank: Int,
  record: StudentRecordEntity
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("leaderboard_row_$rank"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Rank circle
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(
            when (rank) {
              1 -> Color(0xFFFFD54F)
              2 -> Color(0xFFB0BEC5)
              3 -> Color(0xFFFF8A65)
              else -> Color(0xFFF1F5F9)
            }
          ),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$rank",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 14.sp,
          color = if (rank <= 3) Color.White else Color.DarkGray
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = record.studentName,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = Color(0xFF1E293B)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "${record.studentYear}  •  ${record.gameMode}",
            fontSize = 11.sp,
            color = Color.Gray
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${record.score} pt",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 15.sp,
          color = PrimaryBlue
        )
        Row {
          repeat(record.starsEarned) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = BrightYellow,
              modifier = Modifier.size(13.dp)
            )
          }
        }
      }
    }
  }
}

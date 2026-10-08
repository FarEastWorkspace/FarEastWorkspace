package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryOrange

@Composable
fun StudentProfilePill(
  studentName: String,
  studentYear: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clickable { onClick() }
      .testTag("student_profile_pill"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(CircleShape)
          .background(PrimaryBlue),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = "Profil Murid",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column {
        Text(
          text = studentName.ifBlank { "Murid Pintar" },
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 1
        )
        Text(
          text = studentYear,
          style = MaterialTheme.typography.labelSmall,
          color = PrimaryBlue,
          maxLines = 1
        )
      }
      Spacer(modifier = Modifier.width(4.dp))
      Icon(
        imageVector = Icons.Default.Edit,
        contentDescription = "Tukar Profil",
        tint = Color.Gray,
        modifier = Modifier.size(16.dp)
      )
    }
  }
}

@Composable
fun ProfileEditDialog(
  currentName: String,
  currentYear: String,
  onDismiss: () -> Unit,
  onSave: (name: String, year: String) -> Unit
) {
  var name by remember { mutableStateOf(currentName) }
  var year by remember { mutableStateOf(currentYear) }

  val yearSuggestions = listOf(
    "Prasekolah (5 Tahun)",
    "Prasekolah (6 Tahun)",
    "Tadika Cerdik",
    "Tadika Bijak",
    "Tahun 1"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Person,
          contentDescription = null,
          tint = PrimaryBlue,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Perekodan Murid",
          fontWeight = FontWeight.Bold,
          fontSize = 20.sp
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Masukkan nama dan tahun/kelas murid untuk direkodkan dalam Papan Markah dan Google Sheets:",
          style = MaterialTheme.typography.bodyMedium,
          color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nama Murid") },
          placeholder = { Text("cth: Ahmad Rayyan") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("student_name_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = year,
          onValueChange = { year = it },
          label = { Text("Tahun / Kelas") },
          placeholder = { Text("cth: Prasekolah (6 Tahun)") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("student_year_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Pilihan Pantas Tahun/Kelas:",
          style = MaterialTheme.typography.labelSmall,
          color = Color.Gray
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          yearSuggestions.take(3).forEach { suggestion ->
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (year == suggestion) PrimaryBlue else Color(0xFFE2E8F0))
                .clickable { year = suggestion }
                .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
              Text(
                text = suggestion,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (year == suggestion) Color.White else Color.Black
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onSave(name, year) },
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
        modifier = Modifier.testTag("save_profile_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Simpan Rekod")
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Batal")
      }
    }
  )
}

@Composable
fun GoogleSheetsConfigDialog(
  currentUrl: String,
  currentAutoSync: Boolean,
  onDismiss: () -> Unit,
  onSave: (url: String, autoSync: Boolean) -> Unit
) {
  var url by remember { mutableStateOf(currentUrl) }
  var autoSync by remember { mutableStateOf(currentAutoSync) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Link,
          contentDescription = null,
          tint = FreshGreen,
          modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Tetapan Google Sheets",
          fontWeight = FontWeight.Bold,
          fontSize = 19.sp
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Format Lajur Google Sheet yang Digunakan:",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE8F5E9))
            .border(1.dp, FreshGreen, RoundedCornerShape(8.dp))
            .padding(8.dp)
        ) {
          Text(
            text = "nama murid,tahun,markah",
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1B5E20),
            fontSize = 13.sp
          )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "URL Webhook Google Apps Script (Pilihan):",
          style = MaterialTheme.typography.bodySmall,
          color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
          value = url,
          onValueChange = { url = it },
          placeholder = { Text("https://script.google.com/macros/s/.../exec") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("sheets_url_input"),
          shape = RoundedCornerShape(12.dp),
          maxLines = 2
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Hantar Automatik",
              fontWeight = FontWeight.SemiBold,
              fontSize = 14.sp
            )
            Text(
              text = "Hantar markah ke Google Sheets sebaik sahaja murid selesai kuiz.",
              style = MaterialTheme.typography.labelSmall,
              color = Color.Gray
            )
          }
          Switch(
            checked = autoSync,
            onCheckedChange = { autoSync = it },
            colors = SwitchDefaults.colors(checkedThumbColor = FreshGreen)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onSave(url, autoSync) },
        colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("save_sheets_config_button")
      ) {
        Text("Simpan Tetapan")
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Batal")
      }
    }
  )
}

@Composable
fun StarRewardBar(
  stars: Int,
  modifier: Modifier = Modifier,
  starSize: Int = 36
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    for (i in 1..3) {
      val isFilled = i <= stars
      Icon(
        imageVector = if (isFilled) Icons.Default.Star else Icons.Outlined.Star,
        contentDescription = "Bintang $i",
        tint = if (isFilled) BrightYellow else Color.LightGray,
        modifier = Modifier
          .size(starSize.dp)
          .padding(horizontal = 2.dp)
      )
    }
  }
}

@Composable
fun CelebrationResultDialog(
  studentName: String,
  studentYear: String,
  score: Int,
  starsEarned: Int,
  correctCount: Int,
  totalQuestions: Int,
  onPlayAgain: () -> Unit,
  onViewScoreboard: () -> Unit,
  onShareToSheets: () -> Unit,
  onDismiss: () -> Unit
) {
  val starScale = remember { Animatable(0.5f) }

  LaunchedEffect(Unit) {
    starScale.animateTo(
      targetValue = 1.15f,
      animationSpec = tween(400, easing = FastOutSlowInEasing)
    )
    starScale.animateTo(
      targetValue = 1.0f,
      animationSpec = tween(200, easing = FastOutSlowInEasing)
    )
  }

  AlertDialog(
    onDismissRequest = onDismiss,
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Box(
          modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .background(
              Brush.radialGradient(
                listOf(SecondaryAmber, SecondaryOrange)
              )
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Celebration,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(44.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = if (starsEarned == 3) "MUMTAZ! (ممتاز)" else if (starsEarned == 2) "TAHNIAH! (أَحْسَنْتَ)" else "BAGUS! TERUSKAN USAHA!",
          fontWeight = FontWeight.ExtraBold,
          fontSize = 20.sp,
          color = SecondaryOrange,
          textAlign = TextAlign.Center
        )

        Text(
          text = "Hebatnya $studentName ($studentYear)!",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold,
          color = Color.DarkGray,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Animated Stars
        Box(modifier = Modifier.scale(starScale.value)) {
          StarRewardBar(stars = starsEarned, starSize = 46)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
          colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Markah", fontSize = 12.sp, color = Color.Gray)
              Text(
                text = "$score",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryBlue
              )
            }
            Box(
              modifier = Modifier
                .width(1.dp)
                .height(40.dp)
                .background(Color.LightGray)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Betul", fontSize = 12.sp, color = Color.Gray)
              Text(
                text = "$correctCount / $totalQuestions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = FreshGreen
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Auto Save Notice
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFE8F5E9))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = FreshGreen,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Disimpan secara automatik ke Pangkalan Data!",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1B5E20)
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onPlayAgain,
          colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("play_again_button")
        ) {
          Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Main Sekali Lagi", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedButton(
            onClick = onViewScoreboard,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("view_scoreboard_button")
          ) {
            Text("Scoreboard", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = onShareToSheets,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = FreshGreen),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .weight(1f)
              .testTag("share_sheets_button")
          ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Hantar Sheets", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    },
    confirmButton = {},
    dismissButton = {}
  )
}

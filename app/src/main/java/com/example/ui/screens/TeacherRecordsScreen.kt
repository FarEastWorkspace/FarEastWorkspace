package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.StudentRecordEntity
import com.example.ui.theme.BrightYellow
import com.example.ui.theme.CardPastelGreen
import com.example.ui.theme.FreshGreen
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SecondaryAmber
import com.example.ui.theme.SecondaryOrange

@Composable
fun TeacherRecordsScreen(
  records: List<StudentRecordEntity>,
  webhookUrl: String,
  autoSync: Boolean,
  isSyncing: Boolean,
  syncStatusMessage: String?,
  onBack: () -> Unit,
  onOpenSheetsConfig: () -> Unit,
  onCopyCsv: () -> Unit,
  onExportCsv: () -> Unit,
  onSyncRecord: (StudentRecordEntity) -> Unit,
  onDeleteRecord: (Long) -> Unit,
  onClearAll: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  var showGuide by remember { mutableStateOf(false) }
  var showClearConfirm by remember { mutableStateOf(false) }

  // Statistics calculation
  val totalSessions = records.size
  val uniqueStudents = records.map { it.studentName }.distinct().size
  val avgScore = if (records.isNotEmpty()) (records.sumOf { it.score } / records.size) else 0
  val totalStars = records.sumOf { it.starsEarned }

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
        modifier = Modifier.testTag("teacher_records_back_btn")
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Kembali ke Menu",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Rekod Prestasi & Google Sheets 📊",
          fontSize = 17.sp,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.onBackground
        )
        Text(
          text = "Pemantauan Analisis Masa Nyata untuk Guru",
          fontSize = 11.sp,
          color = Color.Gray
        )
      }

      IconButton(
        onClick = onOpenSheetsConfig,
        modifier = Modifier.testTag("btn_open_sheets_settings")
      ) {
        Icon(
          imageVector = Icons.Default.Link,
          contentDescription = "Tetapan Google Sheets",
          tint = FreshGreen
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Status Message Banner
      if (syncStatusMessage != null) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFFE8F5E9))
              .border(1.dp, FreshGreen, RoundedCornerShape(10.dp))
              .padding(12.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              if (isSyncing) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = FreshGreen)
              } else {
                Icon(Icons.Default.Info, contentDescription = null, tint = FreshGreen, modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = syncStatusMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1B5E20)
              )
            }
          }
        }
      }

      // Real-time Google Sheet Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("google_sheets_panel_card"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = CardPastelGreen),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(FreshGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Assessment, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Penyelarasan Google Sheets",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 15.sp,
                  color = Color(0xFF1B5E20)
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (webhookUrl.isNotBlank()) Color(0xFFC8E6C9) else Color(0xFFFFECB3))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = if (webhookUrl.isNotBlank()) "Masa Nyata (Aktif)" else "Mod Fail / CSV",
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (webhookUrl.isNotBlank()) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
              text = "Format lajur rasmi yang dijana mengikut spesifikasi:",
              fontSize = 12.sp,
              color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .border(1.dp, Color(0xFFA5D6A7), RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text(
                text = "nama murid,tahun,markah",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                fontSize = 13.sp
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = onExportCsv,
                colors = ButtonDefaults.buttonColors(containerColor = FreshGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_export_sheets")
              ) {
                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Buka / Hantar CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              OutlinedButton(
                onClick = onCopyCsv,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1B5E20)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("btn_copy_sheets")
              ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Salin Data Sheets", fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Toggle Guide Button
            Row(
              modifier = Modifier
                .clickable { showGuide = !showGuide }
                .padding(vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (showGuide) "Sembunyikan Panduan Sambung Google Sheet" else "Lihat Panduan 1-Minit Sambung Google Sheet Terus (Apps Script)",
                fontSize = 11.sp,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.SemiBold
              )
            }

            // Collapsible Guide
            AnimatedVisibility(visible = showGuide) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = 8.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color.White)
                  .padding(10.dp)
              ) {
                Text(
                  text = "Langkah Sambung Terus ke Google Sheet Anda:",
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = Color.Black
                )
                Text(
                  text = "1. Buka Google Sheet baru dengan lajur: nama murid | tahun | markah\n" +
                    "2. Di menu atas Google Sheet: Extensions > Apps Script\n" +
                    "3. Masukkan kod berikut dan klik Deploy > New deployment > Web app (Access: Anyone):\n\n" +
                    "function doPost(e) {\n" +
                    "  var s = SpreadsheetApp.getActiveSpreadsheet().getActiveSheet();\n" +
                    "  var d = JSON.parse(e.postData.contents);\n" +
                    "  s.appendRow([d[\"nama murid\"], d[\"tahun\"], d[\"markah\"]]);\n" +
                    "  return ContentService.createTextOutput(\"OK\");\n" +
                    "}\n\n" +
                    "4. Salin Web App URL ke dalam Tetapan Google Sheets aplikasi ini!",
                  fontSize = 11.sp,
                  color = Color(0xFF334155),
                  lineHeight = 15.sp,
                  fontFamily = FontFamily.Monospace
                )
              }
            }
          }
        }
      }

      // Statistics Summary Cards
      item {
        Text(
          text = "Analisis Keseluruhan Kelas:",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.DarkGray
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          StatBox(
            label = "Jumlah Sesi",
            value = "$totalSessions",
            color = PrimaryBlue,
            modifier = Modifier.weight(1f)
          )
          StatBox(
            label = "Murid Terlibat",
            value = "$uniqueStudents",
            color = SecondaryOrange,
            modifier = Modifier.weight(1f)
          )
          StatBox(
            label = "Purata Markah",
            value = "$avgScore",
            color = FreshGreen,
            modifier = Modifier.weight(1f)
          )
          StatBox(
            label = "Bintang ⭐",
            value = "$totalStars",
            color = SecondaryAmber,
            modifier = Modifier.weight(1f)
          )
        }
      }

      // Record List Header & Clear Button
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Senarai Penuh Rekod Tersimpan (${records.size}):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
          )

          if (records.isNotEmpty()) {
            OutlinedButton(
              onClick = { showClearConfirm = true },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("btn_clear_records")
            ) {
              Text("Padam Semua", fontSize = 10.sp, color = Color.Red)
            }
          }
        }
      }

      if (records.isEmpty()) {
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "Tiada data rekod murid ditemui lagi.\nSebaik sahaja murid bermain kuiz, rekod mereka akan disimpan ke dalam pangkalan data secara automatik di sini.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 12.sp,
                color = Color.Gray
              )
            }
          }
        }
      } else {
        items(records) { record ->
          StudentRecordItemRow(
            record = record,
            onSyncNow = { onSyncRecord(record) },
            onDelete = { onDeleteRecord(record.id) }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Clear confirmation dialog
  if (showClearConfirm) {
    AlertDialog(
      onDismissRequest = { showClearConfirm = false },
      title = { Text("Padam Semua Rekod?") },
      text = { Text("Tindakan ini akan memadam semua data pangkalan data tempatan murid.") },
      confirmButton = {
        Button(
          onClick = {
            onClearAll()
            showClearConfirm = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
          Text("Ya, Padam")
        }
      },
      dismissButton = {
        OutlinedButton(onClick = { showClearConfirm = false }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun StatBox(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = label, fontSize = 10.sp, color = Color.Gray, maxLines = 1)
      Spacer(modifier = Modifier.height(2.dp))
      Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
    }
  }
}

@Composable
fun StudentRecordItemRow(
  record: StudentRecordEntity,
  onSyncNow: () -> Unit,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("record_item_${record.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = record.studentName,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = Color(0xFF1E293B)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(Color(0xFFE2E8F0))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = record.studentYear,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color.DarkGray
            )
          }
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
          text = "${record.gameMode}  •  ${record.correctCount}/${record.totalQuestions} Betul  •  ${record.formattedDate}",
          fontSize = 11.sp,
          color = Color.Gray
        )

        Spacer(modifier = Modifier.height(3.dp))

        // Synced status badge
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (record.syncedToSheets) Icons.Default.CloudDone else Icons.Default.CheckCircle,
            contentDescription = null,
            tint = if (record.syncedToSheets) FreshGreen else PrimaryBlue,
            modifier = Modifier.size(12.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (record.syncedToSheets) "Terselaras Google Sheets" else "Tersimpan di Pangkalan Data",
            fontSize = 10.sp,
            color = if (record.syncedToSheets) FreshGreen else PrimaryBlue,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${record.score} pt",
          fontWeight = FontWeight.Black,
          fontSize = 16.sp,
          color = PrimaryBlue
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

        IconButton(
          onClick = onDelete,
          modifier = Modifier.size(24.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Padam",
            tint = Color.LightGray,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}

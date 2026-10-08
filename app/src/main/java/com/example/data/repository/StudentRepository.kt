package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.StudentRecordDao
import com.example.data.db.StudentRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class StudentRepository(
  private val dao: StudentRecordDao,
  private val context: Context
) {

  private val prefs: SharedPreferences =
    context.getSharedPreferences("nombor_arab_prefs", Context.MODE_PRIVATE)

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(10, TimeUnit.SECONDS)
    .build()

  companion object {
    private const val KEY_STUDENT_NAME = "key_student_name"
    private const val KEY_STUDENT_YEAR = "key_student_year"
    private const val KEY_SHEETS_WEBHOOK_URL = "key_sheets_webhook_url"
    private const val KEY_AUTO_SYNC_SHEETS = "key_auto_sync_sheets"
    const val CSV_HEADER = "nama murid,tahun,markah"
  }

  fun getSavedStudentName(): String {
    return prefs.getString(KEY_STUDENT_NAME, "") ?: ""
  }

  fun saveStudentName(name: String) {
    prefs.edit().putString(KEY_STUDENT_NAME, name.trim()).apply()
  }

  fun getSavedStudentYear(): String {
    return prefs.getString(KEY_STUDENT_YEAR, "Prasekolah (6 Tahun)") ?: "Prasekolah (6 Tahun)"
  }

  fun saveStudentYear(year: String) {
    prefs.edit().putString(KEY_STUDENT_YEAR, year.trim()).apply()
  }

  fun getSheetsWebhookUrl(): String {
    return prefs.getString(KEY_SHEETS_WEBHOOK_URL, "") ?: ""
  }

  fun saveSheetsWebhookUrl(url: String) {
    prefs.edit().putString(KEY_SHEETS_WEBHOOK_URL, url.trim()).apply()
  }

  fun isAutoSyncEnabled(): Boolean {
    return prefs.getBoolean(KEY_AUTO_SYNC_SHEETS, true)
  }

  fun setAutoSyncEnabled(enabled: Boolean) {
    prefs.edit().putBoolean(KEY_AUTO_SYNC_SHEETS, enabled).apply()
  }

  fun getAllRecords(): Flow<List<StudentRecordEntity>> = dao.getAllRecords()

  fun getLeaderboard(): Flow<List<StudentRecordEntity>> = dao.getLeaderboard()

  suspend fun insertRecord(record: StudentRecordEntity): Long {
    val id = dao.insertRecord(record)
    val webhookUrl = getSheetsWebhookUrl()
    if (webhookUrl.isNotBlank() && isAutoSyncEnabled()) {
      // Attempt real-time sync in background
      withContext(Dispatchers.IO) {
        try {
          val success = syncToSheetsWebhook(webhookUrl, record)
          if (success) {
            dao.markSynced(id, true)
          }
        } catch (_: Exception) {
          // Keep syncedToSheets as false so teacher can retry
        }
      }
    }
    return id
  }

  suspend fun deleteRecord(id: Long) {
    dao.deleteRecord(id)
  }

  suspend fun clearAllRecords() {
    dao.clearAllRecords()
  }

  suspend fun syncSingleRecord(record: StudentRecordEntity): Boolean {
    val webhookUrl = getSheetsWebhookUrl()
    if (webhookUrl.isBlank()) return false
    return withContext(Dispatchers.IO) {
      val success = syncToSheetsWebhook(webhookUrl, record)
      if (success && record.id != 0L) {
        dao.markSynced(record.id, true)
      }
      success
    }
  }

  private fun syncToSheetsWebhook(webhookUrl: String, record: StudentRecordEntity): Boolean {
    return try {
      val json = JSONObject().apply {
        put("nama murid", record.studentName)
        put("tahun", record.studentYear)
        put("markah", record.score)
        put("bintang", record.starsEarned)
        put("mod", record.gameMode)
        put("tarikh", record.formattedDate)
      }
      val body = json.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
      val request = Request.Builder()
        .url(webhookUrl)
        .post(body)
        .build()

      val response = okHttpClient.newCall(request).execute()
      response.isSuccessful
    } catch (_: Exception) {
      false
    }
  }

  /**
   * Generates formatted CSV content matching the exact format:
   * nama murid,tahun,markah
   */
  fun generateCsvContent(records: List<StudentRecordEntity>): String {
    val sb = StringBuilder()
    sb.append(CSV_HEADER).append("\n")
    for (r in records) {
      // Escape commas in student name or year if any
      val safeName = r.studentName.replace("\"", "\"\"")
      val formattedName = if (safeName.contains(",")) "\"$safeName\"" else safeName
      val safeYear = r.studentYear.replace("\"", "\"\"")
      val formattedYear = if (safeYear.contains(",")) "\"$safeYear\"" else safeYear
      sb.append("$formattedName,$formattedYear,${r.score}\n")
    }
    return sb.toString()
  }
}

package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.StudentRecordEntity
import com.example.data.model.ArabicNumbersData
import com.example.data.repository.StudentRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Nombor Arab Ceria", appName)
  }

  @Test
  fun `verify arabic numbers 1 to 10 are defined`() {
    val numbers = ArabicNumbersData.numbers
    assertEquals(10, numbers.size)
    assertEquals("١", numbers[0].arabicDigit)
    assertEquals("Wahid", numbers[0].transliteration)
    assertEquals("١٠", numbers[9].arabicDigit)
    assertEquals("'Asharah", numbers[9].transliteration)
  }

  @Test
  fun `verify csv format matches nama murid,tahun,markah`() {
    val sampleRecords = listOf(
      StudentRecordEntity(
        studentName = "Ahmad Rayyan",
        studentYear = "Prasekolah (6 Tahun)",
        score = 100,
        maxScore = 150,
        starsEarned = 3,
        gameMode = "Kuiz Padanan",
        totalQuestions = 5,
        correctCount = 5,
        formattedDate = "08/10/2026 10:00"
      )
    )
    val sb = StringBuilder()
    sb.append(StudentRepository.CSV_HEADER).append("\n")
    for (r in sampleRecords) {
      sb.append("${r.studentName},${r.studentYear},${r.score}\n")
    }
    val csv = sb.toString()
    assertTrue(csv.startsWith("nama murid,tahun,markah\n"))
    assertTrue(csv.contains("Ahmad Rayyan,Prasekolah (6 Tahun),100"))
  }
}

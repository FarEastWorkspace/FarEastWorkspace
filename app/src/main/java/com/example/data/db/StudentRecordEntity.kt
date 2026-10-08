package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_records")
data class StudentRecordEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val studentName: String,
  val studentYear: String,
  val score: Int,
  val maxScore: Int,
  val starsEarned: Int,
  val gameMode: String,
  val totalQuestions: Int,
  val correctCount: Int,
  val timestamp: Long = System.currentTimeMillis(),
  val formattedDate: String,
  val syncedToSheets: Boolean = false
)

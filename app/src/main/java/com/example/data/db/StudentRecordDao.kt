package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentRecordDao {

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertRecord(record: StudentRecordEntity): Long

  @Query("SELECT * FROM student_records ORDER BY timestamp DESC")
  fun getAllRecords(): Flow<List<StudentRecordEntity>>

  @Query("SELECT * FROM student_records ORDER BY score DESC, starsEarned DESC, timestamp DESC LIMIT 50")
  fun getLeaderboard(): Flow<List<StudentRecordEntity>>

  @Query("SELECT MAX(score) FROM student_records WHERE studentName = :name")
  suspend fun getHighScoreForStudent(name: String): Int?

  @Query("UPDATE student_records SET syncedToSheets = :synced WHERE id = :id")
  suspend fun markSynced(id: Long, synced: Boolean)

  @Query("DELETE FROM student_records WHERE id = :id")
  suspend fun deleteRecord(id: Long)

  @Query("DELETE FROM student_records")
  suspend fun clearAllRecords()
}

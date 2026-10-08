package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CardPastelBlue
import com.example.ui.theme.CardPastelGreen
import com.example.ui.theme.CardPastelOrange
import com.example.ui.theme.CardPastelPink
import com.example.ui.theme.CardPastelPurple
import com.example.ui.theme.CardPastelYellow

data class ArabicNumber(
  val number: Int,
  val arabicDigit: String,
  val arabicWord: String,
  val transliteration: String,
  val malayMeaning: String,
  val emoji: String,
  val objectNameMs: String,
  val cardColor: Color,
  val accentColor: Color
)

object ArabicNumbersData {
  val numbers: List<ArabicNumber> = listOf(
    ArabicNumber(
      number = 1,
      arabicDigit = "١",
      arabicWord = "وَاحِدٌ",
      transliteration = "Wahid",
      malayMeaning = "Satu",
      emoji = "🍎",
      objectNameMs = "1 Epal Merah",
      cardColor = CardPastelPink,
      accentColor = Color(0xFFE91E63)
    ),
    ArabicNumber(
      number = 2,
      arabicDigit = "٢",
      arabicWord = "اِثْنَانِ",
      transliteration = "Ithnan",
      malayMeaning = "Dua",
      emoji = "⭐",
      objectNameMs = "2 Bintang Bersinar",
      cardColor = CardPastelYellow,
      accentColor = Color(0xFFF57F17)
    ),
    ArabicNumber(
      number = 3,
      arabicDigit = "٣",
      arabicWord = "ثَلَاثَةٌ",
      transliteration = "Thalathah",
      malayMeaning = "Tiga",
      emoji = "🐱",
      objectNameMs = "3 Anak Kucing",
      cardColor = CardPastelOrange,
      accentColor = Color(0xFFFF6F00)
    ),
    ArabicNumber(
      number = 4,
      arabicDigit = "٤",
      arabicWord = "أَرْبَعَةٌ",
      transliteration = "Arba'ah",
      malayMeaning = "Empat",
      emoji = "🌸",
      objectNameMs = "4 Bunga Mekar",
      cardColor = CardPastelPink,
      accentColor = Color(0xFFD81B60)
    ),
    ArabicNumber(
      number = 5,
      arabicDigit = "٥",
      arabicWord = "خَمْسَةٌ",
      transliteration = "Khamsah",
      malayMeaning = "Lima",
      emoji = "🎈",
      objectNameMs = "5 Belon Ceria",
      cardColor = CardPastelBlue,
      accentColor = Color(0xFF1E88E5)
    ),
    ArabicNumber(
      number = 6,
      arabicDigit = "٦",
      arabicWord = "سِتَّةٌ",
      transliteration = "Sittah",
      malayMeaning = "Enam",
      emoji = "🦋",
      objectNameMs = "6 Rama-Rama",
      cardColor = CardPastelPurple,
      accentColor = Color(0xFF8E24AA)
    ),
    ArabicNumber(
      number = 7,
      arabicDigit = "٧",
      arabicWord = "سَبْعَةٌ",
      transliteration = "Sab'ah",
      malayMeaning = "Tujuh",
      emoji = "🐠",
      objectNameMs = "7 Ikan Berenang",
      cardColor = CardPastelGreen,
      accentColor = Color(0xFF2E7D32)
    ),
    ArabicNumber(
      number = 8,
      arabicDigit = "٨",
      arabicWord = "ثَمَانِيَةٌ",
      transliteration = "Thamaniyah",
      malayMeaning = "Lapan",
      emoji = "🍬",
      objectNameMs = "8 Gula-Gula Manis",
      cardColor = CardPastelOrange,
      accentColor = Color(0xFFE65100)
    ),
    ArabicNumber(
      number = 9,
      arabicDigit = "٩",
      arabicWord = "تِسْعَةٌ",
      transliteration = "Tis'ah",
      malayMeaning = "Sembilan",
      emoji = "✏️",
      objectNameMs = "9 Pensel Warna",
      cardColor = CardPastelBlue,
      accentColor = Color(0xFF0288D1)
    ),
    ArabicNumber(
      number = 10,
      arabicDigit = "١٠",
      arabicWord = "عَشَرَةٌ",
      transliteration = "'Asharah",
      malayMeaning = "Sepuluh",
      emoji = "🍓",
      objectNameMs = "10 Strawberi Manis",
      cardColor = CardPastelPink,
      accentColor = Color(0xFFC2185B)
    )
  )

  fun getByNumber(num: Int): ArabicNumber {
    return numbers.firstOrNull { it.number == num } ?: numbers[0]
  }
}

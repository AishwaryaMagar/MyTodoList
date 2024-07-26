package com.example.myto_dolist

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {

    fun getCurrentDateString(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

    fun getStartAndEndOfDay(date: String): Pair<String, String> {
        return Pair(date + " 00:00:00", date + " 23:59:59")
    }
}
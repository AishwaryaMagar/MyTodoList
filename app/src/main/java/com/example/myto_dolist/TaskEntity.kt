package com.example.myto_dolist


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "task-table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var task: String = "",
    var category: String = "All",
    var date: String = DateUtils.getCurrentDateString(), // default to current date in "yyyy-MM-dd" format
    var isCompleted: Boolean = false
)

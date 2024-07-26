package com.example.myto_dolist

import android.app.Application


class TodoApp: Application(){
    val db by lazy {
        taskDatabase.getInstance(this)
    }
}
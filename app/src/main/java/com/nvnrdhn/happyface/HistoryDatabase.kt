package com.nvnrdhn.happyface

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Model.History::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}
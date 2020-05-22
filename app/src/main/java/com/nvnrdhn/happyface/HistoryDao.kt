package com.nvnrdhn.happyface

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Insert
    fun insertHistory(history: Model.History)
    @Query("SELECT * FROM History ORDER BY id DESC LIMIT 30")
    fun getHistory(): List<Model.History>
}
package com.waterreminder.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_entries")
data class WaterEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    @ColumnInfo(name = "amount_ml")
    val amountMl: Int,
    
    @ColumnInfo(name = "note")
    val note: String? = null,
    
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    
    @ColumnInfo(name = "date")
    val date: String // format: yyyy-MM-dd
) {
    init {
        require(amountMl in 50..2000) { "Amount must be between 50 and 2000 ml" }
        require(note == null || note.length <= 200) { "Note must be max 200 characters" }
    }
}

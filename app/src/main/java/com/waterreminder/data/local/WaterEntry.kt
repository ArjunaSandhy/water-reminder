package com.waterreminder.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_entry")
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
    val date: String // Format: yyyy-MM-dd
) {
    init {
        require(amountMl in 50..2000) {
            "Amount must be between 50ml and 2000ml"
        }
        require(note == null || note.length <= 200) {
            "Note must not exceed 200 characters"
        }
    }
}

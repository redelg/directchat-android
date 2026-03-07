package com.codergang.chatdirecto.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatDB (
    val timestamp: Long,
    val number: String,
    val formattedNumber: String,
    val numberWithoutCode: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
){
    override fun equals(other: Any?): Boolean {
        if (other is ChatDB) {
            return other.timestamp == timestamp && other.number == number && other.id == id
        }
        return false
    }

    override fun hashCode(): Int {
        var result = timestamp.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + id
        return result
    }
}
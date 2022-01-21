package com.codergang.directchat.data.entity

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
            return other == timestamp && other == number && other == id
        }
        return false
    }
}
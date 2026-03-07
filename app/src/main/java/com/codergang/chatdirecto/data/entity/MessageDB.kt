package com.codergang.chatdirecto.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MessageDB (
    @PrimaryKey(autoGenerate = true) val id: Int,
    var title: String,
    var content: String,
    @ColumnInfo(defaultValue = "0")
    var isFavorite: Boolean = false,
    @ColumnInfo(defaultValue = "1")
    var categoryId: Int = 1
)
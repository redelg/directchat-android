package com.codergang.directchat.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MessageDB (
    @PrimaryKey(autoGenerate = true) val id: Int,
    var title: String,
    var content: String
)
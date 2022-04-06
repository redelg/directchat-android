package com.codergang.directchat.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.data.entity.MessageDB

@Dao
interface MessageDAO {

    @Query("SELECT * FROM MESSAGEDB")
    fun getAll(): LiveData<List<MessageDB>>
    @Query("SELECT * FROM MESSAGEDB WHERE id = :id")
    suspend fun get(id: Int): MessageDB
    @Insert
    suspend fun insert(message: MessageDB): Long
    @Update
    suspend fun update(message: MessageDB): Int
    @Delete
    suspend fun delete(message: MessageDB)

}
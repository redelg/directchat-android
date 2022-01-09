package com.codergang.directchat.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.codergang.directchat.data.entity.ChatDB

@Dao
interface ChatDAO {

    @Insert
    suspend fun insert(chatDB: ChatDB)

    @Delete
    suspend fun delete(chatDB: ChatDB)

    @Query("DELETE FROM ChatDB")
    suspend fun deleteAll()

    @Query("SELECT * FROM CHATDB")
    fun observeAll(): LiveData<List<ChatDB>>

}
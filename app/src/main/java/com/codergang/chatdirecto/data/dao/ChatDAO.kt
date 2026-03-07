package com.codergang.chatdirecto.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.codergang.chatdirecto.data.entity.ChatDB
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDAO {

    @Insert
    suspend fun insert(chatDB: ChatDB)

    @Delete
    suspend fun delete(chatDB: ChatDB)

    @Query("DELETE FROM ChatDB")
    suspend fun deleteAll()

    @Query("SELECT * FROM CHATDB ORDER BY timestamp DESC LIMIT 70")
    fun observeAll(): Flow<List<ChatDB>>

}
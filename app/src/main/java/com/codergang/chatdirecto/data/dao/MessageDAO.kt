package com.codergang.chatdirecto.data.dao

import androidx.room.*
import com.codergang.chatdirecto.data.entity.MessageDB
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDAO {

    @Query("SELECT * FROM MESSAGEDB")
    fun getAll(): Flow<List<MessageDB>>
    @Query("SELECT * FROM MESSAGEDB WHERE id = :id")
    suspend fun get(id: Int): MessageDB
    @Query("SELECT COUNT(*) FROM MESSAGEDB WHERE isFavorite = 1")
    suspend fun favoriteCount(): Int
    @Insert
    suspend fun insert(message: MessageDB): Long
    @Update
    suspend fun update(message: MessageDB): Int
    @Query("SELECT * FROM MESSAGEDB WHERE categoryId = :categoryId")
    fun getByCategory(categoryId: Int): Flow<List<MessageDB>>
    @Query("UPDATE MESSAGEDB SET categoryId = 1 WHERE categoryId = :categoryId")
    suspend fun resetCategoryToGeneral(categoryId: Int)
    @Delete
    suspend fun delete(message: MessageDB)

}
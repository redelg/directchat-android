package com.codergang.chatdirecto.data.dao

import androidx.room.*
import com.codergang.chatdirecto.data.entity.CategoryDB
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO {

    @Query("SELECT * FROM CategoryDB ORDER BY displayOrder ASC")
    fun getAll(): Flow<List<CategoryDB>>

    @Insert
    suspend fun insert(category: CategoryDB): Long

    @Update
    suspend fun update(category: CategoryDB)

    @Delete
    suspend fun delete(category: CategoryDB)

    @Query("SELECT MAX(displayOrder) FROM CategoryDB")
    suspend fun maxDisplayOrder(): Int?
}

package com.codergang.directchat.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.codergang.directchat.R
import com.codergang.directchat.data.dao.ChatDAO
import com.codergang.directchat.data.entity.ChatDB

@Database(entities = [ChatDB::class], version = 1, exportSchema = false)
abstract class ChatDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDAO

    companion object {
        private  var instancia: ChatDatabase? = null
        @Synchronized
        fun getInstancia(context: Context): ChatDatabase {
            if (instancia == null) {
                instancia = Room.databaseBuilder(context, ChatDatabase::class.java,context.getString(
                    R.string.database_name))
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instancia as ChatDatabase
        }
    }

}
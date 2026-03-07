package com.codergang.chatdirecto.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codergang.chatdirecto.R
import com.codergang.chatdirecto.data.dao.CategoryDAO
import com.codergang.chatdirecto.data.dao.ChatDAO
import com.codergang.chatdirecto.data.dao.MessageDAO
import com.codergang.chatdirecto.data.entity.CategoryDB
import com.codergang.chatdirecto.data.entity.ChatDB
import com.codergang.chatdirecto.data.entity.MessageDB

@Database(entities = [ChatDB::class, MessageDB::class, CategoryDB::class], version = 5, exportSchema = false)
abstract class ChatDatabase: RoomDatabase() {

    abstract fun chatDao(): ChatDAO
    abstract fun messageDao(): MessageDAO
    abstract fun categoryDao(): CategoryDAO

    companion object {
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE MessageDB ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS CategoryDB (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                        "name TEXT NOT NULL, " +
                        "isDefault INTEGER NOT NULL DEFAULT 0, " +
                        "displayOrder INTEGER NOT NULL DEFAULT 0)"
                )
                db.execSQL("INSERT INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (1, 'General', 1, 0)")
                db.execSQL("INSERT INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (2, 'Business', 1, 1)")
                db.execSQL("INSERT INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (3, 'Personal', 1, 2)")
                db.execSQL("INSERT INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (4, 'Sales', 1, 3)")
                db.execSQL("INSERT INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (5, 'Support', 1, 4)")
                db.execSQL("ALTER TABLE MessageDB ADD COLUMN categoryId INTEGER NOT NULL DEFAULT 1")
            }
        }

        private var instancia: ChatDatabase? = null
        @Synchronized
        fun getInstancia(context: Context): ChatDatabase {
            if (instancia == null) {
                instancia = Room.databaseBuilder(context, ChatDatabase::class.java, context.getString(
                    R.string.database_name))
                    .addMigrations(MIGRATION_3_4, MIGRATION_4_5)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            db.execSQL("INSERT OR IGNORE INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (1, 'General', 1, 0)")
                            db.execSQL("INSERT OR IGNORE INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (2, 'Business', 1, 1)")
                            db.execSQL("INSERT OR IGNORE INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (3, 'Personal', 1, 2)")
                            db.execSQL("INSERT OR IGNORE INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (4, 'Sales', 1, 3)")
                            db.execSQL("INSERT OR IGNORE INTO CategoryDB (id, name, isDefault, displayOrder) VALUES (5, 'Support', 1, 4)")
                        }
                    })
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
            }
            return instancia as ChatDatabase
        }
    }
}
package com.codergang.directchat.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codergang.directchat.data.ChatDatabase
import com.codergang.directchat.data.entity.ChatDB
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database: ChatDatabase = ChatDatabase.getInstancia(application)
    val chats = database.chatDao().observeAll()

    fun deleteItem(item: ChatDB) = viewModelScope.launch {
        database.chatDao().delete(item)
    }

}

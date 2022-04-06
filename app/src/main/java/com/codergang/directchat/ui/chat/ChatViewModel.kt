package com.codergang.directchat.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codergang.directchat.data.ChatDatabase
import com.codergang.directchat.data.entity.ChatDB
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val database: ChatDatabase = ChatDatabase.getInstancia(application)

    fun saveChat(chat: ChatDB) = viewModelScope.launch {
        database.chatDao().insert(chat)
    }

}
package com.codergang.directchat.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.codergang.directchat.data.ChatDatabase

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val database: ChatDatabase = ChatDatabase.getInstancia(application)
    val chats = database.chatDao().observeAll()

}

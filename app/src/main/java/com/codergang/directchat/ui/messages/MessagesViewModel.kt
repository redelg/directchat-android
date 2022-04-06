package com.codergang.directchat.ui.messages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codergang.directchat.data.ChatDatabase
import com.codergang.directchat.data.entity.ChatDB
import com.codergang.directchat.data.entity.MessageDB
import kotlinx.coroutines.launch

class MessagesViewModel(application: Application) : AndroidViewModel(application) {

    private val database: ChatDatabase = ChatDatabase.getInstancia(application)

    val messages = database.messageDao().getAll()

    val message = MutableLiveData<MessageDB>()
    val saved = MutableLiveData<Boolean>()
    val deleted = MutableLiveData<Boolean>()


    fun getMessage(id: Int) = viewModelScope.launch {
        message.postValue(database.messageDao().get(id))
    }

    fun saveMessage(message: MessageDB) = viewModelScope.launch {
        database.messageDao().insert(message)
        saved.postValue(true)
    }

    fun editMessage(message: MessageDB) = viewModelScope.launch {
        database.messageDao().update(message)
        saved.postValue(true)
    }

    fun deleteMessage(message: MessageDB) = viewModelScope.launch {
        database.messageDao().delete(message)
        deleted.postValue(true)
    }

}
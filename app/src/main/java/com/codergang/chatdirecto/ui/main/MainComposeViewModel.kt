package com.codergang.chatdirecto.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.codergang.chatdirecto.data.ChatDatabase
import com.codergang.chatdirecto.data.ChatRepository
import com.codergang.chatdirecto.data.entity.CategoryDB
import com.codergang.chatdirecto.data.entity.ChatDB
import com.codergang.chatdirecto.data.entity.MessageDB
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainComposeViewModel(application: Application) : AndroidViewModel(application) {

    private val database: ChatDatabase = ChatDatabase.getInstancia(application)
    private val repository: ChatRepository = ChatRepository(
        database.chatDao(), database.messageDao(), database.categoryDao()
    )

    val chats: StateFlow<List<ChatDB>> = repository.allChats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val messages: StateFlow<List<MessageDB>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryDB>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategoryId = MutableStateFlow<Int?>(null)
    val selectedCategoryId: StateFlow<Int?> = _selectedCategoryId.asStateFlow()

    fun selectCategory(id: Int?) {
        _selectedCategoryId.value = id
    }

    fun saveChat(numberWithCode: String, formattedNumber: String, numberWithoutCode: String) {
        viewModelScope.launch {
            repository.insertChat(
                ChatDB(
                    timestamp = System.currentTimeMillis(),
                    number = numberWithCode,
                    formattedNumber = formattedNumber,
                    numberWithoutCode = numberWithoutCode
                )
            )
        }
    }

    fun deleteChat(chat: ChatDB) {
        viewModelScope.launch {
            repository.deleteChat(chat)
        }
    }

    fun saveMessage(title: String, content: String, categoryId: Int = 1) {
        viewModelScope.launch {
            repository.insertMessage(MessageDB(0, title, content, categoryId = categoryId))
        }
    }

    fun updateMessage(message: MessageDB, title: String, content: String, categoryId: Int = message.categoryId) {
        viewModelScope.launch {
            message.title = title
            message.content = content
            message.categoryId = categoryId
            repository.updateMessage(message)
        }
    }

    fun deleteMessage(message: MessageDB) {
        viewModelScope.launch {
            repository.deleteMessage(message)
        }
    }

    fun toggleFavorite(message: MessageDB, isPro: Boolean, onLimitReached: () -> Unit) {
        viewModelScope.launch {
            if (!message.isFavorite && !isPro && repository.favoriteCount() >= FREE_FAVORITE_LIMIT) {
                onLimitReached()
                return@launch
            }
            repository.toggleFavorite(message)
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            val nextOrder = repository.maxCategoryOrder() + 1
            repository.insertCategory(CategoryDB(name = name, isDefault = false, displayOrder = nextOrder))
        }
    }

    fun renameCategory(category: CategoryDB, newName: String) {
        viewModelScope.launch {
            repository.updateCategory(category.copy(name = newName))
        }
    }

    fun deleteCategory(category: CategoryDB) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            if (_selectedCategoryId.value == category.id) {
                _selectedCategoryId.value = null
            }
        }
    }

    companion object {
        const val FREE_FAVORITE_LIMIT = 1
    }
}

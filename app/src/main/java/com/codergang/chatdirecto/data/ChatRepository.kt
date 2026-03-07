package com.codergang.chatdirecto.data

import com.codergang.chatdirecto.data.dao.CategoryDAO
import com.codergang.chatdirecto.data.dao.ChatDAO
import com.codergang.chatdirecto.data.dao.MessageDAO
import com.codergang.chatdirecto.data.entity.CategoryDB
import com.codergang.chatdirecto.data.entity.ChatDB
import com.codergang.chatdirecto.data.entity.MessageDB
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val chatDao: ChatDAO,
    private val messageDao: MessageDAO,
    private val categoryDao: CategoryDAO
) {

    val allChats: Flow<List<ChatDB>> = chatDao.observeAll()
    val allMessages: Flow<List<MessageDB>> = messageDao.getAll()
    val allCategories: Flow<List<CategoryDB>> = categoryDao.getAll()

    suspend fun insertChat(chat: ChatDB) {
        chatDao.insert(chat)
    }

    suspend fun deleteChat(chat: ChatDB) {
        chatDao.delete(chat)
    }

    suspend fun insertMessage(message: MessageDB) {
        messageDao.insert(message)
    }

    suspend fun updateMessage(message: MessageDB) {
        messageDao.update(message)
    }

    suspend fun deleteMessage(message: MessageDB) {
        messageDao.delete(message)
    }

    suspend fun favoriteCount(): Int {
        return messageDao.favoriteCount()
    }

    suspend fun toggleFavorite(message: MessageDB) {
        message.isFavorite = !message.isFavorite
        messageDao.update(message)
    }

    fun messagesByCategory(categoryId: Int): Flow<List<MessageDB>> {
        return messageDao.getByCategory(categoryId)
    }

    suspend fun resetMessagesToGeneral(categoryId: Int) {
        messageDao.resetCategoryToGeneral(categoryId)
    }

    suspend fun insertCategory(category: CategoryDB): Long {
        return categoryDao.insert(category)
    }

    suspend fun updateCategory(category: CategoryDB) {
        categoryDao.update(category)
    }

    suspend fun deleteCategory(category: CategoryDB) {
        resetMessagesToGeneral(category.id)
        categoryDao.delete(category)
    }

    suspend fun maxCategoryOrder(): Int {
        return categoryDao.maxDisplayOrder() ?: 0
    }
}

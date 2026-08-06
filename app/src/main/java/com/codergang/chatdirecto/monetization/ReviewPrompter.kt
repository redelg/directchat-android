package com.codergang.chatdirecto.monetization

import android.app.Activity
import android.content.Context
import androidx.core.content.edit
import com.google.android.play.core.review.ReviewManagerFactory

/**
 * Pide la reseña in-app justo después de abrir un chat con éxito (momento de tarea
 * completada). Google decide si la tarjeta se muestra realmente (cuota ~1/mes por
 * usuario), así que se puede llamar en cada apertura sin spamear.
 */
object ReviewPrompter {

    private const val PREFERENCES_NAME = "PREF_REVIEW"
    private const val KEY_CHAT_OPEN_COUNT = "KEY_CHAT_OPEN_COUNT"
    private const val MIN_OPENS_BEFORE_PROMPT = 2

    fun onChatOpened(activity: Activity) {
        val prefs = activity.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        val count = prefs.getInt(KEY_CHAT_OPEN_COUNT, 0) + 1
        prefs.edit { putInt(KEY_CHAT_OPEN_COUNT, count) }
        if (count < MIN_OPENS_BEFORE_PROMPT) return

        val manager = ReviewManagerFactory.create(activity)
        manager.requestReviewFlow().addOnCompleteListener { request ->
            if (request.isSuccessful && !activity.isFinishing && !activity.isDestroyed) {
                manager.launchReviewFlow(activity, request.result)
            }
        }
    }
}

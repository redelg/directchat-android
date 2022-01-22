package com.codergang.directchat.data.preferences

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson

object UserPreferences {

    private const val PREFERENCE_NAME = "PREF_USUARIO"
    private const val CLASS_NAME = "USUARIO"
    private val gson = Gson()

    fun get(context: Context): Usuario? {
        val sharedPref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        val data = sharedPref.getString(CLASS_NAME, null)
        return if(data == null){
            null
        }else {
            gson.fromJson(sharedPref.getString(CLASS_NAME, ""), Usuario::class.java)
        }
    }

    fun set(context: Context, usuario: Usuario){
        val sharedPref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        sharedPref.edit(commit = true) {
            putString(CLASS_NAME, gson.toJson(usuario))
        }
    }

    fun clear(context: Context){
        val sharedPref = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        sharedPref.edit(commit = true) {
            clear()
        }
    }

}
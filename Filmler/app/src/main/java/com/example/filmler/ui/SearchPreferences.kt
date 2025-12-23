package com.example.filmler.ui

import android.content.Context
import android.content.SharedPreferences

class SearchPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("film_app_prefs", Context.MODE_PRIVATE)
    private val KEY_HISTORY = "search_history_v2"

    fun getSearchHistory(): List<String> {
        val historyString = prefs.getString(KEY_HISTORY, "") ?: ""
        if (historyString.isBlank()) return emptyList()

        return historyString.split("|").filter { it.isNotBlank() }
    }

    fun addSearchTerm(term: String) {
        if (term.isBlank()) return

        val currentList = getSearchHistory().toMutableList()

        if (currentList.contains(term)) {
            currentList.remove(term)
        }

        currentList.add(0, term)

        val limitedList = currentList.take(5)

        val newString = limitedList.joinToString("|")


        prefs.edit().putString(KEY_HISTORY, newString).commit()
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).commit()
    }
}
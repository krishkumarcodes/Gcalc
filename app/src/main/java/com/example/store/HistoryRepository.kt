package com.example.store

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class PersistentHistoryItem(
    val id: String,
    val expression: String,
    val result: String,
    val timestamp: String,
    val isFavorite: Boolean = false
)

class HistoryRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("gcalc_history_store", Context.MODE_PRIVATE)

    val items = mutableStateListOf<PersistentHistoryItem>()

    init {
        migrateFromLegacyStore(context)
        loadFromDisk()
    }

    private fun migrateFromLegacyStore(context: Context) {
        val oldPrefs = context.getSharedPreferences("glasscalc_history_store", Context.MODE_PRIVATE)
        if (oldPrefs.contains(KEY_HISTORY)) {
            val oldData = oldPrefs.getString(KEY_HISTORY, null)
            if (!oldData.isNullOrEmpty() && !prefs.contains(KEY_HISTORY)) {
                prefs.edit().putString(KEY_HISTORY, oldData).apply()
            }
            oldPrefs.edit().clear().apply()
        }
    }

    fun loadFromDisk() {
        items.clear()
        val jsonStr = prefs.getString(KEY_HISTORY, null) ?: return
        try {
            val jsonArray = JSONArray(jsonStr)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                items.add(
                    PersistentHistoryItem(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        expression = obj.getString("expression"),
                        result = obj.getString("result"),
                        timestamp = obj.optString("timestamp", "12:00 PM"),
                        isFavorite = obj.optBoolean("isFavorite", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveToDisk() {
        val jsonArray = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("expression", item.expression)
            obj.put("result", item.result)
            obj.put("timestamp", item.timestamp)
            obj.put("isFavorite", item.isFavorite)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, jsonArray.toString()).apply()
    }

    fun addHistoryItem(expression: String, result: String, numericResult: Double? = null) {
        addCalculation(expression, result)
    }

    fun addCalculation(expression: String, result: String) {
        if (expression.isEmpty() || result.isEmpty() || result.contains("Error") || result.contains("Invalid") || result.contains("Can't divide")) {
            return
        }

        // Avoid duplicate consecutive saves
        val first = items.firstOrNull()
        if (first != null && first.expression == expression && first.result == result) {
            return
        }

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val newItem = PersistentHistoryItem(
            id = UUID.randomUUID().toString(),
            expression = expression,
            result = result,
            timestamp = sdf.format(Date()),
            isFavorite = false
        )

        items.add(0, newItem)
        saveToDisk()
    }

    fun deleteItem(id: String) {
        items.removeAll { it.id == id }
        saveToDisk()
    }

    fun toggleFavorite(id: String) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx != -1) {
            val item = items[idx]
            items[idx] = item.copy(isFavorite = !item.isFavorite)
            saveToDisk()
        }
    }

    fun clearAll(keepStarred: Boolean = false) {
        if (keepStarred) {
            items.retainAll { it.isFavorite }
        } else {
            items.clear()
        }
        saveToDisk()
    }

    fun restoreList(backupList: List<PersistentHistoryItem>) {
        items.clear()
        items.addAll(backupList)
        saveToDisk()
    }

    companion object {
        private const val KEY_HISTORY = "persistent_history_data"
    }
}

package com.thindie.avezer.application.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.Flow
import com.thindie.avezer.engine.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

internal class StorageImpl(private val persistence: SharedPreferences) : Storage {
  private val state = MutableStateFlow<List<StorageId>?>(null)

  init {
    loadKeys()
  }

  override suspend fun read(id: StorageId): String? {
    return try {
      val value = persistence.getString(id.value, null)
      Log.d("[avezer]: StorageImpl", "Read successful for ID ${id.value}. Value: $value")
      value
    } catch (e: Exception) {
      Log.e("[avezer]: StorageImpl", "Error reading storage for ID ${id.value}", e)
      null
    }
  }

  override suspend fun write(value: String) {
    try {
      val key = UUID.randomUUID().toString()
      persistence.edit { putString(ID_KEY + key, value) }
      Log.d("[avezer]: StorageImpl", "Write successful. Key: ${ID_KEY + key}")
      loadKeys()
    } catch (e: Exception) {
      Log.e("[avezer]: StorageImpl", "Error writing storage.", e)
    }
  }

  override suspend fun delete(id: StorageId) {
    try {
      persistence.edit { remove(ID_KEY + id.value) }
      Log.d("[avezer]: StorageImpl", "Delete successful for ID ${id.value}")
      loadKeys()
    } catch (e: Exception) {
      Log.e("[avezer]: StorageImpl", "Error deleting storage for ID ${id.value}", e)
    }
  }

  override val saved: Flow<List<StorageId>?>
    get() = state

  private fun loadKeys() {
    try {
      val keys =
        persistence.all
          .also {
            Log.d("[avezer]: StorageImpl", "whole keys: $it")
          }
          .filterKeys { it.startsWith(ID_KEY) }
          .keys
          .mapNotNull { key ->
            val id = key.removePrefix(ID_KEY)
            StorageId(id)
          }
      Log.d("[avezer]: StorageImpl", "Successfully loaded keys: $keys")
      state.update { keys.toList().ifEmpty { null } }
    } catch (e: Exception) {
      Log.e("[avezer]: StorageImpl", "Error loading storage keys.", e)
      // Optionally reset state or handle failure gracefully here
      state.update { null }
    }
  }
}

private const val ID_KEY = "id_key_###"

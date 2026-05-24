package com.thindie.avezer.application.storage

import android.content.SharedPreferences
import androidx.core.content.edit
import com.thindie.avezer.engine.Log
import kotlinx.coroutines.flow.Flow
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
      val key = ID_KEY + id.value
      val value = persistence.getString(key, null)
      if (value != null) {
        Log.d({ "Read successful for ID ${id.value}. Value found." })
      } else {
        Log.w({ "Read attempt successful for ID ${id.value}, but value is null (key might be missing)." })
      }
      value
    } catch (e: Exception) {
      Log.e({ "Error reading storage for ID ${id.value}" }, throwable = e)
      null
    }
  }

  override suspend fun write(value: String) {
    try {
      val key = UUID.randomUUID().toString()
      persistence.edit { putString(ID_KEY + key, value) }
      Log.d({ "Write successful. Key: ${ID_KEY + key}" })
      loadKeys()
    } catch (e: Exception) {
      Log.e({ "Error writing storage." }, throwable = e)
    }
  }

  override suspend fun delete(id: StorageId) {
    try {
      persistence.edit { remove(ID_KEY + id.value) }
      Log.d({ "Delete successful for ID ${id.value}" })
      loadKeys()
    } catch (e: Exception) {
      Log.e({ "Error deleting storage for ID ${id.value}" }, throwable = e)
    }
  }

  override val saved: Flow<List<StorageId>?>
    get() = state

  private fun loadKeys() {
    try {
      val keys =
        persistence.all
          .also { Log.d({ "whole keys: $it" }) }
          .filterKeys { it.startsWith(ID_KEY) }
          .keys
          .mapNotNull { key ->
            val id = key.removePrefix(ID_KEY)
            StorageId(id)
          }
      Log.d({ "Successfully loaded keys: $keys" })
      state.update { keys.toList().ifEmpty { null } }
    } catch (e: Exception) {
      Log.e({ "Error loading storage keys." }, throwable = e)
      state.update { null }
    }
  }
}

private const val ID_KEY = "id_key_###"

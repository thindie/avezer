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
        Log.w(
          {
            "Read attempt successful for ID ${id.value}, but value is null (key might be missing)."
          },
        )
      }
      value
    } catch (e: Exception) {
      Log.e({ "Error reading storage for ID ${id.value}" }, throwable = e)
      null
    }
  }

  @Deprecated("Use createOrUpdate(id,value) for deterministic keys")
  override suspend fun write(value: String) {
    internalWrite(UUID.randomUUID().toString(), value)
  }

  /** Create or replace an entry with the supplied id. */
  override suspend fun createOrUpdate(
    id: StorageId,
    value: String,
  ) {
    internalWrite(id.value, value)
  }

  private suspend fun internalWrite(
    keySuffix: String,
    value: String,
  ) {
    try {
      val key = ID_KEY + keySuffix
      persistence.edit { putString(key, value) }
      Log.d({ "Write successful. Key: $key" })
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
          .mapNotNull { key -> StorageId(key.removePrefix(ID_KEY)) }
      Log.d({ "Successfully loaded keys: $keys" })
      state.update { if (keys.isEmpty()) null else keys.toList() }
    } catch (e: Exception) {
      Log.e({ "Error loading storage keys." }, throwable = e)
      state.update { null }
    }
  }
}

private const val ID_KEY = "id_key_###"

package com.thindie.avezer.application.storage

import android.content.SharedPreferences
import androidx.core.content.edit
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
    return persistence.getString(id.value, null)
  }

  override suspend fun write(value: String) {
    val key = UUID.randomUUID().toString()
    persistence.edit { putString(ID_KEY + key, value) }
    loadKeys()
  }

  override suspend fun delete(id: StorageId) {
    persistence.edit { remove(ID_KEY + id.value) }
    loadKeys()
  }

  override val saved: Flow<List<StorageId>?>
    get() = state

  private fun loadKeys() {
    val keys =
      persistence.all
        .filterKeys { it.startsWith(ID_KEY) }
        .keys
        .mapNotNull { key ->
          val id = key.removePrefix(ID_KEY)
          StorageId(id)
        }

    state.update { keys.toList().ifEmpty { null } }
  }
}

private const val ID_KEY = "id_key_###"

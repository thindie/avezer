package com.thindie.avezer.application.storage

import kotlinx.coroutines.flow.Flow

interface Storage {
  suspend fun read(id: StorageId): String?
  suspend fun write(value: String)
  suspend fun delete(id: StorageId)
  val saved: Flow<List<StorageId>?>
}

@JvmInline
value class StorageId(val value: String)

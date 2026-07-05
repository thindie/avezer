package com.thindie.avezer.application.cache

import com.thindie.avezer.engine.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

internal class CacheExpirationManagerImpl(
) : CacheExpirationManager {
  private val timestamps = MutableStateFlow<Map<String, Instant>>(emptyMap())

  override suspend fun isExpired(key: String): Boolean {
    val lastUpdated = timestamps.value[key] ?: return true
    val ttlSeconds = DEFAULT_TTL_SECONDS
    val expiredAt = lastUpdated.plus(ttlSeconds.toLong().seconds)
    return Clock.System.now() >= expiredAt
  }

  override fun touch(key: String) {
    timestamps.value = timestamps.value + (key to Clock.System.now())
    Log.d({ "Cache touched for key: $key" })
  }

  override suspend fun invalidate(key: String) {
    timestamps.value = timestamps.value - key
    Log.d({ "Cache invalidated for key: $key" })
  }

  override suspend fun timeToLiveMs(key: String): Long? {
    val lastUpdated = timestamps.value[key] ?: return null
    val ttlSeconds = DEFAULT_TTL_SECONDS
    val expiredAt = lastUpdated.plus(ttlSeconds.toLong().seconds)
    val remaining = expiredAt.toEpochMilliseconds() - Clock.System.now().toEpochMilliseconds()
    return if (remaining > 0) remaining else 0L
  }

  override suspend fun invalidateAll() {
    timestamps.value = emptyMap()
    Log.d({ "All caches invalidated" })
  }

  override suspend fun invalidateAllWeatherKeys() {
    val weatherKeys = timestamps.value.keys.filter { it.startsWith("id_key_###weather") }
    weatherKeys.forEach { invalidate(it) }
    Log.d({ "All weather caches invalidated: ${weatherKeys.size} keys" })
  }

  companion object {
    private const val DEFAULT_TTL_SECONDS = 300
  }
}

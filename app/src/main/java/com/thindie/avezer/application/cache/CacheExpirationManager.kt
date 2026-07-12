package com.thindie.avezer.application.cache

interface CacheExpirationManager {
  /** Check if cache is expired for the given key */
  suspend fun isExpired(key: String): Boolean

  /** Update last-updated timestamp for the given key */
  fun touch(key: String)

  /** Force-invalidate cache by key */
  suspend fun invalidate(key: String)

  /** Get remaining time-to-live in milliseconds, or null if not cached */
  suspend fun timeToLiveMs(key: String): Long?

  /** Force-invalidate all caches */
  suspend fun invalidateAll()

  /** Invalidate all weather-related caches */
  suspend fun invalidateAllWeatherKeys()
}

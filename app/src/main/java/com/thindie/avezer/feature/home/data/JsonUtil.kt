package com.thindie.avezer.feature.home.data

import com.google.gson.Gson

object JsonUtil {
  private val gson = Gson()

  fun toJson(src: Any?): String = gson.toJson(src)

  fun <T> fromJson(
    src: String,
    cls: Class<T>,
  ): T? = gson.fromJson(src, cls)
}

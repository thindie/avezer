package com.thindie.avezer.feature.home.data

import com.google.gson.Gson
import java.lang.reflect.Type

object JsonUtil {
  private val gson = Gson()

  fun toJson(src: Any?): String = gson.toJson(src)

  fun <T> fromJson(
    src: String,
    cls: Class<T>,
  ): T? = gson.fromJson(src, cls)

  fun <T> fromJson(
    src: String,
    type: Type,
  ): T? = gson.fromJson(src, type)
}

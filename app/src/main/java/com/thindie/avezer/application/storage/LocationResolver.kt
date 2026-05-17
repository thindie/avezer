package com.thindie.avezer.application.storage

interface LocationResolver {
  suspend  fun read(name: String): Pair<Double, Double>?
  suspend fun read(lat: Double, lon: Double): Address?
}

data class Address(
  val name: String,
  val area: String?
)
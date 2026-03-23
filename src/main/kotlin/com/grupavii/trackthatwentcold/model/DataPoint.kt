package com.grupavii.trackthatwentcold.model

import java.time.Instant

data class DataPoint(
    val id: String,
    val timestamp: Instant,
    val sensorId: String,
    val value: Double,
    val metadata: String
)

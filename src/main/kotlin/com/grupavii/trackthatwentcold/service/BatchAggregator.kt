package com.grupavii.trackthatwentcold.service

import com.grupavii.trackthatwentcold.model.DataPoint
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class BatchAggregator {

    companion object {
        const val DEFAULT_BATCH_SIZE = 20_000
        private val PADDING = "X".repeat(1_000)
    }

    fun generateBatch(size: Int = DEFAULT_BATCH_SIZE): List<DataPoint> =
        (1..size).map { i ->
            DataPoint(
                id = UUID.randomUUID().toString(),
                timestamp = Instant.now(),
                sensorId = "sensor-${i % 100}",
                value = i * 0.1,
                metadata = PADDING
            )
        }
}

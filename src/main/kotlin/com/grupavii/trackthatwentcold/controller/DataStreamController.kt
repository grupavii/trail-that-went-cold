package com.grupavii.trackthatwentcold.controller

import com.grupavii.trackthatwentcold.model.DataPoint
import com.grupavii.trackthatwentcold.service.BatchAggregator
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class DataStreamController(
    private val aggregator: BatchAggregator
) {

    @MessageMapping("data.stream")
    fun streamData(size: Int): List<DataPoint> {
        return aggregator.generateBatch(size)
    }
}

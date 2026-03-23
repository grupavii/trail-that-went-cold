package com.grupavii.trackthatwentcold.controller

import com.grupavii.trackthatwentcold.model.DataPoint
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller

@Controller
class DataStreamController {

    @MessageMapping("data.stream")
    fun receiveStream(batch: List<DataPoint>): String {
        return "Received ${batch.size} data points"
    }
}

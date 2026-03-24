package com.grupavii.trackthatwentcold.controller

import com.grupavii.trackthatwentcold.client.DataStreamClient
import org.springframework.core.env.Environment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class TriggerController(
    private val client: DataStreamClient,
    private val environment: Environment
) {

    @PostMapping("/trigger-batch")
    fun triggerBatch(): Mono<String> {
        val port = environment.getProperty("local.server.port")?.toInt()
            ?: throw IllegalStateException("Server port not available")
        return client.requestBatch(port)
            .map { batch -> "Received ${batch.size} data points" }
    }
}

package com.grupavii.trackthatwentcold.client

import com.grupavii.trackthatwentcold.model.DataPoint
import io.rsocket.transport.netty.client.WebsocketClientTransport
import org.springframework.core.ParameterizedTypeReference
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@Component
class DataStreamClient(
    private val requesterBuilder: RSocketRequester.Builder
) {

    fun requestBatch(port: Int, size: Int = DEFAULT_BATCH_SIZE): Mono<List<DataPoint>> {
        val transport = createTransport(port)
        val requester = requesterBuilder.transport(transport)
        return requester
            .route("data.stream")
            .data(size)
            .retrieveMono(object : ParameterizedTypeReference<List<DataPoint>>() {})
    }

    companion object {
        const val DEFAULT_BATCH_SIZE = 20_000

        fun createTransport(port: Int): WebsocketClientTransport {
            val httpClient = HttpClient.create()
                .host("localhost")
                .port(port)
            return WebsocketClientTransport.create(httpClient, "/rsocket")
        }
    }
}

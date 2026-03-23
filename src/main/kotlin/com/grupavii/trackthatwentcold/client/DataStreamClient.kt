package com.grupavii.trackthatwentcold.client

import com.grupavii.trackthatwentcold.model.DataPoint
import io.rsocket.transport.netty.client.WebsocketClientTransport
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient

@Component
class DataStreamClient(
    private val requesterBuilder: RSocketRequester.Builder
) {

    fun sendBatch(port: Int, batch: List<DataPoint>): Mono<String> {
        val transport = createTransport(port)
        val requester = requesterBuilder.transport(transport)
        return requester
            .route("data.stream")
            .data(batch)
            .retrieveMono(String::class.java)
    }

    companion object {
        fun createTransport(port: Int): WebsocketClientTransport {
            val httpClient = HttpClient.create()
                .host("localhost")
                .port(port)
            return WebsocketClientTransport.create(httpClient, "/rsocket")
        }
    }
}

package com.grupavii.trackthatwentcold

import com.grupavii.trackthatwentcold.service.BatchAggregator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("broken")
class SmallBatchTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var requesterBuilder: RSocketRequester.Builder

    @Test
    fun `small batch succeeds even without fragmentation`() {
        val requester = requesterBuilder
            .websocket(URI.create("ws://localhost:$port/rsocket"))

        val smallBatch = BatchAggregator().generateBatch(5) // ~5KB, well under 16MB

        StepVerifier.create(
            requester.route("data.stream")
                .data(smallBatch)
                .retrieveMono(String::class.java)
        )
            .expectNext("Received ${smallBatch.size} data points")
            .verifyComplete()
    }
}

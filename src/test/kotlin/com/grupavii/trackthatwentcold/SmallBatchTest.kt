package com.grupavii.trackthatwentcold

import com.grupavii.trackthatwentcold.model.DataPoint
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
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

        StepVerifier.create(
            requester.route("data.stream")
                .data(5)
                .retrieveMono(object : ParameterizedTypeReference<List<DataPoint>>() {})
        )
            .expectNextMatches { it.size == 5 }
            .verifyComplete()
    }
}

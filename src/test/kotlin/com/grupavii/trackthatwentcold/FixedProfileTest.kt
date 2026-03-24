package com.grupavii.trackthatwentcold

import com.grupavii.trackthatwentcold.client.DataStreamClient
import com.grupavii.trackthatwentcold.model.DataPoint
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("fixed")
class FixedProfileTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var requesterBuilder: RSocketRequester.Builder

    @Test
    fun `large batch succeeds with manual fragmentation config`() {
        val transport = DataStreamClient.createTransport(port)
        val requester = requesterBuilder.transport(transport)

        StepVerifier.create(
            requester.route("data.stream")
                .data(20_000)
                .retrieveMono(object : ParameterizedTypeReference<List<DataPoint>>() {})
        )
            .expectNextMatches { it.size == 20_000 }
            .verifyComplete()
    }
}

package com.grupavii.trackthatwentcold

import com.grupavii.trackthatwentcold.client.DataStreamClient
import com.grupavii.trackthatwentcold.service.BatchAggregator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
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

        val batch = BatchAggregator().generateBatch()

        StepVerifier.create(
            requester.route("data.stream")
                .data(batch)
                .retrieveMono(String::class.java)
        )
            .expectNext("Received ${batch.size} data points")
            .verifyComplete()
    }
}

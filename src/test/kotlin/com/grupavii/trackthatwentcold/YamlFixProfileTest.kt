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
import java.time.Duration

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("yaml-fix")
class YamlFixProfileTest {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var requesterBuilder: RSocketRequester.Builder

    @Test
    fun `large batch still fails despite YAML fragmentation config`() {
        val transport = DataStreamClient.createTransport(port)
        val requester = requesterBuilder.transport(transport)

        val batch = BatchAggregator().generateBatch()

        StepVerifier.create(
            requester.route("data.stream")
                .data(batch)
                .retrieveMono(String::class.java)
        )
            .expectError()
            .verify(Duration.ofSeconds(30))
    }
}

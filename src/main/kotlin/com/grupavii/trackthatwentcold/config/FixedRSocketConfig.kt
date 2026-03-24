package com.grupavii.trackthatwentcold.config

import org.springframework.boot.rsocket.server.RSocketServerCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.messaging.rsocket.RSocketConnectorConfigurer

@Configuration
@Profile("fixed")
class FixedRSocketConfig {

    companion object {
        const val FRAGMENT_SIZE = 1024 * 1024 // 1MB fragments
    }

    @Bean
    fun rSocketServerCustomizer() = RSocketServerCustomizer { server ->
        server.fragment(FRAGMENT_SIZE)
    }


}

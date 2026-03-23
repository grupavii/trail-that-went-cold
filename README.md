# The Trail That Went Cold — RSocket Fragmentation Demo

Companion project for [The Trail That Went Cold](https://grupavii.eu/blog/trail-that-went-cold) blog post. Reproduces a production bug where Spring Boot's RSocket fragmentation configuration is silently ignored when using WebSocket transport.

## The Bug

RSocket uses a 24-bit field for frame length, capping a single frame at ~16MB. Payloads larger than this need fragmentation. Spring Boot provides a YAML property for this:

```yaml
spring:
  rsocket:
    server:
      fragment-size: 1MB
```

This config **parses without error**, IDE autocomplete works, and the property is stored — but it is **never applied** when using WebSocket transport. The configuration class that reads it (`EmbeddedServerConfiguration`) is gated behind `@ConditionalOnProperty("spring.rsocket.server.port")`, which is not set when RSocket shares the HTTP port via WebSocket.

## Profiles

The project uses three Spring profiles to demonstrate each stage of the debugging journey:

| Profile | What happens | Why |
|---------|-------------|-----|
| `broken` | Large payloads fail silently | No fragmentation configured — default behavior |
| `yaml-fix` | Large payloads still fail | YAML `fragment-size` property is silently ignored |
| `fixed` | Large payloads succeed | Programmatic `RSocketServerCustomizer` + `RSocketConnectorConfigurer` beans bypass autoconfiguration |

## Running

### Tests

```bash
./gradlew test
```

Runs all three profile tests plus a small-batch baseline:

- **BrokenProfileTest** — sends ~20MB batch, expects RSocket error
- **YamlFixProfileTest** — sends ~20MB batch, expects same error despite YAML config
- **FixedProfileTest** — sends ~20MB batch, expects success
- **SmallBatchTest** — sends 5 records, succeeds without fragmentation

### Manual exploration

```bash
# Run with the "fixed" profile (fragmentation enabled):
./gradlew bootRun --args='--spring.profiles.active=fixed'

# Trigger a large batch:
curl -X POST http://localhost:8080/trigger-batch

# Run with "broken" or "yaml-fix" to see the failure:
./gradlew bootRun --args='--spring.profiles.active=broken'
```

## The Fix

Instead of relying on YAML properties, configure fragmentation programmatically on both server and client sides:

```kotlin
@Configuration
@Profile("fixed")
class FixedRSocketConfig {

    @Bean
    fun rSocketServerCustomizer() = RSocketServerCustomizer { server ->
        server.fragment(1024 * 1024) // server-side fragmentation
    }

    @Bean
    fun rSocketConnectorConfigurer() = RSocketConnectorConfigurer { connector ->
        connector.fragment(1024 * 1024) // client-side fragmentation
    }
}
```

## Spring Boot 4.x WebSocket Frame Size Quirk

When upgrading from Spring Boot 3.x to 4.x, a second issue appears. The `rsocket-transport-netty` library sets the WebSocket `maxFramePayloadLength` to 16,777,215 bytes (the 24-bit protocol max) by default. However, Spring Boot 4.x's `WebFluxServerConfiguration` creates its own `WebsocketServerSpec` that **overwrites** this default with reactor-netty's 65KB default.

This means payloads over 65KB fail at the WebSocket transport layer before RSocket even sees them — regardless of fragmentation config.

The workaround is to restore the original default in `application.properties`:

```properties
spring.rsocket.server.spec.max-frame-payload-length=16777215
```

This property is read by `WebFluxServerConfiguration` (not gated behind `@ConditionalOnProperty`), so it actually applies in WebSocket transport mode.

On the client side, using `WebsocketClientTransport.create(...)` directly (instead of `RSocketRequester.Builder.websocket(URI)`) preserves the transport's built-in 16MB default.

## Tech Stack

- Spring Boot 4.0.4
- Kotlin 2.2.21
- RSocket over WebSocket
- Gradle 9.4

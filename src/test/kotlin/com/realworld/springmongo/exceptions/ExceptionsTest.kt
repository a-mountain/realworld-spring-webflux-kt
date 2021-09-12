package com.realworld.springmongo.exceptions

import com.realworld.springmongo.validation.LocaleConfigurer
import com.realworld.springmongo.validation.NotBlankOrNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@WebFluxTest(
    controllers = [ExceptionsTest.Controller::class],
    excludeAutoConfiguration = [ReactiveSecurityAutoConfiguration::class]
)
@Import(LocaleConfigurer::class, ExceptionsTest.Controller::class)
internal class ExceptionsTest(
    @Autowired val client: WebTestClient,
) {

    @Test
    fun `should format InvalidRequestException`() {
        client.get()
            .uri("/error")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody<String>()
            .isEqualTo("{\"errors\":{\"Username\":[\"already in use\"]}}")
    }

    @Test
    fun `should format email validation error`() {
        val dto = TestDto(
            name = null,
            email = "frostnext2gmail.com"
        )
        client.post()
            .uri("/validation")
            .bodyValue(dto)
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody<String>()
            .isEqualTo("{\"errors\":{\"email\":[\"must be a well-formed email address\"]}}")
    }

    @Test
    fun `should format not blank validation error`() {
        val dto = TestDto(
            name = "   ",
            email = "frostnext2@gmail.com"
        )
        client.post()
            .uri("/validation")
            .bodyValue(dto)
            .exchange()
            .expectStatus()
            .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)
            .expectBody<String>()
            .isEqualTo("{\"errors\":{\"name\":[\"must not be blank\"]}}")
    }

    @RestController
    class Controller {
        @GetMapping("/error")
        fun error() {
            throw InvalidRequestException("Username", "already in use")
        }

        @PostMapping("/validation")
        fun validation(@RequestBody @Valid testDTO: TestDto) {
        }
    }

    data class TestDto(
        @field:NotBlankOrNull
        val name: String?,
        @field:Email @field:NotBlank
        val email: String,
    )
}
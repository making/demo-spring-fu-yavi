package com.example.demospringfuyavi

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

class DemoSpringFuYaviApplicationTests {

    private val client = WebTestClient.bindToServer().baseUrl("http://localhost:8181").build()

    private lateinit var context: ConfigurableApplicationContext

    @BeforeAll
    fun beforeAll() {
        context = app.run(profiles = "test")
    }

    @Test
    fun `Request root endpoint`() {
        client.post().uri("/")
                .syncBody(Message("hey"))
                .exchange()
                .expectStatus().isOk
                .expectBody<Message>().isEqualTo(Message("hey"))
    }

    @Test
    fun `Text is too long`() {
        client.post().uri("/")
                .syncBody(Message("Hello"))
                .exchange()
                .expectStatus().isBadRequest
                .expectBody<Map<String, List<Map<String, String>>>>()
                .consumeWith { res ->
                    Assertions.assertEquals(
                            "The size of \"text\" must be less than or equal to 3. The given size is 5",
                            res.responseBody!!.getValue("details")[0]["defaultMessage"])
                }
    }

    @AfterAll
    fun afterAll() {
        context.close()
    }

}

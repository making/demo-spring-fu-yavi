package com.example.demospringfuyavi

import am.ik.yavi.builder.ValidatorBuilder
import am.ik.yavi.builder.constraint
import org.springframework.boot.WebApplicationType
import org.springframework.fu.kofu.application
import org.springframework.fu.kofu.webflux.webFlux

val app = application(WebApplicationType.REACTIVE) {
    webFlux {
        port = if (profiles.contains("test")) 8181 else 8080
        router {
            POST("/") { req ->
                req.bodyToMono(Message::class.java)
                        .flatMap { message ->
                            Message.validator.validateToEither(message)
                                    .leftMap { mapOf("details" to it.details()) }
                                    .fold(badRequest()::syncBody, ok()::syncBody)
                        }
            }
        }
        codecs {
            jackson()
        }
    }
}

data class Message(
        val text: String
) {
    companion object {
        val validator = ValidatorBuilder.of<Message>()
                .constraint(Message::text, {
                    notBlank().lessThanOrEqual(3)
                })
                .build()
    }
}

fun main() {
    app.run()
}

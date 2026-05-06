package com.back

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class RestApiApplication

fun main(args: Array<String>) {
    runApplication<RestApiApplication>(*args)
}

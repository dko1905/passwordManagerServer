package io.github.dko1905.passwordManagerServer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PasswordManagerServerApplication

fun main(args: Array<String>) {
	runApplication<PasswordManagerServerApplication>(*args)
}

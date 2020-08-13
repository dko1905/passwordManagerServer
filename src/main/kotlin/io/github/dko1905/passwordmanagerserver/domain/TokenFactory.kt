package io.github.dko1905.passwordmanagerserver.domain

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class TokenFactory(private val lifetime: Long) {
	fun createToken(userid: Long): Token {
		return Token(userid, UUID.randomUUID(), Instant.now().plusSeconds(lifetime).epochSecond)
	}
}
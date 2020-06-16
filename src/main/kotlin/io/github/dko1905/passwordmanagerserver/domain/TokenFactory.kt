package io.github.dko1905.passwordmanagerserver.domain

import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class TokenFactory {
    private val currentTime = ZonedDateTime.now( ZoneId.of("UTC") )

    fun createToken(userid: Long, lifetime: Long): Token {
        return Token(userid, UUID.randomUUID(), currentTime.plusSeconds(lifetime).toEpochSecond());
    }
}
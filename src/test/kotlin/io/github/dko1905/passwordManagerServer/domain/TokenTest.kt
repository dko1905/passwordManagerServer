package io.github.dko1905.passwordManagerServer.domain

import io.github.dko1905.passwordManagerServer.domain.Token
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneOffset
import java.util.*

class TokenTest {
	@Test
	fun `Test token without USERID`() {
		val uuid = UUID.randomUUID()
		val exp = Instant.now().atZone(ZoneOffset.UTC).toEpochSecond()

		val token = Token(null, uuid, exp)

		Assertions.assertEquals(uuid, token.UUID)
		Assertions.assertEquals(exp, token.EXP)
		Assertions.assertNull(token.USERID)
	}

	@Test
	fun `Test token with USERID`() {
		val userid: Long = 101
		val uuid = UUID.randomUUID()
		val exp = Instant.now().atZone(ZoneOffset.UTC).toEpochSecond()

		val token = Token(userid, uuid, exp)

		Assertions.assertEquals(uuid, token.UUID)
		Assertions.assertEquals(exp, token.EXP)
		Assertions.assertEquals(userid, token.USERID)
	}
}
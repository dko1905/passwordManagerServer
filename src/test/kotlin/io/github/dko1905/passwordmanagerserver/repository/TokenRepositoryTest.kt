package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Token
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.junit.jupiter.api.Assertions
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TokenRepositoryTest(@Autowired private val accountRepository: TokenRepository) {


	@Test
	fun `replace token 1 and then check if it succeeded with getToken ACCOUNTID)`(){
		val random = Random()
		val token = Token(1, UUID.randomUUID(), random.nextLong())

		accountRepository.replaceToken(token)

		val token2 = accountRepository.getToken(token.USERID!!)

		Assertions.assertNotNull(token2)

		Assertions.assertEquals(token.USERID!!, token2!!.USERID)
		Assertions.assertTrue(token.UUID == token2.UUID)
		Assertions.assertEquals(token.EXP, token2.EXP)
	}

	@Test
	fun `replace token 2 and then check if it succeeded with getToken UUID`(){
		val random = Random()
		val token = Token(2, UUID.randomUUID(), random.nextLong())

		accountRepository.replaceToken(token)

		val token2 = accountRepository.getToken(token.UUID)

		Assertions.assertNotNull(token2)

		Assertions.assertEquals(token.USERID!!, token2!!.USERID)
		Assertions.assertTrue(token.UUID == token2.UUID)
		Assertions.assertEquals(token.EXP, token2.EXP)
	}
}
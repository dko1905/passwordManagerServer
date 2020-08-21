package io.github.dko1905.passwordManagerServer.domain

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AccountTest {
	@Test
	fun `Test Account without ID`() {
		val username = "Test1234"
		val passwordHash = "cool1234"
		val role = AccountRole.USER

		val account = Account(null, username, passwordHash, role)

		Assertions.assertEquals(username, account.USERNAME)
		Assertions.assertEquals(passwordHash, account.HASH)
		Assertions.assertEquals(role, account.ACCOUNTROLE)
		Assertions.assertNull(account.ID)
	}

	@Test
	fun `Test Account with ID`() {
		val id: Long = 102
		val username = "Test123"
		val passwordHash = "cool123"
		val role = AccountRole.USER

		val account = Account(id, username, passwordHash, role)

		Assertions.assertEquals(username, account.USERNAME)
		Assertions.assertEquals(passwordHash, account.HASH)
		Assertions.assertEquals(role, account.ACCOUNTROLE)
		Assertions.assertEquals(id, account.ID)
	}
}
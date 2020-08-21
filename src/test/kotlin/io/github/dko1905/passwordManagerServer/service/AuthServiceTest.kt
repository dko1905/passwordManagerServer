package io.github.dko1905.passwordManagerServer.service

import io.github.dko1905.passwordManagerServer.domain.Account
import io.github.dko1905.passwordManagerServer.domain.AccountRole
import io.github.dko1905.passwordManagerServer.domain.Token
import io.github.dko1905.passwordManagerServer.domain.TokenFactory
import io.github.dko1905.passwordManagerServer.repository.AccountRepository
import io.github.dko1905.passwordManagerServer.repository.TokenRepository
import io.github.dko1905.passwordManagerServer.service.AuthService
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import io.github.dko1905.passwordManagerServer.domain.AccessDeniedException
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest(
		@Autowired private val authService: AuthService,
		@Autowired private val accountRepository: AccountRepository,
		@Autowired private val tokenRepository: TokenRepository,
		@Autowired private val env: Environment
) {
	private val lifetime = env.getProperty("token.lifetime")!!.toLong()
	private val minlifetime = env.getProperty("token.minlifetime")!!.toLong()
	private val tokenFactory = TokenFactory(lifetime)
	private val random = Random()

	private lateinit var adminAccount: Account
	private lateinit var adminToken: Token
	private lateinit var userAccount: Account
	private lateinit var userToken: Token
	private val toAddAccount = Account(
			null,
			"toAdd-${random.nextInt()}",
			"toAdd-Hash-${random.nextInt()}",
			AccountRole.USER
	)

	private fun createGetToken(accountId: Long): Token {
		val token2 = tokenFactory.createToken(accountId)
		tokenRepository.replaceToken(token2)
		return token2
	}

	@BeforeAll
	fun init() {
		adminAccount = Account(
				null,
				"cool username-${random.nextInt()}",
				"cool password-${random.nextInt()}",
				AccountRole.ADMIN
		)
		adminAccount.ID = accountRepository.addAccount(adminAccount)
		adminToken = createGetToken(adminAccount.ID!!)
		userAccount = Account(
				null,
				"user name-${random.nextInt()}",
				"user pass-${random.nextInt()}",
				AccountRole.USER
		)
		userAccount.ID = accountRepository.addAccount(userAccount)
		userToken = createGetToken(userAccount.ID!!)
	}

	@AfterAll
	fun deinit() {
		accountRepository.removeAccount(adminAccount.ID!!)
		accountRepository.removeAccount(userAccount.ID!!)
	}

	@Test
	fun `Test creating account`() {
		var caught = false
		try {
			authService.addAccount(userToken, toAddAccount)
		} catch (e: AccessDeniedException) {
			caught = true
		}
		Assertions.assertTrue(caught)

		caught = false
		try {
			authService.addAccount(Token(1, UUID.randomUUID(), 0), toAddAccount)
		} catch (e: AccessDeniedException) {
			caught = true
		}
		Assertions.assertTrue(caught)

		authService.addAccount(adminToken, toAddAccount)

		toAddAccount.ID = accountRepository.getAccount(toAddAccount.USERNAME, toAddAccount.HASH)!!.ID
		Assertions.assertNotNull(
				toAddAccount.ID
		)

		accountRepository.removeAccount(toAddAccount.ID!!)
	}

	@Test
	fun `Test getting all accounts`() {
		var caught = false
		try {
			authService.getAccounts(userToken)
		} catch (e: AccessDeniedException) {
			caught = true
		}
		Assertions.assertTrue(caught)

		caught = false
		try {
			authService.getAccounts(Token(1, UUID.randomUUID(), 0))
		} catch (e: AccessDeniedException) {
			caught = true
		}
		Assertions.assertTrue(caught)

		val accounts = authService.getAccounts(adminToken)

		Assertions.assertNotNull(
				accounts
		)

		Assertions.assertTrue(accounts.size > 0)
	}

	@Test
	fun `Test login using account`() {
		authService.addAccount(adminToken, toAddAccount)
		toAddAccount.ID = accountRepository.getAccount(toAddAccount.USERNAME, toAddAccount.HASH)!!.ID

		val oGToken = createGetToken(toAddAccount.ID!!)
		val token = authService.login(toAddAccount.USERNAME, toAddAccount.HASH)

		Assertions.assertNotNull(token)
		Assertions.assertEquals(oGToken.USERID!!, token!!.USERID)
		Assertions.assertEquals(oGToken.UUID, token.UUID)
		Assertions.assertEquals(oGToken.EXP, token.EXP)

		accountRepository.removeAccount(toAddAccount.ID!!)
	}

	@Test
	fun `Test deleting account`() {
		authService.addAccount(adminToken, toAddAccount)
		toAddAccount.ID = accountRepository.getAccount(toAddAccount.USERNAME, toAddAccount.HASH)!!.ID

		var caught = false
		try {
			authService.deleteAccount(userToken, toAddAccount.ID!!)
		} catch (e: AccessDeniedException) {
			caught = true
		}
		Assertions.assertTrue(caught)

		caught = false
		try {
			authService.deleteAccount(Token(1, UUID.randomUUID(), 0), toAddAccount.ID!!)
		} catch (e: AccessDeniedException) {
			caught = true
		}
		Assertions.assertTrue(caught)

		Assertions.assertNotNull(
				accountRepository.getAccount(toAddAccount.USERNAME, toAddAccount.HASH)
		)

		authService.deleteAccount(adminToken, toAddAccount.ID!!)

		Assertions.assertNull(
				accountRepository.getAccount(toAddAccount.USERNAME, toAddAccount.HASH)
		)
	}
}
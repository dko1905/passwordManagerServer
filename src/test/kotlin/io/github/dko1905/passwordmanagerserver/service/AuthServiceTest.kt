package io.github.dko1905.passwordmanagerserver.service

import io.github.dko1905.passwordmanagerserver.domain.Account
import io.github.dko1905.passwordmanagerserver.domain.AccountRole
import io.github.dko1905.passwordmanagerserver.repository.AccountRepository
import io.github.dko1905.passwordmanagerserver.repository.TokenRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.env.Environment
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthServiceTest (
        @Autowired private val authService: AuthService,
        @Autowired private val accountRepository: AccountRepository,
        @Autowired private val tokenRepository: TokenRepository,
        @Autowired private val env: Environment
) {
    private val minlifetime: Long = env.getProperty("token.minlifetime")!!.toLong()
    private val random = Random()

    @Test
    fun `Create new account using repo, and then check login, then check token with token repo `(){
        val username = "username-${random.nextInt()}"
        val password = "password-${random.nextInt()}"
        val account = Account(null, username, password, AccountRole.USER)

        Assertions.assertNull(authService.login(username, password))

        account.ID = accountRepository.addAccount(account)

        val token = authService.login(username, password)
        Assertions.assertNotNull(token)
        Assertions.assertEquals(account.ID!!, token!!.USERID)

        val token2 = tokenRepository.getToken(account.ID!!)

        Assertions.assertEquals(token.USERID!!, token2!!.USERID)
        Assertions.assertTrue(token.UUID == token2.UUID)
        Assertions.assertEquals(token.EXP, token2.EXP)

        Assertions.assertTimeout(Duration.ofSeconds(minlifetime + 2)) {
            TimeUnit.SECONDS.sleep(minlifetime + 1)
            val token3 = authService.login(username, password)
            Assertions.assertNotNull(token)
            Assertions.assertEquals(account.ID!!, token.USERID!!)

            Assertions.assertFalse(token.UUID == token3!!.UUID)
            Assertions.assertNotEquals(token.EXP, token3.EXP)
        }
    }
}
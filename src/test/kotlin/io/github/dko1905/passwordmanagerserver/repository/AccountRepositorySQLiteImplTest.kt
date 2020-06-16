package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Account
import io.github.dko1905.passwordmanagerserver.domain.AccountRole
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AccountRepositorySQLiteImplTest(@Autowired private val accountRepository: AccountRepository) {
    @Test
    fun `add account to database`(){
        val accountId = accountRepository.addAccount(Account(null, "Cool Username", "Cool password hash", AccountRole.USER));
        println(accountId);
    }
}
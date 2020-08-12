package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Account
import io.github.dko1905.passwordmanagerserver.domain.AccountRole
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.platform.commons.logging.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.lang.RuntimeException
import kotlin.random.Random

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountRepositoryTest(@Autowired private val accountRepository: AccountRepository) {
	private val idsAdded: ArrayList<Long> = ArrayList()

	@Test
	fun `add account to database, and then get the account by the id`(){
		val account1 = Account(null, "Cool Username " + Random.nextInt(), "cool password hash " + Random.nextInt(), AccountRole.USER)
		val accountId = accountRepository.addAccount(account1)
		account1.ID = accountId

		val account2 = accountRepository.getAccount(account1.ID!!)
		Assertions.assertEquals(account1.ID!!, account2!!.ID)
		Assertions.assertEquals(account1.USERNAME, account2.USERNAME)
		Assertions.assertEquals(account1.HASH, account2.HASH)
		Assertions.assertEquals(account1.ACCOUNTROLE, account2.ACCOUNTROLE)

		idsAdded.add(accountId)
	}

	@Test
	fun `add 3 accounts to the database, then replace number 2 and then check all accounts`(){
		val account1 = Account(null, "daniel "+Random.nextBits(8), "hash "+Random.nextBits(8), AccountRole.USER)
		var account2 = Account(null, "axel "+Random.nextBits(8), "hash "+Random.nextBits(8), AccountRole.READONLY)
		val account3 = Account(null, "rasmus "+Random.nextBits(8), "hash "+Random.nextBits(8), AccountRole.ADMIN)

		account1.ID = accountRepository.addAccount(account1)
		account2.ID = accountRepository.addAccount(account2)
		account3.ID = accountRepository.addAccount(account3)

		Assertions.assertNotNull(account1.ID)
		Assertions.assertNotNull(account2.ID)
		Assertions.assertNotNull(account3.ID)

		account2 = Account(account2.ID, "axel "+Random.nextBits(8), "hash2 "+Random.nextBits(8), AccountRole.READONLY)
		accountRepository.replaceAccount(account2)

		val account1Copy = accountRepository.getAccount(account1.ID!!)
		val account2Copy = accountRepository.getAccount(account2.ID!!)
		val account3Copy = accountRepository.getAccount(account3.ID!!)

		Assertions.assertEquals(account1.ID!!, account1Copy!!.ID)
		Assertions.assertEquals(account1.USERNAME, account1Copy.USERNAME)
		Assertions.assertEquals(account1.HASH, account1Copy.HASH)
		Assertions.assertEquals(account1.ACCOUNTROLE, account1Copy.ACCOUNTROLE)

		Assertions.assertEquals(account2.ID!!, account2Copy!!.ID)
		Assertions.assertEquals(account2.USERNAME, account2Copy.USERNAME)
		Assertions.assertEquals(account2.HASH, account2Copy.HASH)
		Assertions.assertEquals(account2.ACCOUNTROLE, account2Copy.ACCOUNTROLE)

		Assertions.assertEquals(account3.ID!!, account3Copy!!.ID)
		Assertions.assertEquals(account3.USERNAME, account3Copy.USERNAME)
		Assertions.assertEquals(account3.HASH, account3Copy.HASH)
		Assertions.assertEquals(account3.ACCOUNTROLE, account3Copy.ACCOUNTROLE)

		idsAdded.add(account1.ID!!)
		idsAdded.add(account2.ID!!)
		idsAdded.add(account3.ID!!)
	}

	@Test
	fun `add two accounts and then test if they are returns in getAccounts`(){
		val account1 = Account(null, "daniel "+Random.nextBits(8), "hash "+Random.nextBits(8), AccountRole.USER)
		val account2 = Account(null, "axel "+Random.nextBits(8), "hash "+Random.nextBits(8), AccountRole.USER)

		account1.ID = accountRepository.addAccount(account1)
		account2.ID = accountRepository.addAccount(account2)

		Assertions.assertNotNull(account1.ID)
		Assertions.assertNotNull(account2.ID)

		val accounts = accountRepository.getAccounts()
		var returned: Account? = null
		for(account in accounts){
			if(account1.ID!! == account.ID!!){
				returned = account
			}
		}
		if(returned != null){
			Assertions.assertEquals(account1.ID!!, returned.ID!!)
			Assertions.assertEquals(account1.USERNAME, returned.USERNAME)
			Assertions.assertEquals(account1.HASH, returned.HASH)
			Assertions.assertEquals(account1.ACCOUNTROLE, returned.ACCOUNTROLE)
		}
		else{
			throw RuntimeException("No result matching returned in test")
		}

		idsAdded.add(account1.ID!!)
		idsAdded.add(account2.ID!!)
	}

	@Test
	fun `add two accounts and then remove the second and then the first`(){
		val account1 = Account(null, "daniel "+Random.nextBits(8), "hash "+Random.nextBits(8), AccountRole.USER)
		val account2 = Account(null, "axel "+Random.nextBits(8), "hash "+Random.nextBits(8), AccountRole.USER)

		account1.ID = accountRepository.addAccount(account1)
		account2.ID = accountRepository.addAccount(account2)

		accountRepository.removeAccount(account1.ID!!)

		val a = accountRepository.getAccount(account1.ID!!)
		Assertions.assertNull(a)
		Assertions.assertNotNull(accountRepository.getAccount(account2.ID!!))

		accountRepository.removeAccount(account2.ID!!)

		Assertions.assertNull(accountRepository.getAccount(account2.ID!!))
	}

	@AfterAll
	fun clearDatabase(){
		for(id in idsAdded){
			accountRepository.removeAccount(id)
		}
	}
}
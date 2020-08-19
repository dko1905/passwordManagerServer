package io.github.dko1905.passwordmanagerserver.service

import io.github.dko1905.passwordmanagerserver.domain.Account
import io.github.dko1905.passwordmanagerserver.domain.AccountRole
import io.github.dko1905.passwordmanagerserver.domain.Token
import io.github.dko1905.passwordmanagerserver.domain.TokenFactory
import io.github.dko1905.passwordmanagerserver.repository.AccountRepository
import io.github.dko1905.passwordmanagerserver.repository.TokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.sql.SQLException
import java.time.Instant
import org.springframework.security.access.AccessDeniedException

@Service
class AuthService(
        @Autowired private val accountRepository: AccountRepository,
        @Autowired private val tokenRepository: TokenRepository,
        @Autowired private val env: Environment
) {
    private val tokenFactory = TokenFactory(env.getProperty("token.lifetime")!!.toLong())
    private val minlifetime: Long = env.getProperty("token.minlifetime")!!.toLong()

	/**
	 * Check token if it is valid
	 * @return false if it is invalid and so on.
	 */
	private fun checkToken(token: Token): Boolean{
		// Check for null
		if(token.USERID == null){
			return false
		}

		// Check if it is expired
		if(Instant.now().isAfter(Instant.ofEpochSecond(token.EXP))){
			return false
		}

		// Check if it exists
		val token2 = tokenRepository.getToken(token.USERID)
		return token2 != null && token2.USERID == token.USERID && token2.EXP == token.EXP && token2.UUID == token2.UUID
	}

    /**
     * Deletes account from db, requires ADMIN role
     * @exception SQLException The normal sql exception, if something is wrong with the db
     * @exception Exception An abnormal exception
     * @exception AccessDeniedException If you are not allowed to view it
     */
    fun deleteAccount(token: Token, otherAccountId: Long) {
        try {
			if(!checkToken(token)){
				throw AccessDeniedException("Token invalid")
			}
            val result = accountRepository.getAccount(token.USERID!!)
            if(result != null){
                if(result.ACCOUNTROLE == AccountRole.ADMIN){
					if(accountRepository.getAccount(otherAccountId) != null){
						accountRepository.removeAccount(otherAccountId)
					} else{
						throw AccessDeniedException("NOT_FOUND")
					}
                } else{
                    throw AccessDeniedException("Account not authorized")
                }
            } else{
                throw AccessDeniedException("Account could not be found")
            }
        } catch (sqlException: SQLException) {
            throw sqlException
        } catch (ade: AccessDeniedException) {
            throw ade
        } catch (e: Exception) {
            throw e
        }
    }

    /**
     * Adds account to db, requires ADMIN role
     * @exception SQLException The normal sql exception, if something is wrong with the db
     * @exception Exception An abnormal exception
     * @exception AccessDeniedException If you are not allowed to view it
     */
    fun addAccount(token: Token, account: Account) {
        try {
			if(!checkToken(token)){
				throw AccessDeniedException("Token invalid")
			}
            val result = accountRepository.getAccount(token.USERID!!)
            if(result != null){
                if(result.ACCOUNTROLE == AccountRole.ADMIN){
                    accountRepository.addAccount(account)
                } else{
                    throw AccessDeniedException("Account not authorized")
                }
            } else{
                throw AccessDeniedException("No account found")
            }
        } catch (sqlException: SQLException) {
            throw sqlException
        } catch (ade: AccessDeniedException) {
            throw ade
        } catch (e: Exception) {
            throw e
        }
    }

	/**
	 * Get array of accounts
	 * @exception SQLException The normal sql exception, if something is wrong with the db
	 * @exception Exception An abnormal exception
	 * @exception AccessDeniedException If you are not allowed to view it
	 */
	fun getAccounts(token: Token): ArrayList<Account> {
		try {
			if(!checkToken(token)){
				throw AccessDeniedException("Token invalid")
			}
			val result = accountRepository.getAccount(token.USERID!!)
			if(result != null){
				if(result.ACCOUNTROLE == AccountRole.ADMIN){
					return accountRepository.getAccounts()
				} else{
					throw AccessDeniedException("Account not authorized")
				}
			} else{
				throw AccessDeniedException("No account found")
			}
		} catch (sqlException: SQLException) {
			throw sqlException
		} catch (ade: AccessDeniedException) {
			throw ade
		} catch (e: Exception) {
			throw e
		}
	}

    /**
     * @return Returns a <code>token</code> or null
     * @exception SQLException The normal sql exception, if something is wrong with the db
     * @exception Exception An abnormal exception
     */
    fun login(username: String, password: String): Token? {
        try {
            val result = accountRepository.getAccount(username, password)
            if (result != null) {
                val token = tokenRepository.getToken(result.ID!!)
                if (token == null) {
                    val replacement = tokenFactory.createToken(result.ID!!)
                    tokenRepository.replaceToken(replacement)
                    return replacement
                } else {
                    val exp = Instant.ofEpochSecond(token.EXP).minusSeconds(minlifetime)
                    val now = Instant.now()
                    if (exp <= now) { // Is too old
                        val replacement = tokenFactory.createToken(result.ID!!)
                        tokenRepository.replaceToken(replacement)
                        return replacement
                    } else {
                        return token
                    }
                }
            } else {
                return null
            }
        } catch (sqlException: SQLException) {
            throw sqlException
        } catch (e: Exception) {
            throw e
        }
    }
}


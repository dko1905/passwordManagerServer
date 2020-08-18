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
import java.util.logging.Logger
import org.springframework.security.access.AccessDeniedException

@Service
class AuthService(
        @Autowired private val accountRepository: AccountRepository,
        @Autowired private val tokenRepository: TokenRepository,
        @Autowired private val env: Environment
) {
    private val logger = Logger.getLogger(AuthService::class.java.name)

    private val tokenFactory = TokenFactory(env.getProperty("token.lifetime")!!.toLong())
    private val minlifetime: Long = env.getProperty("token.minlifetime")!!.toLong()

    /**
     * Deletes account from db, requires ADMIN role
     * @exception SQLException The normal sql exception, if something is wrong with the db
     * @exception Exception An abnormal exception
     * @exception AccessDeniedException If you are not allowed to view it
     */
    fun deleteAccount(token: Token, otherAccountId: Long) {
        try {
            val result = accountRepository.getAccount(token.USERID!!)
            if(result != null){
                if(result.ACCOUNTROLE == AccountRole.ADMIN){
                    accountRepository.removeAccount(otherAccountId)
                } else{
                    throw AccessDeniedException("Account not authorized")
                }
            } else{
                throw AccessDeniedException("No account found")
            }
        } catch (sqlException: SQLException) {
            logger.warning("SQLException thrown in AuthService: ${sqlException.message}")
            throw SQLException(sqlException)
        } catch (ade: AccessDeniedException) {
            logger.info("Access denied thrown in AuthService")
            throw ade
        } catch (e: Exception) {
            logger.warning("Normal Exception thrown in AuthService: ${e.message}")
            throw Exception(e)
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
            logger.warning("SQLException thrown in AuthService: ${sqlException.message}")
            throw SQLException(sqlException)
        } catch (ade: AccessDeniedException) {
            logger.info("Access denied thrown in AuthService")
            throw ade
        } catch (e: Exception) {
            logger.warning("Normal Exception thrown in AuthService: ${e.message}")
            throw Exception(e)
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
			logger.warning("SQLException thrown in AuthService: ${sqlException.message}")
			throw SQLException(sqlException)
		} catch (ade: AccessDeniedException) {
			logger.info("Access denied thrown in AuthService")
			throw ade
		} catch (e: Exception) {
			logger.warning("Normal Exception thrown in AuthService: ${e.message}")
			throw Exception(e)
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
                    val exp = token.EXP - minlifetime
                    val now = Instant.now().epochSecond
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
            logger.warning("SQLException thrown in AuthService: ${sqlException.message}")
            throw SQLException(sqlException)
        } catch (e: Exception) {
            logger.warning("Normal Exception thrown in AuthService: ${e.message}")
            throw Exception(e)
        }
    }
}


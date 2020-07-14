package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Token
import org.springframework.stereotype.Repository
import java.sql.SQLException
import java.util.*

@Repository
interface TokenRepository {
    /**
     * Replaces the token at the given location, if the location does not exist,
     * it will create a new location. The function does not return anything, but
     * it throws an error if something went wrong.
     * @exception SQLException Throws exception if something went wrong
     */
    fun replaceToken(token: Token)

    /**
     * Get the token at the specified userID.
     * @return Returns null if the token is not found, else it returns the token
     * @exception SQLException Throws exception if something went wrong
     */
    fun getToken(userID: Long): Token?

    /**
     * Get the token by searching by the uuid
     * @return Returns null if the token is not found, else it returns the token
     * @exception SQLException Throws exception if something went wrong
     */
    fun getToken(uuid: UUID): Token?
}
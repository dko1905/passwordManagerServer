package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Credential
import org.springframework.stereotype.Repository
import java.sql.SQLException

@Repository
interface CredentialRepository {

    /**
     * Add credential to db
     * @return Returns the id of the newly added credential
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun addCredential(credential: Credential): Long

    /**
     * Remove credential from db
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun removeCredential(id: Long)

    /**
     * Replaces all the data at the specified id
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun replaceCredential(credential: Credential)

    /**
     * Gets the credential at the index
     * @return Returns the credential or null
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun getCredential(id: Long): Credential?

    /**
     * Returns all the credentials in the database
     * @return All the credentials in the database in an <code>ArrayList</code>
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun getCredentials(): ArrayList<Credential>
}
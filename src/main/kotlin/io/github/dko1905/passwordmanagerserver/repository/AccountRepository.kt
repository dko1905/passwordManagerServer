package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Account
import org.springframework.stereotype.Repository
import java.sql.SQLException

@Repository
interface AccountRepository {
    /**
     * Adds an account to the repository
     * @return Returns the id of the account in the table
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun addAccount(account: Account): Long

    /**
     * Removes the account at the specified index
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun removeAccount(id: Long)

    /**
     * Replaces all the data at the specified id
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun replaceAccount(account: Account)

    /**
     * Tries to return the account at the id
     * @return Returns the account or null if it's not found
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun getAccount(id: Long): Account?

    /**
     * Returns all the accounts in the database
     * @return All the accounts in the database in an ArrayList
     * @exception SQLException Throws exception if there was any problems with the database
     */
    fun getAccounts(): ArrayList<Account>
}
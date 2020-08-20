package io.github.dko1905.passwordManagerServer.repository

import io.github.dko1905.passwordManagerServer.domain.Account
import io.github.dko1905.passwordManagerServer.domain.AccountRole
import org.springframework.beans.factory.annotation.Autowired
import java.sql.SQLException
import javax.sql.DataSource

class AccountRepositorySQLiteImpl(@Autowired private val dataSource: DataSource) : AccountRepository {

	override fun addAccount(account: Account): Long {
		dataSource.connection.use { connection ->
			connection.prepareStatement("INSERT INTO ACCOUNT(USERNAME, HASH, ROLE) VALUES(?,?,?);").use { preparedStatement ->
				preparedStatement.setString(1, account.USERNAME)
				preparedStatement.setString(2, account.HASH)
				preparedStatement.setInt(3, account.ACCOUNTROLE.value)
				preparedStatement.execute()
			}
			connection.prepareStatement("SELECT last_insert_rowid();").use { preparedStatement ->
				preparedStatement.executeQuery().use { resultSet ->
					if(resultSet.next()){
						return resultSet.getLong(1)
					}
					else{
						throw SQLException("Not found")
					}
				}
			}
		}
	}

	override fun removeAccount(id: Long) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("DELETE FROM ACCOUNT WHERE ID=?;").use { preparedStatement ->
				preparedStatement.setLong(1, id)
				preparedStatement.execute()
			}
		}
	}

	override fun replaceAccount(account: Account) {
		dataSource.connection.use { connection ->
			connection.prepareStatement("UPDATE ACCOUNT SET USERNAME=?, HASH=?, ROLE=? WHERE ID=?;").use { preparedStatement ->
				preparedStatement.setString(1, account.USERNAME)
				preparedStatement.setString(2, account.HASH)
				preparedStatement.setInt(3, account.ACCOUNTROLE.value)
				preparedStatement.setLong(4, account.ID!!)
				preparedStatement.execute()
			}
		}
	}

	override fun getAccount(id: Long): Account? {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT ID,USERNAME,HASH,ROLE FROM ACCOUNT WHERE ID=?;").use { preparedStatement ->
				preparedStatement.setLong(1, id)
				preparedStatement.executeQuery().use { resultSet ->
					return if(resultSet.next()){
						Account(
								resultSet.getLong(1),
								resultSet.getString(2),
								resultSet.getString(3),
								AccountRole.fromInt(resultSet.getInt(4))
						)
					} else{
						null // Could not find result
					}
				}
			}
		}
	}

	override fun getAccount(username: String, hash: String): Account? {
		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT ID,USERNAME,HASH,ROLE FROM ACCOUNT WHERE USERNAME=? AND HASH=?").use { preparedStatement ->
				preparedStatement.setString(1, username)
				preparedStatement.setString(2, hash)

				preparedStatement.executeQuery().use { resultSet ->
					return if(resultSet.next()){
						Account(
								resultSet.getLong(1),
								resultSet.getString(2),
								resultSet.getString(3),
								AccountRole.fromInt(resultSet.getInt(4))
						)
					} else{
						null // Could not find result
					}
				}
			}
		}
	}

	override fun getAccounts(): ArrayList<Account> {
		val accounts: ArrayList<Account> = ArrayList()

		dataSource.connection.use { connection ->
			connection.prepareStatement("SELECT ID,USERNAME,HASH,ROLE FROM ACCOUNT;").use { preparedStatement ->
				preparedStatement.executeQuery().use { resultSet ->
					while(resultSet.next()){
						val ID = resultSet.getLong(1)
						val USERNAME = resultSet.getString(2)
						val PASSWORD = resultSet.getString(3)
						val ACCOUNTROLE = AccountRole.fromInt(resultSet.getInt(4))
						accounts.add(Account(ID, USERNAME, PASSWORD, ACCOUNTROLE))
					}
				}
			}
		}
		return accounts
	}
}

package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Account
import org.springframework.beans.factory.annotation.Autowired
import java.sql.SQLException
import javax.sql.DataSource

class AccountRepositorySQLiteImpl(@Autowired val dataSource: DataSource) : AccountRepository {

    override fun addAccount(account: Account): Long {
        dataSource.connection.use { connection ->
            connection.prepareStatement("INSERT INTO ACCOUNT(USERNAME, HASH, ROLE) VALUES(?,?,?);").use { preparedStatement ->
                preparedStatement.setString(1, account.USERNAME);
                preparedStatement.setString(2, account.HASH);
                preparedStatement.setInt(3, account.ACCOUNTROLE.value);
                preparedStatement.execute();
            }
            connection.prepareStatement("SELECT ID FROM ACCOUNT WHERE USERNAME=? AND HASH=? AND ROLE=?").use { preparedStatement ->
                preparedStatement.setString(1, account.USERNAME);
                preparedStatement.setString(2, account.HASH);
                preparedStatement.setInt(3, account.ACCOUNTROLE.value);
                preparedStatement.executeQuery().use { resultSet ->
                    if(resultSet.next()){
                        return resultSet.getLong("ID");
                    }
                    else{
                        throw SQLException("Not found");
                    }
                }
            }
        }
    }

    override fun removeAccount(id: Long) {
        TODO("Not yet implemented")
    }

    override fun replaceAccount(account: Account) {
        TODO("Not yet implemented")
    }

    override fun getAccount(id: Long): Account? {
        TODO("Not yet implemented")
    }

    override fun getAccounts(): ArrayList<Account> {
        TODO("Not yet implemented")
    }

}
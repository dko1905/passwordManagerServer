package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Credential
import org.springframework.beans.factory.annotation.Autowired
import java.sql.SQLException
import javax.sql.DataSource

class CredentialRepositorySQLiteImpl(@Autowired private val dataSource: DataSource) : CredentialRepository {
    override fun addCredential(credential: Credential): Long {
        var id: Long? = null
        dataSource.connection.use { connection ->
            connection.prepareStatement("INSERT INTO CREDENTIAL (WEBSITE,USERNAME,PASSWORD,EXTRA) VALUES (?, ?, ?, ?);").use { preparedStatement ->
                preparedStatement.setString(1, credential.URL)
                preparedStatement.setString(2, credential.USERNAME)
                preparedStatement.setString(3, credential.PASSWORD)
                preparedStatement.setString(4, credential.EXTRA)

                preparedStatement.execute()
            }

            connection.prepareStatement("SELECT last_insert_rowid();").use { preparedStatement ->
                preparedStatement.executeQuery().use { resultSet ->
                    if(resultSet.next()){
                        id = resultSet.getLong(1)
                    }
                    else{
                        throw SQLException("ID not returned")
                    }
                }
            }
        }

        if(id == null){
            throw SQLException("Something went wrong")
        }
        else{
            return id as Long
        }
    }

    override fun removeCredential(id: Long) {
        dataSource.connection.use { connection ->
            connection.prepareStatement("DELETE FROM CREDENTIAL WHERE ACCOUNTID=?").use { preparedStatement ->
                preparedStatement.setLong(1, id)

                preparedStatement.execute()
            }
        }
    }

    override fun replaceCredential(credential: Credential) {
        dataSource.connection.use { connection ->
            connection.prepareStatement("UPDATE CREDENTIAL SET WEBSITE=?, USERNAME=?, PASSWORD=?, EXTRA=? WHERE ACCOUNTID=?").use { preparedStatement ->
                preparedStatement.setString(1, credential.URL)
                preparedStatement.setString(2, credential.USERNAME)
                preparedStatement.setString(3, credential.PASSWORD)
                preparedStatement.setString(4, credential.EXTRA)

                preparedStatement.setLong(5, credential.ACCOUNTID!!)

                preparedStatement.execute()
            }
        }
    }

    override fun getCredential(id: Long): Credential? {
        var credential: Credential? = null
        dataSource.connection.use {  connection ->
            connection.prepareStatement("SELECT WEBSITE,USERNAME,PASSWORD,EXTRA FROM CREDENTIAL WHERE ACCOUNTID=?").use { preparedStatement ->
                preparedStatement.setLong(1, id)

                preparedStatement.executeQuery().use { resultSet ->
                    if(resultSet.next()){
                        credential = Credential(id, resultSet.getString(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4))
                    }
                    else{
                        credential = null
                    }
                }
            }

        }
        return credential
    }

    override fun getCredentials(): ArrayList<Credential> {
        val arr: ArrayList<Credential> = ArrayList()
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT ACCOUNTID,WEBSITE,USERNAME,PASSWORD,EXTRA FROM CREDENTIAL").use { preparedStatement ->
                preparedStatement.executeQuery().use { resultSet ->
                    while(resultSet.next()){
                        val credential = Credential(resultSet.getLong(1), resultSet.getString(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5))
                        arr.add(credential)
                    }
                }
            }
        }
        return arr
    }

}
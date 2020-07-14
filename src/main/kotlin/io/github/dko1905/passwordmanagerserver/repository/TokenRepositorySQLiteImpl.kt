package io.github.dko1905.passwordmanagerserver.repository

import io.github.dko1905.passwordmanagerserver.domain.Token
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import javax.sql.DataSource

class TokenRepositorySQLiteImpl(@Autowired val dataSource: DataSource) : TokenRepository {

    override fun replaceToken(token: Token) {
        dataSource.connection.use{ connection ->
            var rowsEffected: Int = 0
            connection.prepareStatement("UPDATE TOKEN SET UUID=?, EXP=? WHERE ACCOUNTID=?;").use { preparedStatement ->
                preparedStatement.setString(1, token.UUID.toString())
                preparedStatement.setLong(2, token.EXP)
                preparedStatement.setLong(3, token.USERID!!)

                rowsEffected = preparedStatement.executeUpdate()
            }

            // If no rows are effected, INSERT into the table
            if(rowsEffected < 1){
                //INSERT INTO demo(id, name, hint) values(?, ?, ?);
                connection.prepareStatement("INSERT INTO TOKEN(ACCOUNTID, UUID, EXP) VALUES(?, ?, ?);").use { preparedStatement ->
                    preparedStatement.setLong(1, token.USERID!!)
                    preparedStatement.setString(2, token.UUID.toString())
                    preparedStatement.setLong(3, token.EXP)

                    preparedStatement.executeUpdate()
                }
            }
        }
    }

    override fun getToken(userID: Long): Token? {
        var token: Token? = null
        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT ACCOUNTID,UUID,EXP FROM TOKEN WHERE ACCOUNTID=?").use { preparedStatement ->
                preparedStatement.setLong(1, userID)

                preparedStatement.executeQuery().use { resultSet ->
                    val USERID = resultSet.getLong(1)
                    val UUID: UUID = UUID.fromString(resultSet.getString(2))
                    val EXP = resultSet.getLong(3)

                    token = Token(USERID, UUID, EXP)
                }
            }
        }

        return token
    }

    override fun getToken(uuid: UUID): Token? {
        var token: Token? = null

        dataSource.connection.use { connection ->
            connection.prepareStatement("SELECT ACCOUNTID,UUID,EXP FROM TOKEN WHERE UUID=?").use { preparedStatement ->
                preparedStatement.setString(1, uuid.toString())

                preparedStatement.executeQuery().use { resultSet ->
                    val USERID = resultSet.getLong(1)
                    val UUID: UUID = UUID.fromString(resultSet.getString(2))
                    val EXP = resultSet.getLong(3)

                    token = Token(USERID, UUID, EXP)
                }
            }
        }

        return token
    }
}
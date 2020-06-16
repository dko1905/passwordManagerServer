package io.github.dko1905.passwordmanagerserver.domain.config

import io.github.dko1905.passwordmanagerserver.repository.AccountRepository
import io.github.dko1905.passwordmanagerserver.repository.AccountRepositorySQLiteImpl
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.sqlite.SQLiteDataSource
import java.sql.Connection
import javax.sql.DataSource

@Configuration
class ApplicationConfig {
    @Bean
    fun accountRepositoryProvider(dataSource: DataSource): AccountRepository{
        return AccountRepositorySQLiteImpl(dataSource)
    }

    @Bean
    fun dataSourceProvider(): DataSource{
        Class.forName("org.sqlite.JDBC")

        val ds = SQLiteDataSource()
        ds.url = "jdbc:sqlite:credential.db"

        ds.connection.use{ connection ->
            connection.createStatement().use {
                it.execute("CREATE TABLE IF NOT EXISTS ACCOUNT" +
                    "(" +
                    "ID INTEGER PRIMARY KEY," +
                    "USERNAME TEXT NOT NULL," +
                    "HASH TEXT NOT NULL," +
                    "ROLE INTEGER NOT NULL" +
                    ");")
                it.execute("CREATE TABLE IF NOT EXISTS TOKEN" +
                    "(" +
                    "ACCOUNTID INTEGER PRIMARY KEY," +
                    "UUID TEXT NOT NULL," +
                    "EXP INTEGER NOT NULL" +
                    ");")
                it.execute("CREATE TABLE IF NOT EXISTS TOKEN" +
                    "(" +
                    "ACCOUNTID INTEGER PRIMARY KEY," +
                    "WEBSITE TEXT NOT NULL," +
                    "USERNAME TEXT NOT NULL," +
                    "PASSWORD TEXT NOT NULL," +
                    "EXTRA TEXT NOT NULL" +
                    ");")
            }
        }
        return ds
    }
}
package com.leo.demos.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

import java.sql.ResultSet

class DbUtil {
    private static final HikariDataSource dataSource

    static {
        def config = new HikariConfig()
        config.with {
            jdbcUrl = "jdbc:h2:./imdb;AUTO_RECONNECT=TRUE"
            username = "sa"
            password = ""
            driverClassName = "org.h2.Driver"
            maximumPoolSize = 10
        }
        dataSource = new HikariDataSource(config)
    }

    static openConnection() {
        dataSource.connection
    }

    static int executeUpdate(String sql, Object... params) {
        try(
            def connection = openConnection();
            def statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i])
            }
            return statement.executeUpdate()
        }
    }
    static ResultSet executeQuery(String sql, Object... params) {
        try(
            def connection = openConnection();
            def statement = connection.prepareStatement(sql)
        ) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i])
            }
            return statement.executeQuery()
        }
    }
}

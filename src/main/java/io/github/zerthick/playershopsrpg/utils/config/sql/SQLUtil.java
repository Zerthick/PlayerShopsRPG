/*
 * Copyright (C) 2017  Zerthick
 *
 * This file is part of PlayerShopsRPG.
 *
 * PlayerShopsRPG is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * PlayerShopsRPG is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PlayerShopsRPG.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.zerthick.playershopsrpg.utils.config.sql;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SQLUtil {

    private static SqlService sql = Sponge.getServiceManager().provide(SqlService.class).get();

    private static DataSource getDataSource() throws SQLException {
        return sql.getDataSource("jdbc:h2:./config/playershopsrpg/data");
    }

    public static void createTable(String name, List<String> columns) throws SQLException {

        Connection connection = getDataSource().getConnection();

        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                name.toUpperCase() +
                "(" + columns.stream().map(String::toUpperCase).collect(Collectors.joining(", ")) + ")"
        );

        statement.executeUpdate();

        connection.close();
    }

    public static void dropTable(String name) throws SQLException {

        Connection connection = getDataSource().getConnection();

        PreparedStatement statement = connection.prepareStatement("DROP TABLE " + name.toUpperCase());

        statement.executeUpdate();

        connection.close();
    }

    public static void select(String tableName, Consumer<ResultSet> consumer) throws SQLException {

        ResultSet resultSet;

        Connection connection = getDataSource().getConnection();

        PreparedStatement statement = connection.prepareStatement("SELECT * " +
                " FROM " + tableName.toUpperCase()
        );

        resultSet = statement.executeQuery();

        consumer.accept(resultSet);

        connection.close();
    }

    public static void select(String tableName, String primaryKey, String primaryKeyValue, Consumer<ResultSet> consumer) throws SQLException {

        ResultSet resultSet;

        Connection connection = getDataSource().getConnection();

        PreparedStatement statement = connection.prepareStatement("SELECT * " +
                " FROM " + tableName.toUpperCase() +
                " WHERE " + primaryKey + " = ?"
        );
        statement.setString(1, primaryKeyValue);
        resultSet = statement.executeQuery();

        consumer.accept(resultSet);

        connection.close();
    }

    public static void delete(String tableName, String primaryKey, String primaryKeyValue) throws SQLException {

        Connection connection = getDataSource().getConnection();

        PreparedStatement statement = connection.prepareStatement("DELETE " +
                " FROM " + tableName.toUpperCase() +
                " WHERE " + primaryKey + " = ?"
        );
        statement.setString(1, primaryKeyValue);
        statement.executeUpdate();
        connection.close();
    }


    public static void executeUpdate(String sql) throws SQLException {
        executeUpdate(sql, preparedStatement -> {
        });
    }

    public static void executeUpdate(String sql, Consumer<PreparedStatement> consumer) throws SQLException {
        Connection connection = getDataSource().getConnection();

        PreparedStatement statement = connection.prepareStatement(sql);
        consumer.accept(statement);
        statement.executeUpdate();
        connection.close();
    }
}

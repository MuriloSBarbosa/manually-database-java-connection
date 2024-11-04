package com.murilo.barbosa;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JdbcConnectionFactory implements AutoCloseable {

    private final Connection connection;

    public JdbcConnectionFactory(String jdbcUrl)
          throws SQLException {
        this.connection = DriverManager.getConnection(jdbcUrl);
    }

    // execute method: return id inserted
    public boolean execute(String sql) throws SQLException {
        return this.connection.createStatement().execute(sql);
    }


    public int update(String sql) throws SQLException {
        return this.connection.createStatement().executeUpdate(sql);
    }

    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException {
        ResultSet result = this.connection.createStatement().executeQuery(sql);

        List<T> list = new ArrayList<>();

        while (result.next()) {
            try {
                list.add(rowMapper.map(result));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return list;
    }

    public <T> boolean insert(List<T> data, Class<T> clazz) throws SQLException {
        TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
        var tableName = tableNameAnnotation.value();

        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
              .toList();

        String columns = fields.stream()
              .map(Field::getName)
              .collect(Collectors.joining(", "));

        String values = createValues(data, clazz, fields);

        var sql = String.format("INSERT INTO %s (%s) VALUES %s", tableName, columns, values);

        return this.execute(sql);
    }

    private <T> String createValues(List<T> data, Class<T> clazz, List<Field> fields) {
        return data.stream()
              .map(value -> getValueStatement(clazz, fields, value))
              .map(value -> "(" + value + ")")
              .collect(Collectors.joining(", "));
    }

    private <T> String getValueStatement(Class<T> clazz, List<Field> fields, T value) {
        T instance = clazz.cast(value);
        return fields.stream()
              .map(field -> {
                  try {
                      field.trySetAccessible();
                      return "'" + field.get(instance) + "'";
                  } catch (IllegalAccessException e) {
                      throw new RuntimeException(e);
                  }
              })
              .collect(Collectors.joining(", "));
    }

    public <T> List<T> get(Class<T> clazz) throws SQLException {
        TableName tableNameAnnotation = clazz.getAnnotation(TableName.class);
        var tableName = tableNameAnnotation.value();

        String sql = String.format("SELECT * FROM %s", tableName);

        return this.query(sql, new RowMapper<>(clazz));
    }

    @Override
    public void close() throws Exception {
        this.connection.close();
    }
}

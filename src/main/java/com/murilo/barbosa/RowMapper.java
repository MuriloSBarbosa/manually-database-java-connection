package com.murilo.barbosa;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class RowMapper<T> {

    Class<T> clazz;

    public T map(ResultSet result)
          throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Field[] fields = clazz.getDeclaredFields();

        T instance = clazz.getConstructor().newInstance();

        for (Field field : fields) {
            field.trySetAccessible();
            try {
                field.set(instance, result.getObject(field.getName()));
            } catch (IllegalAccessException | SQLException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }
}

package com.murilo.barbosa;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        try (JdbcConnectionFactory factory = new JdbcConnectionFactory("jdbc:h2:mem:test"
        )) {

            // creating table
            factory.execute(
                  "CREATE TABLE IF NOT EXISTS pessoa (id INT PRIMARY KEY, nome VARCHAR(255))");

            // inserting data manually
            factory.execute(
                  "INSERT INTO pessoa (id, nome) VALUES (1, 'Murilo'), (2, 'Barbosa'), (3, 'Silva')");

            // inserting data using insert method
            List<Pessoa> newPessoas = List.of(new Pessoa(4, "João"), new Pessoa(5, "Maria"));
            factory.insert(newPessoas, Pessoa.class);

            // using sql and row mapper to query data
            List<Pessoa> pessoas = factory.query("SELECT * FROM pessoa",
                  new RowMapper<>(Pessoa.class));

            for (Pessoa pessoa : pessoas) {
                System.out.println(pessoa);
            }

            // using factory.get method to query data
            for (Pessoa pessoa : factory.get(Pessoa.class)) {
                System.out.println(pessoa);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
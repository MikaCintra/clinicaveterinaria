
package com.example.clinica.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

public class ConnectionFactory {
    private static final String URL = "jdbc:h2:./data/clinica;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        Path dataDir = Paths.get("data");
        try {
            if (!Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao criar diretório de dados: " + e.getMessage(), e);
        }

        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Animal (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "especie VARCHAR(100) NOT NULL);"
                );

                connection.createStatement().executeUpdate(
                    "CREATE TABLE IF NOT EXISTS Consulta (" +
                    "id IDENTITY PRIMARY KEY, " +
                    "idAnimal INT NOT NULL, " +
                    "data DATE NOT NULL, " +
                    "descricao VARCHAR(255) NOT NULL, " +
                    "FOREIGN KEY (idAnimal) REFERENCES Animal(id) ON DELETE CASCADE);"
                );

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Erro ao inicializar banco de dados: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage(), e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

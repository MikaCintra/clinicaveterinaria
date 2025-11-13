
package com.example.clinica.dao;

import com.example.clinica.database.ConnectionFactory;
import com.example.clinica.model.Animal;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {
    private static final Logger LOGGER = Logger.getLogger(AnimalDAO.class.getName());
    public void salvar(Animal animal) {
        String sql = "INSERT INTO Animal (nome, especie, dono, telefone) VALUES (?, ?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getDono());
            stmt.setString(4, animal.getTelefone());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    animal.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar animal", e);
        }
    }

    public List<Animal> listar() {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM Animal";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                animais.add(new Animal(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("especie"),
                    rs.getString("dono"),
                    rs.getString("telefone")
                ));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao listar animais", e);
        }
        return animais;
    }

    public Animal buscarPorId(int id) {
        String sql = "SELECT * FROM Animal WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Animal(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("especie"),
                        rs.getString("dono"),
                        rs.getString("telefone")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao buscar animal por id", e);
        }
        return null;
    }

    public boolean deletarPorId(int id) {
        String sql = "DELETE FROM Animal WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao deletar animal", e);
            return false;
        }
    }

    public boolean atualizar(Animal animal) {
        String sql = "UPDATE Animal SET nome = ?, especie = ?, dono = ?, telefone = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getDono());
            stmt.setString(4, animal.getTelefone());
            stmt.setInt(5, animal.getId());
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar animal", e);
            return false;
        }
    }
}


package com.example.clinica.dao;

import com.example.clinica.database.ConnectionFactory;
import com.example.clinica.model.Animal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {
    public void salvar(Animal animal) {
        String sql = "INSERT INTO Animal (nome, especie) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    animal.setId(generatedId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Animal> listar() {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM Animal";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                animais.add(new Animal(rs.getInt("id"), rs.getString("nome"), rs.getString("especie")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                    return new Animal(rs.getInt("id"), rs.getString("nome"), rs.getString("especie"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
            return false;
        }
    }
}

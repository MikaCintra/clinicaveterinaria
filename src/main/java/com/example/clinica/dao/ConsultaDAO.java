
package com.example.clinica.dao;

import com.example.clinica.database.ConnectionFactory;
import com.example.clinica.model.Consulta;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    public void salvar(Consulta consulta) {
        String sql = "INSERT INTO Consulta (idAnimal, data, descricao) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, consulta.getIdAnimal());
            stmt.setDate(2, Date.valueOf(consulta.getData()));
            stmt.setString(3, consulta.getDescricao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Consulta> listar() {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM Consulta ORDER BY data DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                consultas.add(new Consulta(rs.getInt("id"), rs.getInt("idAnimal"), 
                    rs.getDate("data").toLocalDate(), rs.getString("descricao")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    public Consulta buscarPorId(int id) {
        String sql = "SELECT * FROM Consulta WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Consulta(rs.getInt("id"), rs.getInt("idAnimal"),
                        rs.getDate("data").toLocalDate(), rs.getString("descricao"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean deletarPorId(int id) {
        String sql = "DELETE FROM Consulta WHERE id = ?";
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

    public List<Consulta> listarPorAnimal(int idAnimal) {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM Consulta WHERE idAnimal = ? ORDER BY data DESC";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idAnimal);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    consultas.add(new Consulta(rs.getInt("id"), rs.getInt("idAnimal"),
                        rs.getDate("data").toLocalDate(), rs.getString("descricao")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }
}

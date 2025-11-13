
package com.example.clinica.dao;

import com.example.clinica.database.ConnectionFactory;
import com.example.clinica.model.Consulta;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    private static final Logger LOGGER = Logger.getLogger(ConsultaDAO.class.getName());
    public void salvar(Consulta consulta) {
        String sql = "INSERT INTO Consulta (idAnimal, data, descricao) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, consulta.getIdAnimal());
            stmt.setDate(2, Date.valueOf(consulta.getData()));
            stmt.setString(3, consulta.getDescricao());
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar consulta", e);
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
            LOGGER.log(Level.SEVERE, "Erro ao listar consultas", e);
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
            LOGGER.log(Level.SEVERE, "Erro ao buscar consulta por id", e);
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
            LOGGER.log(Level.SEVERE, "Erro ao deletar consulta", e);
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
            LOGGER.log(Level.SEVERE, "Erro ao listar por animal", e);
        }
        return consultas;
    }

    public boolean atualizar(Consulta consulta) {
        String sql = "UPDATE Consulta SET idAnimal = ?, data = ?, descricao = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, consulta.getIdAnimal());
            stmt.setDate(2, Date.valueOf(consulta.getData()));
            stmt.setString(3, consulta.getDescricao());
            stmt.setInt(4, consulta.getId());
            int affected = stmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao atualizar consulta", e);
            return false;
        }
    }
}

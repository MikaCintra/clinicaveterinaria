
package com.example.clinica.service;

import com.example.clinica.dao.ConsultaDAO;
import com.example.clinica.model.Consulta;
import java.time.LocalDate;
import java.util.List;

public class ConsultaService {
    private final ConsultaDAO consultaDAO = new ConsultaDAO();

    public void cadastrarConsulta(int idAnimal, String descricao) {
        consultaDAO.salvar(new Consulta(idAnimal, LocalDate.now(), descricao));
    }

    public List<Consulta> listarConsultas() {
        return consultaDAO.listar();
    }

    public Consulta buscarPorId(int id) {
        return consultaDAO.buscarPorId(id);
    }

    public boolean excluirConsulta(int id) {
        return consultaDAO.deletarPorId(id);
    }

    public List<Consulta> listarConsultasPorAnimal(int idAnimal) {
        return consultaDAO.listarPorAnimal(idAnimal);
    }

    public boolean atualizarConsulta(Consulta consulta) {
        return consultaDAO.atualizar(consulta);
    }
}

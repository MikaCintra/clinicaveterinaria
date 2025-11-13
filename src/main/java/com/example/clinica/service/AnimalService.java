
package com.example.clinica.service;

import com.example.clinica.dao.AnimalDAO;
import com.example.clinica.model.Animal;
import java.util.List;

public class AnimalService {
    private final AnimalDAO animalDAO = new AnimalDAO();

    public void cadastrarAnimal(Animal animal) {
        animalDAO.salvar(animal);
    }

    public List<Animal> listarAnimais() {
        return animalDAO.listar();
    }

    public boolean excluirAnimal(int id) {
        return animalDAO.deletarPorId(id);
    }

    public boolean atualizarAnimal(Animal animal) {
        return animalDAO.atualizar(animal);
    }
}

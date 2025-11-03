
package com.example.clinica.service;

import com.example.clinica.dao.AnimalDAO;
import com.example.clinica.model.Animal;
import java.util.List;
import java.util.Scanner;

public class AnimalService {
    private final AnimalDAO animalDAO = new AnimalDAO();

    public void cadastrarAnimal(Animal animal) {
        animalDAO.salvar(animal);
    }

    public List<Animal> listarAnimais() {
        return animalDAO.listar();
    }

    public void exibirAnimalPorId(Scanner scanner) {
        System.out.print("ID do animal: ");
        String entrada = scanner.nextLine();
        try {
            int id = Integer.parseInt(entrada.trim());
            Animal a = animalDAO.buscarPorId(id);
            if (a != null) {
                System.out.println("ID: " + a.getId() + " - " + a.getNome() + " (" + a.getEspecie() + ")");
            } else {
                System.out.println("Animal não encontrado com ID " + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    public boolean excluirAnimal(int id) {
        return animalDAO.deletarPorId(id);
    }
}

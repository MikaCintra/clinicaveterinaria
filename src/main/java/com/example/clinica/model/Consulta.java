
package com.example.clinica.model;

import java.time.LocalDate;

public class Consulta {
    private int id;
    private int idAnimal;
    private LocalDate data;
    private String descricao;

    public Consulta(int id, int idAnimal, LocalDate data, String descricao) {
        this.id = id;
        this.idAnimal = idAnimal;
        this.data = data;
        this.descricao = descricao;
    }

    public Consulta(int idAnimal, LocalDate data, String descricao) {
        this.idAnimal = idAnimal;
        this.data = data;
        this.descricao = descricao;
    }

    public int getId() { return id; }
    public int getIdAnimal() { return idAnimal; }
    public LocalDate getData() { return data; }
    public String getDescricao() { return descricao; }

    public void setIdAnimal(int idAnimal) { this.idAnimal = idAnimal; }
    public void setData(LocalDate data) { this.data = data; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}

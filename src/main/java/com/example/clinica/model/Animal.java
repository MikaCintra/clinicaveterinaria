
package com.example.clinica.model;

public class Animal {
    private int id;
    private String nome;
    private String especie;

    public Animal(int id, String nome, String especie) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
    }

    public Animal(String nome, String especie) {
        this.nome = nome;
        this.especie = especie;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
}

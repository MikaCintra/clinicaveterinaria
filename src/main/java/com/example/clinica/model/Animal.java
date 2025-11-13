
package com.example.clinica.model;

public class Animal {
    private int id;
    private String nome;
    private String especie;
    private String dono;
    private String telefone;

    public Animal(int id, String nome, String especie, String dono, String telefone) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.dono = dono;
        this.telefone = telefone;
    }

    public Animal(String nome, String especie, String dono, String telefone) {
        this.nome = nome;
        this.especie = especie;
        this.dono = dono;
        this.telefone = telefone;
    }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEspecie() { return especie; }
    public String getDono() { return dono; }
    public String getTelefone() { return telefone; }

    public void setNome(String nome) { this.nome = nome; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setDono(String dono) { this.dono = dono; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
}

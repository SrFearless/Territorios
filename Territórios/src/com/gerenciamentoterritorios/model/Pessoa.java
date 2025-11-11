package com.gerenciamentoterritorios.model;

import java.time.LocalDate;

public class Pessoa {
    private String nome;
    private String telefone;
    private LocalDate dataCadastro;

    public Pessoa(String nome, String telefone) {
        this.nome = nome;
        this.telefone = telefone;
        this.dataCadastro = LocalDate.now();
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public LocalDate getDataCadastro() { return dataCadastro; }

    @Override
    public String toString() {
        return nome + " (" + telefone + ")";
    }
}
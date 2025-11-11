package com.gerenciamentoterritorios.model;

import java.util.ArrayList;
import java.util.List;

public class Dirigente {
    private String nome;
    private String telefone;
    private List<String> territoriosPermitidos;
    private String bairro;

    public Dirigente(String nome, String telefone, String bairro) {
        this.nome = nome;
        this.telefone = telefone;
        this.bairro = bairro;
        this.territoriosPermitidos = new ArrayList<>();
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public List<String> getTerritoriosPermitidos() { return territoriosPermitidos; }
    public void setTerritoriosPermitidos(List<String> territoriosPermitidos) { 
        this.territoriosPermitidos = territoriosPermitidos; 
    }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public void adicionarTerritorioPermitido(String territorio) {
        if (!territoriosPermitidos.contains(territorio)) {
            territoriosPermitidos.add(territorio);
        }
    }

    public void removerTerritorioPermitido(String territorio) {
        territoriosPermitidos.remove(territorio);
    }

    @Override
    public String toString() {
        return nome + " - " + telefone + " - " + bairro;
    }

    public String getInfoCompleta() {
        StringBuilder territorios = new StringBuilder();
        for (String territorio : territoriosPermitidos) {
            if (territorios.length() > 0) {
                territorios.append(", ");
            }
            territorios.append(territorio);
        }
        return String.format("Nome: %s\nTelefone: %s\nBairro: %s\nTerritórios Permitidos: %s",
                nome, telefone, bairro, 
                territorios.length() > 0 ? territorios.toString() : "Nenhum");
    }
}
package com.gerenciamentoterritorios.model;

public class Territorio {
    private int numero;
    private String bairro;
    private String descricao;
    private boolean disponivel;
    private String imagemPath; // Novo campo para caminho da imagem

    public Territorio(int numero, String bairro, String descricao) {
        this.numero = numero;
        this.bairro = bairro;
        this.descricao = descricao;
        this.disponivel = true;
        this.imagemPath = null;
    }

    // Getters e Setters
    public int getNumero() { return numero; }
    public String getBairro() { return bairro; }
    public String getDescricao() { return descricao; }
    public boolean isDisponivel() { return disponivel; }
    public void setDisponivel(boolean disponivel) { this.disponivel = disponivel; }
    public String getImagemPath() { return imagemPath; }
    public void setImagemPath(String imagemPath) { this.imagemPath = imagemPath; }

    @Override
    public String toString() {
        return "Território " + numero + " - " + bairro + " - " + descricao + 
               " - " + (disponivel ? "Disponível" : "Ocupado");
    }
}
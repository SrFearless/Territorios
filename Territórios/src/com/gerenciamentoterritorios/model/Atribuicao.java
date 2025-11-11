package com.gerenciamentoterritorios.model;

import java.time.LocalDate;

public class Atribuicao implements Comparable<Atribuicao> {
    private Pessoa pessoa;
    private Territorio territorio;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private boolean ativa;

    public Atribuicao(Pessoa pessoa, Territorio territorio, LocalDate dataInicio) {
        this.pessoa = pessoa;
        this.territorio = territorio;
        this.dataInicio = dataInicio;
        this.ativa = true;
        territorio.setDisponivel(false);
    }

    // Getters
    public Pessoa getPessoa() { return pessoa; }
    public Territorio getTerritorio() { return territorio; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public boolean isAtiva() { return ativa; }

    public void finalizarAtribuicao(LocalDate dataFim) {
        this.dataFim = dataFim;
        this.ativa = false;
        territorio.setDisponivel(true);
    }

    @Override
    public int compareTo(Atribuicao outra) {
        if (this.ativa && !outra.ativa) {
            return -1;
        } else if (!this.ativa && outra.ativa) {
            return 1;
        }
        return outra.dataInicio.compareTo(this.dataInicio);
    }

    @Override
    public String toString() {
        String status = ativa ? "EM ANDAMENTO" : "FINALIZADA";
        return String.format("%s - %s | Início: %s | Fim: %s | Status: %s",
                pessoa.getNome(), territorio, dataInicio, 
                dataFim != null ? dataFim : "---", status);
    }
    
}
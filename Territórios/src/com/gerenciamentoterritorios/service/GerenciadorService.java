package com.gerenciamentoterritorios.service;

import com.gerenciamentoterritorios.model.*;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class GerenciadorService {
    private List<Pessoa> pessoas;
    private List<Territorio> territorios;
    private List<Atribuicao> atribuicoes;
    private List<Dirigente> dirigentes;
    
    private static final String DATA_FILE = "dados_territorios.txt";

    public GerenciadorService() {
        this.pessoas = new ArrayList<>();
        this.territorios = new ArrayList<>();
        this.atribuicoes = new ArrayList<>();
        this.dirigentes = new ArrayList<>();
        
        carregarDados();
        
        if (pessoas.isEmpty() && territorios.isEmpty()) {
            inicializarDadosExemplo();
        }
    }

    private void carregarDados() {
        try {
            File arquivo = new File(DATA_FILE);
            if (arquivo.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(arquivo));
                String linha;
                
                while ((linha = reader.readLine()) != null) {
                    if (linha.startsWith("PESSOA:")) {
                        String[] partes = linha.substring(7).split("\\|");
                        if (partes.length == 2) {
                            pessoas.add(new Pessoa(partes[0], partes[1]));
                        }
                    }
                    else if (linha.startsWith("TERRITORIO:")) {
                        String[] partes = linha.substring(11).split("\\|");
                        if (partes.length >= 3) {
                            Territorio territorio = new Territorio(
                                Integer.parseInt(partes[0]), 
                                partes[1], 
                                partes[2]
                            );
                            territorio.setDisponivel(Boolean.parseBoolean(partes[3]));
                            if (partes.length > 4 && !partes[4].equals("null")) {
                                territorio.setImagemPath(partes[4]);
                            }
                            territorios.add(territorio);
                        }
                    }
                    else if (linha.startsWith("ATRIBUICAO:")) {
                        String[] partes = linha.substring(11).split("\\|");
                        if (partes.length >= 5) {
                            // Encontrar a pessoa
                            Pessoa pessoa = null;
                            for (Pessoa p : pessoas) {
                                if (p.getNome().equals(partes[0])) {
                                    pessoa = p;
                                    break;
                                }
                            }
                            
                            // Encontrar o território
                            Territorio territorio = null;
                            for (Territorio t : territorios) {
                                if (t.getNumero() == Integer.parseInt(partes[1])) {
                                    territorio = t;
                                    break;
                                }
                            }
                            
                            if (pessoa != null && territorio != null) {
                                LocalDate dataInicio = LocalDate.parse(partes[2]);
                                LocalDate dataFim = partes[3].equals("null") ? null : LocalDate.parse(partes[3]);
                                boolean ativa = Boolean.parseBoolean(partes[4]);
                                
                                Atribuicao atribuicao = new Atribuicao(pessoa, territorio, dataInicio);
                                if (!ativa && dataFim != null) {
                                    atribuicao.finalizarAtribuicao(dataFim);
                                }
                                atribuicoes.add(atribuicao);
                            }
                        }
                    }
                    // ADICIONE ESTE BLOCO PARA CARREGAR DIRIGENTES
                    else if (linha.startsWith("DIRIGENTE:")) {
                        String[] partes = linha.substring(10).split("\\|");
                        if (partes.length >= 3) {
                            Dirigente dirigente = new Dirigente(partes[0], partes[1], partes[2]);
                            
                            // Carregar territórios permitidos se existirem
                            if (partes.length > 3 && !partes[3].equals("null")) {
                                String[] territoriosArray = partes[3].split(",");
                                for (String territorio : territoriosArray) {
                                    if (!territorio.trim().isEmpty()) {
                                        dirigente.adicionarTerritorioPermitido(territorio.trim());
                                    }
                                }
                            }
                            dirigentes.add(dirigente);
                        }
                    }
                }
                reader.close();
                System.out.println("Dados carregados: " + pessoas.size() + " pessoas, " + 
                                  territorios.size() + " territórios, " + 
                                  atribuicoes.size() + " atribuições, " +
                                  dirigentes.size() + " dirigentes");
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar dados: " + e.getMessage());
            pessoas = new ArrayList<>();
            territorios = new ArrayList<>();
            atribuicoes = new ArrayList<>();
            dirigentes = new ArrayList<>();
        }
    }

    public void salvarDados() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter(DATA_FILE));
            
            // Salvar pessoas
            for (Pessoa pessoa : pessoas) {
                writer.println("PESSOA:" + pessoa.getNome() + "|" + pessoa.getTelefone());
            }
            
            // Salvar territórios
            for (Territorio territorio : territorios) {
                writer.println("TERRITORIO:" + territorio.getNumero() + "|" + 
                              territorio.getBairro() + "|" + 
                              territorio.getDescricao() + "|" + 
                              territorio.isDisponivel() + "|" + 
                              (territorio.getImagemPath() != null ? territorio.getImagemPath() : "null"));
            }
            
            // Salvar atribuições
            for (Atribuicao atribuicao : atribuicoes) {
                writer.println("ATRIBUICAO:" + atribuicao.getPessoa().getNome() + "|" + 
                              atribuicao.getTerritorio().getNumero() + "|" + 
                              atribuicao.getDataInicio() + "|" + 
                              (atribuicao.getDataFim() != null ? atribuicao.getDataFim() : "null") + "|" + 
                              atribuicao.isAtiva());
            }
            
            // ADICIONE ESTE BLOCO PARA SALVAR DIRIGENTES
            for (Dirigente dirigente : dirigentes) {
                StringBuilder territoriosStr = new StringBuilder();
                for (String territorio : dirigente.getTerritoriosPermitidos()) {
                    if (territoriosStr.length() > 0) {
                        territoriosStr.append(",");
                    }
                    territoriosStr.append(territorio);
                }
                writer.println("DIRIGENTE:" + dirigente.getNome() + "|" + 
                              dirigente.getTelefone() + "|" + 
                              dirigente.getBairro() + "|" + 
                              (territoriosStr.length() > 0 ? territoriosStr.toString() : "null"));
            }
            
            writer.close();
            System.out.println("Dados salvos com sucesso!");
        } catch (Exception e) {
            System.out.println("Erro ao salvar dados: " + e.getMessage());
        }
    }

    private void inicializarDadosExemplo() {
        // Adiciona alguns territórios de exemplo
        territorios.add(new Territorio(1, "Centro", "Quadras 1-15"));
        territorios.add(new Territorio(2, "Jardim das Flores", "Quadras A-H"));
        territorios.add(new Territorio(3, "Vila Nova", "Ruas 1-20"));
        
        // Adiciona algumas pessoas de exemplo
        pessoas.add(new Pessoa("João Silva", "(11) 9999-1111"));
        pessoas.add(new Pessoa("Maria Santos", "(11) 9999-2222"));
        pessoas.add(new Pessoa("Pedro Oliveira", "(11) 9999-3333"));
        
        // ADICIONE ALGUNS DIRIGENTES DE EXEMPLO (OPCIONAL)
        Dirigente dirigente1 = new Dirigente("Carlos Alberto", "(11) 98888-1111", "Centro");
        dirigente1.adicionarTerritorioPermitido("Território 1");
        dirigente1.adicionarTerritorioPermitido("Território 2");
        dirigentes.add(dirigente1);
        
        Dirigente dirigente2 = new Dirigente("Ana Paula", "(11) 97777-2222", "Jardim das Flores");
        dirigente2.adicionarTerritorioPermitido("Território 2");
        dirigente2.adicionarTerritorioPermitido("Território 3");
        dirigentes.add(dirigente2);
        
        salvarDados();
    }

    // Métodos para Pessoas
    public void adicionarPessoa(String nome, String telefone) {
        pessoas.add(new Pessoa(nome, telefone));
        salvarDados();
    }

    public List<Pessoa> getPessoas() {
        return new ArrayList<>(pessoas);
    }

    // Métodos para Territórios
    public void adicionarTerritorio(int numero, String bairro, String descricao) {
        territorios.add(new Territorio(numero, bairro, descricao));
        salvarDados();
    }

    public void adicionarTerritorio(Territorio territorio) {
        territorios.add(territorio);
        salvarDados();
    }

    public List<Territorio> getTerritoriosDisponiveis() {
        List<Territorio> disponiveis = new ArrayList<>();
        for (Territorio t : territorios) {
            if (t.isDisponivel()) {
                disponiveis.add(t);
            }
        }
        return disponiveis;
    }

    public List<Territorio> getAllTerritorios() {
        return new ArrayList<>(territorios);
    }

    // Métodos para Atribuições
    public boolean atribuirTerritorio(Pessoa pessoa, Territorio territorio, LocalDate dataInicio) {
        if (!territorio.isDisponivel()) {
            return false;
        }
        Atribuicao atribuicao = new Atribuicao(pessoa, territorio, dataInicio);
        atribuicoes.add(atribuicao);
        salvarDados();
        return true;
    }

    public boolean finalizarAtribuicao(Territorio territorio, LocalDate dataFim) {
        for (Atribuicao a : atribuicoes) {
            if (a.getTerritorio().equals(territorio) && a.isAtiva()) {
                a.finalizarAtribuicao(dataFim);
                salvarDados();
                return true;
            }
        }
        return false;
    }

    public List<Atribuicao> getAtribuicoesOrdenadas() {
        List<Atribuicao> ordenadas = new ArrayList<>(atribuicoes);
        Collections.sort(ordenadas);
        return ordenadas;
    }

    public List<Atribuicao> getAtribuicoesAtivas() {
        List<Atribuicao> ativas = new ArrayList<>();
        for (Atribuicao a : atribuicoes) {
            if (a.isAtiva()) {
                ativas.add(a);
            }
        }
        Collections.sort(ativas);
        return ativas;
    }

    // Métodos para Dirigentes
    public void adicionarDirigente(String nome, String telefone, String bairro) {
        dirigentes.add(new Dirigente(nome, telefone, bairro));
        salvarDados();
    }

    public void adicionarDirigente(Dirigente dirigente) {
        dirigentes.add(dirigente);
        salvarDados();
    }

    public List<Dirigente> getDirigentes() {
        return new ArrayList<>(dirigentes);
    }

    public boolean adicionarTerritorioParaDirigente(Dirigente dirigente, String territorio) {
        if (dirigentes.contains(dirigente)) {
            dirigente.adicionarTerritorioPermitido(territorio);
            salvarDados();
            return true;
        }
        return false;
    }
    
    // NOVO MÉTODO: Remover dirigente
    public boolean removerDirigente(Dirigente dirigente) {
        boolean removido = dirigentes.remove(dirigente);
        if (removido) {
            salvarDados();
        }
        return removido;
    }
}
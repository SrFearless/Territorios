package com.gerenciamentoterritorios.ui;

import com.gerenciamentoterritorios.model.*;
import com.gerenciamentoterritorios.service.GerenciadorService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MainUI {
    private GerenciadorService service;
    private Scanner scanner;

    public MainUI() {
        this.service = new GerenciadorService();
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcao;
        do {
            exibirMenu();
            opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    listarAtribuicoesRecentes();
                    break;
                case 2:
                    atribuirTerritorio();
                    break;
                case 3:
                    finalizarTerritorio();
                    break;
                case 4:
                    listarPessoas();
                    break;
                case 5:
                    listarTerritorios();
                    break;
                case 6:
                    adicionarPessoa();
                    break;
                case 7:
                    adicionarTerritorio();
                    break;
                case 0:
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opção inválida!");
            }
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("\n=== SISTEMA DE GERENCIAMENTO DE TERRITÓRIOS ===");
        System.out.println("1. Listar atribuições recentes");
        System.out.println("2. Atribuir território");
        System.out.println("3. Finalizar território");
        System.out.println("4. Listar pessoas");
        System.out.println("5. Listar territórios");
        System.out.println("6. Adicionar pessoa");
        System.out.println("7. Adicionar território");
        System.out.println("0. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private void listarAtribuicoesRecentes() {
        System.out.println("\n--- ATRIBUIÇÕES RECENTES ---");
        List<Atribuicao> atribuicoes = service.getAtribuicoesOrdenadas();
        
        if (atribuicoes.isEmpty()) {
            System.out.println("Nenhuma atribuição cadastrada.");
        } else {
            for (int i = 0; i < atribuicoes.size(); i++) {
                System.out.println((i + 1) + ". " + atribuicoes.get(i));
            }
        }
    }

    private void atribuirTerritorio() {
        System.out.println("\n--- ATRIBUIR TERRITÓRIO ---");
        
        // Listar pessoas
        List<Pessoa> pessoas = service.getPessoas();
        if (pessoas.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
            return;
        }
        
        System.out.println("Pessoas disponíveis:");
        for (int i = 0; i < pessoas.size(); i++) {
            System.out.println((i + 1) + ". " + pessoas.get(i));
        }
        
        System.out.print("Escolha a pessoa (número): ");
        int idxPessoa = scanner.nextInt() - 1;
        
        // Listar territórios disponíveis
        List<Territorio> territorios = service.getTerritoriosDisponiveis();
        if (territorios.isEmpty()) {
            System.out.println("Nenhum território disponível.");
            return;
        }
        
        System.out.println("Territórios disponíveis:");
        for (int i = 0; i < territorios.size(); i++) {
            System.out.println((i + 1) + ". " + territorios.get(i));
        }
        
        System.out.print("Escolha o território (número): ");
        int idxTerritorio = scanner.nextInt() - 1;
        scanner.nextLine(); // Limpar buffer
        
        if (idxPessoa >= 0 && idxPessoa < pessoas.size() && 
            idxTerritorio >= 0 && idxTerritorio < territorios.size()) {
            
            boolean sucesso = service.atribuirTerritorio(
                pessoas.get(idxPessoa), 
                territorios.get(idxTerritorio), 
                LocalDate.now()
            );
            
            if (sucesso) {
                System.out.println("Território atribuído com sucesso!");
            } else {
                System.out.println("Erro ao atribuir território.");
            }
        } else {
            System.out.println("Índices inválidos!");
        }
    }

    private void finalizarTerritorio() {
        System.out.println("\n--- FINALIZAR TERRITÓRIO ---");
        List<Atribuicao> ativas = service.getAtribuicoesAtivas();
        
        if (ativas.isEmpty()) {
            System.out.println("Nenhum território em andamento.");
            return;
        }
        
        System.out.println("Territórios em andamento:");
        for (int i = 0; i < ativas.size(); i++) {
            System.out.println((i + 1) + ". " + ativas.get(i));
        }
        
        System.out.print("Escolha o território para finalizar (número): ");
        int idx = scanner.nextInt() - 1;
        
        if (idx >= 0 && idx < ativas.size()) {
            boolean sucesso = service.finalizarAtribuicao(
                ativas.get(idx).getTerritorio(), 
                LocalDate.now()
            );
            
            if (sucesso) {
                System.out.println("Território finalizado com sucesso!");
            } else {
                System.out.println("Erro ao finalizar território.");
            }
        } else {
            System.out.println("Índice inválido!");
        }
    }

    private void listarPessoas() {
        System.out.println("\n--- PESSOAS CADASTRADAS ---");
        List<Pessoa> pessoas = service.getPessoas();
        
        if (pessoas.isEmpty()) {
            System.out.println("Nenhuma pessoa cadastrada.");
        } else {
            for (int i = 0; i < pessoas.size(); i++) {
                System.out.println((i + 1) + ". " + pessoas.get(i));
            }
        }
    }

    private void listarTerritorios() {
        System.out.println("\n--- TODOS OS TERRITÓRIOS ---");
        List<Territorio> territorios = service.getAllTerritorios();
        
        for (int i = 0; i < territorios.size(); i++) {
            System.out.println((i + 1) + ". " + territorios.get(i));
        }
    }

    private void adicionarPessoa() {
        System.out.println("\n--- ADICIONAR PESSOA ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        
        service.adicionarPessoa(nome, telefone);
        System.out.println("Pessoa adicionada com sucesso!");
    }

    private void adicionarTerritorio() {
        System.out.println("\n--- ADICIONAR TERRITÓRIO ---");
        System.out.print("Número: ");
        int numero = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer
        System.out.print("Bairro: ");
        String bairro = scanner.nextLine();
        System.out.print("Descrição (quadras/ruas): ");
        String descricao = scanner.nextLine();
        
        service.adicionarTerritorio(numero, bairro, descricao);
        System.out.println("Território adicionado com sucesso!");
    }
}
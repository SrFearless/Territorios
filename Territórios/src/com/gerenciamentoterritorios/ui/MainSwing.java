package com.gerenciamentoterritorios.ui;

import com.gerenciamentoterritorios.model.*;
import com.gerenciamentoterritorios.service.GerenciadorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainSwing {
    private GerenciadorService service;
    private JFrame frame;
    private JTable tabelaAtribuicoes;
    private DefaultTableModel modeloTabela;
    private JLabel labelImagem;

    // CORES DO LAYOUT - FACILMENTE PERSONALIZÁVEIS
    @SuppressWarnings("unused")
	private final Color COR_PRIMARIA = new Color(217,135,25);     
    private final Color COR_SECUNDARIA = new Color(217,135,25);    
    private final Color COR_SUCESSO = new Color(46, 204, 113);      
    private final Color COR_ALERTA = new Color(231, 76, 60);      
    private final Color COR_DESTAQUE = new Color(217,135,25);
    private final Color COR_ACAO = new Color(217,135,25);     
    private final Color COR_FUNDO = new Color(255,255,255);    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainSwing().iniciar();
        });
    }

    private void configurarAutoSave() {
        Timer timer = new Timer(30000, e -> {
            service.salvarDados();
        });
        timer.start();
        
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                service.salvarDados();
            }
        });
    }
    
    private void configurarIcone() { 
        try {
            java.net.URL url = getClass().getResource("/icone.png");
            if (url != null) {
                ImageIcon icone = new ImageIcon(url);
                frame.setIconImage(icone.getImage());
            } else {
                ImageIcon icone = new ImageIcon("icone.png");
                frame.setIconImage(icone.getImage());
            }
        } catch (Exception e) {
            System.out.println("Ícone não carregado: " + e.getMessage());
        }
    }

    public void iniciar() {
        service = new GerenciadorService();
        criarInterface();
        configurarAutoSave();
        atualizarTabela();
    }

    private void criarInterface() {
        frame = new JFrame("JW - Territórios");
        configurarIcone();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        
        try {
            ImageIcon icone = new ImageIcon("icone.png"); 
            frame.setIconImage(icone.getImage());
        } catch (Exception e) {
            System.out.println("Ícone não encontrado. Continuando sem ícone.");
        }

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setResizeWeight(0.7);

        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painelEsquerdo.setBackground(COR_FUNDO);

        JPanel painelBotoes = criarPainelBotoes();
        painelEsquerdo.add(painelBotoes, BorderLayout.NORTH);

        JPanel painelTabela = criarPainelTabela();
        painelEsquerdo.add(new JScrollPane(painelTabela), BorderLayout.CENTER);

        JPanel painelDireito = criarPainelImagem();

        splitPane.setLeftComponent(painelEsquerdo);
        splitPane.setRightComponent(painelDireito);

        frame.add(splitPane);
        frame.setVisible(true);
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        painel.setBackground(COR_FUNDO);

        JButton btnAtribuir = criarBotaoEstilizado("Atribuir Território", COR_SUCESSO);
        JButton btnFinalizar = criarBotaoEstilizado("Finalizar Território", COR_ALERTA);
        JButton btnAtualizar = criarBotaoEstilizado("Atualizar", COR_ACAO);
        JButton btnDirigentes = criarBotaoEstilizado("Dirigentes", new Color(217,135,25));

        JButton btnMenuAdicionar = criarBotaoMenuAdicionar();

 
        btnAtribuir.addActionListener(e -> abrirDialogoAtribuicao());
        btnFinalizar.addActionListener(e -> finalizarAtribuicao());
        btnAtualizar.addActionListener(e -> atualizarTabela());
        btnDirigentes.addActionListener(e -> abrirDialogoDirigentes());

        painel.add(btnAtribuir);
        painel.add(btnFinalizar);
        painel.add(btnMenuAdicionar);
        painel.add(btnDirigentes);
        painel.add(btnAtualizar);

        return painel;
    }

	private JButton criarBotaoMenuAdicionar() {
        JButton btnMenu = new JButton("+");
        btnMenu.setBackground(COR_DESTAQUE);
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);
        btnMenu.setFont(new Font("Arial", Font.BOLD, 16));
        btnMenu.setPreferredSize(new Dimension(50, 30));
        
        JPopupMenu menuPopup = new JPopupMenu();
        
        JMenuItem itemAddTerritorio = new JMenuItem("Adicionar Território");
        JMenuItem itemAddDirigente = new JMenuItem("Adicionar Dirigente");
        JMenuItem itemAddPessoa = new JMenuItem("Adicionar Pessoa");
        
        itemAddTerritorio.addActionListener(e -> abrirDialogoAddTerritorioComImagem());
        itemAddDirigente.addActionListener(e -> abrirDialogoAddDirigente());
        itemAddPessoa.addActionListener(e -> abrirDialogoAddPessoa());
        
        menuPopup.add(itemAddTerritorio);
        menuPopup.add(itemAddDirigente); 
        menuPopup.add(itemAddPessoa); 

        btnMenu.addActionListener(e -> {
            menuPopup.show(btnMenu, 0, btnMenu.getHeight());
        });

        return btnMenu;
    }

    private JButton criarBotaoEstilizado(String texto, Color cor) {
        JButton botao = new JButton(texto);
        botao.setBackground(cor);
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setFont(new Font("Arial", Font.BOLD, 12));
        botao.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(cor.darker(), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return botao;
    }

    private JPanel criarPainelImagem() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Imagem do Território"),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        painel.setBackground(Color.WHITE);

        // Label para exibir imagem
        labelImagem = new JLabel("Nenhuma imagem selecionada", JLabel.CENTER);
        labelImagem.setVerticalTextPosition(JLabel.BOTTOM);
        labelImagem.setHorizontalTextPosition(JLabel.CENTER);
        labelImagem.setPreferredSize(new Dimension(1600, 1100));
        labelImagem.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        painel.add(labelImagem, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelTabela() {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBackground(Color.WHITE);

        // Título
        JLabel titulo = new JLabel(" Atribuições de Territórios - Ultimos Trabalhados no Topo");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(COR_SECUNDARIA);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        painel.add(titulo, BorderLayout.NORTH);

        // Modelo da tabela
        String[] colunas = {"Pessoa", "Território", "Bairro", "Data Início", "Data Fim", "Status", "Imagem"};
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tabelaAtribuicoes = new JTable(modeloTabela);
        tabelaAtribuicoes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaAtribuicoes.getTableHeader().setReorderingAllowed(false);
        tabelaAtribuicoes.getTableHeader().setBackground(COR_SECUNDARIA);
        tabelaAtribuicoes.getTableHeader().setForeground(Color.WHITE);
        tabelaAtribuicoes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Ajustar largura das colunas
        tabelaAtribuicoes.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabelaAtribuicoes.getColumnModel().getColumn(1).setPreferredWidth(150);
        tabelaAtribuicoes.getColumnModel().getColumn(2).setPreferredWidth(100);
        tabelaAtribuicoes.getColumnModel().getColumn(3).setPreferredWidth(80);
        tabelaAtribuicoes.getColumnModel().getColumn(4).setPreferredWidth(80);
        tabelaAtribuicoes.getColumnModel().getColumn(5).setPreferredWidth(100);
        tabelaAtribuicoes.getColumnModel().getColumn(6).setPreferredWidth(80);

        // Listener para quando selecionar uma linha
        tabelaAtribuicoes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                exibirImagemSelecionada();
            }
        });

        painel.add(new JScrollPane(tabelaAtribuicoes), BorderLayout.CENTER);
        return painel;
    }

    private void abrirDialogoDirigentes() {
        JDialog dialog = new JDialog(frame, "Gerenciar Dirigentes", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        // Tabela de dirigentes
        String[] colunas = {"Nome", "Telefone", "Bairro", "Territórios Permitidos"};
        DefaultTableModel modeloDirigentes = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable tabelaDirigentes = new JTable(modeloDirigentes);
        tabelaDirigentes.getTableHeader().setBackground(COR_SECUNDARIA);
        tabelaDirigentes.getTableHeader().setForeground(Color.WHITE);
        tabelaDirigentes.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Ajustar largura das colunas
        tabelaDirigentes.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabelaDirigentes.getColumnModel().getColumn(1).setPreferredWidth(120);
        tabelaDirigentes.getColumnModel().getColumn(2).setPreferredWidth(150);
        tabelaDirigentes.getColumnModel().getColumn(3).setPreferredWidth(250);

        // Botões
        JPanel painelBotoes = new JPanel();
        JButton btnAddDirigente = new JButton("Adicionar Dirigente");
        JButton btnEditarDirigente = new JButton("Editar Territórios");
        JButton btnRemoverDirigente = new JButton("Remover Dirigente");
        JButton btnFechar = new JButton("Fechar");

        btnAddDirigente.addActionListener(e -> {
            abrirDialogoAddDirigente();
            atualizarTabelaDirigentes(modeloDirigentes);
        });

        btnEditarDirigente.addActionListener(e -> {
            int linha = tabelaDirigentes.getSelectedRow();
            if (linha != -1) {
                Dirigente dirigente = service.getDirigentes().get(linha);
                abrirDialogoEditarTerritorios(dirigente);
                atualizarTabelaDirigentes(modeloDirigentes);
            } else {
                JOptionPane.showMessageDialog(dialog, "Selecione um dirigente para editar!", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnRemoverDirigente.addActionListener(e -> {
            int linha = tabelaDirigentes.getSelectedRow();
            if (linha != -1) {
                int confirmacao = JOptionPane.showConfirmDialog(dialog,
                    "Deseja remover este dirigente?",
                    "Confirmar Remoção", JOptionPane.YES_NO_OPTION);
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    service.getDirigentes().remove(linha);
                    service.salvarDados();
                    atualizarTabelaDirigentes(modeloDirigentes);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Selecione um dirigente para remover!", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnFechar.addActionListener(e -> dialog.dispose());

        painelBotoes.add(btnAddDirigente);
        painelBotoes.add(btnEditarDirigente);
        painelBotoes.add(btnRemoverDirigente);
        painelBotoes.add(btnFechar);

        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel titulo = new JLabel("Lista de Dirigentes");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setForeground(COR_SECUNDARIA);
        
        painelPrincipal.add(titulo, BorderLayout.NORTH);
        painelPrincipal.add(new JScrollPane(tabelaDirigentes), BorderLayout.CENTER);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        dialog.add(painelPrincipal);
        atualizarTabelaDirigentes(modeloDirigentes);
        dialog.setVisible(true);
    }

    private void atualizarTabelaDirigentes(DefaultTableModel modelo) {
        modelo.setRowCount(0);
        for (Dirigente dirigente : service.getDirigentes()) {
            StringBuilder territorios = new StringBuilder();
            for (String territorio : dirigente.getTerritoriosPermitidos()) {
                if (territorios.length() > 0) {
                    territorios.append(", ");
                }
                territorios.append(territorio);
            }
            modelo.addRow(new Object[]{
                dirigente.getNome(),
                dirigente.getTelefone(),
                dirigente.getBairro(),
                territorios.toString()
            });
        }
    }

    private void abrirDialogoAddDirigente() {
        JTextField txtNome = new JTextField(20);
        JTextField txtTelefone = new JTextField(20);
        JTextField txtBairro = new JTextField(20);
        
        JPanel painel = new JPanel(new GridLayout(3, 2, 5, 5));
        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);
        painel.add(new JLabel("Telefone:"));
        painel.add(txtTelefone);
        painel.add(new JLabel("Bairro:"));
        painel.add(txtBairro);
        
        int resultado = JOptionPane.showConfirmDialog(frame, painel, 
            "Adicionar Dirigente", JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION && !txtNome.getText().trim().isEmpty()) {
            service.adicionarDirigente(txtNome.getText().trim(), 
                                     txtTelefone.getText().trim(), 
                                     txtBairro.getText().trim());
            JOptionPane.showMessageDialog(frame, "Dirigente adicionado com sucesso!");
        }
    }
    
    private void abrirDialogoAddPessoa() {
        JTextField txtNome = new JTextField(20);
        JTextField txtTelefone = new JTextField(20);
        
        JPanel painel = new JPanel(new GridLayout(2, 2, 5, 5));
        painel.add(new JLabel("Nome:"));
        painel.add(txtNome);
        painel.add(new JLabel("Telefone:"));
        painel.add(txtTelefone);
        
        int resultado = JOptionPane.showConfirmDialog(frame, painel, 
            "Adicionar Pessoa", JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION && !txtNome.getText().trim().isEmpty()) {
            service.adicionarPessoa(txtNome.getText().trim(), txtTelefone.getText().trim());
            JOptionPane.showMessageDialog(frame, "Pessoa adicionada com sucesso!");
        }
    }


    private void abrirDialogoEditarTerritorios(Dirigente dirigente) {
        JTextArea txtTerritorios = new JTextArea(10, 30);
        txtTerritorios.setLineWrap(true);
        txtTerritorios.setWrapStyleWord(true);
        
        // Preencher com territórios existentes
        StringBuilder territorios = new StringBuilder();
        for (String territorio : dirigente.getTerritoriosPermitidos()) {
            if (territorios.length() > 0) {
                territorios.append("\n");
            }
            territorios.append(territorio);
        }
        txtTerritorios.setText(territorios.toString());
        
        JPanel painel = new JPanel(new BorderLayout());
        painel.add(new JLabel("Territórios Permitidos (um por linha):"), BorderLayout.NORTH);
        painel.add(new JScrollPane(txtTerritorios), BorderLayout.CENTER);
        
        int resultado = JOptionPane.showConfirmDialog(frame, painel, 
            "Editar Territórios para " + dirigente.getNome(), 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION) {
            // Limpar territórios existentes e adicionar novos
            dirigente.getTerritoriosPermitidos().clear();
            String[] novosTerritorios = txtTerritorios.getText().split("\n");
            for (String territorio : novosTerritorios) {
                String territorioLimpo = territorio.trim();
                if (!territorioLimpo.isEmpty()) {
                    dirigente.adicionarTerritorioPermitido(territorioLimpo);
                }
            }
            service.salvarDados();
            JOptionPane.showMessageDialog(frame, "Territórios atualizados com sucesso!");
        }
    }
    
    private void atualizarTabela() {
        modeloTabela.setRowCount(0);
        
        List<Atribuicao> atribuicoes = service.getAtribuicoesOrdenadas();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (Atribuicao a : atribuicoes) {
            String dataInicio = a.getDataInicio().format(formatter);
            String dataFim = a.getDataFim() != null ? a.getDataFim().format(formatter) : "---";
            String status = a.isAtiva() ? "EM ANDAMENTO" : "FINALIZADA";
            String temImagem = a.getTerritorio().getImagemPath() != null ? "📷" : "";
            
            modeloTabela.addRow(new Object[]{
                a.getPessoa().getNome(),
                "Território " + a.getTerritorio().getNumero(),
                a.getTerritorio().getBairro(),
                dataInicio,
                dataFim,
                status,
                temImagem
            });
        }
    }

    private void exibirImagemSelecionada() {
        int linhaSelecionada = tabelaAtribuicoes.getSelectedRow();
        if (linhaSelecionada != -1) {
            List<Atribuicao> atribuicoes = service.getAtribuicoesOrdenadas();
            Atribuicao selecionada = atribuicoes.get(linhaSelecionada);
            Territorio territorio = selecionada.getTerritorio();
            
            if (territorio.getImagemPath() != null && !territorio.getImagemPath().isEmpty()) {
                try {
                    ImageIcon imagemIcon = new ImageIcon(territorio.getImagemPath());
                    Image imagem = imagemIcon.getImage();
                    
                    // Redimensionar a imagem para caber no painel
                    Image novaImagem = imagem.getScaledInstance(1600, 1100, Image.SCALE_SMOOTH);
                    labelImagem.setIcon(new ImageIcon(novaImagem));
                    labelImagem.setText("Território " + territorio.getNumero() + " - " + territorio.getBairro());
                } catch (Exception e) {
                    labelImagem.setIcon(null);
                    labelImagem.setText("Erro ao carregar imagem");
                }
            } else {
                labelImagem.setIcon(null);
                labelImagem.setText("Nenhuma imagem para este território");
            }
        }
    }

    // NOVO MÉTODO: Adicionar território com imagem
    private void abrirDialogoAddTerritorioComImagem() {
        JTextField txtNumero = new JTextField(10);
        JTextField txtBairro = new JTextField(20);
        JTextField txtDescricao = new JTextField(20);
        JButton btnSelecionarImagem = new JButton("Selecionar Imagem");
        JLabel labelInfoImagem = new JLabel("Nenhuma imagem selecionada");
        
        String[] caminhoImagem = { null }; // Array para armazenar o caminho

        btnSelecionarImagem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Selecionar Imagem do Território");
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Imagens", "jpg", "jpeg", "png", "gif", "bmp"));

            int resultado = fileChooser.showOpenDialog(frame);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File arquivo = fileChooser.getSelectedFile();
                caminhoImagem[0] = arquivo.getAbsolutePath();
                labelInfoImagem.setText(arquivo.getName());
            }
        });

        JPanel painel = new JPanel(new GridLayout(5, 2, 5, 5));
        painel.add(new JLabel("Número:"));
        painel.add(txtNumero);
        painel.add(new JLabel("Bairro:"));
        painel.add(txtBairro);
        painel.add(new JLabel("Descrição:"));
        painel.add(txtDescricao);
        painel.add(new JLabel("Imagem:"));
        painel.add(btnSelecionarImagem);
        painel.add(new JLabel(""));
        painel.add(labelInfoImagem);

        int resultado = JOptionPane.showConfirmDialog(frame, painel, 
            "Adicionar Território", JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION && !txtBairro.getText().trim().isEmpty()) {
            try {
                int numero = Integer.parseInt(txtNumero.getText().trim());
                Territorio novoTerritorio = new Territorio(numero, txtBairro.getText().trim(), txtDescricao.getText().trim());
                
                // Adicionar imagem se foi selecionada
                if (caminhoImagem[0] != null) {
                    novoTerritorio.setImagemPath(caminhoImagem[0]);
                }
                
                service.adicionarTerritorio(novoTerritorio);
                JOptionPane.showMessageDialog(frame, "Território adicionado com sucesso!");
                atualizarTabela();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Número inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    @SuppressWarnings("unused")
	private void abrirDialogoAddTerritorio() {
        abrirDialogoAddTerritorioComImagem();
    }

    private void abrirDialogoAtribuicao() {
        List<Pessoa> pessoas = service.getPessoas();
        List<Territorio> territorios = service.getTerritoriosDisponiveis();
        
        if (pessoas.isEmpty() || territorios.isEmpty()) {
            JOptionPane.showMessageDialog(frame, 
                pessoas.isEmpty() ? "Nenhuma pessoa cadastrada!" : "Nenhum território disponível!",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JComboBox<Pessoa> comboPessoas = new JComboBox<>(pessoas.toArray(new Pessoa[0]));
        comboPessoas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pessoa) {
                    Pessoa p = (Pessoa) value;
                    setText(p.getNome() + " - " + p.getTelefone());
                }
                return this;
            }
        });
        
        JComboBox<Territorio> comboTerritorios = new JComboBox<>(territorios.toArray(new Territorio[0]));
        
        JPanel painel = new JPanel(new GridLayout(2, 2, 5, 5));
        painel.add(new JLabel("Pessoa:"));
        painel.add(comboPessoas);
        painel.add(new JLabel("Território:"));
        painel.add(comboTerritorios);
        
        int resultado = JOptionPane.showConfirmDialog(frame, painel, 
            "Atribuir Território", JOptionPane.OK_CANCEL_OPTION);
        
        if (resultado == JOptionPane.OK_OPTION) {
            Pessoa pessoa = (Pessoa) comboPessoas.getSelectedItem();
            Territorio territorio = (Territorio) comboTerritorios.getSelectedItem();
            
            if (pessoa != null && territorio != null) {
                boolean sucesso = service.atribuirTerritorio(pessoa, territorio, java.time.LocalDate.now());
                if (sucesso) {
                    JOptionPane.showMessageDialog(frame, "Território atribuído com sucesso!");
                    atualizarTabela();
                }
            }
        }
    }

    private void finalizarAtribuicao() {
        int linhaSelecionada = tabelaAtribuicoes.getSelectedRow();
        if (linhaSelecionada == -1) {
            JOptionPane.showMessageDialog(frame, "Selecione uma atribuição para finalizar!", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        List<Atribuicao> atribuicoes = service.getAtribuicoesOrdenadas();
        Atribuicao selecionada = atribuicoes.get(linhaSelecionada);
        
        if (!selecionada.isAtiva()) {
            JOptionPane.showMessageDialog(frame, "Esta atribuição já está finalizada!", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacao = JOptionPane.showConfirmDialog(frame,
            "Deseja finalizar o território: " + selecionada.getTerritorio() + "?",
            "Confirmar Finalização", JOptionPane.YES_NO_OPTION);
        
        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean sucesso = service.finalizarAtribuicao(selecionada.getTerritorio(), java.time.LocalDate.now());
            if (sucesso) {
                JOptionPane.showMessageDialog(frame, "Território finalizado com sucesso!");
                atualizarTabela();
            }
        }
    }
}
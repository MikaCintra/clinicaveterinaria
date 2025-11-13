package com.example.clinica.ui;

import com.example.clinica.service.AnimalService;
import com.example.clinica.service.ConsultaService;
import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    private final AnimalService animalService;
    private final ConsultaService consultaService;
    private final JPanel contentPanel;
    private ListagemAnimaisPanel listagemAnimaisPanel;
    private CadastroAnimalPanel cadastroAnimalPanel;
    private ListagemConsultasPanel listagemConsultasPanel;
    private CadastroConsultaPanel cadastroConsultaPanel;

    public MainWindow() {
        super("Clínica Veterinária");
        this.animalService = new AnimalService();
        this.consultaService = new ConsultaService();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Menu principal
        JMenuBar menuBar = new JMenuBar();
        
        JMenu menuAnimais = new JMenu("Animais");
        JMenuItem menuCadastrarAnimal = new JMenuItem("Cadastrar");
        JMenuItem menuListarAnimais = new JMenuItem("Listar");
        menuCadastrarAnimal.addActionListener(e -> showCadastroAnimalPanel());
        menuListarAnimais.addActionListener(e -> showListagemAnimaisPanel());
        menuAnimais.add(menuCadastrarAnimal);
        menuAnimais.add(menuListarAnimais);
        
        JMenu menuConsultas = new JMenu("Consultas");
        JMenuItem menuCadastrarConsulta = new JMenuItem("Cadastrar");
        JMenuItem menuListarConsultas = new JMenuItem("Listar");
        menuCadastrarConsulta.addActionListener(e -> showCadastroConsultaPanel());
        menuListarConsultas.addActionListener(e -> showListagemConsultasPanel());
        menuConsultas.add(menuCadastrarConsulta);
        menuConsultas.add(menuListarConsultas);
        
        JMenu menuArquivo = new JMenu("Arquivo");
        JMenuItem menuSair = new JMenuItem("Sair");
        menuSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(menuSair);
        
        menuBar.add(menuAnimais);
        menuBar.add(menuConsultas);
        menuBar.add(menuArquivo);
        setJMenuBar(menuBar);

        // Toolbar com botões principais
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        
        // Botões de Animais
        JButton btnCadastrarAnimal = new JButton("Cadastrar Animal");
        JButton btnListarAnimais = new JButton("Listar Animais");
        btnCadastrarAnimal.addActionListener(e -> showCadastroAnimalPanel());
        btnListarAnimais.addActionListener(e -> showListagemAnimaisPanel());
        
        // Botões de Consultas
        JButton btnCadastrarConsulta = new JButton("Cadastrar Consulta");
        JButton btnListarConsultas = new JButton("Listar Consultas");
        btnCadastrarConsulta.addActionListener(e -> showCadastroConsultaPanel());
        btnListarConsultas.addActionListener(e -> showListagemConsultasPanel());
        
        toolbar.add(btnCadastrarAnimal);
        toolbar.add(btnListarAnimais);
        toolbar.addSeparator();
        toolbar.add(btnCadastrarConsulta);
        toolbar.add(btnListarConsultas);
        
        add(toolbar, BorderLayout.NORTH);

        // Painel de conteúdo central (card layout para alternar entre painéis)
        contentPanel = new JPanel(new CardLayout());
        add(contentPanel, BorderLayout.CENTER);

        // Inicializar painel de listagem de animais
        listagemAnimaisPanel = new ListagemAnimaisPanel(animalService);

        // Inicializar painéis de Consultas primeiro para que possamos passar um
        // callback ao painel de cadastro de animal que atualize também o combo
        cadastroConsultaPanel = new CadastroConsultaPanel(consultaService, animalService, this::showListagemConsultasPanel);
        listagemConsultasPanel = new ListagemConsultasPanel(consultaService, animalService);

        // Inicializar painel de Cadastro de Animal. O callback agora também
        // força atualização do combo de `cadastroConsultaPanel` imediatamente
        cadastroAnimalPanel = new CadastroAnimalPanel(animalService, () -> {
            showListagemAnimaisPanel();
            cadastroConsultaPanel.refreshAnimals();
        });
        
        // Adicionar painéis ao card layout
        contentPanel.add(cadastroAnimalPanel, "CADASTRO_ANIMAL");
        contentPanel.add(listagemAnimaisPanel, "LISTAGEM_ANIMAIS");
        contentPanel.add(cadastroConsultaPanel, "CADASTRO_CONSULTA");
        contentPanel.add(listagemConsultasPanel, "LISTAGEM_CONSULTAS");

        // Mostrar listagem inicial
        showListagemAnimaisPanel();
    }

    private void showCadastroAnimalPanel() {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "CADASTRO_ANIMAL");
    }

    private void showListagemAnimaisPanel() {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "LISTAGEM_ANIMAIS");
        listagemAnimaisPanel.refreshTable();
    }

    private void showCadastroConsultaPanel() {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        // Recarrega animais antes de mostrar para garantir que novos animais apareçam no combo
        cadastroConsultaPanel.refreshAnimals();
        cl.show(contentPanel, "CADASTRO_CONSULTA");
    }

    private void showListagemConsultasPanel() {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "LISTAGEM_CONSULTAS");
        listagemConsultasPanel.refreshTable();
    }

    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, "Erro ao definir LookAndFeel", e);
        }
        
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
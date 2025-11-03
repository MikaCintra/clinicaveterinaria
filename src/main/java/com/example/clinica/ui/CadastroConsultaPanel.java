package com.example.clinica.ui;

import com.example.clinica.model.Animal;
import com.example.clinica.model.Consulta;
import com.example.clinica.service.AnimalService;
import com.example.clinica.service.ConsultaService;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class CadastroConsultaPanel extends JPanel {
    private final ConsultaService consultaService;
    private final AnimalService animalService;
    private final JComboBox<AnimalItem> comboAnimal;
    private final JTextArea txtDescricao;
    private final Runnable onSaveCallback;

    public CadastroConsultaPanel(ConsultaService consultaService, AnimalService animalService, Runnable onSaveCallback) {
        this.consultaService = consultaService;
        this.animalService = animalService;
        this.onSaveCallback = onSaveCallback;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Painel do formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo Animal
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Animal:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comboAnimal = new JComboBox<>();
        formPanel.add(comboAnimal, gbc);

        // Campo Descrição
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtDescricao = new JTextArea(5, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDescricao);
        formPanel.add(scrollPane, gbc);

        // Botão Salvar
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> salvarConsulta());
        formPanel.add(btnSalvar, gbc);

        // Adiciona o formulário ao centro
        add(formPanel, BorderLayout.CENTER);

        // Carrega a lista de animais
        carregarAnimais();
    }

    private void carregarAnimais() {
        SwingWorker<List<Animal>, Void> worker = new SwingWorker<List<Animal>, Void>() {
            @Override
            protected List<Animal> doInBackground() {
                return animalService.listarAnimais();
            }

            @Override
            protected void done() {
                try {
                    List<Animal> animais = get();
                    comboAnimal.removeAllItems();
                    for (Animal animal : animais) {
                        comboAnimal.addItem(new AnimalItem(animal));
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CadastroConsultaPanel.this,
                        "Erro ao carregar lista de animais: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void salvarConsulta() {
        AnimalItem animalItem = (AnimalItem) comboAnimal.getSelectedItem();
        if (animalItem == null) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecione um animal",
                "Campo obrigatório",
                JOptionPane.WARNING_MESSAGE);
            comboAnimal.requestFocus();
            return;
        }

        String descricao = txtDescricao.getText().trim();
        if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, informe a descrição da consulta",
                "Campo obrigatório",
                JOptionPane.WARNING_MESSAGE);
            txtDescricao.requestFocus();
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                consultaService.cadastrarConsulta(animalItem.animal.getId(), descricao);
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    get(); // Verifica se houve exceção
                    JOptionPane.showMessageDialog(CadastroConsultaPanel.this,
                        "Consulta registrada com sucesso!",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    txtDescricao.setText("");
                    if (onSaveCallback != null) {
                        onSaveCallback.run();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CadastroConsultaPanel.this,
                        "Erro ao salvar consulta: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // Classe auxiliar para exibir os animais no ComboBox
    private static class AnimalItem {
        private final Animal animal;

        public AnimalItem(Animal animal) {
            this.animal = animal;
        }

        @Override
        public String toString() {
            return String.format("%d - %s (%s)", animal.getId(), animal.getNome(), animal.getEspecie());
        }
    }
}
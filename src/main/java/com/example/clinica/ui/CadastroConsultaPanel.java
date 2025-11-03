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
    private final JTextField txtData;
    private final Runnable onSaveCallback;
    private final JButton btnSalvar;
    private Consulta editingConsulta = null;

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

    // Campo Data
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    formPanel.add(new JLabel("Data (dd/MM/yyyy):"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtData = new JTextField(10);
    txtData.setToolTipText("Formato: dd/MM/yyyy");
    formPanel.add(txtData, gbc);

        // Botão Salvar
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
    btnSalvar = new JButton("Salvar");
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
        String dataStr = txtData.getText().trim();
        LocalDate data;
        try {
            if (dataStr.isEmpty()) {
                data = LocalDate.now();
            } else {
                String[] parts = dataStr.split("/\\s*");
                if (parts.length != 3) throw new IllegalArgumentException("Formato de data inválido");
                int d = Integer.parseInt(parts[0]);
                int m = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                data = LocalDate.of(y, m, d);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use dd/MM/yyyy", "Erro", JOptionPane.WARNING_MESSAGE);
            txtData.requestFocus();
            return;
        }
        if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, informe a descrição da consulta",
                "Campo obrigatório",
                JOptionPane.WARNING_MESSAGE);
            txtDescricao.requestFocus();
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                if (editingConsulta == null) {
                    consultaService.cadastrarConsulta(animalItem.animal.getId(), descricao);
                    return true;
                } else {
                    editingConsulta.setIdAnimal(animalItem.animal.getId());
                    editingConsulta.setDescricao(descricao);
                    editingConsulta.setData(data);
                    return consultaService.atualizarConsulta(editingConsulta);
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    boolean ok = get();
                    if (ok) {
                        String msg = (editingConsulta == null) ? "Consulta registrada com sucesso!" : "Consulta atualizada com sucesso!";
                        JOptionPane.showMessageDialog(CadastroConsultaPanel.this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        txtDescricao.setText("");
                        txtData.setText("");
                        editingConsulta = null;
                        btnSalvar.setText("Salvar");
                        if (onSaveCallback != null) {
                            onSaveCallback.run();
                        }
                    } else {
                        JOptionPane.showMessageDialog(CadastroConsultaPanel.this, "Não foi possível salvar/atualizar a consulta.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CadastroConsultaPanel.this, "Erro ao salvar consulta: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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

    public void loadConsulta(Consulta c) {
        if (c == null) return;
        this.editingConsulta = c;
        // selecionar animal
        for (int i = 0; i < comboAnimal.getItemCount(); i++) {
            AnimalItem it = comboAnimal.getItemAt(i);
            if (it.animal.getId() == c.getIdAnimal()) {
                comboAnimal.setSelectedIndex(i);
                break;
            }
        }
        txtDescricao.setText(c.getDescricao());
        txtData.setText(String.format("%02d/%02d/%04d", c.getData().getDayOfMonth(), c.getData().getMonthValue(), c.getData().getYear()));
        btnSalvar.setText("Atualizar");
    }

    /**
     * Recarrega a lista de animais no combo. Público para que outras telas possam forçar atualização.
     */
    public void refreshAnimals() {
        carregarAnimais();
    }
}
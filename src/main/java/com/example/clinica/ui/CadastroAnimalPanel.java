package com.example.clinica.ui;

import com.example.clinica.model.Animal;
import com.example.clinica.service.AnimalService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class CadastroAnimalPanel extends JPanel {
    private final AnimalService animalService;
    private final JTextField txtNome;
    private final JComboBox<String> comboEspecie;
    private final Runnable onSaveCallback;

    public CadastroAnimalPanel(AnimalService animalService, Runnable onSaveCallback) {
        this.animalService = animalService;
        this.onSaveCallback = onSaveCallback;
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Cadastro de Animal"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Painel do formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Campo Nome
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        txtNome.setToolTipText("Digite o nome do animal");
        formPanel.add(txtNome, gbc);

        // Campo Espécie
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Espécie:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] especies = {"Cachorro", "Gato", "Pássaro", "Coelho", "Outro"};
        comboEspecie = new JComboBox<>(especies);
        comboEspecie.setEditable(true);
        formPanel.add(comboEspecie, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
        btnSalvar.addActionListener(e -> salvarAnimal());
        
        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setIcon(UIManager.getIcon("FileView.fileIcon"));
        btnLimpar.addActionListener(e -> limparCampos());
        
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnLimpar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        // Adicionar ação ENTER nos campos
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        
        String enterKey = "enter";
        inputMap.put(KeyStroke.getKeyStroke("ENTER"), enterKey);
        actionMap.put(enterKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarAnimal();
            }
        });

        // Adiciona o formulário ao centro
        add(formPanel, BorderLayout.CENTER);
    }

    private void salvarAnimal() {
        String nome = txtNome.getText().trim();
        String especie = comboEspecie.getSelectedItem().toString().trim();

        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, informe o nome do animal",
                "Campo obrigatório",
                JOptionPane.WARNING_MESSAGE);
            txtNome.requestFocus();
            return;
        }

        if (especie.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecione ou digite a espécie do animal",
                "Campo obrigatório",
                JOptionPane.WARNING_MESSAGE);
            comboEspecie.requestFocus();
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Animal, Void> worker = new SwingWorker<Animal, Void>() {
            @Override
            protected Animal doInBackground() throws Exception {
                Animal animal = new Animal(nome, especie);
                animalService.cadastrarAnimal(animal);
                return animal;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    Animal savedAnimal = get();
                    JOptionPane.showMessageDialog(CadastroAnimalPanel.this,
                        "Animal cadastrado com sucesso!\nID gerado: " + savedAnimal.getId(),
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                    limparCampos();
                    if (onSaveCallback != null) {
                        onSaveCallback.run();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CadastroAnimalPanel.this,
                        "Erro ao salvar animal: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void limparCampos() {
        txtNome.setText("");
        comboEspecie.setSelectedIndex(0);
        txtNome.requestFocus();
    }
}
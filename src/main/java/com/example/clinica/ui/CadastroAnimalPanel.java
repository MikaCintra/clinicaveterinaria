package com.example.clinica.ui;

import com.example.clinica.model.Animal;
import com.example.clinica.service.AnimalService;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import com.example.clinica.util.PhoneUtils;

public class CadastroAnimalPanel extends JPanel {
    private final AnimalService animalService;
    private final JTextField txtNome;
    private final JTextField txtDono;
    private final JTextField txtTelefone;
    private final JComboBox<String> comboEspecie;
    private final Runnable onSaveCallback;
    private final JButton btnSalvar;
    private Animal editingAnimal = null;

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

    // Campo Dono
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weightx = 0;
    formPanel.add(new JLabel("Nome do dono:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
    txtDono = new JTextField(20);
    txtDono.setToolTipText("Nome do responsável pelo animal");
    formPanel.add(txtDono, gbc);

    // Campo Telefone
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weightx = 0;
    formPanel.add(new JLabel("Telefone de contato:"), gbc);

    gbc.gridx = 1;
    gbc.weightx = 1.0;
        txtTelefone = new JTextField(20);
        txtTelefone.setToolTipText("Telefone para contato");
        // Aplica filtro para aceitar apenas dígitos enquanto digita
        ((AbstractDocument) txtTelefone.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, javax.swing.text.AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                String filtered = string.replaceAll("\\D", "");
                super.insertString(fb, offset, filtered, attr);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                String filtered = text.replaceAll("\\D", "");
                super.replace(fb, offset, length, filtered, attrs);
            }
        });
        // Formata visualmente ao perder foco e remove formatação ao focar
        txtTelefone.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                String d = PhoneUtils.onlyDigits(txtTelefone.getText());
                txtTelefone.setText(PhoneUtils.format(d));
            }

            @Override
            public void focusGained(FocusEvent e) {
                // mostra apenas dígitos para facilitar edição
                txtTelefone.setText(PhoneUtils.onlyDigits(txtTelefone.getText()));
            }
        });
        formPanel.add(txtTelefone, gbc);

        // Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        
    btnSalvar = new JButton("Salvar");
    btnSalvar.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
    btnSalvar.addActionListener(e -> salvarAnimal());
        
        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setIcon(UIManager.getIcon("FileView.fileIcon"));
        btnLimpar.addActionListener(e -> limparCampos());
        
        buttonPanel.add(btnSalvar);
        buttonPanel.add(btnLimpar);

    gbc.gridx = 0;
    gbc.gridy = 4;
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
        // Atalho ESC para limpar campos
        String escKey = "esc";
        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), escKey);
        actionMap.put(escKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limparCampos();
            }
        });

        // Adiciona o formulário ao centro
        add(formPanel, BorderLayout.CENTER);
    }

    private void salvarAnimal() {
    String nome = txtNome.getText().trim();
    Object sel = comboEspecie.getSelectedItem();
    String especie = sel == null ? "" : sel.toString().trim();
    String dono = txtDono.getText().trim();
    String telefone = txtTelefone.getText().trim();

        // Validações: nome do animal, dono e telefone
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

        if (dono.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, informe o nome do dono/responsável",
                "Campo obrigatório",
                JOptionPane.WARNING_MESSAGE);
            txtDono.requestFocus();
            return;
        }

    // Normaliza telefone retirando tudo que não seja dígito
    String apenasDigitos = PhoneUtils.onlyDigits(telefone);
        if (apenasDigitos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor, informe o telefone do dono",
                "Campo obrigatório",
                JOptionPane.WARNING_MESSAGE);
            txtTelefone.requestFocus();
            return;
        }
        // Aceitamos números com 10 (fixo) ou 11 (com 9 móvel) dígitos
        if (!(apenasDigitos.length() == 10 || apenasDigitos.length() == 11)) {
            JOptionPane.showMessageDialog(this,
                "Telefone inválido. Informe 10 ou 11 dígitos (ex.: DDD + número)",
                "Telefone inválido",
                JOptionPane.WARNING_MESSAGE);
            txtTelefone.requestFocus();
            return;
        }
    // Armazenamos o telefone como apenas dígitos
    telefone = apenasDigitos;
    final String telefoneFinal = apenasDigitos;

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Animal, Void> worker = new SwingWorker<Animal, Void>() {
            @Override
            protected Animal doInBackground() throws Exception {
                if (editingAnimal == null) {
                    Animal animal = new Animal(nome, especie, dono, telefoneFinal);
                    animalService.cadastrarAnimal(animal);
                    return animal;
                } else {
                    editingAnimal.setNome(nome);
                    editingAnimal.setEspecie(especie);
                    editingAnimal.setDono(dono);
                    editingAnimal.setTelefone(telefoneFinal);
                    boolean ok = animalService.atualizarAnimal(editingAnimal);
                    return ok ? editingAnimal : null;
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    Animal result = get();
                    if (result != null) {
                        String msg = (editingAnimal == null) ? "Animal cadastrado com sucesso!\nID gerado: " + result.getId() : "Animal atualizado com sucesso!";
                        JOptionPane.showMessageDialog(CadastroAnimalPanel.this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        limparCampos();
                        editingAnimal = null;
                        btnSalvar.setText("Salvar");
                        if (onSaveCallback != null) {
                            onSaveCallback.run();
                        }
                    } else {
                        JOptionPane.showMessageDialog(CadastroAnimalPanel.this, "Não foi possível salvar/atualizar o animal.", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(CadastroAnimalPanel.this, "Erro ao salvar animal: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void limparCampos() {
        txtNome.setText("");
        comboEspecie.setSelectedIndex(0);
        txtDono.setText("");
        txtTelefone.setText("");
        editingAnimal = null;
        btnSalvar.setText("Salvar");
        txtNome.requestFocus();
    }

    public void loadAnimal(Animal animal) {
        if (animal == null) return;
        this.editingAnimal = animal;
        txtNome.setText(animal.getNome());
        comboEspecie.setSelectedItem(animal.getEspecie());
        txtDono.setText(animal.getDono() == null ? "" : animal.getDono());
        txtTelefone.setText(animal.getTelefone() == null ? "" : PhoneUtils.format(animal.getTelefone()));
        btnSalvar.setText("Atualizar");
    }
}
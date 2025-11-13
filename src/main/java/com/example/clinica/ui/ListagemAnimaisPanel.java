package com.example.clinica.ui;

import com.example.clinica.model.Animal;
import com.example.clinica.service.AnimalService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ListagemAnimaisPanel extends JPanel {
    private final AnimalService animalService;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public ListagemAnimaisPanel(AnimalService animalService) {
        this.animalService = animalService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // Modelo da tabela
    String[] colunas = {"ID", "Nome", "Espécie", "Dono", "Telefone"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        if (table.getColumnModel().getColumnCount() > 3) {
            table.getColumnModel().getColumn(1).setMaxWidth(200);
            table.getColumnModel().getColumn(2).setMaxWidth(120);
            table.getColumnModel().getColumn(3).setMaxWidth(200);
            table.getColumnModel().getColumn(4).setMaxWidth(120);
        }

        // Scroll pane para a tabela
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnBuscar = new JButton("Buscar por ID");
        btnBuscar.addActionListener(e -> buscarPorId());
        
        JButton btnExibir = new JButton("Exibir");
        btnExibir.addActionListener(e -> exibirAnimalSelecionado());
        
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirAnimalSelecionado());
        
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> refreshTable());
        
    JButton btnEditar = new JButton("Editar");
    btnEditar.addActionListener(e -> editarAnimalSelecionado());

        buttonPanel.add(btnBuscar);
        buttonPanel.add(btnExibir);
        buttonPanel.add(btnExcluir);
    buttonPanel.add(btnEditar);
        buttonPanel.add(btnAtualizar);

        add(buttonPanel, BorderLayout.SOUTH);
        
        // Carregar dados iniciais
        refreshTable();
    }

    public void refreshTable() {
        SwingWorker<List<Animal>, Void> worker = new SwingWorker<List<Animal>, Void>() {
            @Override
            protected List<Animal> doInBackground() {
                return animalService.listarAnimais();
            }

            @Override
            protected void done() {
                try {
                    List<Animal> animais = get();
                    tableModel.setRowCount(0);
                    for (Animal animal : animais) {
                        String telefoneFmt = animal.getTelefone() == null ? "" : com.example.clinica.util.PhoneUtils.format(animal.getTelefone());
                        tableModel.addRow(new Object[]{
                            animal.getId(),
                            animal.getNome(),
                            animal.getEspecie(),
                            animal.getDono(),
                            telefoneFmt
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ListagemAnimaisPanel.this,
                        "Erro ao carregar animais: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void exibirAnimalSelecionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecione um animal para exibir",
                "Seleção Necessária",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

    int id = (int) table.getValueAt(row, 0);
    String nome = (String) table.getValueAt(row, 1);
    String especie = (String) table.getValueAt(row, 2);
    String dono = table.getValueAt(row, 3) == null ? "" : (String) table.getValueAt(row, 3);
    String telefone = table.getValueAt(row, 4) == null ? "" : (String) table.getValueAt(row, 4);

    String mensagem = String.format("ID: %d\nNome: %s\nEspécie: %s\nDono: %s\nTelefone: %s", id, nome, especie, dono, telefone);

        JOptionPane.showMessageDialog(this,
            mensagem,
            "Detalhes do Animal",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarAnimalSelecionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecione um animal para editar",
                "Seleção Necessária",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

    int id = (int) table.getValueAt(row, 0);
    String nome = (String) table.getValueAt(row, 1);
    String especie = (String) table.getValueAt(row, 2);
    String dono = table.getValueAt(row, 3) == null ? "" : (String) table.getValueAt(row, 3);
    String telefone = table.getValueAt(row, 4) == null ? "" : (String) table.getValueAt(row, 4);

    JTextField nomeField = new JTextField(nome);
    JComboBox<String> especieBox = new JComboBox<>(new String[] {"Cachorro","Gato","Pássaro","Coelho","Outro"});
        especieBox.setEditable(true);
        especieBox.setSelectedItem(especie);

    JTextField donoField = new JTextField(dono);
    JTextField telefoneField = new JTextField(telefone);

    JPanel panel = new JPanel(new GridLayout(0,1,5,5));
    panel.add(new JLabel("Nome:"));
    panel.add(nomeField);
    panel.add(new JLabel("Espécie:"));
    panel.add(especieBox);
    panel.add(new JLabel("Nome do dono:"));
    panel.add(donoField);
    panel.add(new JLabel("Telefone de contato:"));
    panel.add(telefoneField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Editar Animal", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            String novoNome = nomeField.getText().trim();
            String novaEspecie = especieBox.getSelectedItem().toString().trim();
            if (novoNome.isEmpty() || novaEspecie.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nome e espécie são obrigatórios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                    // Ao salvar, garantimos enviar somente dígitos para persistência
                    String telDigits = com.example.clinica.util.PhoneUtils.onlyDigits(telefoneField.getText());
                    Animal a = new Animal(id, novoNome, novaEspecie, donoField.getText().trim(), telDigits);
                return animalService.atualizarAnimal(a);
            }

                @Override
                protected void done() {
                    setCursor(Cursor.getDefaultCursor());
                    try {
                        boolean ok = get();
                        if (ok) {
                            JOptionPane.showMessageDialog(ListagemAnimaisPanel.this, "Animal atualizado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(ListagemAnimaisPanel.this, "Não foi possível atualizar o animal.", "Erro", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ListagemAnimaisPanel.this, "Erro ao atualizar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void excluirAnimalSelecionado() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Selecione um animal para excluir",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        String nome = (String) table.getValueAt(row, 1);

        int confirma = JOptionPane.showConfirmDialog(this,
            "Deseja realmente excluir o animal '" + nome + "' (ID: " + id + ")?",
            "Confirmar exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);

        if (confirma == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return animalService.excluirAnimal(id);
                }

                @Override
                protected void done() {
                    try {
                        boolean sucesso = get();
                        if (sucesso) {
                            tableModel.removeRow(row);
                            JOptionPane.showMessageDialog(ListagemAnimaisPanel.this,
                                "Animal excluído com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(ListagemAnimaisPanel.this,
                                "Não foi possível excluir o animal.\nVerifique se não existem consultas associadas.",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ListagemAnimaisPanel.this,
                            "Erro ao excluir animal: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void buscarPorId() {
        String input = JOptionPane.showInputDialog(this, 
            "Digite o ID do animal:", 
            "Buscar Animal", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (input == null || input.trim().isEmpty()) {
            return;
        }
        
        try {
            int id = Integer.parseInt(input.trim());
            
            SwingWorker<Animal, Void> worker = new SwingWorker<Animal, Void>() {
                @Override
                protected Animal doInBackground() {
                    return animalService.buscarPorId(id);
                }
                
                @Override
                protected void done() {
                    try {
                        Animal animal = get();
                        
                        if (animal == null) {
                            JOptionPane.showMessageDialog(ListagemAnimaisPanel.this,
                                "Animal não encontrado com ID: " + id,
                                "Não encontrado",
                                JOptionPane.WARNING_MESSAGE);
                        } else {
                            String telefone = animal.getTelefone() == null ? "" 
                                : com.example.clinica.util.PhoneUtils.format(animal.getTelefone());
                            
                            String mensagem = String.format(
                                "ID: %d\nNome: %s\nEspécie: %s\nDono: %s\nTelefone: %s",
                                animal.getId(),
                                animal.getNome(),
                                animal.getEspecie(),
                                animal.getDono(),
                                telefone
                            );
                            
                            JOptionPane.showMessageDialog(ListagemAnimaisPanel.this,
                                mensagem,
                                "Animal Encontrado",
                                JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ListagemAnimaisPanel.this,
                            "Erro ao buscar animal: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "ID inválido. Digite apenas números.",
                "Erro de Validação",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

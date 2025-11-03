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
        String[] colunas = {"ID", "Nome", "Espécie"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);

        // Scroll pane para a tabela
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnExibir = new JButton("Exibir");
        btnExibir.addActionListener(e -> exibirAnimalSelecionado());
        
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirAnimalSelecionado());
        
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> refreshTable());

        buttonPanel.add(btnExibir);
        buttonPanel.add(btnExcluir);
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
                        tableModel.addRow(new Object[]{
                            animal.getId(),
                            animal.getNome(),
                            animal.getEspecie()
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

        JOptionPane.showMessageDialog(this,
            String.format("Detalhes do Animal:\nID: %d\nNome: %s\nEspécie: %s",
                id, nome, especie),
            "Informações do Animal",
            JOptionPane.INFORMATION_MESSAGE);

        String mensagem = String.format(
            "ID: %d\nNome: %s\nEspécie: %s",
            id, nome, especie
        );

        JOptionPane.showMessageDialog(this,
            mensagem,
            "Detalhes do Animal",
            JOptionPane.INFORMATION_MESSAGE);
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
}
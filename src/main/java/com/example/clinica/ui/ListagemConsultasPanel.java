package com.example.clinica.ui;

import com.example.clinica.model.Consulta;
import com.example.clinica.service.ConsultaService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ListagemConsultasPanel extends JPanel {
    private final ConsultaService consultaService;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ListagemConsultasPanel(ConsultaService consultaService) {
        this.consultaService = consultaService;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Modelo da tabela
        String[] colunas = {"ID", "ID Animal", "Data", "Descrição"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setMaxWidth(70);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setMaxWidth(100);

        // Scroll pane para a tabela
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton btnExibir = new JButton("Exibir");
        btnExibir.addActionListener(e -> exibirConsultaSelecionada());
        
        JButton btnExcluir = new JButton("Excluir");
        btnExcluir.addActionListener(e -> excluirConsultaSelecionada());
        
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
        SwingWorker<List<Consulta>, Void> worker = new SwingWorker<List<Consulta>, Void>() {
            @Override
            protected List<Consulta> doInBackground() {
                return consultaService.listarConsultas();
            }

            @Override
            protected void done() {
                try {
                    List<Consulta> consultas = get();
                    tableModel.setRowCount(0);
                    for (Consulta consulta : consultas) {
                        tableModel.addRow(new Object[]{
                            consulta.getId(),
                            consulta.getIdAnimal(),
                            consulta.getData().format(dateFormatter),
                            consulta.getDescricao()
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ListagemConsultasPanel.this,
                        "Erro ao carregar consultas: " + ex.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void exibirConsultaSelecionada() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecione uma consulta para exibir",
                "Seleção Necessária",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        int idAnimal = (int) table.getValueAt(row, 1);
        String data = (String) table.getValueAt(row, 2);
        String descricao = (String) table.getValueAt(row, 3);

        JOptionPane.showMessageDialog(this,
            String.format("Detalhes da Consulta:\nID: %d\nID do Animal: %d\nData: %s\nDescrição: %s",
                id, idAnimal, data, descricao),
            "Informações da Consulta",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void excluirConsultaSelecionada() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                "Por favor, selecione uma consulta para excluir",
                "Seleção Necessária",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) table.getValueAt(row, 0);
        int option = JOptionPane.showConfirmDialog(this,
            "Tem certeza que deseja excluir esta consulta?",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION);

        if (option == JOptionPane.YES_OPTION) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() {
                    return consultaService.excluirConsulta(id);
                }

                @Override
                protected void done() {
                    setCursor(Cursor.getDefaultCursor());
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(ListagemConsultasPanel.this,
                                "Consulta excluída com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                            refreshTable();
                        } else {
                            JOptionPane.showMessageDialog(ListagemConsultasPanel.this,
                                "Não foi possível excluir a consulta",
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ListagemConsultasPanel.this,
                            "Erro ao excluir consulta: " + ex.getMessage(),
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
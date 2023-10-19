package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import DAO.ConexaoBDSistemaImpressora;

@SuppressWarnings("serial")
public class ConsultarRecursos extends JDialog {
	private JTable table;

	public ConsultarRecursos(JFrame parent) {
		super(parent, "Quantidade de Recursos Cadastrados", true);
		initComponents();
		loadTableData();
	}

	private void initComponents() {
		String[] columnNames = { "Modelo", "Número de Toners", "Número de Cilindros" };

		// Inicializa a tabela
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(500, 150));
		table.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(table);

		getContentPane().add(scrollPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}

	private void loadTableData() {
		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");

				String query = "SELECT modelo, SUM(toner) as tonerSum, SUM(cilindro) as cilindroSum FROM recursos GROUP BY modelo";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query);
						ResultSet resultSet = preparedStatement.executeQuery()) {

					// Atualiza os dados da tabela
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.setRowCount(0); // Limpa a tabela antes de adicionar os novos dados

					while (resultSet.next()) {
						String modelo = resultSet.getString("modelo");
						int tonerSum = resultSet.getInt("tonerSum");
						int cilindroSum = resultSet.getInt("cilindroSum");

						// Adiciona os dados à tabela
						Object[] rowData = { modelo, tonerSum, cilindroSum };
						model.addRow(rowData);
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			// Trate o erro de conexão ou consulta aqui
		}
	}
}

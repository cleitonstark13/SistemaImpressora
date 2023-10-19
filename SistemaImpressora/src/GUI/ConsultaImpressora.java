package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import DAO.ConexaoBDSistemaImpressora;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

@SuppressWarnings("serial")
public class ConsultaImpressora extends JDialog implements ActionListener {
	private JButton editarButton, fecharButton, excluirButton;
	private JTable table;

	public ConsultaImpressora(JFrame parent) {
		super(parent, "Impressoras Cadastradas", true);
		initComponents();
		loadTableData();
	}

	private void initComponents() {
		String[] columnNames = { "Marca", "Modelo", "Número de Série", "Data da Aquisição", "IP", "Scanner" };
		ImageIcon imagemTituloJanela = new ImageIcon(getClass().getResource("/IMG/logo.png"));
		setIconImage(imagemTituloJanela.getImage());

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LogoSistema = new JLabel(new ImageIcon(getClass().getResource("/IMG/logoSCI.png")));
		northPanel.add(LogoSistema);
		add(northPanel, BorderLayout.NORTH);
		// Inicializa a tabela
		DefaultTableModel model = new DefaultTableModel(columnNames, 0);
		table = new JTable(model);
		table.setPreferredScrollableViewportSize(new Dimension(500, 150));
		table.setFillsViewportHeight(true);
		JScrollPane scrollPane = new JScrollPane(table);

		ImageIcon btnFechar = new ImageIcon(getClass().getResource("/IMG/cancelar.png"));
		ImageIcon btnEditar = new ImageIcon(getClass().getResource("/IMG/editar.png"));
		ImageIcon btnExcluir = new ImageIcon(getClass().getResource("/IMG/excluir.png"));
		JPanel auxS = new JPanel(new FlowLayout(FlowLayout.CENTER));
		fecharButton = new JButton("Fechar", btnFechar);
		editarButton = new JButton("Editar Dados", btnEditar);
		excluirButton = new JButton("Excluir", btnExcluir);
		auxS.add(editarButton);
		auxS.add(excluirButton);
		auxS.add(fecharButton);
		add(auxS, (BorderLayout.SOUTH));
		addActionListeners(); // Adiciona os ouvintes de ação
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(null);
	}

	void loadTableData() {
		// Carrega os dados do banco de dados
		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");

				String query = "SELECT marca, modelo, numserie, datacompra, ip, scanner FROM impressoras ORDER BY modelo";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					ResultSet resultSet = preparedStatement.executeQuery();

					// Limpa a tabela antes de adicionar os novos dados
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.setRowCount(0);

					while (resultSet.next()) {
						// Adiciona os dados na tabela
						Vector<String> rowData = new Vector<>();
						rowData.add(resultSet.getString("marca"));
						rowData.add(resultSet.getString("modelo"));
						rowData.add(resultSet.getString("numserie"));
						rowData.add(resultSet.getString("datacompra"));
						rowData.add(resultSet.getString("ip"));
						rowData.add(resultSet.getString("scanner"));
						model.addRow(rowData);
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + ex.getMessage());
		}
	}

	private void editarImpressora() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow != -1) {
			// Obtenha os dados da linha selecionada
			String marca = (String) table.getValueAt(selectedRow, 0);
			String modelo = (String) table.getValueAt(selectedRow, 1);
			String numSerie = (String) table.getValueAt(selectedRow, 2);
			String dataCompra = (String) table.getValueAt(selectedRow, 3);
			String ip = (String) table.getValueAt(selectedRow, 4);
			String scanner = (String) table.getValueAt(selectedRow, 5);

			// Abra a janela de edição com os dados da impressora selecionada
			EditarImpressora editarImpressora = new EditarImpressora(this, marca, modelo, numSerie, dataCompra, ip,
					scanner);
			editarImpressora.setVisible(true);
		}
	}

	private void excluirImpressora() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow != -1) {
			try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
				if (connection != null) {
					// Obtenha os dados da linha selecionada
					String numSerie = (String) table.getValueAt(selectedRow, 2); // Assume que o número de série está na
																					// terceira coluna

					// Inicie uma transação para garantir que ambas as operações (exclusão da
					// impressora e da tabela Recursos) sejam bem-sucedidas ou falhem juntas.
					connection.setAutoCommit(false);

					try {
						// Execute a operação de exclusão no banco de dados para a tabela Impressoras
						String impressoraQuery = "DELETE FROM impressoras WHERE numserie = ?";
						try (PreparedStatement impressoraStatement = connection.prepareStatement(impressoraQuery)) {
							impressoraStatement.setString(1, numSerie);
							impressoraStatement.executeUpdate();
						}

						// Faça commit da transação se tudo ocorrer bem
						connection.commit();
						JOptionPane.showMessageDialog(this, "Impressora excluída com sucesso.");
						loadTableData(); // Atualize a tabela após a exclusão
					} catch (SQLException ex) {
						// Em caso de erro, faça rollback da transação
						connection.rollback();
						ex.printStackTrace();
						JOptionPane.showMessageDialog(this, "Erro ao excluir impressora: " + ex.getMessage());
					} finally {
						// Restaure o modo automático de commit
						connection.setAutoCommit(true);
					}
				} else {
					System.out.println("Connection is null. Check database connection.");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Erro ao excluir impressora: " + ex.getMessage());
			}
		} else {
			JOptionPane.showMessageDialog(this, "Selecione uma impressora para excluir.");
		}
	}

	private void addActionListeners() {
		fecharButton.addActionListener(this);
		editarButton.addActionListener(this);
		excluirButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == editarButton) {
			editarImpressora();
		} else if (e.getSource() == excluirButton) {
			excluirImpressora();
		} else if (e.getSource() == fecharButton) {
			// Adicione aqui a lógica para fechar a janela, se necessário
			dispose();
		}
	}
}

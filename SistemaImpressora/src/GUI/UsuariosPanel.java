package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import DAO.ConexaoBDSistemaImpressora;

@SuppressWarnings("serial")
public class UsuariosPanel extends JDialog implements ActionListener, KeyListener {
	private JButton cadastrarButton, fecharButton, editarButton, excluirButton;
	@SuppressWarnings("unused")
	private KeyEvent e;
	private JTable table;
	@SuppressWarnings("unused")
	private String cpf, sexo, tipo;

	public UsuariosPanel(Gui parent) {
		super(parent, "Usuários Cadastrados", true);
		initComponents();
		loadTableData();
	}

	private void initComponents() {
		String[] columnNames = { "CPF", "Nome", "Permissões", "Sexo", "Data de Nascimento", "E-mail" };
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
		ImageIcon btnCadastrar = new ImageIcon(getClass().getResource("/IMG/cadastro.png"));

		JPanel auxS = new JPanel(new FlowLayout(FlowLayout.CENTER));
		cadastrarButton = new JButton("Novo usuário", btnCadastrar);
		fecharButton = new JButton("Fechar", btnFechar);
		editarButton = new JButton("Editar Dados", btnEditar);
		excluirButton = new JButton("Excluir", btnExcluir);
		auxS.add(cadastrarButton);
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

				String query = "SELECT cpf, nome, tipo, sexo, data_nascimento, email FROM usuario ORDER BY nome";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					ResultSet resultSet = preparedStatement.executeQuery();

					// Limpa a tabela antes de adicionar os novos dados
					DefaultTableModel model = (DefaultTableModel) table.getModel();
					model.setRowCount(0);

					while (resultSet.next()) {
						// Adiciona os dados na tabela
						Vector<String> rowData = new Vector<>();
						rowData.add(resultSet.getString("cpf"));
						rowData.add(resultSet.getString("nome"));
						rowData.add(resultSet.getString("tipo"));
						tipo = resultSet.getString("tipo");
						rowData.add(resultSet.getString("sexo"));
						sexo = resultSet.getString("sexo");
						rowData.add(resultSet.getString("data_nascimento"));
						rowData.add(resultSet.getString("email"));
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

	private void excluirUsuario() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow != -1) {
			try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
				if (connection != null) {
					// Obtenha os dados da linha selecionada
					String cpf = (String) table.getValueAt(selectedRow, 0); // Assume que o CPF está na primeira coluna

					// Inicie uma transação para garantir que ambas as operações (exclusão da
					// tabela 'usuario' e 'credenciais') sejam bem-sucedidas ou falhem juntas.
					connection.setAutoCommit(false);

					try {
						// Execute a operação de exclusão na tabela 'credenciais'
						String credenciaisQuery = "DELETE FROM credenciais WHERE cpf = ?";
						try (PreparedStatement credenciaisStatement = connection.prepareStatement(credenciaisQuery)) {
							credenciaisStatement.setString(1, cpf);
							credenciaisStatement.executeUpdate();
						}

						// Execute a operação de exclusão na tabela 'usuario'
						String usuarioQuery = "DELETE FROM usuario WHERE cpf = ?";
						try (PreparedStatement usuarioStatement = connection.prepareStatement(usuarioQuery)) {
							usuarioStatement.setString(1, cpf);
							usuarioStatement.executeUpdate();
						}

						// Faça commit da transação se tudo ocorrer bem
						connection.commit();
						JOptionPane.showMessageDialog(this, "Usuário excluído com sucesso.");
						loadTableData(); // Atualize a tabela após a exclusão
					} catch (SQLException ex) {
						// Em caso de erro, faça rollback da transação
						connection.rollback();
						ex.printStackTrace();
						JOptionPane.showMessageDialog(this, "Erro ao excluir usuário: " + ex.getMessage());
					} finally {
						// Restaure o modo automático de commit
						connection.setAutoCommit(true);
					}
				} else {
					System.out.println("Connection is null. Check database connection.");
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Erro ao excluir usuário: " + ex.getMessage());
			}
		} else {
			JOptionPane.showMessageDialog(this, "Selecione um usuário para excluir.");
		}
	}

	private void editarUsuario() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow != -1) {
			// Obtenha os dados da linha selecionada
			String cpf = (String) table.getValueAt(selectedRow, 0);
			String nome = (String) table.getValueAt(selectedRow, 1);
			String tipo = (String) table.getValueAt(selectedRow, 2);
			String sexo = (String) table.getValueAt(selectedRow, 3);
			String data_nascimento = (String) table.getValueAt(selectedRow, 4);
			String email = (String) table.getValueAt(selectedRow, 5);

			// Abra a janela de edição com os dados da impressora selecionada
			EdicaoDeUsuario editarUsuario = new EdicaoDeUsuario(this, cpf, nome, tipo, sexo, data_nascimento, email);
			editarUsuario.setVisible(true);
		}
	}

	private void abrirTelaCadastro() {
		CadastrarNovoUsuario dialog = new CadastrarNovoUsuario(this);
		dialog.setVisible(true);
	}

	private void addActionListeners() {
		fecharButton.addActionListener(this);
		editarButton.addActionListener(this);
		excluirButton.addActionListener(this);
		cadastrarButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fecharButton) {
			dispose();
		} else if (e.getSource() == editarButton) {
			editarUsuario();
		} else if (e.getSource() == excluirButton) {
			excluirUsuario();
		} else if (e.getSource() == cadastrarButton) {
			abrirTelaCadastro();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.e = e;
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dispose();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}

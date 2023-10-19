package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import DAO.ConexaoBDSistemaImpressora;

@SuppressWarnings("serial")
public class EdicaoDados extends JDialog implements ActionListener, KeyListener {
	private JComboBox<String> comboSexo;
	private JTextField nomeField, dataNascimentoField, emailField;
	private JPasswordField senhaField, senhaCField;
	private JButton salvarButton, cancelarButton;
	private String cpf, sexo; // Adicione uma variável para armazenar o CPF
	private Runnable tabelaCallback;
	@SuppressWarnings("unused")
	private KeyEvent e;

	public EdicaoDados(String cpf, String sexo, Runnable tabelaCallback) {
		super(null, "Editar Dados do Usuário", ModalityType.APPLICATION_MODAL);
		this.cpf = cpf;
		this.sexo = sexo;
		this.tabelaCallback = tabelaCallback;
		initComponents();
		carregarDados();
		setSize(625, 450);
		setResizable(false);
	}

	private void initComponents() {
		// Inicializar os componentes da interface

		ImageIcon imagemTituloJanela = new ImageIcon(getClass().getResource("/IMG/logo.png"));
		setIconImage(imagemTituloJanela.getImage());

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LogoSistema = new JLabel(new ImageIcon(getClass().getResource("/IMG/logoSCI.png")));
		northPanel.add(LogoSistema);
		add(northPanel, BorderLayout.NORTH);

		nomeField = new JTextField(20);
		comboSexo = new JComboBox<String>();
		comboSexo.addItem(sexo);
		for (String s : carregarSexo()) {
			comboSexo.addItem(s);
		}
		dataNascimentoField = new JTextField(10);
		emailField = new JTextField(20);
		senhaField = new JPasswordField(20);
		senhaCField = new JPasswordField(20);
		JLabel nomeLabel = new JLabel("Nome:");
		JLabel sexoLabel = new JLabel("Sexo:");
		JLabel dataLabel = new JLabel("Data de Nascimentos:");
		JLabel emailLabel = new JLabel("e-mail:");
		JLabel senhaLabel = new JLabel("senha:");
		JLabel senhaCLabel = new JLabel("Confirme a senha:");

		ImageIcon btnSalvar = new ImageIcon(getClass().getResource("/IMG/salvar.png"));
		ImageIcon btnCancelar = new ImageIcon(getClass().getResource("/IMG/cancelar.png"));

		salvarButton = new JButton("Salvar", btnSalvar);
		cancelarButton = new JButton("Cancelar", btnCancelar);

		// Adicionar componentes ao painel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(8, 2, 0, 0));
		centerPanel.add(nomeLabel);
		centerPanel.add(nomeField);
		centerPanel.add(sexoLabel);
		centerPanel.add(comboSexo);
		centerPanel.add(dataLabel);
		centerPanel.add(dataNascimentoField);
		centerPanel.add(emailLabel);
		centerPanel.add(emailField);
		centerPanel.add(senhaLabel);
		centerPanel.add(senhaField);
		centerPanel.add(senhaCLabel);
		centerPanel.add(senhaCField);
		add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(salvarButton);
		southPanel.add(cancelarButton);
		add(southPanel, BorderLayout.SOUTH);

		addActionListeners();
		pack();
		setLocationRelativeTo(null);

		nomeField.addKeyListener(this);
		dataNascimentoField.addKeyListener(this);
		emailField.addKeyListener(this);
		senhaField.addKeyListener(this);
		senhaCField.addKeyListener(this);
	}

	private String[] carregarSexo() {
		List<String> sexos = new ArrayList<>();

		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");

				// Modifique a query para incluir o ID na seleção e ordenar pelo ID
				String query = "SELECT ID, sexo FROM sexo ORDER BY ID";

				try (PreparedStatement preparedStatement = connection.prepareStatement(query);
						ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						// Adicione o campo "sexo" à lista, mas use o ID se o campo "sexo" for nulo
						String sexo = resultSet.getString("sexo");
						if (sexo != null && !sexo.equals(this.sexo)) {
							sexos.add(sexo);
						}
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + ex.getMessage());
		}

		return sexos.toArray(new String[0]);
	}

	private void addActionListeners() {
		salvarButton.addActionListener(this);
		cancelarButton.addActionListener(this);
	}

	private void carregarDados() {
		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				String sql = "SELECT nome, data_nascimento, email FROM usuario WHERE cpf = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
					preparedStatement.setString(1, cpf);

					try (ResultSet resultSet = preparedStatement.executeQuery()) {
						if (resultSet.next()) {
							// Preencher os campos de texto com os dados obtidos do banco de dados
							nomeField.setText(resultSet.getString("nome"));
							dataNascimentoField.setText(resultSet.getString("data_nascimento"));
							emailField.setText(resultSet.getString("email"));
						} else {
							JOptionPane.showMessageDialog(this, "Nenhum usuário encontrado para o CPF informado.");
							dispose(); // Fechar a janela se não houver usuário encontrado
						}
					}
				}
				String sql1 = "SELECT email, senha FROM credenciais WHERE cpf = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(sql1)) {
					preparedStatement.setString(1, cpf);
					try (ResultSet resultSet = preparedStatement.executeQuery()) {
						if (resultSet.next()) {
							// Preencher os campos de texto com os dados obtidos do banco de dados
							senhaField.setText(resultSet.getString("senha"));
						} else {
							JOptionPane.showMessageDialog(this, "Nenhum usuário encontrado para o CPF informado.");
							dispose(); // Fechar a janela se não houver usuário encontrado
						}
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao carregar os dados: " + ex.getMessage());
		}
	}

	private String formatarData(String dataNascimento) {
		// Remove qualquer caractere que não seja dígito
		dataNascimento = dataNascimento.replaceAll("[^\\d]", "");

		// Verifica se a data de nascimento possui 8 dígitos
		if (dataNascimento.length() != 8) {
			JOptionPane.showMessageDialog(this, "Data inválida. Deve conter 8 dígitos.");
			return "";
		}

		// Formata a data de nascimento com máscara
		return dataNascimento.substring(0, 2) + "/" + dataNascimento.substring(2, 4) + "/"
				+ dataNascimento.substring(4);
	}

	private void salvarDados() {
		String nome = nomeField.getText();
		String sexo = (String) comboSexo.getSelectedItem();
		String dataNascimento = dataNascimentoField.getText();
		String email = emailField.getText();
		String senha = new String(senhaField.getPassword());
		String confirmSenha = new String(senhaCField.getPassword());

		if (!senha.equals(confirmSenha)) {
			JOptionPane.showMessageDialog(this, "Senhas não coincidem. Tente novamente.");
			return;
		}

		dataNascimento = formatarData(dataNascimento);

		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				String sql = "UPDATE usuario SET nome = ?, sexo = ?, data_nascimento = ?, email = ? WHERE cpf = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
					preparedStatement.setString(1, nome);
					preparedStatement.setString(2, sexo);
					preparedStatement.setString(3, dataNascimento);
					preparedStatement.setString(4, email);
					preparedStatement.setString(5, cpf);

					int rowsAffected = preparedStatement.executeUpdate();

					if (rowsAffected > 0) {
						JOptionPane.showMessageDialog(this, "Dados atualizados com sucesso!");
					} else {
						JOptionPane.showMessageDialog(this, "Nenhum usuário encontrado para o CPF informado.");
						return; // Se nenhum usuário for encontrado, não continua com a atualização das
								// credenciais
					}
				}

				// Atualizar a tabela credenciais
				String sqlCredenciais = "UPDATE credenciais SET email = ?, senha = ? WHERE cpf = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(sqlCredenciais)) {
					preparedStatement.setString(1, email);
					preparedStatement.setString(2, senha);
					preparedStatement.setString(3, cpf);

					int rowsAffected = preparedStatement.executeUpdate();

					if (rowsAffected > 0) {
						JOptionPane.showMessageDialog(this, "Credenciais atualizadas com sucesso!");
						tabelaCallback.run();
						dispose();
					} else {
						JOptionPane.showMessageDialog(this, "Nenhum usuário encontrado para o CPF informado.");
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao salvar os dados: " + ex.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == salvarButton) {
			salvarDados();
		} else if (e.getSource() == cancelarButton) {
			dispose();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.e = e;
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			salvarDados();
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dispose();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}
}

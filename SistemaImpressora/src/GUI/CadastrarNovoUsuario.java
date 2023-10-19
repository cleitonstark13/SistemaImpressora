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

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import DAO.ConexaoBDSistemaImpressora;

@SuppressWarnings("serial")
public class CadastrarNovoUsuario extends JDialog implements ActionListener, KeyListener {
	private JComboBox<String> comboSexo, comboTipo;
	private JTextField cpfField, nomeField, dataField, emailField, confirmemailField;
	private JPasswordField passwordField, confirmpasswordField;
	private JButton cadastrarButton, cancelarButton; // Declare cadastrarButton as an instance variable
	@SuppressWarnings("unused")
	private KeyEvent e;

	public CadastrarNovoUsuario(UsuariosPanel parent) {
		super(parent, "Cadastrar Novo Usuário", true);
		initializeFrame();
		initializeGUI();
		addComponents();
		addActionListeners();
		pack();
		setLocationRelativeTo(parent);
	}

	private void initializeFrame() {
		// Initialization code for the frame
		setTitle("Cadastrar Novo Usuário");
		setSize(625, 470);

		ImageIcon imagemTituloJanela = new ImageIcon(getClass().getResource("/IMG/logo.png"));
		setIconImage(imagemTituloJanela.getImage());

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
	}

	private void initializeGUI() {
		// GUI initialization code
		// Use layout managers, create labels, text fields, buttons, etc.
		setLayout(new BorderLayout());

		ImageIcon btnSalvar = new ImageIcon(getClass().getResource("/IMG/salvar.png"));
		ImageIcon btnCancelar = new ImageIcon(getClass().getResource("/IMG/cancelar.png"));

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LogoSistema = new JLabel(new ImageIcon(getClass().getResource("/IMG/logoSCI.png")));
		northPanel.add(LogoSistema);
		add(northPanel, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(10, 2, 0, 0)); // Usando GridLayout

		JLabel cpfLabel = new JLabel("CPF:");
		JLabel nomeLabel = new JLabel("Nome:");
		JLabel tipoLabel = new JLabel("Tipo de usuário:");
		JLabel sexoLabel = new JLabel("Sexo:");
		JLabel dataLabel = new JLabel("Data de Nascimento:");
		JLabel emailconfirmLabel = new JLabel("Confirme o e-mail:");
		JLabel emailLabel = new JLabel("e-mail:");
		JLabel passwordLabel = new JLabel("Senha:");
		JLabel passwordconfirmLabel = new JLabel("Confirme a Senha:");

		cpfField = new JTextField();
		nomeField = new JTextField();
		comboSexo = new JComboBox<>();
		comboSexo.addItem("");
		for (String s : carregarSexo()) {
			comboSexo.addItem(s);
		}
		comboTipo = new JComboBox<>();
		comboTipo.addItem("");
		for (String s : carregaTipo()) {
			comboTipo.addItem(s);
		}
		dataField = new JTextField();
		emailField = new JTextField();
		confirmemailField = new JTextField();
		passwordField = new JPasswordField();
		confirmpasswordField = new JPasswordField();

		cpfField.addKeyListener(this);
		nomeField.addKeyListener(this);
		dataField.addKeyListener(this);
		emailField.addKeyListener(this);
		confirmemailField.addKeyListener(this);
		passwordField.addKeyListener(this);
		confirmpasswordField.addKeyListener(this);

		JPanel auxS = new JPanel(new FlowLayout(FlowLayout.CENTER));
		cadastrarButton = new JButton("Salvar", btnSalvar); // Initialize cadastrarButton
		cancelarButton = new JButton("Cancelar", btnCancelar); // Initialize cadastrarButton
		auxS.add(cadastrarButton);
		auxS.add(cancelarButton);
		add(auxS, BorderLayout.SOUTH);

		panel.add(cpfLabel);
		panel.add(cpfField);
		panel.add(nomeLabel);
		panel.add(nomeField);
		panel.add(tipoLabel);
		panel.add(comboTipo);
		panel.add(sexoLabel);
		panel.add(comboSexo);
		panel.add(dataLabel);
		panel.add(dataField);
		panel.add(emailLabel);
		panel.add(emailField);
		panel.add(emailconfirmLabel);
		panel.add(confirmemailField);
		panel.add(passwordLabel);
		panel.add(passwordField);
		panel.add(passwordconfirmLabel);
		panel.add(confirmpasswordField);
		panel.add(new JLabel());

		add(panel, BorderLayout.CENTER);

		comboSexo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Verifique se "Outro" foi selecionado
				if ("Outro".equals(comboSexo.getSelectedItem())) {
					// Abra uma caixa de diálogo de entrada para inserir o novo sexo
					String novoSexo = JOptionPane.showInputDialog(CadastrarNovoUsuario.this, "Digite o novo sexo:");

					// Adicione o novo sexo ao banco de dados (se não for vazio)
					if (novoSexo != null && !novoSexo.isEmpty()) {
						adicionarNovoSexo(novoSexo);
					}

					// Atualize o JComboBox com os sexos atualizados
					comboSexo.setModel(new DefaultComboBoxModel<>(carregarSexo()));
				}
			}
		});
	}

	private boolean sexoExiste(Connection connection, String novoSexo) throws SQLException {
		String sql = "SELECT COUNT(*) FROM sexo WHERE sexo = ?";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, novoSexo);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int count = resultSet.getInt(1);
					return count > 0;
				}
			}
		}

		return false;
	}

	private void adicionarNovoSexo(String novoSexo) {
		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");

				// Verifica se o novo sexo já existe na tabela
				if (!sexoExiste(connection, novoSexo)) {
					String sql = "INSERT INTO sexo (sexo) VALUES (?)";
					try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
						preparedStatement.setString(1, novoSexo);
						preparedStatement.executeUpdate();
						System.out.println("Novo sexo '" + novoSexo + "' inserido na tabela 'sexo'.");
					}
				} else {
					System.out.println("Sexo '" + novoSexo + "' já existe na tabela 'sexo'.");
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + ex.getMessage());
		}
	}

	private String[] carregarSexo() {
		List<String> sexo = new ArrayList<>();

		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");

				// Modifique a query para incluir o ID na seleção e ordenar pelo ID
				String query = "SELECT ID, sexo FROM sexo ORDER BY ID";

				try (PreparedStatement preparedStatement = connection.prepareStatement(query);
						ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						// Adicione o campo "sexo" à lista, mas use o ID se o campo "sexo" for nulo
						sexo.add(resultSet.getString("sexo") != null ? resultSet.getString("sexo")
								: resultSet.getString("ID"));
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + ex.getMessage());
		}

		return sexo.toArray(new String[0]);
	}

	private String[] carregaTipo() {
		List<String> tipo = new ArrayList<>();

		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");

				// Modifique a query para incluir o ID na seleção e ordenar pelo ID
				String query = "SELECT ID, tipo FROM tipoUser ORDER BY ID";

				try (PreparedStatement preparedStatement = connection.prepareStatement(query);
						ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						// Adicione o campo "sexo" à lista, mas use o ID se o campo "sexo" for nulo
						tipo.add(resultSet.getString("tipo") != null ? resultSet.getString("tipo")
								: resultSet.getString("ID"));
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + ex.getMessage());
		}

		return tipo.toArray(new String[0]);
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

	private String formatarCPF(String cpf) {
		// Remove qualquer caractere que não seja dígito
		cpf = cpf.replaceAll("[^\\d]", "");

		// Verifica se o CPF possui 11 dígitos
		if (cpf.length() != 11) {
			JOptionPane.showMessageDialog(this, "CPF inválido. Deve conter 11 dígitos.");
			return "";
		}

		// Formata o CPF com máscara
		return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
	}

	private boolean isCPFValido(String cpf) {
		// Remove todos os caracteres que não são dígitos
		cpf = cpf.replaceAll("[^\\d]", "");

		// Verifica se o CPF possui 11 dígitos
		if (cpf.length() != 11) {
			return false;
		}

		// Calcula o primeiro dígito verificador
		int soma = 0;
		for (int i = 10; i >= 2; i--) {
			soma += (cpf.charAt(10 - i) - '0') * i;
		}
		int primeiroDigito = 11 - (soma % 11);
		if (primeiroDigito >= 10) {
			primeiroDigito = 0;
		}

		// Verifica se o primeiro dígito verificador é igual ao dígito no CPF
		if (primeiroDigito != (cpf.charAt(9) - '0')) {
			return false;
		}

		// Calcula o segundo dígito verificador
		soma = 0;
		for (int i = 11; i >= 2; i--) {
			soma += (cpf.charAt(11 - i) - '0') * i;
		}
		int segundoDigito = 11 - (soma % 11);
		if (segundoDigito >= 10) {
			segundoDigito = 0;
		}

		// Verifica se o segundo dígito verificador é igual ao dígito no CPF
		return segundoDigito == (cpf.charAt(10) - '0');
	}

	private void salvarDados() throws SQLException {
		String sexo = (String) comboSexo.getSelectedItem();
		String tipo = (String) comboTipo.getSelectedItem();
		String cpf = cpfField.getText();
		String nome = nomeField.getText();
		String data = dataField.getText();
		String email = emailField.getText();
		String confirmemail = confirmemailField.getText();
		String senha = new String(passwordField.getPassword());
		String confirmSenha = new String(confirmpasswordField.getPassword());

		data = formatarData(data);
		cpf = formatarCPF(cpf);

		// Verifica se o CPF é válido
		if (!isCPFValido(cpf)) {
			JOptionPane.showMessageDialog(this, "CPF inválido. Digite um CPF válido.");
			return;
		}
		// Verifica se o email é válido
		if (!isEmailValido(email)) {
			return;
		}

		if (!senha.equals(confirmSenha)) {
			JOptionPane.showMessageDialog(this, "Senhas não coincidem. Tente novamente.");
			return;
		}

		if (!email.equals(confirmemail)) {
			JOptionPane.showMessageDialog(this, "e-mails não coincidem. Tente novamente.");
			return;
		}

		if (validarCampos(cpf, nome, data, email, confirmemail, senha, confirmSenha)) {
			try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
				if (connection != null) {
					// Inicia a transação
	                connection.setAutoCommit(false);

	                try {
	                    // Insere dados na tabela "usuario"
	                    inserirDados(connection, nome, tipo, sexo, data, email, cpf);

	                    // Insere dados na tabela "credenciais"
	                    inserirCredenciais(connection, cpf, tipo, email, senha);

	                    // Comita a transação se tudo ocorrer bem
	                    connection.commit();

	                    JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!");
	                    if (getParent() instanceof UsuariosPanel) {
							UsuariosPanel usuariosPanel = (UsuariosPanel) getParent();
							usuariosPanel.loadTableData();
							System.out.print("\nTabela atualizada");
						}
	                    dispose();
	                } catch (SQLException ex) {
	                    // Ocorreu um erro, faz rollback na transação
	                    connection.rollback();
	                    ex.printStackTrace();
	                    JOptionPane.showMessageDialog(this, "Erro ao salvar dados: " + ex.getMessage());
	                } finally {
	                    // Restaura o autocommit para o estado original
	                    connection.setAutoCommit(true);
	                }
				} else {
					System.out.println("Connection is null. Check database connection.");
				}

			}
		}
	}

	private void inserirDados(Connection connection, String nome, String tipo, String sexo, String data, String email, String cpf)
			throws SQLException {
		String sql = "INSERT INTO usuario (nome, tipo, sexo, data_nascimento, email, cpf) VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, nome);
			preparedStatement.setString(2, tipo);
			preparedStatement.setString(3, sexo);
			preparedStatement.setString(4, data);
			preparedStatement.setString(5, email);
			preparedStatement.setString(6, cpf);

			preparedStatement.executeUpdate();
			System.out.println("Dados inseridos com sucesso!");
		}
	}

	private void inserirCredenciais(Connection connection, String cpf, String tipo, String email, String senha) throws SQLException {
		String sql = "INSERT INTO credenciais (cpf, tipo, email, senha) VALUES (?, ?, ?, ?)";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, cpf);
			preparedStatement.setString(2, tipo);
			preparedStatement.setString(3, email);
			preparedStatement.setString(4, senha);

			preparedStatement.executeUpdate();
			System.out.println("Dados inseridos com sucesso!");
		}
	}

	private boolean isEmailValido(String email) {
		// Verifica se o email contém o caractere "@"
		if (email.contains("@hotmail.com") || email.contains("@hotmail.com.br") || email.contains("@gmail.com")
				|| email.contains("@outlook.com") || email.contains("@outlook.com.br")) {
			return true;
		} else {
			JOptionPane.showMessageDialog(this, "Email inválido. Digite um e-mail válido.");
			return false;
		}
	}

	private boolean validarCampos(String cpf, String nome, String data, String email, String confirmemail, String senha,
			String confirmSenha) {

		// Example: Check if fields are empty
		if (cpf.isEmpty() || nome.isEmpty() || data.isEmpty() || email.isEmpty() || confirmemail.isEmpty()
				|| senha.isEmpty() || confirmSenha.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
			return false;
		}

		// Add more validation as needed

		return true;
	}

	private void addComponents() {
		// Add components to the frame
	}

	private void addActionListeners() {
		// Add action listeners to buttons, if any
		cadastrarButton.addActionListener(this);
		cancelarButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == cadastrarButton) {
			try {
				salvarDados();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
		// TODO Auto-generated method stub
				this.e = e;
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					try {
						salvarDados();
					} catch (SQLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					dispose();
				}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	// Add other methods as needed
}

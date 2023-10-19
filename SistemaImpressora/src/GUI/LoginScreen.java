package GUI;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import DAO.ConexaoBDSistemaImpressora;

@SuppressWarnings("serial")
public class LoginScreen extends JFrame implements ActionListener, KeyListener {
	private JTextField cpfField;
	private JPasswordField passwordField;
	private JButton loginButton, cadastrarButton, GitHubButton;
	@SuppressWarnings("unused")
	private KeyEvent e;

	public LoginScreen() {
		initializeFrame();
		initializeGUI();
		addComponents();
		addActionListeners();
	}

	private void initializeFrame() {
		setTitle("Login - Sistema de Cadastro de Impressoras");

		ImageIcon imagemTituloJanela = new ImageIcon(getClass().getResource("/IMG/logo.png"));
		setIconImage(imagemTituloJanela.getImage());

		setSize(625, 320);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
	}

	private void initializeGUI() {
		setLayout(new BorderLayout());

		ImageIcon btnLogin = new ImageIcon(getClass().getResource("/IMG/login.png"));
		ImageIcon btnCadastrar = new ImageIcon(getClass().getResource("/IMG/cadastro.png"));
		ImageIcon btnGitHub = new ImageIcon(getClass().getResource("/IMG/github.png"));

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LogoSistema = new JLabel(new ImageIcon(getClass().getResource("/IMG/logoSCI.png")));
		northPanel.add(LogoSistema);
		add(northPanel, BorderLayout.NORTH);

		JPanel auxS = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel cadastroLabel = new JLabel("Não Tem acesso?");
		cadastrarButton = new JButton("Cadastre aqui", btnCadastrar);
		GitHubButton = new JButton("Meu Github", btnGitHub);
		auxS.add(cadastroLabel);
		auxS.add(cadastrarButton);
		auxS.add(GitHubButton);
		add(auxS, BorderLayout.SOUTH);

		JPanel panel = new JPanel(new GridLayout(3, 2));

		JLabel cpfLabel = new JLabel("CPF:");
		JLabel passwordLabel = new JLabel("Senha:");

		cpfField = new JTextField(10); // Set the number of columns for JTextField
		passwordField = new JPasswordField(10); // Set the number of columns for JPasswordField
		loginButton = new JButton("Login", btnLogin);

		cpfField.addKeyListener(this);
		passwordField.addKeyListener(this);

		panel.add(cpfLabel);
		panel.add(cpfField);
		panel.add(passwordLabel);
		panel.add(passwordField);
		panel.add(new JLabel());
		panel.add(loginButton);

		add(panel, BorderLayout.CENTER);
	}

	private void addComponents() {
		// Add any additional initialization logic here
	}

	private void addActionListeners() {
		loginButton.addActionListener(this);
		cadastrarButton.addActionListener(this);
		GitHubButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == cadastrarButton) {
			abrirTelaCadastro();
		} else if (e.getSource() == loginButton) {
			abrirTelaPrincipal();
		} else if (e.getSource() == GitHubButton) {
			String url = "https://github.com/cleitonstark13/";
			try {
				// Abre a URL no navegador padrão
				Desktop.getDesktop().browse(new URI(url));
			} catch (IOException | URISyntaxException ex) {
				ex.printStackTrace();
			}
		}
	}

	// Método para verificar as credenciais
	private boolean verificarCredenciais(String cpf, String senha) {
		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				String sql = "SELECT * FROM credenciais WHERE cpf = ? AND senha = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
					preparedStatement.setString(1, cpf);
					preparedStatement.setString(2, senha);

					try (ResultSet resultSet = preparedStatement.executeQuery()) {
						return resultSet.next(); // Se houver uma linha, as credenciais são válidas
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false; // Credenciais inválidas
	}

	private void abrirTelaPrincipal() {
		String cpf = cpfField.getText();
		String senha = new String(passwordField.getPassword());

		cpf = formatarCPF(cpf);

		if (verificarCredenciais(cpf, senha)) {
			Gui telaPrincipal = new Gui(cpf);
			telaPrincipal.setVisible(true);
			dispose(); // Fecha a tela de login
		} else {
			JOptionPane.showMessageDialog(this, "Credenciais inválidas. Tente novamente.");
		}
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

	private void abrirTelaCadastro() {
		CadastroUsuario dialog = new CadastroUsuario(this);
		dialog.setVisible(true);
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
			abrirTelaPrincipal();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}

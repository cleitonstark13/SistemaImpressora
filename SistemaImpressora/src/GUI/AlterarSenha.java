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
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import DAO.ConexaoBDSistemaImpressora;

@SuppressWarnings("serial")
public class AlterarSenha extends JFrame implements ActionListener, KeyListener {
	private JPasswordField passwordField, confirmpasswordField;
	private JButton salvarButton, cancelarButton;
	private String cpfUser;
	@SuppressWarnings("unused")
	private KeyEvent e;

	public AlterarSenha(Gui parent, String cpfUser) {
		super();
		this.cpfUser = cpfUser;
		initializeFrame();
		initializeGUI();
		addComponents();
		addActionListeners();
		pack();
		setLocationRelativeTo(parent);
	}

	private void addComponents() {
		// TODO Auto-generated method stub

	}

	private void initializeFrame() {
		// Initialization code for the frame
		setTitle("Alterar a senha");
		setSize(625, 250);

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
		panel.setLayout(new GridLayout(3, 2, 0, 0)); // Usando GridLayout

		JLabel passwordLabel = new JLabel("Senha:");
		JLabel passwordconfirmLabel = new JLabel("Confirme a Senha:");

		passwordField = new JPasswordField();
		confirmpasswordField = new JPasswordField();

		passwordField.addKeyListener(this);
		confirmpasswordField.addKeyListener(this);

		JPanel auxS = new JPanel(new FlowLayout(FlowLayout.CENTER));
		salvarButton = new JButton("Salvar", btnSalvar); // Initialize cadastrarButton
		cancelarButton = new JButton("Cancelar", btnCancelar); // Initialize cadastrarButton
		auxS.add(salvarButton);
		auxS.add(cancelarButton);
		add(auxS, BorderLayout.SOUTH);

		panel.add(passwordLabel);
		panel.add(passwordField);
		panel.add(passwordconfirmLabel);
		panel.add(confirmpasswordField);
		panel.add(new JLabel());

		add(panel, BorderLayout.CENTER);
	}

	private void addActionListeners() {
		// Add action listeners to buttons, if any
		salvarButton.addActionListener(this);
		cancelarButton.addActionListener(this);
	}

	private void salvarDados() throws SQLException {
		String senha = new String(passwordField.getPassword());
		String confirmSenha = new String(confirmpasswordField.getPassword());

		if (!senha.equals(confirmSenha)) {
			JOptionPane.showMessageDialog(this, "Senhas não coincidem. Tente novamente.");
			return;
		}

		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				// Inicia a transação
				connection.setAutoCommit(false);

				try {
					// Insere dados na tabela "credenciais"
					inserirCredenciais(connection, senha, cpfUser);
					// Comita a transação se tudo ocorrer bem
					connection.commit();

					JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!");
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

	private void inserirCredenciais(Connection connection, String senha, String cpf) throws SQLException {
		String sql = "UPDATE credenciais SET senha = ? WHERE cpf = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, senha);
			preparedStatement.setString(2, cpf);

			preparedStatement.executeUpdate();
			System.out.println("Dados inseridos com sucesso!");
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

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == salvarButton) {
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
}

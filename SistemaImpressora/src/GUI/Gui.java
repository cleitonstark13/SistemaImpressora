package GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import DAO.ConexaoBDSistemaImpressora;

public class Gui extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JPanel JPPrincipal;
	private JButton CadastroImpressora, CadastroRecursos, ConsultarImpressora, ConsultarRecurso, RetiradaRecurso,
			LogoutButton, DadosUsuarios, usuariosButton, SenhaButton;
	private String userCPF;

	public Gui(String userCPF) {
		this.userCPF = userCPF;
		System.out.print("cpf armazenado: " + userCPF);
		initializeFrame();
		initializeGUI();
		addComponents();
		addActionListeners();
	}

	private void initializeFrame() {
		setTitle("Sistema de Cadastro de Impressoras");
		setSize(625, 325);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		ImageIcon imagemTituloJanela = new ImageIcon(getClass().getResource("/IMG/logo.png"));
		setIconImage(imagemTituloJanela.getImage());
	}

	private void initializeGUI() {
		setLayout(new BorderLayout());

		/*
		 * Imagens do sistema
		 */

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LogoSistema = new JLabel(new ImageIcon(getClass().getResource("/IMG/logoSCI.png")));
		northPanel.add(LogoSistema);
		add(northPanel, BorderLayout.NORTH);

		ImageIcon CadastrarIcon = new ImageIcon(getClass().getResource("/IMG/Cadastrar.png"));
		ImageIcon PesquisarIcon = new ImageIcon(getClass().getResource("/IMG/Pesquisar.png"));
		ImageIcon RecursosIcon = new ImageIcon(getClass().getResource("/IMG/Recursos.png"));
		ImageIcon RetiradaIcon = new ImageIcon(getClass().getResource("/IMG/Retirada.png"));
		ImageIcon UsuarioIcon = new ImageIcon(getClass().getResource("/IMG/usuario.png"));
		ImageIcon LogoutIcon = new ImageIcon(getClass().getResource("/IMG/login.png"));
		ImageIcon AlterarSenhaIcon = new ImageIcon(getClass().getResource("/IMG/senha.png"));

		JPPrincipal = new JPanel(new FlowLayout());
		DadosUsuarios = new JButton("Dados de usuário", UsuarioIcon);
		CadastroImpressora = new JButton("Cadastrar Impressora", CadastrarIcon);
		CadastroRecursos = new JButton("Cadastrar Recurso", RecursosIcon);
		ConsultarImpressora = new JButton("Consultar Impressoras", PesquisarIcon);
		ConsultarRecurso = new JButton("Consultar Recursos", PesquisarIcon);
		RetiradaRecurso = new JButton("Retirada de Recursos", RetiradaIcon);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new GridLayout(1, 2, 5, 5));
		JPanel aux1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JPanel aux2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		SenhaButton = new JButton("Alterar Senha", AlterarSenhaIcon);
		LogoutButton = new JButton("Logout", LogoutIcon);
		aux2.add(SenhaButton);
		aux2.add(LogoutButton);
		southPanel.add(aux1, BorderLayout.EAST);
		southPanel.add(aux2, BorderLayout.WEST);
		add(southPanel, BorderLayout.SOUTH);

		boolean isAdmin = isAdministrador(userCPF);
		System.out.println("Usuário é administrador? " + isAdmin);
		ImageIcon UserPanel = new ImageIcon(getClass().getResource("/IMG/cadastro.png"));
		if (isAdmin) {
			usuariosButton = new JButton("Usuários", UserPanel);
			JPPrincipal.add(usuariosButton);
			usuariosButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					abrirUsuarios();
				}
			});
		}
	}

	private boolean isAdministrador(String userCPF) {
		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				String query = "SELECT tipo FROM credenciais WHERE cpf = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					preparedStatement.setString(1, userCPF);

					try (ResultSet resultSet = preparedStatement.executeQuery()) {
						if (resultSet.next()) {
							String tipo = resultSet.getString("tipo");
							// Se o tipo for "admin", o usuário é administrador
							return "Administrador".equals(tipo);
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

		// Retorna falso se houver algum erro ou se não encontrar o usuário na tabela
		return false;
	}

	private void abrirUsuarios() {
		// Implemente o código para abrir a janela de usuários ou qualquer ação
		// relacionada aos usuários administradores.
		// Por exemplo:
		UsuariosPanel usuariosPanel = new UsuariosPanel(this);
		usuariosPanel.setVisible(true);
	}

	private void addComponents() {
		JPPrincipal.add(DadosUsuarios);
		JPPrincipal.add(CadastroImpressora);
		JPPrincipal.add(ConsultarImpressora);
		JPPrincipal.add(CadastroRecursos);
		JPPrincipal.add(ConsultarRecurso);
		JPPrincipal.add(RetiradaRecurso);

		add(JPPrincipal, BorderLayout.CENTER);

	}

	private void addActionListeners() {
		DadosUsuarios.addActionListener(this);
		CadastroImpressora.addActionListener(this);
		CadastroRecursos.addActionListener(this);
		ConsultarImpressora.addActionListener(this);
		ConsultarRecurso.addActionListener(this);
		RetiradaRecurso.addActionListener(this);
		LogoutButton.addActionListener(this);
		SenhaButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == CadastroImpressora) {
			abrirCadastrodeimpressora();

		} else if (e.getSource() == CadastroRecursos) {
			abrirCadastroderecursos();

		} else if (e.getSource() == ConsultarImpressora) {
			abrirConsultaImpressora();

		} else if (e.getSource() == ConsultarRecurso) {
			abrirConsultarRecursos();
		} else if (e.getSource() == RetiradaRecurso) {
			abrirRetiradaDeRecursos();
		} else if (e.getSource() == DadosUsuarios) {
			abrirDadosDeUsuarios();
		} else if (e.getSource() == LogoutButton) {
			fazerLogout();
		} else if (e.getSource() == SenhaButton) {
			AlterarSenha(userCPF);
		}
	}

	private void AlterarSenha(String cpf) {
		// TODO Auto-generated method stub
		AlterarSenha dialog = new AlterarSenha(this, cpf);
		dialog.setVisible(true);
	}

	/*
	 * Criação da janela de Dados de Usuários
	 */

	private void abrirDadosDeUsuarios() {
		DadosUsuario dialog = new DadosUsuario(this, userCPF);
		dialog.setVisible(true);
	}

	/*
	 * Criação da janela de Cadastro de impressora
	 */

	private void abrirCadastrodeimpressora() {
		CadastroImpressora dialog = new CadastroImpressora(this);
		dialog.setVisible(true);
	}

	/*
	 * Criação da janela de Cadastro de recursos
	 */

	private void abrirCadastroderecursos() {
		CadastroRecursos dialog = new CadastroRecursos(this);
		dialog.setVisible(true);
	}

	/*
	 * Criação da janela de Consulta de Impressoras
	 */

	private void abrirConsultaImpressora() {
		ConsultaImpressora consultaImpressora = new ConsultaImpressora(this);
		consultaImpressora.setVisible(true);
	}

	/*
	 * Criação da janela de Consulta de Recursos
	 */

	private void abrirConsultarRecursos() {
		ConsultarRecursos consultaImpressora = new ConsultarRecursos(this);
		consultaImpressora.setVisible(true);
	}

	private void abrirRetiradaDeRecursos() {
		RetiradaRecursos consultaImpressora = new RetiradaRecursos(this);
		consultaImpressora.setVisible(true);
	}

	private void fazerLogout() {
		LoginScreen telaLogin = new LoginScreen();
		telaLogin.setVisible(true);
		dispose();
	}

}

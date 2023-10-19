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
public class DadosUsuario extends JDialog implements ActionListener, KeyListener {
	private JTable tabela;
	private JButton fecharButton, editarButton;
	@SuppressWarnings("unused")
	private KeyEvent e;
	private String userCPF;
	private String sexo;

	public DadosUsuario(Gui parent, String userCPF) {
		super(parent, "Dados do Usuário", true);
		this.userCPF = userCPF;
		System.out.print("\ncpf armazenado: " + userCPF);
		ImageIcon btnFechar = new ImageIcon(getClass().getResource("/IMG/cancelar.png"));
		ImageIcon btnEditar = new ImageIcon(getClass().getResource("/IMG/editar.png"));
		ImageIcon imagemTituloJanela = new ImageIcon(getClass().getResource("/IMG/logo.png"));
		setIconImage(imagemTituloJanela.getImage());

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LogoSistema = new JLabel(new ImageIcon(getClass().getResource("/IMG/logoSCI.png")));
		northPanel.add(LogoSistema);
		add(northPanel, BorderLayout.NORTH);
		// Criar uma tabela com modelo padrão
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn("Dados");
		model.addColumn("");

		tabela = new JTable(model);

		// Adicionar a tabela a um JScrollPane
		JScrollPane scrollPane = new JScrollPane(tabela);

		// Adicionar botão de fechar
		JPanel auxS = new JPanel(new FlowLayout(FlowLayout.CENTER));
		fecharButton = new JButton("Fechar", btnFechar);
		editarButton = new JButton("Editar Dados", btnEditar);
		auxS.add(editarButton);
		auxS.add(fecharButton);
		add(auxS, (BorderLayout.SOUTH));

		// Adicionar o JScrollPane ao centro do JDialog
		add(scrollPane, BorderLayout.CENTER);

		setPreferredSize(new Dimension(625, 400));
		pack();
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(parent);
		addActionListeners(); // Adiciona os ouvintes de ação

		// Carregar automaticamente os dados ao abrir a janela
		preencherTabela(userCPF);
	}

	private void preencherTabela(String cpf) {
		// Limpa a tabela antes de preenchê-la
		DefaultTableModel model = (DefaultTableModel) tabela.getModel();
		model.setRowCount(0);

		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				// Consulta SQL para obter os dados do usuário com base no CPF
				String sql = "SELECT * FROM usuario WHERE cpf = ?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
					preparedStatement.setString(1, cpf);

					try (ResultSet resultSet = preparedStatement.executeQuery()) {
						if (resultSet.next()) {
							// Adiciona os dados do usuário ao modelo da tabela
							model.addRow(new Object[] { "CPF", resultSet.getString("cpf") });
							model.addRow(new Object[] { "Nome", resultSet.getString("nome") });
							model.addRow(new Object[] { "Tipo", resultSet.getString("tipo") });
							model.addRow(new Object[] { "Sexo", resultSet.getString("sexo") });
							sexo = resultSet.getString("sexo");
							model.addRow(new Object[] { "Data de Nascimento", resultSet.getString("data_nascimento") });
							model.addRow(new Object[] { "Email", resultSet.getString("email") });
						} else {
							JOptionPane.showMessageDialog(this, "Nenhum usuário encontrado para o CPF informado.");
						}
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao consultar os dados do usuário: " + ex.getMessage());
		}
	}

	private void addActionListeners() {
		fecharButton.addActionListener(this);
		editarButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fecharButton) {
			dispose();
		} else if (e.getSource() == editarButton) {
			editarDados(userCPF, sexo);
		}
	}

	private void editarDados(String cpf, String sexo) {
		// Abra a janela de edição passando o CPF
		EdicaoDados edicaoDados = new EdicaoDados(cpf, sexo, this::atualizarTabela);
		edicaoDados.setVisible(true);
	}
	
	// Adiciona um método para atualizar a tabela na classe DadosUsuario
	private void atualizarTabela() {
	    preencherTabela(userCPF);
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

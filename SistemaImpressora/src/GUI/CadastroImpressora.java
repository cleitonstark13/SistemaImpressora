package GUI;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import DAO.ConexaoBDSistemaImpressora;

public class CadastroImpressora extends JDialog implements ActionListener, KeyListener {
	private JTextField JTMarca, JTModelo, JTNumSerie, JTDataCompra, JTIP, JTScanner;
	private JButton salvarButton, cancelarButton;
	@SuppressWarnings("unused")
	private KeyEvent e;
	private static final long serialVersionUID = 1L;

	public CadastroImpressora(Gui parent) {
		super(parent, "Cadastrar Impressora", true);
		ImageIcon btnSalvar = new ImageIcon(getClass().getResource("/IMG/salvar.png"));
		ImageIcon btnCancelar = new ImageIcon(getClass().getResource("/IMG/cancelar.png"));

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(6, 2, 0, 0)); // Usando GridLayout

		JLabel marcaLabel = new JLabel("Marca:");
		JTMarca = new JTextField(10);

		JLabel modeloLabel = new JLabel("Modelo:");
		JTModelo = new JTextField(10);

		JLabel numSerieLabel = new JLabel("Número de Série:");
		JTNumSerie = new JTextField(10);

		JLabel DataCompraLabel = new JLabel("Data de Aquisição:");
		JTDataCompra = new JTextField(10);

		JLabel IPLabel = new JLabel("IP:");
		JTIP = new JTextField(10);

		JLabel scannerLabel = new JLabel("Caminho Scanner:");
		JTScanner = new JTextField(10);

		salvarButton = new JButton("Salvar", btnSalvar);
		cancelarButton = new JButton("Cancelar", btnCancelar);

		JTMarca.addKeyListener(this);
		JTModelo.addKeyListener(this);
		JTNumSerie.addKeyListener(this);
		JTDataCompra.addKeyListener(this);
		JTIP.addKeyListener(this);
		JTScanner.addKeyListener(this);

		// Adiciona os componentes ao painel
		panel.add(marcaLabel);
		panel.add(JTMarca);

		panel.add(modeloLabel);
		panel.add(JTModelo);

		panel.add(numSerieLabel);
		panel.add(JTNumSerie);

		panel.add(DataCompraLabel);
		panel.add(JTDataCompra);

		panel.add(IPLabel);
		panel.add(JTIP);

		panel.add(scannerLabel);
		panel.add(JTScanner);

		// Adiciona os botões usando BoxLayout para garantir o alinhamento horizontal
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(Box.createVerticalGlue());
		buttonPanel.add(salvarButton);
		buttonPanel.add(Box.createRigidArea(new Dimension(5, 0))); // Adiciona espaçamento
		buttonPanel.add(cancelarButton);

		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getContentPane().add(panel);
		getContentPane().add(Box.createVerticalGlue());
		getContentPane().add(buttonPanel);
		addActionListeners();
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setPreferredSize(new Dimension(300, 250)); // Define o tamanho preferido para ser quadrado
		pack();
		setLocationRelativeTo(parent);
	}

	private void addActionListeners() {
		salvarButton.addActionListener(this);
		cancelarButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Salvar")) {
			try {
				salvarDados();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JTMarca.setText("");
			JTModelo.setText("");
			JTNumSerie.setText("");
			JTDataCompra.setText("");
			JTIP.setText("");
			JTScanner.setText("");
		}
		if (e.getActionCommand().equals("Cancelar")) {
			dispose();
		}
	}

	/*
	 * Método que formata a data de nascimento
	 */

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

	private void salvarDados() throws SQLException {
		String marca = JTMarca.getText();
		String modelo = JTModelo.getText();
		String numserie = JTNumSerie.getText();
		String datacompra = JTDataCompra.getText();
		String ip = JTIP.getText();
		String scanner = JTScanner.getText();

		// Formata a data de aquisição
		datacompra = formatarData(datacompra);

		if (marca.isEmpty() && modelo.isEmpty() && numserie.isEmpty()) {
			JOptionPane.showMessageDialog(this,
					"Não foi possível Cadastrar. A Marca, Modelo e Número de Série não podem ficar vazios.");
			return;
		}

		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");
				inserirDados(connection, marca, modelo, numserie, datacompra, ip, scanner);
				inserirMarcaModelo(connection, marca, modelo);
				JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!");
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		}

	}

	private void inserirDados(Connection connection, String marca, String modelo, String numserie, String datacompra,
			String ip, String scanner) throws SQLException {
		String sql = "INSERT INTO Impressoras (marca, modelo, numserie, datacompra, ip, scanner) VALUES (?, ?, ?, ?, ?, ?)";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, marca);
			preparedStatement.setString(2, modelo);
			preparedStatement.setString(3, numserie);
			preparedStatement.setString(4, datacompra);
			preparedStatement.setString(5, ip);
			preparedStatement.setString(6, scanner);

			preparedStatement.executeUpdate();
		}
	}

	private void inserirMarcaModelo(Connection connection, String marca, String modelo) throws SQLException {
		String sql = "INSERT INTO marca_modelo (marca, modelo) VALUES (?, ?)";

		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, marca);
			preparedStatement.setString(2, modelo);

			preparedStatement.executeUpdate();
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
			try {
				salvarDados();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			JTMarca.setText("");
			JTModelo.setText("");
			JTNumSerie.setText("");
			JTDataCompra.setText("");
			JTIP.setText("");
			JTScanner.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
	}
}

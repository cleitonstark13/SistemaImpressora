package GUI;

import java.awt.Dimension;
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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import DAO.ConexaoBDSistemaImpressora;

public class RetiradaRecursos extends JDialog implements ActionListener, KeyListener {
	@SuppressWarnings("unused")
	private Gui parent;
	private JComboBox<String> comboModelo;
	private JTextField JTToner, JTCilindro, JTRetirada;
	private JButton salvarButton, cancelarButton;
	@SuppressWarnings("unused")
	private KeyEvent e;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RetiradaRecursos(Gui parent) {
		super(parent, "Cadastrar Recursos de Equipamentos", true);
		this.parent = parent;

		ImageIcon btnSalvar = new ImageIcon(getClass().getResource("/IMG/salvar.png"));
		ImageIcon btnCancelar = new ImageIcon(getClass().getResource("/IMG/cancelar.png"));

		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4, 2, 0, 0)); // Usando GridLayout

		JLabel modeloLabel = new JLabel("Modelo:");
		comboModelo = new JComboBox<>(carregarModelos());

		JLabel tonerLabel = new JLabel("Quantidade de torners retirados:");
		JTToner = new JTextField(10);

		JLabel cilindroLabel = new JLabel("Quantidade de cilindros retirados:");
		JTCilindro = new JTextField(10);
		
		JLabel retiradaLabel = new JLabel("Data da Retirada:");
		JTRetirada = new JTextField(10);

		salvarButton = new JButton("Salvar", btnSalvar);
		cancelarButton = new JButton("Cancelar", btnCancelar);

		JTToner.addKeyListener(this);
		JTCilindro.addKeyListener(this);

		// Adiciona os componentes ao painel

		panel.add(modeloLabel);
		panel.add(comboModelo);

		panel.add(tonerLabel);
		panel.add(JTToner);

		panel.add(cilindroLabel);
		panel.add(JTCilindro);
		
		panel.add(retiradaLabel);
		panel.add(JTRetirada);

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
		setPreferredSize(new Dimension(420, 200)); // Define o tamanho preferido para ser quadrado
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
			salvarDados();
			dispose(); // Fecha a janela de edição
		}
		if (e.getActionCommand().equals("Cancelar")) {
			dispose();
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
		String modelo = (String) comboModelo.getSelectedItem();
		String data = JTRetirada.getText();
		int toner = JTToner.getText().isEmpty() ? 0 : -Integer.parseInt(JTToner.getText());
		int cilindro = JTCilindro.getText().isEmpty() ? 0 : -Integer.parseInt(JTCilindro.getText());

		data = formatarData(data);
		
		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");

				String query = "INSERT INTO recursos (modelo, toner, cilindro, datacompra) VALUES (?, ?, ?, ?)";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					preparedStatement.setString(1, modelo);
					preparedStatement.setInt(2, toner);
					preparedStatement.setInt(3, cilindro);
					preparedStatement.setString(4, data);

					preparedStatement.executeUpdate();
					UIManager.put("OptionPane.okButtonText", "Sair");
					JOptionPane.showMessageDialog(this, "Dados salvos com sucesso!");
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + ex.getMessage());
		}
	}

	private String[] carregarModelos() {
		List<String> modelos = new ArrayList<>();

		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				System.out.println("Conexão obtida com sucesso!");

				String query = "SELECT DISTINCT modelo FROM impressoras";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query);
						ResultSet resultSet = preparedStatement.executeQuery()) {
					while (resultSet.next()) {
						modelos.add(resultSet.getString("modelo"));
					}
				}
			} else {
				System.out.println("Connection is null. Check database connection.");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao conectar ao banco de dados: " + ex.getMessage());
		}

		return modelos.toArray(new String[0]);
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
			JTToner.setText("");
			JTCilindro.setText("");
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}

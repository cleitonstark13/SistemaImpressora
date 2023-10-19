package GUI;

import javax.swing.*;

import DAO.ConexaoBDSistemaImpressora;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings("serial")
public class EditarImpressora extends JDialog implements ActionListener, KeyListener {
	private JTextField marcaField, modeloField, numSerieField, dataCompraField, ipField, scannerField;
	private JButton salvarButton, cancelarButton;
	@SuppressWarnings("unused")
	private KeyEvent e;

	public EditarImpressora(ConsultaImpressora consultaImpressora, String marca, String modelo, String numSerie,
			String dataCompra, String ip, String scanner) {
		super(consultaImpressora, "Editar Impressora", true);
		initComponents(marca, modelo, numSerie, dataCompra, ip, scanner);
	}

	private void initComponents(String marca, String modelo, String numSerie, String dataCompra, String ip,
			String scanner) {
		// Crie seus componentes GUI para edição (JTextField, JLabel, etc.)
		// Defina seus valores iniciais com os dados existentes da impressora
		ImageIcon imagemTituloJanela = new ImageIcon(getClass().getResource("/IMG/logo.png"));
		setIconImage(imagemTituloJanela.getImage());

		JPanel northPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JLabel LogoSistema = new JLabel(new ImageIcon(getClass().getResource("/IMG/logoSCI.png")));
		northPanel.add(LogoSistema);
		add(northPanel, BorderLayout.NORTH);

		marcaField = new JTextField(marca, 10);
		modeloField = new JTextField(modelo, 10);
		numSerieField = new JTextField(numSerie, 10);
		dataCompraField = new JTextField(dataCompra, 10);
		ipField = new JTextField(ip, 10);
		scannerField = new JTextField(scanner, 10);

		JLabel marcaLabel = new JLabel("Marca:");
		JLabel modeloLabel = new JLabel("Modelo:");
		JLabel numLabel = new JLabel("Número de Série:");
		JLabel dataLabel = new JLabel("Data da aquisição:");
		JLabel ipLabel = new JLabel("IP:");
		JLabel scannerLabel = new JLabel("Caminho do Scanner:");

		ImageIcon btnSalvar = new ImageIcon(getClass().getResource("/IMG/salvar.png"));
		ImageIcon btnCancelar = new ImageIcon(getClass().getResource("/IMG/cancelar.png"));
		salvarButton = new JButton("Salvar", btnSalvar);
		cancelarButton = new JButton("Cancelar", btnCancelar);

		salvarButton.addActionListener(this);
		cancelarButton.addActionListener(this);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(6, 2, 0, 0));
		centerPanel.add(marcaLabel);
		centerPanel.add(marcaField);
		centerPanel.add(modeloLabel);
		centerPanel.add(modeloField);
		centerPanel.add(numLabel);
		centerPanel.add(numSerieField);
		centerPanel.add(dataLabel);
		centerPanel.add(dataCompraField);
		centerPanel.add(ipLabel);
		centerPanel.add(ipField);
		centerPanel.add(scannerLabel);
		centerPanel.add(scannerField);
		add(centerPanel, BorderLayout.CENTER);

		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(salvarButton);
		southPanel.add(cancelarButton);
		add(southPanel, BorderLayout.SOUTH);
		setPreferredSize(new Dimension(625, 420));
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}

	private void salvarDados() {
		// Lógica para salvar as edições no banco de dados
		try (Connection connection = ConexaoBDSistemaImpressora.getConnection()) {
			if (connection != null) {
				String query = "UPDATE impressoras SET marca=?, modelo=?, numserie=?, datacompra=?, ip=?, scanner=? WHERE numserie=?";
				try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
					preparedStatement.setString(1, marcaField.getText());
					preparedStatement.setString(2, modeloField.getText());
					preparedStatement.setString(3, numSerieField.getText());
					preparedStatement.setString(4, dataCompraField.getText());
					preparedStatement.setString(5, ipField.getText());
					preparedStatement.setString(6, scannerField.getText());
					preparedStatement.setString(7, numSerieField.getText()); // Condição para a cláusula WHERE

					preparedStatement.executeUpdate();
				}

				// Atualize a tabela na janela principal após salvar as edições
				if (getParent() instanceof ConsultaImpressora) {
					ConsultaImpressora consultaImpressora = (ConsultaImpressora) getParent();
					consultaImpressora.loadTableData();
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, "Erro ao salvar no banco de dados: " + ex.getMessage());
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

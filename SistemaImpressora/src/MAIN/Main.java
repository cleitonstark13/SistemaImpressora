package MAIN;

import DAO.ConexaoBDSistemaImpressora;
import GUI.LoginScreen;

public class Main {
	public static void main(String[] args) {

		ConexaoBDSistemaImpressora.criarTabelaUsuario();
		ConexaoBDSistemaImpressora.criarTabelaCredenciais();
		ConexaoBDSistemaImpressora.criarTabelaImpressoras();
		ConexaoBDSistemaImpressora.criarTabelaRecursos();
		ConexaoBDSistemaImpressora.criarTabelaSexo();
		ConexaoBDSistemaImpressora.insertSexo();
		ConexaoBDSistemaImpressora.criarTabelaTipoUsuario();
		ConexaoBDSistemaImpressora.insertTipoUser();

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new LoginScreen().setVisible(true);
			}
		});
	}
}
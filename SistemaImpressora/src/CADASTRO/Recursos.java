package CADASTRO;

public class Recursos {
	private int qtd_Toner;
	private int qtd_Cilindro;
	private String NumSerie;

	public Recursos(int qtd_Toner, int qtd_Cilindro, String NumSerie) {
		this.qtd_Toner = qtd_Toner;
		this.qtd_Cilindro = qtd_Cilindro;
		this.NumSerie = NumSerie;
	}

	public int getqtd_Toner() {
		return qtd_Toner;
	}

	public void setqtd_Toner(int qtd_Toner) {
		this.qtd_Toner = qtd_Toner;
	}

	public int getqtd_Cilindro() {
		return qtd_Cilindro;
	}

	public void setqtd_Cilindro(int qtd_Cilindro) {
		this.qtd_Cilindro = qtd_Cilindro;
	}

	public String getNumSerie() {
		return NumSerie;
	}

	public void setNumSerie(String NumSerie) {
		this.NumSerie = NumSerie;
	}
}

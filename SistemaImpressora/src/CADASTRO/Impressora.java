package CADASTRO;

/* Autor
 * 
 * Cleiton Alves e Silva Júnior
 * 
 */
public class Impressora {
	private String Marca;
	private String Modelo;
	private String NumSerie;
	private int DataCompra;
	private int IP;
	private String Scanner;

	public Impressora(String Marca, String Modelo, String NumSerie, int DataCompra, int IP, String Scanner) {
		this.Marca = Marca;
		this.Modelo = Modelo;
		this.NumSerie = NumSerie;
		this.DataCompra = DataCompra;
		this.IP = IP;
		this.Scanner = Scanner;
	}

	public String getMarca() {
		return Marca;
	}

	public void setMarca(String Marca) {
		this.Marca = Marca;
	}

	public String getModelo() {
		return Modelo;
	}

	public void setModelo(String Modelo) {
		this.Modelo = Modelo;
	}

	public String getNumSerie() {
		return NumSerie;
	}

	public void setNumSerie(String NumSerie) {
		this.NumSerie = NumSerie;
	}
	
	public int getDataCompra() {
		return DataCompra;
	}

	public void setDataCompra(int DataCompra) {
		this.DataCompra = DataCompra;
	}
	public int getIP() {
		return IP;
	}

	public void setIP(int IP) {
		this.IP = IP;
	}

	public String getScanner() {
		return Scanner;
	}

	public void setScanner(String Scanner) {
		this.Scanner = Scanner;
	}
}

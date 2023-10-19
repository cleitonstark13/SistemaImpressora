package CADASTRO;

public class Pessoa {
	private String cpf, nome, sexo, data, email, senha;

	public Pessoa(String cpf, String nome, String sexo, String data, String email, String senha) {
		this.cpf = cpf;
		this.nome = nome;
		this.sexo = sexo;
		this.data = data;
		this.email = email;
		this.senha = senha;
	}

	public String getCPF() {
		return cpf;
	}

	public void setCPF(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
}

package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexaoBDSistemaImpressora {
	// Configurações de conexão com o banco de dados
	private static final String URL = "jdbc:postgresql://localhost:5433/SistemaImpressora";
	private static final String USUARIO = "StarkUser";
	private static final String SENHA = "a7xston1";

	// Método para obter a conexão com o banco de dados
	public static Connection getConnection() {
		try {
			// Carrega o driver JDBC para o PostgreSQL
			Class.forName("org.postgresql.Driver");

			// Retorna a conexão
			return DriverManager.getConnection(URL, USUARIO, SENHA);
		} catch (ClassNotFoundException | SQLException e) {
			// Imprime mensagens de erro e lança uma exceção
			e.printStackTrace();
			throw new RuntimeException("Erro ao conectar ao banco de dados: " + e.getMessage());
		}
	}

	public static void criarTabelaUsuario() {
		try (Connection connection = getConnection()) {
			String sql = "CREATE TABLE IF NOT EXISTS usuario (" + "nome VARCHAR(100)," + "tipo VARCHAR(25),"
					+ "sexo VARCHAR(100)," + "data_nascimento VARCHAR(20) NOT NULL," + "email VARCHAR(100) NOT NULL,"
					+ "cpf VARCHAR(20) NOT NULL PRIMARY KEY" + ")";

			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
				System.out.println("Tabela 'usuario' criada ou já existente.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erro ao criar a tabela 'usuario': " + e.getMessage());
		}
	}

	public static void criarTabelaCredenciais() {
		try (Connection connection = getConnection()) {
			String sql = "CREATE TABLE IF NOT EXISTS credenciais ("
					+ "cpf VARCHAR(20) NOT NULL PRIMARY KEY REFERENCES usuario(cpf)," + "tipo VARCHAR(25) NOT NULL,"
					+ "email VARCHAR(100) NOT NULL," + "senha VARCHAR  NOT NULL" + ")";

			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
				System.out.println("Tabela 'credenciais' criada ou já existente.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erro ao criar a tabela 'credenciais': " + e.getMessage());
		}
	}

	public static void criarTabelaImpressoras() {
		try (Connection connection = getConnection()) {
			String sql = "CREATE TABLE IF NOT EXISTS impressoras (" + "id SERIAL PRIMARY KEY," + "marca VARCHAR(20),"
					+ "modelo VARCHAR(30)," + "numserie VARCHAR(100)," + "datacompra VARCHAR(20)," + "ip VARCHAR(30),"
					+ "scanner VARCHAR(30)" + ")";

			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
				System.out.println("Tabela 'impressoras' criada ou já existente.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erro ao criar a tabela 'impressoras': " + e.getMessage());
		}
	}

	public static void criarTabelaRecursos() {
		try (Connection connection = getConnection()) {
			String sql = "CREATE TABLE IF NOT EXISTS recursos (" + "modelo VARCHAR(30)," + "toner int,"
					+ "cilindro int," + "datacompra VARCHAR(20)" + ")";

			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
				System.out.println("Tabela 'recursos' criada ou já existente.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erro ao criar a tabela 'recursos': " + e.getMessage());
		}
	}

	public static void criarTabelaSexo() {
		try (Connection connection = getConnection()) {
			String sql = "CREATE TABLE IF NOT EXISTS sexo (" + "id SERIAL PRIMARY KEY," + "sexo VARCHAR(100)" + ")";

			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
				System.out.println("Tabela 'sexo' criada ou já existente.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erro ao criar a tabela 'sexo': " + e.getMessage());
		}
	}

	public static void insertSexo() {
		// Lista de sexos a serem adicionados
		String[] sexos = { "Masculino", "Feminino", "Outro", "Prefiro não informar" };

		try (Connection connection = getConnection()) {
			for (String novoSexo : sexos) {
				// Verifica se o sexo já existe na tabela
				if (!sexoExiste(connection, novoSexo)) {
					String sql = "INSERT INTO sexo (sexo) VALUES (?)";

					try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
						preparedStatement.setString(1, novoSexo);
						preparedStatement.executeUpdate();
						System.out.println("Sexo '" + novoSexo + "' inserido na tabela 'sexo'.");
					}
				} else {
					System.out.println("Sexo '" + novoSexo + "' já existe na tabela 'sexo'.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erro ao inserir os sexos na tabela 'sexo': " + e.getMessage());
		}
	}

	private static boolean sexoExiste(Connection connection, String sexo) throws SQLException {
		String sql = "SELECT COUNT(*) FROM sexo WHERE sexo = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, sexo);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();
				int count = resultSet.getInt(1);
				return count > 0;
			}
		}
	}

	public static void criarTabelaTipoUsuario() {
		try (Connection connection = getConnection()) {
			String sql = "CREATE TABLE IF NOT EXISTS tipoUser (" + "id SERIAL PRIMARY KEY," + "tipo VARCHAR(20)" + ")";

			try (Statement statement = connection.createStatement()) {
				statement.execute(sql);
				System.out.println("Tabela 'tipoUser' criada ou já existente.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erro ao criar a tabela 'tipoUser': " + e.getMessage());
		}
	}

	public static void insertTipoUser() {
		// Lista de sexos a serem adicionados
		String[] tipo = { "Administrador", "Padrão" };

		try (Connection connection = getConnection()) {
			for (String novoTipo : tipo) {
				// Verifica se o sexo já existe na tabela
				if (!tipoExiste(connection, novoTipo)) {
					String sql = "INSERT INTO tipoUser (tipo) VALUES (?)";

					try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
						preparedStatement.setString(1, novoTipo);
						preparedStatement.executeUpdate();
						System.out.println("Tipo '" + novoTipo + "' inserido na tabela 'tipoUser'.");
					}
				} else {
					System.out.println("Tipo '" + novoTipo + "' já existe na tabela 'tipoUser'.");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Erro ao inserir os sexos na tabela 'sexo': " + e.getMessage());
		}
	}

	private static boolean tipoExiste(Connection connection, String tipo) throws SQLException {
		String sql = "SELECT COUNT(*) FROM tipoUser WHERE tipo = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, tipo);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				resultSet.next();
				int count = resultSet.getInt(1);
				return count > 0;
			}
		}
	}

}

package tp2.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
	public static final String URL = "jdbc:mysql://localhost:3306/sakila";
	public static final String USER = "root";
	public static final String PASSWORD = "";
	
	public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	
	public static Connection getConnection() throws SQLException{
		try {
			Class.forName(DRIVER);
			
			return DriverManager.getConnection(URL, USER, PASSWORD);
		}catch (ClassNotFoundException e){
            throw new SQLException("Driver MySQL non trouvé", e);
		}
	}
	
	public static boolean testConnection() {
		try (Connection conn = getConnection()){
			return conn != null && !conn.isClosed();
		}catch(SQLException e) {
			System.out.println("Errreur du connexion :" +e.getMessage());
			return false;
		}
	}
}











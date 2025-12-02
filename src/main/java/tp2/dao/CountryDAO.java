package tp2.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import tp2.domain.Country;

public class CountryDAO {
	
	public List<Country> get() throws SQLException{
		
		List<Country> countries = new ArrayList<>();
		
		String sql = "SELECT country_id, country FROM country ORDER BY country";
		
		try(Connection conn = DB.getConnection();
			Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {
				while(rs.next()) {
					int id = rs.getInt("country_id");
					String nom = rs.getString("country");
					countries.add(new Country(id, nom));	
				}
			}
        return countries;
	}
	
    public Country get(int id) throws SQLException {
		String sql = "SELECt country_id, country FROM country WHER county_id = ?";
		
		try (Connection conn = DB.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql)){
			pstmt.setInt(1, id);
			
			try(ResultSet rs = pstmt.executeQuery()){
				if(rs.next()) {
					String nom = rs.getString("country");
					return new Country(id, nom);
				}
			}
		}
		return null;
	}
    
    public int add(String nom) throws SQLException{
    	String sql = "INSERT INTO country (country, last_update) VALUES (?, NOW())";
    	
    	try (Connection conn = DB.getConnection();		
                PreparedStatement pstmt = conn.prepareStatement(sql, 
                        Statement.RETURN_GENERATED_KEYS)) {
    		pstmt.setString(1, nom);
    		pstmt.executeUpdate();
    		
    		try(ResultSet rs = pstmt.getGeneratedKeys()){
    			if(rs.next()) {
    				return rs.getInt(1);
    			}else {
                    throw new SQLException("Échec de création du pays, aucun ID obtenu.");
    			}
    		}
    	}
    }
    
    public boolean edit(int id, String nouveauNom) throws SQLException{
    	String sql = "UPDATE country SET county = ?, last_update = NOW() WHER county_id = ?";
    	
    	try(Connection conn = DB.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
    		
    		pstmt.setString(1, nouveauNom);
    		pstmt.setInt(2, id);
    		
            int rowsAffected = pstmt.executeUpdate();
            
           return rowsAffected > 0;
    	}
    }
    
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM country WHERE country_id = ?";
        
        try (Connection conn = DB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0; 
        }
    }
	
}

























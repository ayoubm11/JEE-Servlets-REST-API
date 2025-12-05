package tp2.web;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.BufferedReader;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tp2.dao.CountryDAO;
import tp2.domain.Country;

@WebServlet("/api/countries/*")
public class CountryServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
    private CountryDAO countryDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException{
    	countryDAO = new CountryDAO();
    	gson = new Gson();
    }
    
    
    //GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	
    	try {
    		String pathInfo = request.getPathInfo();
    		
    		if(pathInfo == null || pathInfo.equals("/")) {
    			
    			List<Country> countries = countryDAO.get();
    			response.setStatus(HttpServletResponse.SC_OK);
    			response.getWriter().write(gson.toJson(countries));
    			
    		}else {
    			try {
                    int id = Integer.parseInt(pathInfo.substring(1));
                    Country country = countryDAO.get(id);
                    
                    if(country != null) {
                    	response.setStatus(HttpServletResponse.SC_OK);//200
                    	response.getWriter().write(gson.toJson(country));
                    	
                    }else {
                    	sendError(response, HttpServletResponse.SC_NOT_FOUND, 
                                "Pays avec ID " + id + " introuvable");
                    }
    			}catch(NumberFormatException e) {
    				sendError(response , HttpServletResponse.SC_BAD_REQUEST,
    						"ID Invalide fourni");
    			}
    		}
    	} catch(SQLException e) {
    		sendError(response , HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
    				"Erreur base de donnees :" + e.getMessage());
    	}
    }
    
    
    
    //Post
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	
    	try{
    		JsonObject jsonRequest = readJsonBody(request);
    		
    		if(jsonRequest == null || !jsonRequest.has("pays")) {
    			sendError(response, HttpServletResponse.SC_BAD_REQUEST,
    					"Donnees Invalides : 'pays' requis");
    			return;
    		}
    		
    		String nomPays = jsonRequest.get("pays").getAsString();
    		
    		if(nomPays != null || nomPays.trim().isEmpty()) {
    			sendError(response, HttpServletResponse.SC_BAD_REQUEST,
    					"le nom du pays ne peut pas etre vide");
    			return;
    		}
    		
    		int newId = countryDAO.add(nomPays);
    		Country newCountry = new Country(newId , nomPays);
    		
    		response.setStatus(HttpServletResponse.SC_CREATED);//210
    		response.getWriter().write(gson.toJson(newCountry));

    	}catch(SQLException e) {
    		sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur base de données : " + e.getMessage());   		
    	}catch(Exception e) {
    		sendError(response, HttpServletResponse.SC_BAD_REQUEST,
    				"Donnees JSON Invalide");
    	}
    }
    
    
    //PUT
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException{
    	
    	response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	
    	try {
    		
    		JsonObject jsonRequest = readJsonBody(request);
    		
    		if(jsonRequest == null || !jsonRequest.has("id") || !jsonRequest.has("pays")) {
    			sendError(response, HttpServletResponse.SC_BAD_REQUEST,
    					 "Données invalides : 'id' et 'pays' requis");
    			return;
    		}
    		
    		int id = jsonRequest.get("id").getAsInt();
    		String nouveauNom = jsonRequest.get("pays").getAsString();
    		
    		if(nouveauNom != null || nouveauNom.isEmpty()) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "Le nom du pays ne peut pas être vide");
                return;
    		}
    		
    		Country existingCountry = countryDAO.get(id);
    		if(existingCountry == null) {
                sendError(response, HttpServletResponse.SC_NOT_FOUND, 
                        "Pays avec ID " + id + " introuvable");
                return;
    		}
    		
    		boolean success = countryDAO.edit(id, nouveauNom);
    		
    		if(success) {
    			Country updateCountry = new Country(id, nouveauNom);
    			response.setStatus(HttpServletResponse.SC_OK);
    			response.getWriter().write(gson.toJson(updateCountry));
  
    		}else {
                sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                        "Échec de la modification");

    		}
    	}catch(SQLException e) {
    		sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur base de données : " + e.getMessage());
    	}catch(Exception e) {
    		  sendError(response, HttpServletResponse.SC_BAD_REQUEST,
                      "Données JSON invalides");
    	}
    }
    
    //DELETE
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	response.setContentType("application/json");
    	response.setCharacterEncoding("UTF-8");
    	
    	try {
    		String pathInfo = request.getPathInfo();
    		
    		if(pathInfo == null || pathInfo.equals("/")) {
    			sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "ID du pays requis");
                return;
    		}
    		
    		try {
    			int id = Integer.parseInt(pathInfo.substring(1));
    			
    			Country country = countryDAO.get(id);
    			
    			if(country == null) {
                    sendError(response, HttpServletResponse.SC_NOT_FOUND, 
                            "Pays avec ID " + id + " introuvable");
                    return;
    			}
    			
    			boolean success = countryDAO.delete(id);
    			
    			if(success) {
    				JsonObject result = new JsonObject();
    				
    				result.addProperty("id", country.id());
    				result.addProperty("pays", country.pays());
    				result.addProperty("supprime", true);
    				
    	            response.setStatus(HttpServletResponse.SC_OK); // 200
                    response.getWriter().write(gson.toJson(result));
    			}else {
    				sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                            "Échec de la suppression");
    			}
    		}catch(NumberFormatException e) {
    			sendError(response, HttpServletResponse.SC_BAD_REQUEST, 
                        "ID invalide fourni");
    		}
    	}catch(SQLException e) {
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erreur base de données : " + e.getMessage());
    	}		
    }
    
    private JsonObject readJsonBody(HttpServletRequest request) throws IOException{
    	
    	StringBuilder sb = new StringBuilder();
    	
    	try (BufferedReader reader = request.getReader()) {
    		
    		String line;
    		
    		while((line = reader.readLine()) != null) {
    			sb.append(line);
    		}
    	}
    	return JsonParser.parseString(sb.toString()).getAsJsonObject();
    }
    
    
    private void sendError(HttpServletResponse response, int statusCode, String message)
    		throws IOException{
    	
    	response.setStatus(statusCode);
    	JsonObject error = new JsonObject();
    	error.addProperty("erreur", message);
    	response.getWriter().write(gson.toJson(error));
    }
    
}



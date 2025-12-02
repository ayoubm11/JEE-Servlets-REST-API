package tp2.domain;

public record Country (int id, String pays) {
	
	public Country{
		if(pays == null || pays.trim().isEmpty()) {
			throw new IllegalArgumentException("le nom du pays ne paeut pas etre vide");
		}
	}
	
	public Country(String pays) {
		this(0, pays);
	}
}
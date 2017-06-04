package parser;

public enum JdbcParserCharacter {
	
	NAVEGATION_CHAR("_"),
	PARENT_CHAR("*");
	
	private final String valor;
	
	JdbcParserCharacter(String valor) {
		this.valor = valor;
	}
	
	public String getValor(){
		return valor;
	}
	

}

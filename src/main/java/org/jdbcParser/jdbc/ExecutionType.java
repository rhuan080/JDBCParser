package org.jdbcParser.jdbc;

public enum ExecutionType {
	
	PRODUCTION(1),
	DEBUG(2);
	
	private final int valor;
	
	ExecutionType(int valor) {
		this.valor = valor;
	}
	
	public int getValor(){
		return valor;
	}

}

package org.jdbcParser.exception;

public class JDBCParserException extends Exception{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JDBCParserException() { super(); }
	
	public JDBCParserException(String message) { super(message); }
		
	public JDBCParserException(String message, Throwable cause) { super(message, cause); }
	
	public JDBCParserException(Throwable cause) { super(cause); }


}

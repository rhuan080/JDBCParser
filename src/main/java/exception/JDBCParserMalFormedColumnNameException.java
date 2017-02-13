package exception;

public class JDBCParserMalFormedColumnNameException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public JDBCParserMalFormedColumnNameException() { super(); }
	
	public JDBCParserMalFormedColumnNameException(String message) { super(message); }
		
	public JDBCParserMalFormedColumnNameException(String message, Throwable cause) { super(message, cause); }
	
	public JDBCParserMalFormedColumnNameException(Throwable cause) { super(cause); }

}

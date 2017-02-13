package jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import exception.JDBCParserException;

public interface IJDBCParser {
	
	/**
	 * Parse the resultSet to JavaBean.
	 * 
	 * @param resultSet
	 * @param type
	 * @return if no exist result, then return null else return JavaBean. The resultSet can not have more the one line.
	 * @throws SQLException 
	 * @throws JDBCParserException 
	 */
	public Object parseToBean(ResultSet resultSet, Class type) throws SQLException, JDBCParserException;
		
	/**
	 * Parse the resultSet to JavaBean list.
	 * 
	 * @param resultSet
	 * @param type
	 * @return if no exist result, then return null else return JavaBean list.
	 * @throws SQLException 
	 * @throws JDBCParserException 
	 */
	public List<Object> parseToBeans(ResultSet resultSet, Class type) throws JDBCParserException, SQLException;

}

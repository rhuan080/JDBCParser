package jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.JDBCParserException;
import parser.ParserToBean;

public class JDBCParser implements IJDBCParser{

	public Object parseToBean(ResultSet resultSet, Class type) throws SQLException, JDBCParserException {
		
		return new ParserToBean().parser(buildLineResult(resultSet),type);
		
	}
	
	

	public List<Object> parseToBeans(ResultSet resultSet, Class type) throws JDBCParserException, SQLException {
		// TODO Auto-generated method stub
		return new ParserToBean().parserToList(buildLineResult(resultSet),type);
	}
	
	protected List<LineResult> buildLineResult(ResultSet resultSet) throws SQLException{
			
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		
		List<LineResult> lineResults = new ArrayList<LineResult>();
		
		while (resultSet.next()) {
			Map<String,Object> columns = new HashMap<String,Object>();			
			for(int i=1; i <= resultSetMetaData.getColumnCount(); i++){
				columns.put(resultSetMetaData.getColumnName(i), resultSet.getObject(i));
			}
			lineResults.add(new LineResultImpl(columns));
		}
		
		return lineResults;
	}

}

package jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import exception.JDBCParserException;
import parser.ParserToBean;

public class JdbcParser implements IJdbcParser{
	
	protected Logger log = Logger.getLogger(this.getClass().getName()); 
	
	protected Class type;
	
	protected ExecutionType executionType;
	
	public JdbcParser(Class type, ExecutionType executionType){
		this.type = type;
		this.executionType = executionType;
	}

	public Object parseToBean(ResultSet resultSet, Class type) throws SQLException, JDBCParserException {	 
		
		if(this.executionType.equals(ExecutionType.DEBUG)) log.log(Level.INFO, "Start JdbcParser");		
		
		Object object = new ParserToBean(executionType).parser(buildLineResult(resultSet),type);
		
		if(this.executionType.equals(ExecutionType.DEBUG)) log.log(Level.INFO, "End JdbcParser");
		
		return object;
	}
	
	

	public List<Object> parseToBeans(ResultSet resultSet, Class type) throws JDBCParserException, SQLException {
		
		Date beginDate = null;
		if(this.executionType.equals(ExecutionType.DEBUG)){
			log.log(Level.INFO, "Start JdbcParser");
			beginDate = new Date();
		}
		
		List<Object> objects = new ParserToBean(executionType).parserToList(buildLineResult(resultSet),type);
		
		if(this.executionType.equals(ExecutionType.DEBUG)){			
			Date endDate = new Date();
			Long executionTime = endDate.getTime() - beginDate.getTime();
			log.log(Level.INFO, "JdbcParser Execution Time: "+ executionTime);
			log.log(Level.INFO, "End JdbcParser");
		}
		
		return objects;
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

package org.jdbcParser.initialize;

import org.jdbcParser.jdbc.IJdbcParser;
import org.jdbcParser.jdbc.JdbcParser;

public class Bootstrap implements IBootstrap{
	
	public static IJdbcParser getJdbcParser(IConfiguration configuration){
		
		return new JdbcParser(configuration.getType(), configuration.getExecutionType());
		
	}

}

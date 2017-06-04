package org.jdbcParser.initialize;

import org.jdbcParser.jdbc.ExecutionType;

public interface IConfiguration {
	
	public Class getType();
	
	public ExecutionType getExecutionType();
}

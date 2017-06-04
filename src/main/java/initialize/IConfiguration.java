package initialize;

import jdbc.ExecutionType;

public interface IConfiguration {
	
	public Class getType();
	
	public ExecutionType getExecutionType();
}

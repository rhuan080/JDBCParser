package initialize;

import jdbc.ExecutionType;

public class Configuration implements IConfiguration{
	
	protected Class type;
	
	protected ExecutionType executionType;
	
	public Configuration(Class type, ExecutionType executionType) {		
		this.type = type;
		this.executionType = executionType;
	}
	
    public Configuration(ExecutionType executionType) {	
    	this.type = null;
		this.executionType = executionType;
	}
    
    public Configuration(Class type) {		
		this.type = type;
		this.executionType = ExecutionType.PRODUCTION;
	}
    
    public Configuration() {
		
		this.type = null;
		this.executionType = ExecutionType.PRODUCTION;
	}

	

	public Class getType() {
		// TODO Auto-generated method stub
		return type;
	}

	public ExecutionType getExecutionType() {
		// TODO Auto-generated method stub
		return executionType;
	}

}

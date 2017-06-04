package initialize;

import jdbc.IJdbcParser;
import jdbc.JdbcParser;

public class Bootstrap implements IBootstrap{
	
	public static IJdbcParser getJdbcParser(IConfiguration configuration){
		
		return new JdbcParser(configuration.getType(), configuration.getExecutionType());
		
	}

}

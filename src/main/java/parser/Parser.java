package parser;

import java.util.List;

import exception.JDBCParserException;
import jdbc.LineResult;

public interface Parser {
	
	public Object parser(List<LineResult> lineResults, Class type) throws JDBCParserException;
	
	public List<Object> parserToList(List<LineResult> lineResults,Class type) throws JDBCParserException;

}

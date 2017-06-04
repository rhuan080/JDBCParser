package org.jdbcParser.parser;

import java.util.List;

import org.jdbcParser.exception.JDBCParserException;
import org.jdbcParser.jdbc.LineResult;

public interface Parser {
	
	public Object parser(List<LineResult> lineResults, Class type) throws JDBCParserException;
	
	public List<Object> parserToList(List<LineResult> lineResults,Class type) throws JDBCParserException;

}

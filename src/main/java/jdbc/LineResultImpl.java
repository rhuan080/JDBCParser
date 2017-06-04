package jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import parser.JdbcParserCharacter;

public class LineResultImpl implements LineResult{
	
	private Map<String,Object> column;
	
	public LineResultImpl(){
		this.column = new HashMap<String,Object>();
	}
	
	public LineResultImpl(Map<String,Object> column){
		this.column = column;
	}

	public Object getColumn(String nameColumn) {
		
		if(this.column.containsKey(nameColumn)){
			return this.column.get(nameColumn);			
		}
		return null;
	}

	public Class getTypeColumn(String nameColumn) {
		
		if(this.column.containsKey(nameColumn)){
			return this.column.get(nameColumn).getClass();			
		}
		return null;
	}

	public List<String> getColumnNames() {
		
		Set<String> columns = this.column.keySet();
		if(!columns.isEmpty()){
			return new ArrayList<String>(columns);
		}
		return null;
	}

	public List<String> getColumnNamesByLevel(Integer level) {
		
		List<String> nameColumns = getColumnNames();
		List<String> nameColumnsToReturn = new ArrayList<String>();
		
		for(String nameColumn : nameColumns){
			String[] str = nameColumn.split(JdbcParserCharacter.NAVEGATION_CHAR.getValor());
			
			if(level.equals(str.length)){
				nameColumnsToReturn.add(nameColumn);
			}
		}
		
		return nameColumnsToReturn;
	}

	public Integer getLevelOfColumn(String nameColumn) {
		
		if(getColumn(nameColumn) != null){
			String[] str = nameColumn.split(JdbcParserCharacter.NAVEGATION_CHAR.getValor());
			return str.length;
		}
		return -1;
	}

	public String getHashOfLevel(Integer level) {
		// TODO Auto-generated method stub
		return null;
	}

}

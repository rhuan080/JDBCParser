package jdbc;

import java.util.List;

public interface LineResult {
	
	
	/**
	 * Return column value by name column.
	 * 
	 * @param nameColumn 
	 * @return
	 */
	public Object getColumn(String nameColumn);
	
	/**
	 * Return a type of column by name column.
	 * 	
	 * @param nameColumn
	 * @return
	 */
	public Class getTypeColumn(String nameColumn);
	
	/**
	 * Return the name list of columns.
	 * 	
	 * @return
	 */
	public List<String> getColumnNames();
	
	/**
	 * Return the name list of columns by level. This level is defined by joins of tables. 
	 * 	
	 * @return
	 */
	public List<String> getColumnNamesByLevel(Integer level);

	
	/**
	 * Return level of column. This level is defined by joins of tables. 
	 * 	
	 * @return
	 */
	public Integer getLevelOfColumn(String nameColumn);

}

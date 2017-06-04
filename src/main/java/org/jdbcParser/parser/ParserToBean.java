package org.jdbcParser.parser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdbcParser.exception.JDBCParserException;
import org.jdbcParser.jdbc.ExecutionType;
import org.jdbcParser.jdbc.LineResult;

public class ParserToBean implements Parser{	

	protected Logger log = Logger.getLogger(this.getClass().getName());
	
	protected ExecutionType executionType;
	
	public ParserToBean(ExecutionType executionType){
		this.executionType = executionType;
	}
	
	public Object parser(List<LineResult> lineResults, Class type) throws JDBCParserException {
		
		List<Object> objects = buildJavaBeanList(null,lineResults, type, 1);
		
		if(objects.size() > 1){
			throw new JDBCParserException("Multiples results returned by ResultSet. Check the query or use the method parserToList");
		}
		
		if(!objects.isEmpty()){
			return objects.get(0);
		}
		
		return null;
	}

	public List<Object> parserToList(List<LineResult> lineResults, Class type) throws JDBCParserException {
		return  buildJavaBeanList(null,lineResults, type, 1);
	}
	
	protected List<Object> buildJavaBeanList(List<Object> javaBeans, List<LineResult> lineResults, Class type, Integer level) throws JDBCParserException{
		
		if(this.executionType.equals(ExecutionType.DEBUG)) log.log(Level.INFO, "Parsing to Java Bean...");
		
		List<Object> javaBeansToReturn = new ArrayList<Object>();
		
		Map<String, List<Object>> mapJavaBeans = buildJavaBeanMap(null, lineResults, type, level);		
		for(String key : mapJavaBeans.keySet()){
			javaBeansToReturn.addAll(mapJavaBeans.get(key));
		}
		
		if(this.executionType.equals(ExecutionType.DEBUG)) log.log(Level.INFO, "Parsed");
		
		return javaBeansToReturn;
	}
	
	protected Map<String, List<Object>> buildJavaBeanMap(Map<String,List<Object>> javaBeans, List<LineResult> lineResults, Class type, Integer level) throws JDBCParserException{
		
		if(this.executionType.equals(ExecutionType.DEBUG)) log.log(Level.INFO, "Building map JavaBean with column of level " + level);
		
		Map<String,List<Object>> javaBeansToReturn = new TreeMap<String, List<Object>>();		
		
		for(LineResult lineResult : lineResults){
			List<String> nameColumns = lineResult.getColumnNamesByLevel(level);
			
			Map<String,List<String>> mapColumns = mapColumnByColumnParent(nameColumns);
			for(String key : mapColumns.keySet()){
				//get JavaBean on list.
				List<Object> listMapped = javaBeansToReturn.get(key);
				if(listMapped == null){
					listMapped = new ArrayList<Object>();
				}
				
				//Get the javaBean of list if that javaBean already is builded.
				Object javaBean = getJavaBeanByProperties(listMapped, mapColumns.get(key), lineResult, level);
			
				//Get potentials parents of javaBean
				Map<String,Object> parents = getParents(javaBeans, mapColumns, lineResult, level-1);
				
				try {
					
					javaBean = processJavaBean(parents, javaBean,type, key, lineResult, level);
					
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				if(javaBean != null){
					listMapped.add(javaBean);
				}				
				
				javaBeansToReturn.put(key, listMapped);
				
			}
			
		}
				
		if(Utils.existLevel(lineResults, level+1)){
			buildJavaBeanMap(javaBeansToReturn, lineResults, null, level+1);
			//buildJavaBeanMap(javaBeans == null ? javaBeansToReturn :javaBeans , lineResults, null, level+1);
		}
		
		if(this.executionType.equals(ExecutionType.DEBUG)) log.log(Level.INFO, "Level " + level + " Builded");
		
		return javaBeansToReturn;
	}
	
	/*protected Object processJavaBean(Map<String,Object> parents, Class type, String key, LineResult lineResult, Integer level) throws JDBCParserException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
	
		List<String> nameColumns = lineResult.getColumnNamesByLevel(level);
		Map<String,List<String>> mapColumns = mapColumnByColumnParent(nameColumns);
		Object javaBean = null;
		
		if(type == null){
			Map<String,List<String>> mapColumnParents = mapColumnByColumnParent(nameColumns);
			javaBean = processParentJavaBean(parents, key, lineResult, level);
		}
		else{
			javaBean = buildJavaBean(mapColumns.get(key), lineResult, type, level);
		}
		return javaBean;
	}*/
	
	protected Object processJavaBean(Map<String,Object> parents,Object javaBean, Class type,  String key, LineResult lineResult, Integer level) throws JDBCParserException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
		
		Object parent = parents != null ? parents.get(key) : null ;		
		
		List<String> nameColumns = lineResult.getColumnNamesByLevel(level);
		Map<String,List<String>> mapColumns = mapColumnByColumnParent(nameColumns);
		
		
		String[] str = key.split(JdbcParserCharacter.NAVEGATION_CHAR.getValor());				
		String namePropertie = str[str.length-1];
		Class typeJavaBean = null;
		
		try {
			typeJavaBean = type == null ? Utils.getTypeOfJavaBean(parent, namePropertie) : type;
			if(javaBean == null){
				javaBean = buildJavaBean(mapColumns.get(key), lineResult, typeJavaBean, level);
			}		
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if(this.executionType.equals(ExecutionType.DEBUG)) log.log(Level.SEVERE, e.getMessage());
			throw new JDBCParserException("Propertie not exist or haven't method get/set on JavaBean ");
		}
		
		if(parents != null && parent != null ){			
			
				try {
					Utils.bindChildToParent(javaBean, parent, namePropertie);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			
			
		}
		else{
			//TODO The logic about this will be do
		}
		
		return javaBean;
	}
	
	
	
	
	protected Map<String,Object> getParents(Map<String,List<Object>> javaBeans,Map<String,List<String>> mapColumns,LineResult lineResult, Integer level){
		
		if(javaBeans == null){
			return null;
		}
		
		Map<String,Object> mapParents = new TreeMap<String, Object>();
		for(String key : mapColumns.keySet()){
			String[] strParent = key.split(JdbcParserCharacter.NAVEGATION_CHAR.getValor());
			StringBuilder keyParent = new StringBuilder("");
			
			if(strParent.length == 1){
				keyParent.append(JdbcParserCharacter.PARENT_CHAR.getValor());
			}
			else{
				for(int i=0; i<strParent.length-1; i++){
					if(i != 0){
						keyParent.append(JdbcParserCharacter.NAVEGATION_CHAR.getValor());												
					}
					keyParent.append(strParent[i]);
				}
			}			
			
			List<Object> parents = javaBeans.get(keyParent.toString());
			List<String> nameColumns = lineResult.getColumnNamesByLevel(level);
			
			if(parents != null){
				Object javaBeanParent = getJavaBeanByProperties(parents, nameColumns, lineResult, level);
				mapParents.put(key, javaBeanParent);
			}
					
		}
		
		if(mapParents.isEmpty()){
			return null;
		}
		
		return mapParents;
		
		
	}
	
    protected Map<String,Object> getParents(Map<String,List<Object>> javaBeans,LineResult lineResult,Integer level){
    	
    	if(javaBeans == null){
			return null;
		}
		
    	List<String> nameColumns = lineResult.getColumnNamesByLevel(1);		
		Map<String,List<String>> mapColumns = mapColumnByColumnParent(nameColumns);	
		
		Map<String,Object> parents = getParents(javaBeans, mapColumns, lineResult, 1);

		return getParents(javaBeans, mapColumns, lineResult, 1);
	}
	
	protected Map<String,List<String>> mapColumnByColumnParent(List<String> nameColumns){
		
		Map<String,List<String>> mapColumns = new HashMap<String,List<String>>();
		
		for(String nameColumn : nameColumns){
			
			String[] str = nameColumn.split(JdbcParserCharacter.NAVEGATION_CHAR.getValor());
			
			//Form key
			StringBuilder key = new StringBuilder("");
			if(str.length == 1){
				// * parent reference on map
				key.append(JdbcParserCharacter.PARENT_CHAR.getValor());		
			}
			else{
				for(int i=0; i<str.length-1; i++){
					if(i != 0){
						key.append(JdbcParserCharacter.NAVEGATION_CHAR.getValor());												
					}
					key.append(str[i]);
				}
			}
			
			List<String> columnMapped = mapColumns.get(key.toString());
			if(columnMapped == null){
				columnMapped = new ArrayList<String>();
			}
			
			columnMapped.add(nameColumn);
			mapColumns.put(key.toString(),columnMapped);
		}
		
		return mapColumns;
		
	}
	
	protected Map<String,List<String>> mapColumnRelByColumnParent(List<String> nameColumns){
		
		Map<String,List<String>> mapColumns = new HashMap<String,List<String>>();
		
		for(String nameColumn : nameColumns){
			
			String[] str = nameColumn.split(JdbcParserCharacter.NAVEGATION_CHAR.getValor());
			
			//Form key
			StringBuilder key = new StringBuilder("");
			if(str.length == 2){
				// * parent reference on map
				key.append(JdbcParserCharacter.PARENT_CHAR.getValor());		
			}
			if(str.length < 2){
				continue;
			}
			else{
				for(int i=0; i<str.length-2; i++){
					if(i != 0){
						key.append(JdbcParserCharacter.NAVEGATION_CHAR.getValor());												
					}
					key.append(str[i]);
				}
			}
			
			List<String> columnMapped = mapColumns.get(key.toString());
			if(columnMapped == null){
				columnMapped = new ArrayList<String>();
			}
			
			columnMapped.add(nameColumn);
			mapColumns.put(key.toString(),columnMapped);
		}
		
		return mapColumns;
		
	}
	
	protected Object buildJavaBean(List<String> nameColumns, LineResult lineResult, Class type, Integer level) throws JDBCParserException{
				
		try {
			
			Object javaBean = type.newInstance();
			
			for(String nameColumn : nameColumns){	

				String[] str = nameColumn.split(JdbcParserCharacter.NAVEGATION_CHAR.getValor());				
				String propertie = str[level-1];
				Object valuePropertie = lineResult.getColumn(nameColumn);
				
				if(valuePropertie != null){
					Method method = javaBean.getClass()
						.getDeclaredMethod("set"+propertie.substring(0, 1).toUpperCase() 
								+ propertie.substring(1),valuePropertie.getClass());
				
					method.invoke(javaBean,lineResult.getColumn(nameColumn));	
				}
			}
			
			return javaBean;
		
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JDBCParserException("Error to initiation Object. Check if this object is a JavaBean");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JDBCParserException("Error to call the method. Check all methods are corrects");
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JDBCParserException("Error to call the method. Check all methods are corrects");
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JDBCParserException("Error to call the method. Check all methods are corrects");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JDBCParserException("Error to call the method. Check if argument is correct");
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new JDBCParserException("Error to call the method. Check all methods are corrects");
		}
		
		
	}
	
	
	protected Object getJavaBeanParent(List<Object> javaBeans, List<String> nameColumns, LineResult lineResult, Integer level){
		
		Integer levelReference =1;
		Object javaBeanParent = null;
		
		while(levelReference <= level){
			javaBeanParent = getJavaBeanByProperties(javaBeans, nameColumns, lineResult, levelReference);			
		}
		
		return javaBeanParent;
	}
	
	protected Object getJavaBeanByProperties(List<Object> javaBeans, List<String> nameColumns, LineResult lineResult, Integer level){
		for(Object javaBean : javaBeans){
			
			boolean isEqual = true; 
			for(String nameColumn : nameColumns){	
				
				String[] str = nameColumn.split(JdbcParserCharacter.NAVEGATION_CHAR.getValor());				
				String propertie = str[level-1];
				
				try {
					Method method = javaBean.getClass()
							.getDeclaredMethod("get"+propertie.substring(0, 1).toUpperCase() 
									+ propertie.substring(1));
					Object objectMethod = method.invoke(javaBean);
					
					if(objectMethod != null && !objectMethod.equals(lineResult.getColumn(nameColumn))){
						isEqual = false;
					}
					
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(isEqual){
					return javaBean;
				}
			
				
			}
		}
		return null;
	}
	
	

}

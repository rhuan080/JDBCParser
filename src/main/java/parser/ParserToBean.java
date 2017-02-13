package parser;

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

import exception.JDBCParserException;
import jdbc.LineResult;

public class ParserToBean implements Parser{


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
		List<Object> javaBeansToReturn = new ArrayList<Object>();
		
		Map<String, List<Object>> mapJavaBeans = buildJavaBeanMap(null, lineResults, type, level);		
		for(String key : mapJavaBeans.keySet()){
			javaBeansToReturn.addAll(mapJavaBeans.get(key));
		}
		
		return javaBeansToReturn;
	}
	
	protected Map<String, List<Object>> buildJavaBeanMap(Map<String,List<Object>> javaBeans, List<LineResult> lineResults, Class type, Integer level) throws JDBCParserException{
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
				
				Object javaBean = getJavaBeanByProperties(listMapped, mapColumns.get(key), lineResult, level);
			
				//If JavaBean not exist on list, then JavaBean is created and added to list.
				Map<String,Object> parents = getParents(javaBeans, mapColumns, lineResult, level-1);
				
				try {
				
					if(javaBean == null){					
					
						javaBean = processJavaBean(parents, type, key, lineResult, level);
										
					}else{
						processParentJavaBean(parents, key, lineResult, level);
					}
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
		}
		
		return javaBeansToReturn;
	}
	
	protected Object processJavaBean(Map<String,Object> parents, Class type, String key, LineResult lineResult, Integer level) throws JDBCParserException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
	
		List<String> nameColumns = lineResult.getColumnNamesByLevel(level);
		Map<String,List<String>> mapColumns = mapColumnByColumnParent(nameColumns);
		Object javaBean = null;
		
		if(type == null){
			/*Map<String,List<String>> mapColumnParents = mapColumnByColumnParent(nameColumns);*/
			javaBean = processParentJavaBean(parents, key, lineResult, level);
		}
		else{
			javaBean = buildJavaBean(mapColumns.get(key), lineResult, type, level);
		}
		return javaBean;
	}
	
	protected Object processParentJavaBean(Map<String,Object> parents,  String key, LineResult lineResult, Integer level) throws JDBCParserException, IllegalAccessException, InvocationTargetException, NoSuchFieldException{
		Object parent = null;
		Object javaBean = null;
		
		List<String> nameColumns = lineResult.getColumnNamesByLevel(level);
		Map<String,List<String>> mapColumns = mapColumnByColumnParent(nameColumns);
		
		if(parents != null && (parent = parents.get(key)) != null ){
			String[] str = key.split("_");				
			String namePropertie = str[str.length-1];
			Class typeJavaBean;
			
			try {
				typeJavaBean = Utils.getTypeOfProperties(parent, namePropertie);
				javaBean = buildJavaBean(mapColumns.get(key), lineResult, typeJavaBean, level);
				Utils.bindChildToParent(javaBean, parent, namePropertie);
				
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new JDBCParserException(e.getMessage());
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new JDBCParserException("Propertie not exist or haven't method get/set on JavaBean ");
			}catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new JDBCParserException("Propertie not exist or haven't method get/set on JavaBean ");
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
			String[] strParent = key.split("_");
			StringBuilder keyParent = new StringBuilder("");
			
			if(strParent.length == 1){
				keyParent.append("*");
			}
			else{
				for(int i=0; i<strParent.length-1; i++){
					if(i != 0){
						keyParent.append("_");												
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
	
	protected Map<String,List<String>> mapColumnByColumnParent(List<String> nameColumns){
		
		Map<String,List<String>> mapColumns = new HashMap<String,List<String>>();
		
		for(String nameColumn : nameColumns){
			
			String[] str = nameColumn.split("_");
			
			//Form key
			StringBuilder key = new StringBuilder("");
			if(str.length == 1){
				// * parent reference on map
				key.append("*");		
			}
			else{
				for(int i=0; i<str.length-1; i++){
					if(i != 0){
						key.append("_");												
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
			
			String[] str = nameColumn.split("_");
			
			//Form key
			StringBuilder key = new StringBuilder("");
			if(str.length == 2){
				// * parent reference on map
				key.append("*");		
			}
			if(str.length < 2){
				continue;
			}
			else{
				for(int i=0; i<str.length-2; i++){
					if(i != 0){
						key.append("_");												
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

				String[] str = nameColumn.split("_");				
				String propertie = str[level-1];
				Method method = javaBean.getClass()
						.getDeclaredMethod("set"+propertie.substring(0, 1).toUpperCase() 
								+ propertie.substring(1), lineResult.getColumn(nameColumn).getClass());
				
				method.invoke(javaBean,lineResult.getColumn(nameColumn));				
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
				
				String[] str = nameColumn.split("_");				
				String propertie = str[level-1];
				
				try {
					Method method = javaBean.getClass()
							.getDeclaredMethod("get"+propertie.substring(0, 1).toUpperCase() 
									+ propertie.substring(1));
					Object objectMethod = method.invoke(javaBean);
					
					if(!objectMethod.equals(lineResult.getColumn(nameColumn))){
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

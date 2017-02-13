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

import exception.JDBCParserException;
import jdbc.LineResult;

public class Utils {
	
	public static Map<String,List<String>> mapColumnByColumnParent(List<String>nameColumns){
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
	
	public static boolean existLevel(List<LineResult> lineResults, Integer level){
		
		for(LineResult lineResult : lineResults){
			List<String> columns = lineResult.getColumnNamesByLevel(level);
			if(!columns.isEmpty()){
				return true;
			}
		}
		
		return false;
	}
	
	public static String generateKeyParent(String nameColumn){
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
		
		return key.toString();
	}
	
	public static Class getTypeOfProperties(Object javaBean, String namePropertie) throws SecurityException, NoSuchMethodException{
					
			Method method = javaBean.getClass()
				.getDeclaredMethod("get"+namePropertie.substring(0, 1).toUpperCase() 
						+ namePropertie.substring(1));
			Class<?> type = method.getReturnType();
			
			if(Collection.class.isAssignableFrom(type)){
				ParameterizedType genericType = (ParameterizedType) method.getGenericReturnType();
				return (Class<?>) genericType.getActualTypeArguments()[0];
			}
			
			return type;

	}
	
	public static void setValueToPropertie(Object javaBean, String namePropertie, Object value) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		Method method = javaBean.getClass()
				.getDeclaredMethod("set"+namePropertie.substring(0, 1).toUpperCase() 
						+ namePropertie.substring(1), value.getClass());
		
		method.invoke(javaBean,value);
	}
	
	public static void bindChildToParent(Object javaBean,Object javaBeanParent, String namePropertie) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
				
		if(Collection.class.isAssignableFrom(getTypeOfProperties(javaBeanParent, namePropertie))){
			Method method = javaBean.getClass()
					.getDeclaredMethod("get"+namePropertie.substring(0, 1).toUpperCase() 
							+ namePropertie.substring(1));
			Object objectMethod = method.invoke(javaBean);
			((Collection)objectMethod).add(javaBean);
		}
		else{
			setValueToPropertie(javaBeanParent, namePropertie, javaBean);
		}
		
	}

	

}

package parser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public static Class getTypeOfJavaBean(Object javaBean, String namePropertie) throws SecurityException, NoSuchMethodException{
					
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
	
	public static Class getTypeOfPropertie(Object javaBean, String namePropertie) throws SecurityException, NoSuchMethodException{
		
		Method method = javaBean.getClass()
			.getDeclaredMethod("get"+namePropertie.substring(0, 1).toUpperCase() 
					+ namePropertie.substring(1));
		Class<?> type = method.getReturnType();
		
		return type;

}
	
	public static void setValueToPropertie(Object javaBean, String namePropertie, Object value) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		
		Class type = null;
		if(Collection.class.isAssignableFrom(value.getClass())){
			if(Set.class.isAssignableFrom(value.getClass()))
			{ 
				type = Set.class;
			}
			else{
				type = List.class;
			}			
		}
		else{
			type = value.getClass();
		}
		
		Method method = javaBean.getClass()
				.getDeclaredMethod("set"+namePropertie.substring(0, 1).toUpperCase() 
						+ namePropertie.substring(1), type);
		
		
		
		method.invoke(javaBean,value);
	}
	
	public static Object getValueOfPropertie(Object javaBean, String namePropertie){
		
		Method method = null;
		Object objectMethod = null;
		
		try {
			method = javaBean.getClass()
					.getDeclaredMethod("get"+namePropertie.substring(0, 1).toUpperCase() 
							+ namePropertie.substring(1));
			objectMethod = method.invoke(javaBean);
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
		
		return objectMethod; 
		
	}
	
	public static void bindChildToParent(Object javaBean,Object javaBeanParent, String namePropertie) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException{
		
		if( isEmptyObject(javaBean) ) return;
		
		Class type = getTypeOfPropertie(javaBeanParent, namePropertie);
		
		if(Collection.class.isAssignableFrom(type)){
			Method method = javaBeanParent.getClass()
					.getDeclaredMethod("get"+namePropertie.substring(0, 1).toUpperCase() 
							+ namePropertie.substring(1));
			Object objectMethod = method.invoke(javaBeanParent);
			
			if(objectMethod == null){
				objectMethod = newInstanceCollection(type);
			}
			
			if( ((Collection)objectMethod).contains(javaBean) ) return;
			
			((Collection)objectMethod).add(javaBean);
			setValueToPropertie(javaBeanParent, namePropertie, objectMethod);
		}
		else{
			setValueToPropertie(javaBeanParent, namePropertie, javaBean);
		}
		
	}
	
	public static Collection newInstanceCollection(Class type){
		
		if(Set.class.isAssignableFrom(type)){
			return new LinkedHashSet();
		}
		
		return new ArrayList();
				
	}
	
	public static boolean isEmptyObject(Object obj){
		
		Field[] fields = obj.getClass().getDeclaredFields();	
		
		for ( int i = 0; i < fields.length; i++ ){
						
			if ( getValueOfPropertie(obj, fields[i].getName() ) != null ) return false;
		}
		
		return true;
	}

	

}

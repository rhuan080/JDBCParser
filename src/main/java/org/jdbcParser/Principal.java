package org.jdbcParser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.jdbcParser.exception.JDBCParserException;
import org.jdbcParser.initialize.Bootstrap;
import org.jdbcParser.initialize.Configuration;
import org.jdbcParser.jdbc.ExecutionType;
import org.jdbcParser.jdbc.JdbcParser;

public class Principal {

	public static void main(String[] args) {
		Connection con = null;
		try {
		   con = DriverManager.getConnection(
		    		"jdbc:postgresql://localHost:5432/TestMappedBy", "postgres", "postgres");

		} catch (SQLException e) {
		      throw new RuntimeException(e);
		}
		
		PreparedStatement stmt;
		try {
			stmt = con.prepareStatement("select p.id id, p.name,s.codigo sale_name, t.name sale_test_name from product p "
					+ " inner join sale s on p.sale_id = s.id"
					+ " left join test t on t.sale_id = s.id");
			ResultSet rs = stmt.executeQuery();	
			
			List<Object> peoples = Bootstrap
										.getJdbcParser(new Configuration(ExecutionType.DEBUG))
												.parseToBeans(rs, People.class);
			
			
			for(Object people : peoples){
				System.out.println("id: "+((People)people).getId());
				System.out.println("name: "+((People)people).getName());
				System.out.println("sale_name: "+((People)people).getSale().getName());
				if(((People)people).getSale().getTest() != null && !((People)people).getSale().getTest().isEmpty()){
					System.out.println("test_name: "+((People)people).getSale().getTest().get(0).getName());
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDBCParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

		// executa um select
		

	}

}

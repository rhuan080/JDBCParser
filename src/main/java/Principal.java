import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import exception.JDBCParserException;
import jdbc.JDBCParser;

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
			stmt = con.prepareStatement("select p.id id, p.name, s.codigo sale_name from product p "
					+ " inner join sale s on p.sale_id = s.id");
			ResultSet rs = stmt.executeQuery();			
			List<Object> peoples = new JDBCParser().parseToBeans(rs, People.class);
			
			
			for(Object people : peoples){
				System.out.println("id: "+((People)people).getId());
				System.out.println("name: "+((People)people).getName());
				System.out.println("sale_name: "+((People)people).getSale().getName());
				
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

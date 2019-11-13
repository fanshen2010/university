package socket;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;



public class MyDBDriver {
	
	public static Connection getConnection() throws Exception{
		try{
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/socket";
			String userName = "root";
			String password = "831022";
			Class.forName(driver);
			
			Connection conn = DriverManager.getConnection(url,userName,password);
			//System.out.println("connect");
			return conn;
		}catch(Exception e){
			return null;
		}
		
	}
	
}

package semi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConn {
	private static Connection dbConn;
	
	public static Connection getConnection() throws SQLException, ClassNotFoundException {
		if(dbConn == null) {
			String url = "jdbc:mariadb://192.168.36.1:3306/studycafe";
			String user = "root";
			String pw = "1234";
			Class.forName("org.mariadb.jdbc.Driver");
			dbConn = DriverManager.getConnection(url, user, pw);
		}
		return dbConn;
	}
	
	public static void close() throws SQLException{
		if(dbConn != null) {
			if(!dbConn.isClosed()) {
				dbConn.close();
			}
		}
	}
}

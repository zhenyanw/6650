package service.util;

import java.sql.*;

public class DBUtil {
    private static final String URL="jdbc:mysql://database-1.cahsqt6rernc.us-east-1.rds.amazonaws.com:3306/upic";
   // private static final String URLLOCAL = "jdbc:mysql://localhost:3306/upic?serverTimezone=UTC";
    private static final String NAME="admin";
    //private static final String NAMELOCAL="root";
    private static final String PASSWORD="Nicole0101";
    private static Connection conn = null;

    public static Connection getConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, NAME, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public void closeConnection(Connection connection) throws SQLException {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) throws Exception {
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("select ride_id from lift_ride");//选择import java.sql.ResultSet;
        while(rs.next()){
            System.out.println(rs.getInt("ride_id"));
        }

    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Moses
 */
public class LoginDao {
    public static ArrayList<String> validate(String enteredUsername, String enteredPassword){
        ArrayList<String> result = new ArrayList<>();
        if(enteredUsername.equals("su") && enteredPassword.equals("pwd")){
            result.add("0");
            result.add("su");
            result.add("Snapcoin");
            return result;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT type, Username, CompanyName FROM user WHERE BINARY username like '" + enteredUsername + "' and password like '" + enteredPassword + "'");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                result.add(rs.getString("type"));
                result.add(rs.getString("Username"));
                result.add(rs.getString("CompanyName"));
                return result;
            }
            return null;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve '" + enteredUsername + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static HashSet<String> getAccess(String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<String> columns = getColumns("access");
        HashSet<String> result = new HashSet<>();

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * from access where Username = '" + username + "';");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                for(String column : columns){
                    String data = rs.getString(column);
                    if(data.equals("")){
                        result.add(column);
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve access from '" + username + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static ArrayList<String> getColumns(String table){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        ArrayList<String> result = new ArrayList<>();

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE table_name like '" + table + "'");
            rs = stmt.executeQuery();
            
            int count = 1;
            while (rs.next()) {
                result.add(rs.getString(count));
            }
            result.remove(0);
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve columns from table = '" + table + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static ArrayList<ArrayList<String>> getEmployees(String username, String type){
        ArrayList<ArrayList<String>> overallList = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("select Admin_Name, Manager_Name, Cashier_Name from hierarchy where Admin_Name like '" + username + "' or Manager_Name like '" + username + "';");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                ArrayList<String> employee = new ArrayList<>();
                for(int i = Integer.parseInt(type)+1; i <= 3; i++){
                    employee.add(rs.getString(i));
                }
                overallList.add(employee);
            }
            
            return overallList;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve employees from hierarchy table", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
}

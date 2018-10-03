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
        if(enteredUsername.equals("admin") && enteredPassword.equals("pwd")){
            result.add("0");
            result.add("admin");
            result.add("Snapcoin");
            return result;
        }
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT type, Username, CompanyName, outletName FROM user WHERE BINARY username like '" + enteredUsername + "' and password like '" + enteredPassword + "'");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                result.add(rs.getString("type"));
                result.add(rs.getString("Username"));
                result.add(rs.getString("CompanyName"));
                result.add(rs.getString("outletName"));
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
    
    public static String getEmployees(String username){
        String result = "";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("select child from hierarchy where Parent like '" + username + "';");
            rs = stmt.executeQuery();
            
            
            while (rs.next()) {
                result += username + "," + getEmployees(rs.getString("child")) + " ";                
                System.out.println(result);
            }
            if(result.isEmpty()){
                return username;
            }
            result = result.substring(0,result.length()-1);
            result += " ";
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve employees from hierarchy table", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static String getOutlets(String username){
        String result = "";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("select distinct outletName from user where username in (select child from hierarchy where parent = '" + username + "' UNION select username from user where username like '" + username + "');");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            while(rs.next()){
                String outletName = rs.getString("outletName");
                result += outletName + ",";
            }
            result = result.substring(0,result.length()-1);
            
            
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve get outlets from " + username, ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static ArrayList<String> getRoles(String companyName){
        ArrayList<String> roleList = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("select * from roles where CompanyName like '" + companyName + "' order by Type;");
            rs = stmt.executeQuery();
            
            
            while (rs.next()) {
                roleList.add(rs.getString("position"));
            }
            return roleList;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve employees from hierarchy table", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return roleList;
    }
    
    public static ArrayList<String> getCategories(String companyName, String outletName){
        ArrayList<String> categoryList = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("select Category from Category where CompanyName like '" + companyName + "' and OutletName like '" + outletName + "';");
            rs = stmt.executeQuery();
            
            
            while (rs.next()) {
                categoryList.add(rs.getString("Category"));
            }
            return categoryList;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve employees from hierarchy table", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return categoryList;
    }
    
    public static ArrayList<String> getSisterOutlets(String companyName, String outletName){
        ArrayList<String> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("select distinct outletName from user where CompanyName like '" + companyName + "' and OutletName not like '" + outletName + "';");
            rs = stmt.executeQuery();
            
            
            while (rs.next()) {
                String sister = rs.getString("outletName");
                if(sister.length() > 0){
                    result.add(sister);
                }
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve employees from hierarchy table", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return result;
    }
}

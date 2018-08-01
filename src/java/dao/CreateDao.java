/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Moses
 */
public class CreateDao {
    
    public static boolean create(String username, String password, String companyName, String creator, String type, HashSet<String> accessSet){
        String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_.";
        boolean valid = true;
        if(username.equals(password)){
            valid = false;
        }
        for(int i = 0; i < username.length(); i++){
            if(allowedCharacters.indexOf(username.charAt(i)) == -1){
                valid = false;
                break;
            }
        }
        if(password.indexOf(' ') != -1){
            valid = false;
        }
        if(valid){
            return createUser(username, password, companyName, type) && createHierarchy(username, creator, type) && createAccess(username,accessSet);
        }else{
            return false;
        }
    }
    
    public static boolean createUser(String username, String password, String companyName, String type){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM USER WHERE Username like '" + username + "';");
            rs = stmt.executeQuery();
            
            while(rs.next()){
                System.out.println("User already exists");
                return false;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to create user = '" + username + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("INSERT INTO USER (Username, Password, CompanyName, Type) VALUES ('" + username + "', '" + password + "', '" + companyName + "', '"+ type + "');");
            System.out.println(stmt);
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to create user = '" + username + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return true;
    }
    
    public static boolean createHierarchy(String username, String creator, String type){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        System.out.println("username: " + username + "\nCreator: " +  creator +  "\nType: " + type);
        if(type.equals("2")){
            try {
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("INSERT INTO hierarchy (Admin_Name, Manager_Name) VALUES ('" + creator + "', '" + username + "');");
                stmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to create '" + username + "'", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }else if(type.equals("3")){
            String admin = "";
            try {
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("SELECT Admin_Name FROM Hierarchy WHERE Manager_Name = '" + creator + "';");
                rs = stmt.executeQuery();

                while (rs.next()) {
                    admin = rs.getString("Admin_Name");
                }
            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve " + username + "'", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
            
            try {
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("INSERT INTO hierarchy (Admin_Name, Manager_Name, Cashier_Name) VALUES ('" + admin + "', '" + creator + "', '" + username + "');");
                stmt.executeUpdate();
            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to create '" + username + "'", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }
        return true;
    }
    
    public static boolean createAccess(String username, HashSet<String> accessSet){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
                
        try {
            conn = ConnectionManager.getConnection();
            String statement = "INSERT INTO ACCESS (Username,";
            
            for(String column : accessSet){
                statement += column + ",";
            }
            statement = statement.substring(0,statement.length()-1) + ") VALUES ('" + username + "',";
            
            for(String column : accessSet){
                statement += "0x01,";
            }
            statement = statement.substring(0,statement.length()-1) + ");";
            stmt = conn.prepareStatement(statement);
            stmt.executeUpdate();
           
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve '" + username + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        
        return true;
    }
}

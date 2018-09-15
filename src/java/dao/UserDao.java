/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.CollatedTransaction;
import Entity.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author moses
 */
public class UserDao {
        
        
    public static boolean changePassword(String username, String newPassword){
        if(newPassword.indexOf(' ') != -1 || newPassword.length() == 0){
            return false;
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("UPDATE user SET Password = '" + newPassword + "' WHERE Username = '" + username + "';");
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to change '" + username + "' password", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static HashSet<String> getEmployeeAccess(String username){
        return LoginDao.getAccess(username);
    }
    
    public static boolean changeEmployeeAccess(String username, ArrayList<String> accessList){
        ArrayList<String> accessNameList = LoginDao.getColumns("access");
        
        //UPDATE `access` SET `menu_right` = 0x00, `menu` = 0x01 WHERE `access`.`Username` = 'cashier4';
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();
            
            String statement = "Update access SET ";
            for(String access : accessNameList){
                if(accessList.contains(access)){
                    statement += access + " = 0x01,"; 
                }else{
                    statement += access + " = 0x00,";
                }
            }
            statement = statement.substring(0,statement.length()-1);
            statement += " WHERE Username = '" + username + "'";
            stmt = conn.prepareStatement(statement);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to change '" + username + "' access", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static String getCompanyName(String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select CompanyName from user where username = '" + username + "';");
            
            rs = stmt.executeQuery();
            
            while(rs.next()){
                return rs.getString("CompanyName");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' company", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static String getOutlet(String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select Outlet_Name from user where username = '" + username + "';");
            
            rs = stmt.executeQuery();
            
            while(rs.next()){
                return rs.getString("Outlet_Name");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static ArrayList<CollatedTransaction> toggleStart(String username, String dateTime){
        boolean started = isStarted(username);
        
        if(started){
            //return data
            return end(username, dateTime);
        }else{
            //start
            start(username, dateTime);  
            return null;          
        }
        
    }
    
    public static boolean start(String username, String dateTime){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("update user set 'StartTime' = '" + dateTime + "' where username = '" + username + "';");
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static ArrayList<CollatedTransaction> end(String username, String dateTime){
        HashMap<String,ArrayList<String>> collatedTransactionCategoryMap = new HashMap<>();
        
        String[] paymentArray =  {"cash","card","snapcash"};
        ArrayList<String> paymentList = new ArrayList<>(Arrays.asList(paymentArray));
        collatedTransactionCategoryMap.put("payment", paymentList);
        
        ArrayList<String> categoryList = MenuDao.getCategories(username);
        collatedTransactionCategoryMap.put("categories", categoryList);
        
        ArrayList<CollatedTransaction> result = new ArrayList<>();
        // get all categories -> all, breakdown by payment type, breakdown by category
        
        
        
        return result;
    }
    
    public static CollatedTransaction getCollatedTransaction(String username, String dateTime, String categoryName, String categoryValue){
        CollatedTransaction collatedTransaction = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select StartTime from user where username = '" + username + "';");
            
            rs = stmt.executeQuery();
            
            while(rs.next()){
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return collatedTransaction;
    }
    
    
    public static boolean isStarted(String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select StartTime from user where username = '" + username + "';");
            
            rs = stmt.executeQuery();
            
            while(rs.next()){
                String startTime = rs.getString("StartTime");
                if(startTime == null || startTime.length() == 0){
                    return false;
                }else{
                    return true;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
}

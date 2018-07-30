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
import java.util.ArrayList;
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
    
}

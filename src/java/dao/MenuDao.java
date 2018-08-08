/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.MenuItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author moses
 */
public class MenuDao {
    public static ArrayList<MenuItem> getMenuItems(String outletName){
        ArrayList<MenuItem> result = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM menu WHERE outlet_id like '" + outletName + "'");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                result.add(new MenuItem(rs.getString("Food_Name"),rs.getDouble("Price"),rs.getDouble("Cost"),rs.getString("image")));
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve menu from'" + outletName + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static double getFoodPrice(String username, String foodName){
        String outletName = UserDao.getOutlet(username);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT price FROM menu WHERE outlet_id like '" + outletName + "' and Food_Name like '" + foodName + "';");
            System.out.println("Statement: " + stmt);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve menu from'" + outletName + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return 0;
    }
}

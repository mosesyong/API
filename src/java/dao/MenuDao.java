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
import java.util.HashMap;
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
    
    public static boolean addMenuItem(HashMap<String,String> menuParams){
        String name = menuParams.get("name");
        double price = Double.parseDouble(menuParams.get("price"));
        double cost = Double.parseDouble(menuParams.get("cost"));
        String outletId = menuParams.get("outletId");
        String image = menuParams.get("image");
        
        boolean exists = exists(name, outletId);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            if(exists){
                if(image == null){
                    stmt = conn.prepareStatement("UPDATE menu SET price = '" + price + "', cost = '" + cost + "' WHERE Outlet_Id = '" + outletId + "' AND Food_Name = '" + name + "';");
                }else{
                    stmt = conn.prepareStatement("UPDATE menu SET image = '" + image + "', price = '" + price + "', cost = '" + cost + "' WHERE Outlet_Id = '" + outletId + "' AND Food_Name = '" + name + "';");
                }
            }else{
                stmt = conn.prepareStatement("insert into menu (Outlet_id, Food_Name, Price, Cost, image) values ('" + outletId + "', '" + name + "', '" + price + "', '" + cost + "', '" + image + "');");
            }
            System.out.println("menu query: " + stmt);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + name + "' to menu", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean exists(String name, String outletId){
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select * from menu where Outlet_Id like '" + outletId + "' and Food_Name like '" + name + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + name + "' to menu", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
}

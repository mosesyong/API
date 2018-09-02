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
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author moses
 */
public class MenuDao {
    public static ArrayList<MenuItem> getMenuItems(String outletName, String companyName){
        ArrayList<MenuItem> result = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM menu WHERE CompanyName like '" + companyName + "' and outlet_id like '" + outletName + "'");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                String foodName = rs.getString("Food_Name");
                MenuItem menuItem = new MenuItem(foodName, rs.getDouble("Price"),rs.getDouble("Cost"),rs.getString("image"), rs.getString("Description"));
                menuItem.setCategory(getFoodCategory(companyName, outletName, foodName));
                result.add(menuItem);
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
        String companyName = UserDao.getCompanyName(username);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT price FROM menu WHERE CompanyName like '" + companyName + "' and outlet_id like '" + outletName + "' and Food_Name like '" + foodName + "';");
            System.out.println("Statement: " + stmt);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve menu from'" + outletName + " " + companyName + "'", ex);
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
        String companyName = menuParams.get("companyName");
        String desc = menuParams.get("desc");
        
        boolean exists = exists(name, outletId, companyName);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            if(exists){ // this is terrible, please dont do this to yourself moses
                if(image == null){
                    stmt = conn.prepareStatement("UPDATE menu SET price = '" + price + "', cost = '" + cost + "' WHERE CompanyName = '" + companyName + "' and Outlet_Id = '" + outletId + "' AND Food_Name = '" + name + "';");
                }else{
                    stmt = conn.prepareStatement("UPDATE menu SET image = '" + image + "', price = '" + price + "', cost = '" + cost + "' WHERE CompanyName = '" + companyName + "' and Outlet_Id = '" + outletId + "' AND Food_Name = '" + name + "';");
                }
            }else{
                stmt = conn.prepareStatement("insert into menu (CompanyName, Outlet_id, Food_Name, Price, Cost, image, Description) values ('" + companyName + "', '" + outletId + "', '" + name + "', '" + price + "', '" + cost + "', '" + image + "', '" + desc + "');");
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
    
    public static boolean exists(String name, String outletId, String companyName){
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select * from menu where CompanyName like '" + companyName + "' and Outlet_Id like '" + outletId + "' and Food_Name like '" + name + "';");
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
    
    public static ArrayList<String> getCategory(String companyName, String outletName){
        ArrayList<String> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select category from Category where CompanyName like '" + companyName + "' and OutletName like '" + outletName + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                result.add(rs.getString("category"));
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to get " + outletName + " " + companyName + " categories", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return result;
    }
    
    public static ArrayList<String> getFoodCategory(String companyName, String outletName, String foodName){
        ArrayList<String> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select category from FoodCategory where CompanyName like '" + companyName + "' and OutletName like '" + outletName + "' and FoodName like '" + foodName + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                result.add(rs.getString("category"));
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to get " + outletName + " " + companyName + " categories", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return result;
    }
    
    public static boolean addCategory(String companyName, String outletName, String category){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("insert into Category (CompanyName, OutletName, Category) values ('" + companyName + "', '" + outletName + "', '" + category + "');");
            
            System.out.println("category query: " + stmt);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + category + "' to Category", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
}

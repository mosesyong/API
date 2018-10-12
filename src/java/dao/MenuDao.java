/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.Category;
import Entity.MenuItem;
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
public class MenuDao {
    public static ArrayList<MenuItem> getMenuItems(String companyName, String outletName){
        ArrayList<MenuItem> result = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM menu WHERE CompanyName like '" + companyName + "' and outletName like '" + outletName + "'");
            System.out.println(stmt);
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

            stmt = conn.prepareStatement("SELECT price FROM menu WHERE CompanyName like '" + companyName + "' and outletName like '" + outletName + "' and Food_Name like '" + foodName + "';");
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
        String outletName = menuParams.get("outletName");
        String image = menuParams.get("image");
        String companyName = menuParams.get("companyName");
        String desc = menuParams.get("desc");
        String categoryStr = menuParams.get("category");
        categoryStr = categoryStr.substring(1,categoryStr.length()-1);
        ArrayList<String> categoryList = new ArrayList<>(Arrays.asList(categoryStr.split(",")));
        
        boolean exists = exists(name, outletName, companyName);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            
            stmt = conn.prepareStatement("insert into menu (CompanyName, outletName, Food_Name, Price, Cost, image, Description) values ('" + companyName + "', '" + outletName + "', '" + name + "', '" + price + "', '" + cost + "', '" + companyName + "_" + outletName + "_" + image + "', '" + desc + "');");
            
            System.out.println("menu query: " + stmt);
            stmt.executeUpdate();
            
            return addFoodCategory(companyName, outletName, name, categoryList);
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + name + "' to menu", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
     public static boolean addMenuItem(String companyName, String outletName, MenuItem menuItem){
        String name = menuItem.itemName;
        double price = menuItem.price;
        double cost = menuItem.cost;
        String image = menuItem.imageName;
        String desc = menuItem.desc;
        ArrayList<String> categoryList = menuItem.categoryList;
        
        boolean exists = exists(name, outletName, companyName);
        
        if(!exists){
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = ConnectionManager.getConnection();


                stmt = conn.prepareStatement("insert into menu (CompanyName, outletName, Food_Name, Price, Cost, image, Description) values ('" + companyName + "', '" + outletName + "', '" + name + "', '" + price + "', '" + cost + "', '" + image + "', '" + desc + "');");

                System.out.println("menu query: " + stmt);
                stmt.executeUpdate();

                return addFoodCategory(companyName, outletName, name, categoryList);
            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + name + "' to menu", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }
        return false;
    }
    
    public static boolean editMenuItem(HashMap<String,String> menuParams){
        String name = menuParams.get("name");
        double price = Double.parseDouble(menuParams.get("price"));
        double cost = Double.parseDouble(menuParams.get("cost"));
        String outletName = menuParams.get("outletName");
        String image = menuParams.get("image");
        String companyName = menuParams.get("companyName");
        String desc = menuParams.get("desc");
        String categoryStr = menuParams.get("category");
        categoryStr = categoryStr.substring(1,categoryStr.length()-1);
        ArrayList<String> categoryList = new ArrayList<>(Arrays.asList(categoryStr.split(",")));
        
        boolean exists = exists(name, outletName, companyName);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            if(exists){
                if(image == null){
                    stmt = conn.prepareStatement("UPDATE menu SET price = '" + price + "', cost = '" + cost + "', Description = '" + desc + "' WHERE CompanyName = '" + companyName + "' and outletName = '" + outletName + "' AND Food_Name = '" + name + "';");
                }else{
                    stmt = conn.prepareStatement("UPDATE menu SET image = '"  + companyName + "_" + outletName + "_" + image +  "', price = '" + price + "', cost = '" + cost + "', Description = '" + desc + "' WHERE CompanyName = '" + companyName + "' and outletName = '" + outletName + "' AND Food_Name = '" + name + "';");
                }
            }
            System.out.println("menu query: " + stmt);
            stmt.executeUpdate();
            return editFoodCategory(companyName, outletName, name, categoryList);
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + name + "' to menu", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean exists(String name, String outletName, String companyName){
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select * from menu where CompanyName like '" + companyName + "' and OutletName like '" + outletName + "' and Food_Name like '" + name + "';");
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
    
    public static ArrayList<Category> getCategory(String companyName, String outletName){ //
        ArrayList<Category> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select category, image from Category where CompanyName like '" + companyName + "' and OutletName like '" + outletName + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                String categoryName = rs.getString("category");
                String image = rs.getString("image");
                result.add(new Category(companyName, outletName, categoryName, image));
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to get " + outletName + " " + companyName + " categories", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return result;
    } 
    
    public static ArrayList<String> getCategories(String username){
        ArrayList<String> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select Category from Category where CompanyName in (Select CompanyName from user where Username like '" + username + "') and OutletName in (Select OutletName from user where Username like '" + username + "');");
            rs = stmt.executeQuery();
            while(rs.next()){
                result.add(rs.getString("Category"));
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to get " + username + " categories", ex);
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
                result.add(rs.getString("category").trim());
            }
            return result;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to get " + outletName + " " + companyName + " categories", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return result;
    }
    
    public static boolean addCategory(Category category){
        String companyName = category.companyName;
        String outletName = category.outletName;
        String categoryName = category.categoryName;
        String image = category.image;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("insert into Category (CompanyName, OutletName, Category, image) values ('" + companyName + "', '" + outletName + "', '" + categoryName + "', '" + image + "');");
                
            System.out.println("category query: " + stmt);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + categoryName + "' to Category", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean addCategory(HashMap<String, String> categoryParams){
        String companyName = categoryParams.get("companyName");
        String outletName = categoryParams.get("outletName");
        String image = categoryParams.get("image");
        String categoryName = categoryParams.get("category");
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("insert into Category (CompanyName, OutletName, Category, image) values ('" + companyName + "', '" + outletName + "', '" + categoryName + "', '" + companyName + "_" + outletName + "_" + image + "');");
                
            System.out.println("category query: " + stmt);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + categoryName + "' to Category", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    
    public static boolean addFoodCategory(String companyName, String outletName, String foodName, ArrayList<String> categoryList){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            for(String category : categoryList){
                stmt = conn.prepareStatement("insert into FoodCategory (CompanyName, OutletName, FoodName, Category) values ('" + companyName + "', '" + outletName + "', '" + foodName + "', '" + category + "');");

                System.out.println("category query: " + stmt);
                stmt.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add categories for " + foodName + " to FoodCategory", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean editFoodCategory(String companyName, String outletName, String foodName, ArrayList<String> categoryList){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("delete from FoodCategory where CompanyName = '" + companyName + "' and OutletName = '" + outletName + "' and FoodName = '" + foodName + "';");

            System.out.println("category query: " + stmt);
            stmt.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to delete categories from " + companyName + "_" + outletName + " to FoodCategory", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        
        try {
            conn = ConnectionManager.getConnection();
            
            for(String category : categoryList){
                stmt = conn.prepareStatement("insert into FoodCategory (CompanyName, OutletName, FoodName, Category) values ('" + companyName + "', '" + outletName + "', '" + foodName + "', '" + category + "');");

                System.out.println("category query: " + stmt);
                stmt.executeUpdate();
            }
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add categories for " + foodName + " to FoodCategory", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean copyCategory(String companyName, String outletName, String sourceOutletName, boolean append){
        ArrayList<Category> categoryNames = getCategory(companyName, sourceOutletName);
        System.out.println(categoryNames);
        if(!append){
            clearTable("category", companyName, outletName);
        }
        
        for(Category c : categoryNames){
            c.outletName = outletName;
            addCategory(c);
        }
        return true;
    }
    
    public static boolean clearTable(String table, String companyName, String outletName){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("delete from " + table + " where CompanyName = '" + companyName + "' and OutletName = '" + outletName + "';");

            System.out.println("Delete query: " + stmt);
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to delete categories from " + table + " for " + companyName + "_" + outletName, ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean copyMenu(String companyName, String outletName, String sourceOutletName, boolean append){
        ArrayList<MenuItem> menuItemList = getMenuItems(companyName, sourceOutletName);
        System.out.println(menuItemList);
        if(!append){
            clearTable("menu", companyName, outletName);
            clearTable("foodCategory", companyName, outletName);
        }
        
        for(MenuItem m : menuItemList){
            addMenuItem(companyName, outletName, m);
        }
        
        return true;
    }
}

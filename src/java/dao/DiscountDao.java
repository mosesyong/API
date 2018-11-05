/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.Discount;
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
public class DiscountDao {
    public static boolean addDiscount(Discount d){
        String name = d.name;
        String companyName = d.companyName;
        String outletName = d.outletName;
        double discountPercentage = d.discountPercentage;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            
            stmt = conn.prepareStatement("insert into discount (CompanyName, OutletName, Discount_Name, Discount_Percentage) values ('" + companyName + "', '" + outletName + "', '" + name + "', '" + discountPercentage + "');");
            
            System.out.println("discount query: " + stmt);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + name + "' to discount", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static ArrayList<Discount> getDiscounts(String companyName, String outletName){
        ArrayList<Discount> discountList = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM discount WHERE CompanyName like '" + companyName + "' and outletName like '" + outletName + "'");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Discount discount = new Discount(rs.getString("Discount_Name"), rs.getString("CompanyName"),rs.getString("OutletName"),rs.getDouble("Discount_Percentage"));
                discountList.add(discount);
            }
            return discountList;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve discounts from'" + outletName + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        
        return null;
    }
    
    public static boolean exists(String name, String outletName, String companyName){
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select * from discount where CompanyName like '" + companyName + "' and OutletName like '" + outletName + "' and Discount_Name like '" + name + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable find '" + name + "' from discount", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean editMenuItem(Discount d){
        String name = d.name;
        String companyName = d.companyName;
        String outletName = d.outletName;
        double discountPercentage = d.discountPercentage;
        
        boolean exists = exists(name, outletName, companyName);
        
        if (exists){
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("UPDATE Discount SET discountPercentage = '" + discountPercentage +  "' WHERE CompanyName = '" + companyName + "' and outletName = '" + outletName + "' AND Discount_Name = '" + name + "';");

                stmt.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + name + "' to menu", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }
        return false;
    }
    
}

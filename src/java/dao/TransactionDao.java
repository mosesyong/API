/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.FoodItem;
import Entity.MenuItem;
import Entity.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author moses
 */
public class TransactionDao {
    //INSERT INTO `transaction` (`TID`, `Employee_Name`, `Total_Cost`, `Date`) VALUES (NULL, 'Cashier1', '10.0', '2018-07-31 11:01:04');
    //SELECT last_insert_id()
    
    public static boolean addTransaction(Transaction transaction){
        System.out.println(transaction);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("INSERT INTO transaction (Employee_Name, Total_Price, Date) VALUES ('" + transaction.employeeName + "', '" + transaction.totalPrice + "', '" + transaction.dateTime + "');");
            System.out.println("Result: " + stmt);
            stmt.executeUpdate();
            
            stmt = conn.prepareStatement("SELECT last_insert_id()");
            rs = stmt.executeQuery();
            while(rs.next()){
                int tid = rs.getInt(1);
                return addPurchase(transaction, tid);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add transaction from'" + transaction.employeeName + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean addPurchase(Transaction transaction, int tid){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();

            String statement = "INSERT INTO purchase (Outlet_Id, TID, Food_Name, Quantity, Total_Price) VALUES";
            
            for(FoodItem foodItem : transaction.foodList){
                statement += ("('" + transaction.outletName + "', '" + tid + "', '" + foodItem.foodName + "', '" + foodItem.quantity + "', '" + foodItem.totalPrice + "'),");
            }
            
            statement = statement.substring(0,statement.length()-1);
            stmt = conn.prepareStatement(statement);
            
            stmt.executeUpdate();
            
            return true;
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add transaction from'" + transaction.employeeName + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
}

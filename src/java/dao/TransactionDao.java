/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.AnalyticsEntity;
import Entity.FoodItem;
import Entity.MenuItem;
import Entity.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
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

            stmt = conn.prepareStatement("INSERT INTO transaction (Employee_Name, Total_Price, Date, type) VALUES ('" + transaction.employeeName + "', '" + transaction.totalPrice + "', '" + transaction.dateTime + "', '" + transaction.type + "');");
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
        
        try {
            conn = ConnectionManager.getConnection();

            String statement = "INSERT INTO purchase (Outlet_Id, TID, Food_Name, Quantity, Total_Price, CompanyName) VALUES";
            
            for(FoodItem foodItem : transaction.foodList){
                statement += ("('" + transaction.outletName + "', '" + tid + "', '" + foodItem.foodName + "', '" + foodItem.quantity + "', '" + foodItem.totalPrice + "', '" + transaction.companyName + "'),");
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
    
    public static ArrayList<AnalyticsEntity> getAnalytics(String type, String username, String outletName, String period, String analyticsType, int count){
        ArrayList<AnalyticsEntity> result = new ArrayList<>();
        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        if(cal.getTimeZone().getID().equals("Etc/UTC")){
            cal.add(Calendar.HOUR, 8);
        }
        String currentDate = simpleDateFormat.format(cal.getTime());
        System.out.println("Current date: " + currentDate);
        if(period.equals("day")){
            cal.add(Calendar.DATE, -1);
        }else if(period.equals("week")){
            cal.add(Calendar.DATE, -7);
        }else if(period.equals("month")){
            cal.add(Calendar.MONTH, -1);
        }else if(period.equals("year")){
            cal.add(Calendar.YEAR, -1);
        }else{
            cal.add(Calendar.YEAR, -100);
        }
        String startDate = simpleDateFormat.format(cal.getTime());

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        if(analyticsType.equals("sales")){
            try {
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("select SUM(Total_Price) as totalPrice from transaction where Employee_Name in (select Username from user where Outlet_Name like '" + outletName + "') and Date between '" + startDate + "' and '" + currentDate + "';");
                System.out.println("Statement: " + stmt); 

                rs = stmt.executeQuery();
                
                while(rs.next()){
                    double totalPrice = rs.getDouble("totalPrice");
                    AnalyticsEntity saleAnalytics = new AnalyticsEntity();
                    saleAnalytics.name = period + " " + analyticsType;
                    saleAnalytics.quantity = 1;
                    saleAnalytics.price = totalPrice;
                    saleAnalytics.totalPrice = totalPrice;
                    if(totalPrice > 0){
                        result.add(saleAnalytics);
                    }
                }
                System.out.println("Sales result:" + result);
                if(result.isEmpty()){
                    double totalPrice = 0;
                    AnalyticsEntity saleAnalytics = new AnalyticsEntity();
                    saleAnalytics.name = period + " " + analyticsType;
                    saleAnalytics.quantity = 1;
                    saleAnalytics.price = totalPrice;
                    saleAnalytics.totalPrice = totalPrice;
                    result.add(saleAnalytics);
                }
                return result;

            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to get analytics from'" + username + "'", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }else if(analyticsType.equals("items")){
            try {
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("select Food_Name, SUM(Quantity) as Quantity, SUM(Total_Price) as Total_Price from purchase where TID in (select TID from transaction where Employee_Name in (select Username from user where Outlet_Name like '" + outletName + "') and Date between '" + startDate + "' and '" + currentDate + "') group by Food_name;");
                System.out.println("Statement: " + stmt); 

                rs = stmt.executeQuery();
                while(rs.next()){
                    String foodName = rs.getString("Food_Name");
                    int quantity = rs.getInt("Quantity");
                    double totalPrice = rs.getDouble("Total_Price");
                    AnalyticsEntity saleAnalytics = new AnalyticsEntity();
                    saleAnalytics.name = foodName;
                    saleAnalytics.quantity = quantity;
                    saleAnalytics.price = totalPrice / quantity;
                    saleAnalytics.totalPrice = totalPrice;
                    result.add(saleAnalytics);
                }
                
                return result;

            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to get analytics from'" + username + "'", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }
        
        return result;
    }
    
    public static ArrayList<Transaction> getTransactions(String outletId){
        ArrayList<Transaction> transactionList = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("select * from transaction where Employee_Name in (select Username from user where Outlet_Name = '" + outletId + "');");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            while(rs.next()){
                transactionList.add(new Transaction(rs.getString("Employee_Name"),rs.getString("Date"), rs.getDouble("Total_Price")));
            }            
            return transactionList;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve transaction from'" + outletId + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
}

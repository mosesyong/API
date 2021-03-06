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
import Exception.DayNotStartedException;
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
    
    public static int addTransaction(Transaction transaction){
        System.out.println(transaction);
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {// retrieves password from the database for specified username
            conn = ConnectionManager.getConnection();
            
            boolean dineIn = transaction.dineIn;
            String dineInStr = "0x01";
            if(!dineIn){
                dineInStr = "0x00";
            }

            stmt = conn.prepareStatement("INSERT INTO transaction (CompanyName, OutletName, Employee_Name, Total_Price, Discount_Name, Date, type, DineIn) VALUES ('" + transaction.companyName + "', '" + transaction.outletName + "', '" + transaction.employeeName + "', '" + transaction.recievedTotal + "', '" + transaction.discountName + "', '" + transaction.dateTime + "', '" + transaction.type + "', " + dineInStr + ");");
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
        return 0;
    }
    
    public static int addPurchase(Transaction transaction, int tid){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();

            String statement = "INSERT INTO purchase (outletName, TID, Food_Name, Quantity, Total_Price, CompanyName) VALUES";
            
            for(FoodItem foodItem : transaction.foodList){
                statement += ("('" + transaction.outletName + "', '" + tid + "', '" + foodItem.foodName + "', '" + foodItem.quantity + "', '" + foodItem.totalPrice + "', '" + transaction.companyName + "'),");
            }
            
            statement = statement.substring(0,statement.length()-1);
            stmt = conn.prepareStatement(statement);
            
            stmt.executeUpdate();
            
            return tid;
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add transaction from'" + transaction.employeeName + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return 0;
    }
    
    public static ArrayList<AnalyticsEntity> getAnalytics(String companyName, String outletName){
        ArrayList<AnalyticsEntity> result = new ArrayList<>();
//        String pattern = "yyyy-MM-dd kk:mm:ss";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        Calendar cal = Calendar.getInstance();
//        if(cal.getTimeZone().getID().equals("Etc/UTC")){
//            cal.add(Calendar.HOUR, 8);
//        }
//        String currentDate = simpleDateFormat.format(cal.getTime());
//        System.out.println("Current date: " + currentDate);
//        if(period.equals("day")){
//            cal.add(Calendar.DATE, -1);
//        }else if(period.equals("week")){
//            cal.add(Calendar.DATE, -7);
//        }else if(period.equals("month")){
//            cal.add(Calendar.MONTH, -1);
//        }else if(period.equals("year")){
//            cal.add(Calendar.YEAR, -1);
//        }else{
//            cal.add(Calendar.YEAR, -100);
//        }
//        String startDate = simpleDateFormat.format(cal.getTime());

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select t.companyName, t.outletName, t.tid, refunded, refundedBy, dateRefunded, t.Employee_Name, type, Date, Food_Name, Discount_Name, p.Total_Price, quantity from transaction t, purchase p where t.tid = p.TID and t.companyName like '" + companyName + "' and t.outletName like '" + outletName + "';");
            System.out.println("transaction stmt " + stmt );
            rs = stmt.executeQuery();

            while(rs.next()){
                AnalyticsEntity entity = new AnalyticsEntity(rs.getString("companyName"), rs.getString("outletName"), rs.getString("Type"), rs.getString("Date"), rs.getString("Food_Name"), rs.getInt("quantity"), rs.getDouble("Total_Price"), rs.getString("tid"), rs.getString("Discount_Name"), rs.getString("Employee_Name"));
                if(rs.getString("Refunded").equals("1")){
                    entity.refunded = true;
                    entity.refundedBy = rs.getString("refundedBy");
                    entity.refundedDate = rs.getString("dateRefunded");
                }
                result.add(entity);
            }
            return result;

        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to get analytics from'" + companyName + " " + outletName + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        

        
        return result;
    }
    
    public static ArrayList<Transaction> getTransactions(String companyName, String outletName, String username, String timeStr) throws DayNotStartedException{
        ArrayList<Transaction> transactionList = new ArrayList<>();
        
        String pattern = "yyyy-MM-dd kk:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Calendar cal = Calendar.getInstance();
        if(!cal.getTimeZone().getID().equals("Asia/Singapore")){
            cal.add(Calendar.HOUR, 8);
        }
        
        if(username == null && timeStr != null){
            int time = Integer.parseInt(timeStr);
            cal.add(Calendar.HOUR,-time);
            String prevDateTime = simpleDateFormat.format(cal.getTime());

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {// retrieves password from the database for specified username
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("select * from transaction where Employee_Name in (select Username from user where CompanyName = '" + companyName + "' and outletName = '" + outletName + "') and date >= '" + prevDateTime + "' order by Date;");
                System.out.println(stmt);
                rs = stmt.executeQuery();
                while(rs.next()){
                    Transaction t = new Transaction(rs.getString("TID"), rs.getString("Employee_Name"),rs.getString("Date"), rs.getString("Type"), rs.getDouble("Total_Price"), rs.getString("Refunded"));
                    if(t.refunded){
                        t.refundedBy = rs.getString("refundedBy");
                        t.refundedDate = rs.getString("dateRefunded");
                    }
                    transactionList.add(t);                
                }              
                return transactionList;
            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve transaction from'" + outletName + "'", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
            return null;
        }else if(username == null && timeStr == null){
            cal.add(Calendar.HOUR,-3);
            String prevDateTime = simpleDateFormat.format(cal.getTime());

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {// retrieves password from the database for specified username
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("select * from transaction where Employee_Name in (select Username from user where CompanyName = '" + companyName + "' and outletName = '" + outletName + "') and date >= '" + prevDateTime + "' order by Date;");
                System.out.println(stmt);
                rs = stmt.executeQuery();
                while(rs.next()){
                    Transaction t = new Transaction(rs.getString("TID"), rs.getString("Employee_Name"),rs.getString("Date"), rs.getString("Type"), rs.getDouble("Total_Price"), rs.getString("Refunded"));
                    if(t.refunded){
                        t.refundedBy = rs.getString("refundedBy");
                        t.refundedDate = rs.getString("dateRefunded");
                    }
                    transactionList.add(t);                
                }           
                return transactionList;
            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve transaction from'" + outletName + "'", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
            return null;
        }else{
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {// retrieves password from the database for specified username
                conn = ConnectionManager.getConnection();
                
                String dateTime = UserDao.isStarted(username);
                if(dateTime == null){
                    throw new DayNotStartedException("Day/Shift not started");
                }
                stmt = conn.prepareStatement("select * from transaction where Employee_Name in (select Username from user where CompanyName = '" + companyName + "' and outletName = '" + outletName + "') and Employee_Name in (select child from hierarchy where parent like '" + username + "' UNION select username from user where username = '" + username + "') and date >= '" + dateTime + "' order by Date;");
                
                System.out.println(stmt);
                rs = stmt.executeQuery();
                while(rs.next()){
                    Transaction t = new Transaction(rs.getString("TID"), rs.getString("Employee_Name"),rs.getString("Date"), rs.getString("Type"), rs.getDouble("Total_Price"), rs.getString("Refunded"));
                    if(t.refunded){
                        t.refundedBy = rs.getString("refundedBy");
                        t.refundedDate = rs.getString("dateRefunded");
                    }
                    transactionList.add(t);
                }            
                return transactionList;
            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve transaction from'" + outletName + "'", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
            return null;
        }
    }
    
    public static boolean refundTransaction(String username, String transactionId, String dateTime){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("update transaction SET Refunded = 1, refundedBy = '" + username + "', dateRefunded = '" + dateTime + "' where TID = '" + transactionId + "';");
            stmt.executeUpdate(); 
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve transaction from'" + transactionId + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean moveTransaction(String username){
        String parent = UserDao.getParent(username);
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("update transaction SET Employee_Name = '" + parent + "' where Employee_Name = '" + username + "';");
            stmt.executeUpdate(); 
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to remove transactions from'" + username + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static ArrayList<Transaction> getAllTransactions(){
        ArrayList<Transaction> transactionList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select * from transaction;");

            System.out.println(stmt);
            rs = stmt.executeQuery();
            while(rs.next()){
                Transaction t = new Transaction(rs.getString("TID"), rs.getString("companyName"), rs.getString("outletName"), rs.getString("Employee_Name"),rs.getString("Date"), rs.getString("Type"), rs.getDouble("Total_Price"), rs.getString("Refunded"), rs.getString("Discount_Name"), rs.getString("DineIn").equals("0x01"));
                if(t.refunded){
                    t.refundedBy = rs.getString("refundedBy");
                    t.refundedDate = rs.getString("dateRefunded");
                }
                transactionList.add(t);
            }            
            return transactionList;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve transaction for snapcoin admin", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static Transaction getTransaction(String id){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select * from transaction where tid = '" + id + "';");

//            System.out.println(stmt);
            rs = stmt.executeQuery();
            while(rs.next()){
                Transaction t = new Transaction(rs.getString("TID"), rs.getString("companyName"), rs.getString("outletName"), rs.getString("Employee_Name"),rs.getString("Date"), rs.getString("Type"), rs.getDouble("Total_Price"), rs.getString("Refunded"), rs.getString("Discount_Name"), rs.getString("DineIn").equals("0x01"));
                if(t.refunded){
                    t.refundedBy = rs.getString("refundedBy");
                    t.refundedDate = rs.getString("dateRefunded");
                }
                addPurchases(t);
                return(t);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve transaction for snapcoin admin", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static void addPurchases(Transaction t){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            stmt = conn.prepareStatement("select * from Purchase where tid = '" + t.transactionId + "';");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            while(rs.next()){
                FoodItem f = new FoodItem(rs.getString("Food_Name"), Integer.parseInt(rs.getString("Quantity")), Double.parseDouble(rs.getString("Total_Price")));
                t.addFoodItem(f);
            }            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve transaction for snapcoin admin", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
    }
}

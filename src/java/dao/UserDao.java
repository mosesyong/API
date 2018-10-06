/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.CollatedTransaction;
import Entity.Transaction;
import Exception.DayNotStartedException;
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
        
        
    public static boolean changePassword(String username, String oldPassword, String newPassword){
        if(newPassword.indexOf(' ') != -1 || newPassword.length() == 0){
            return false;
        }
        
        boolean verify = verify(username, "password", oldPassword);
        
        if(verify){
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
        }
        return false;
    }
    
    public static boolean verify(String username, String categoryName, String categoryValue){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select username from user where username = '" + username + "' and " + categoryName + " = '" + categoryValue + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                return true;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve '" + username + "' from users", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean resetPassword(String username, String email){
        boolean verify = verify(username, "email", email);
        if(verify){
            String newPassword = "" + (int)(Math.random()*1000);
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {// retrieves password from the database for specified username
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("UPDATE user SET Password = '" + newPassword + "' WHERE Username = '" + username + "';");
                stmt.executeUpdate();
                MailDao.sendMail(username, "Password reset request", "Hello " + username + "\nYour new password is " + newPassword + "\nDon't forget to reset your password as soon as you log in!\nThank you :)");
                return true;

            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to change '" + username + "' password", ex);
            } catch (Exception e){
                return false;
            }finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }
        return false;
    }
    
    public static boolean setSurcharge(Double gst, double svc, String companyName, String outletName){
        if(!checkSurcharge(companyName, outletName)){
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("Insert into surcharge(companyName, outletName, gst,svc) values('" + companyName + "','" + outletName + "', '" + gst + "', '" + svc + "');");
                stmt.executeUpdate();
                return true;

            } catch (SQLException ex) {
                Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to add '" + gst + " & " + svc + "' to " + companyName + "_" + outletName, ex);
            } catch (Exception e){
                return false;
            }finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }
        return false;
    }
    
    public static boolean checkSurcharge(String companyName, String outletName){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select * from surcharge where companyName = '" + companyName + "' and outletName = '" + outletName +"';");
            rs = stmt.executeQuery();
            
            while(rs.next()){
                return true;
            }
            return false;

        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to retrieve surcharge from " + companyName + "_" + outletName, ex);
        } catch (Exception e){
            return false;
        }finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        
        return false;
    }
    
    public static ArrayList<String> getEmployees(String username){
        ArrayList<String> employees = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select Child from hierarchy where Parent = '" + username + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                employees.add(rs.getString("Child"));
            }
            return employees;
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to change '" + username + "' password", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return employees;
    }
    
    public static String getEmail(String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select email from user where username = '" + username + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                return rs.getString("email");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to change '" + username + "' password", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;        
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

            stmt = conn.prepareStatement("select outletName from user where username = '" + username + "';");
            
            rs = stmt.executeQuery();
            
            while(rs.next()){
                return rs.getString("outletName");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static String[] getSurcharge(String companyName, String outletName){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select gst, svc from Surcharge where companyName = '" + companyName + "' and outletName = '" + outletName + "';");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while(rs.next()){
                String[] result = {rs.getString("gst"), rs.getString("svc")};
                for(String str : result){
                    System.out.println(str);
                }
                return result;
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access " + companyName + " " + outletName + " surcharge", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        String[] defaultSurcharge = {"0.07", "0.1"};
        return defaultSurcharge;
    }
    
    public static ArrayList<CollatedTransaction> toggleStartDay(String username, String dateTime){
        String startTime = isStarted(username);
        
        if(startTime != null){
            return endDay(username, startTime);
        }else{
            //start
            updateTime(username, dateTime);  
            return null;          
        }
        
    }
    
    public static ArrayList<CollatedTransaction> toggleStartShift(String username, String dateTime, String amount) throws DayNotStartedException{
        String startTime = isStarted(username);
        boolean dayStarted = dayStarted(username);
        if(startTime != null && dayStarted && amount == null){
            //end shift
            return endShift(username, startTime);
        }else if(dayStarted){
            if(amount == null){
                amount = "0";
            }
            updateTime(username, dateTime, amount);  
        }else{
            //start shift     
            throw new DayNotStartedException("Day not started yet");
        }
        
        return null;     
        
    }
    
    // for start day
    public static boolean updateTime(String username, String dateTime){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("update user set StartTime = '" + dateTime + "' where username = '" + username + "';");
            System.out.println(stmt);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    // for start shift
    public static boolean updateTime(String username, String dateTime, String amount){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("update user set StartTime = '" + dateTime + "', amount = '" + amount + "' where username = '" + username + "';");
            System.out.println(stmt);
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static boolean clearTime(String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("update user set StartTime = null, amount = null where username = '" + username + "';");
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
            
            
    public static ArrayList<CollatedTransaction> endDay(String username, String dateTime){
        HashMap<String,ArrayList<String>> collatedTransactionCategoryMap = new HashMap<>();
        ArrayList<String> employees = getEmployees(username);
        
        String[] paymentArray =  {"cash","card","snapcash"};
        ArrayList<String> paymentList = new ArrayList<>(Arrays.asList(paymentArray));
        collatedTransactionCategoryMap.put("payment", paymentList);
        
        ArrayList<String> categoryList = MenuDao.getCategories(username);
        collatedTransactionCategoryMap.put("categories", categoryList); // not done yet
        
        ArrayList<CollatedTransaction> result = new ArrayList<>();
        // get all categories -> all, breakdown by payment type, breakdown by category
        
        result.add(getAllOutletCollatedTransaction(username, dateTime));
        
        for(String paymentType : paymentList){
            CollatedTransaction resultTransaction = getOutletCollatedTransactionByPayment(username, dateTime, paymentType);
            
            result.add(resultTransaction);
        }
        
        clearTime(username);
        
        return result;
    }
    
    public static ArrayList<CollatedTransaction> endShift(String username, String dateTime){
        HashMap<String,ArrayList<String>> collatedTransactionCategoryMap = new HashMap<>();
        
        String[] paymentArray =  {"cash","card","snapcash"};
        ArrayList<String> paymentList = new ArrayList<>(Arrays.asList(paymentArray));
        collatedTransactionCategoryMap.put("payment", paymentList);
        
        ArrayList<String> categoryList = MenuDao.getCategories(username);
        collatedTransactionCategoryMap.put("categories", categoryList); // not done yet
        
        ArrayList<CollatedTransaction> result = new ArrayList<>();
        // get all categories -> all, breakdown by payment type, breakdown by category
        
        result.add(getAllCollatedTransaction(username, dateTime));
        
        for(String paymentType : paymentList){
            CollatedTransaction resultTransaction = getCollatedTransactionByPayment(username, dateTime, paymentType);
            
            result.add(resultTransaction);
            
            if(paymentType.equals("cash")){
                double cashBoxAmount = getCashBoxAmount(username);
                System.out.println("Cashbox: " + cashBoxAmount);
                System.out.println("Cash: " + resultTransaction.amount);
                double finalCashBoxAmount = cashBoxAmount + resultTransaction.amount;
                result.add(new CollatedTransaction(username + "_cashbox_" + dateTime, finalCashBoxAmount, 1));
            }
        }
        
        
        clearTime(username);
        
        return result;
    }
    
    public static CollatedTransaction getAllCollatedTransaction(String username, String dateTime){
        CollatedTransaction collatedTransaction = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select SUM(Total_Price) as total, count(Total_Price) as count from transaction where Employee_Name = '" + username + "' and Date >= '" + dateTime + "';");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while(rs.next()){
                collatedTransaction = new CollatedTransaction(username + "_all_" + dateTime, rs.getDouble("total"), rs.getInt("count"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return collatedTransaction;
    }
    
    public static CollatedTransaction getCollatedTransactionByPayment(String username, String dateTime, String paymentType){
        CollatedTransaction collatedTransaction = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select SUM(Total_Price) as total, count(Total_Price) as count from transaction where Employee_Name = '" + username + "' and Date >= '" + dateTime + "' and type = '" + paymentType + "';");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while(rs.next()){
                collatedTransaction = new CollatedTransaction(username + "_" + paymentType + "_" + dateTime, rs.getDouble("total"), rs.getInt("count"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return collatedTransaction;
    }
    
    
    public static CollatedTransaction getAllOutletCollatedTransaction(String username, String dateTime){
        CollatedTransaction collatedTransaction = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select SUM(Total_Price) as total, count(Total_Price) as count from transaction where Employee_Name in (select Child from hierarchy where Parent ='" + username + "') and Date >= '" + dateTime + "';");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while(rs.next()){
                collatedTransaction = new CollatedTransaction(username + "_all_" + dateTime, rs.getDouble("total"), rs.getInt("count"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return collatedTransaction;
    }
    
    public static CollatedTransaction getOutletCollatedTransactionByPayment(String username, String dateTime, String paymentType){
        CollatedTransaction collatedTransaction = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select SUM(Total_Price) as total, count(Total_Price) as count from transaction where Employee_Name in (select Child from hierarchy where Parent ='" + username + "') and Date >= '" + dateTime + "' and type = '" + paymentType + "';");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while(rs.next()){
                collatedTransaction = new CollatedTransaction(username + "_" + paymentType + "_" + dateTime, rs.getDouble("total"), rs.getInt("count"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return collatedTransaction;
    }
    
    
    public static String isStarted(String username){
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
                    return null;
                }else{
                    return startTime;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return null;
    }
    
    public static boolean dayStarted(String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select StartTime from user where username like (select Parent from hierarchy where Child = '" + username + "');");
            
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
    
    public static CollatedTransaction getCollatedTransactionByCategory(String username, String dateTime, String category){
        //select quantity, total_price, category from purchase p ,FoodCategory f where tid in (select TID from transaction where date >= '2018-09-20 21:00:00' and Employee_Name = "test") and Food_Name = FoodName and category = "non-coffee"
        CollatedTransaction collatedTransaction = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select quantity, total_price from purchase p ,FoodCategory f where tid in (select TID from transaction where date >= '" + dateTime + "'' and Employee_Name = '" + username + "') and Food_Name = FoodName and category = '" + category + "';");
            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while(rs.next()){
                collatedTransaction = new CollatedTransaction(username + "_" + category + "_" + dateTime, rs.getDouble("total_price"), rs.getInt("squantity"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return collatedTransaction;
    }
    
    public static Double getCashBoxAmount(String username){
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("select amount from user where username  = '" + username + "';");
            
            rs = stmt.executeQuery();
            
            while(rs.next()){
                return rs.getDouble("amount");
            }
        } catch (SQLException ex) {
            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        
        return null;
    }
    
//    public static CollatedTransaction getOutletCollatedTransactionByCategory(String username, String dateTime, String category){
//        CollatedTransaction collatedTransaction = null;
//        Connection conn = null;
//        PreparedStatement stmt = null;
//        ResultSet rs = null;
//        
//        try {
//            conn = ConnectionManager.getConnection();
//
//            stmt = conn.prepareStatement("select SUM(Total_Price) as total, count(Total_Price) as count from transaction where Employee_Name in (select Child from hierarchy where Parent ='" + username + "') and Date >= '" + dateTime + "' and type = '" + paymentType + "';");
//            System.out.println(stmt);
//            rs = stmt.executeQuery();
//            
//            while(rs.next()){
//                collatedTransaction = new CollatedTransaction(username + "_" + paymentType + "_" + dateTime, rs.getDouble("total"), rs.getInt("count"));
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(LoginDao.class.getName()).log(Level.SEVERE, "Unable to access '" + username + "' outlet", ex);
//        } finally {
//            ConnectionManager.close(conn, stmt, rs);
//        }
//        return collatedTransaction;
//    }
}

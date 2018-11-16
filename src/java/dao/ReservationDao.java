/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import Entity.Reservation;
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
public class ReservationDao {
    public static boolean addReservation(Reservation r){
        String companyName = r.companyName;
        String outletName = r.outletName;
        String customerName = r.customerName;
        String pax = r.pax;
        String customerPhoneNumber = r.customerPhoneNumber;
        String dateTime = r.dateTime;
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();
            
            stmt = conn.prepareStatement("insert into reservation (CompanyName, OutletName, customerName, pax, customerPhoneNumber, dateTime) values ('" + companyName + "', '" + outletName + "', '" + customerName + "', '" + pax + "', '" + customerPhoneNumber + "', '" + dateTime + "');");
            
            stmt.executeUpdate();
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, "Unable to add '" + customerName + "' to reservation", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }
    
    public static ArrayList<Reservation> getReservations(String companyName, String outletName, String username){
        ArrayList<Reservation> reservationList = new ArrayList<>();
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("SELECT * FROM reservation WHERE CompanyName like '" + companyName + "' and outletName like '" + outletName + "' and dateTime > (select startTime from user where username = '" + username + "') order by dateTime;" );
//            System.out.println("Reservation");
//            System.out.println(stmt);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                reservationList.add(new Reservation(rs.getString("companyName"), rs.getString("outletName"), rs.getString("customerName"), rs.getString("customerPhoneNumber"), rs.getString("pax"), rs.getString("dateTime"), rs.getString("status")));
            }
            return reservationList;
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, "Unable to retrieve discounts from'" + outletName + "'", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        
        return null;
    }
    
    public static boolean exists(Reservation r){
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ConnectionManager.getConnection();

            stmt = conn.prepareStatement("Select * from reservation where CompanyName like '" + r.companyName + "' and OutletName like '" + r.outletName + "' and CustomerName like '" + r.customerName + "' and dateTime like '" + r.dateTime + "';");
            rs = stmt.executeQuery();
            while(rs.next()){
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, "Unable find '" + r.customerName + "' from reservation", ex);
        } finally {
            ConnectionManager.close(conn, stmt, rs);
        }
        return false;
    }

    public static boolean updateReservation(Reservation r) {
        boolean exists = exists(r);
        
        if (exists){
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = ConnectionManager.getConnection();

                stmt = conn.prepareStatement("UPDATE Reservation SET status = '" + r.status +  "' WHERE CompanyName = '" + r.companyName + "' and outletName = '" + r.outletName + "' AND CustomerName = '" + r.customerName + "' and dateTime = '" + r.dateTime + "';");

                stmt.executeUpdate();
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(ReservationDao.class.getName()).log(Level.SEVERE, "Unable to update " + r.customerName + "'s reservation status", ex);
            } finally {
                ConnectionManager.close(conn, stmt, rs);
            }
        }
        return false;
    }
}

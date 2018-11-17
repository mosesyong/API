/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.Reservation;
import dao.ReservationDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author moses
 */
public class AddReservationServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        try (PrintWriter out = response.getWriter()) {
            String companyName = request.getParameter("companyName");
            String outletName = request.getParameter("outletName");
            String customerName = request.getParameter("customerName");
            String customerPhoneNumber = request.getParameter("customerPhoneNumber");
            String pax = request.getParameter("pax");
            String unformattedDateTime = request.getParameter("dateTime");
            
            String pattern1 = "yyyy-MM-dd hh:mm:ss a";
            SimpleDateFormat sdf1 = new SimpleDateFormat(pattern1);
            Date date = null;
            try {
               date = sdf1.parse(unformattedDateTime);
            } catch (ParseException ex) {
                Logger.getLogger(TransactionInputServlet.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Invalid date input");
            }
            String pattern2 = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf2 = new SimpleDateFormat(pattern2);

            String dateTime = sdf2.format(date);
            
            boolean result = ReservationDao.addReservation(new Reservation(companyName, outletName, customerName, customerPhoneNumber, pax, dateTime));
            
            if(result){
                out.println("{\"status\":\"success\"}");
            }else{
                out.println("{\"status\":\"fail\"}");
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}

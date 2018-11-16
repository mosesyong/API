/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.Discount;
import Entity.Reservation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.DiscountDao;
import dao.ReservationDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author moses
 */
public class ReservationRequestServlet extends HttpServlet {

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
            String username = request.getParameter("username");
            String companyName = request.getParameter("companyName");
            String outletName = request.getParameter("outletName");
            
            ArrayList<Reservation> reservationList = ReservationDao.getReservations(companyName, outletName, username);
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject overall = new JsonObject();
            JsonArray reservationArray = new JsonArray();
            if(reservationList != null){
                for(Reservation r : reservationList){
                    JsonObject reservationObj = new JsonObject();
                    reservationObj.addProperty("customerName", r.customerName);
                    reservationObj.addProperty("customerPhoneNumber", r.customerPhoneNumber);
                    reservationObj.addProperty("pax", r.pax);
                    reservationObj.addProperty("dateTime", r.dateTime);
                    reservationObj.addProperty("status", r.status);
                    reservationArray.add(reservationObj);
                }
            }
            overall.add("reservation", reservationArray);
            out.println(overall);
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.AnalyticsEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.TransactionDao;
import dao.UserDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author moses
 */
public class TransactionOutputServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String companyName = request.getParameter("companyName");
            String outletName = request.getParameter("outletName");
            
            ArrayList<AnalyticsEntity> analyticsResultList = TransactionDao.getAnalytics(companyName, outletName);
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject overall = new JsonObject();
            JsonArray analyticsArr = new JsonArray();
            
            for(AnalyticsEntity entity : analyticsResultList){
                JsonObject analyticsObject = new JsonObject();
                analyticsObject.addProperty("foodName", entity.foodName);
                analyticsObject.addProperty("quantity", entity.quantity);
                analyticsObject.addProperty("totalPrice", entity.totalPrice);
                analyticsObject.addProperty("paymentType", entity.paymentType);
                analyticsObject.addProperty("dateTime", entity.dateTime);
                analyticsObject.addProperty("TID", entity.TID);
                analyticsObject.addProperty("cashierName", entity.cashierName);
                analyticsObject.addProperty("refunded", entity.refunded);
                analyticsArr.add(analyticsObject);
            }
            
//            Calendar cal = Calendar.getInstance();
//            overall.addProperty("timezone", cal.getTimeZone().getID().equals("Etc/UTC"));
            
            overall.add("result", analyticsArr);
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

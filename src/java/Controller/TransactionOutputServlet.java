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
            String type = request.getParameter("type");
            String username = request.getParameter("username");
            String outlet = request.getParameter("outletName");
            String period = request.getParameter("period"); // day, week, month, year
            String analyticsType = request.getParameter("analyticsType"); // sales, items
            System.out.println(request.getParameter("count"));
            int count = Integer.parseInt(request.getParameter("count")); // number of results
            
            ArrayList<AnalyticsEntity> analyticsResultList = TransactionDao.getAnalytics(type, username, outlet, period, analyticsType, count);
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject overall = new JsonObject();
            JsonArray analyticsArr = new JsonArray();
            
            for(AnalyticsEntity entity : analyticsResultList){
                JsonObject analyticsObject = new JsonObject();
                analyticsObject.addProperty("name", entity.name);
                analyticsObject.addProperty("quantity", entity.quantity);
                analyticsObject.addProperty("unitPrice", entity.price);
                analyticsObject.addProperty("totalPrice", entity.totalPrice);
                analyticsArr.add(analyticsObject);
            }
            
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
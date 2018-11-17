/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.Transaction;
import Exception.DayNotStartedException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.TransactionDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
public class TransactionListServlet extends HttpServlet {

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
            String time = request.getParameter("time");
            String outletName = request.getParameter("outletName");
            String companyName = request.getParameter("companyName");
            String username = request.getParameter("username");
            ArrayList<Transaction> transactionList;
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject overall = new JsonObject();
            
            try {
                transactionList = TransactionDao.getTransactions(companyName, outletName, username, time);
                JsonArray transactionArray = new JsonArray();
                for(Transaction t: transactionList){
                    JsonObject transactionObject = new JsonObject();
                    transactionObject.addProperty("id", t.transactionId);
                    transactionObject.addProperty("name", t.employeeName);
                    transactionObject.addProperty("totalPrice", t.totalPrice);
                    transactionObject.addProperty("date", t.dateTime);
                    transactionObject.addProperty("type", t.type);
                    transactionObject.addProperty("discountName", t.discountName);
                    transactionObject.addProperty("dineIn", t.dineIn);
                    transactionObject.addProperty("refunded", t.refunded);
                    transactionObject.addProperty("refundedBy", t.refundedBy);
                    transactionObject.addProperty("refundedDate", t.refundedDate);
                    
                    transactionArray.add(transactionObject);
                }
            overall.add("result", transactionArray);
            } catch (DayNotStartedException ex) {
                overall.addProperty("error", ex.getMessage());
            }
            
            
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.CollatedTransaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.UserDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
public class StartDayServlet extends HttpServlet {

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
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        
        try (PrintWriter out = response.getWriter()) {
            String username = request.getParameter("username");
            String unformattedDateTime = request.getParameter("dateTime");
            String pattern1 = "yyyy-MM-dd hh:mm:ss a";
            SimpleDateFormat sdf1 = new SimpleDateFormat(pattern1);
            Date date = null;
            try {
               date = sdf1.parse(unformattedDateTime);
            } catch (ParseException ex) {
                throw new RuntimeException("Invalid date input");
            }
            String pattern2 = "yyyy-MM-dd HH:mm:ss";
            SimpleDateFormat sdf2 = new SimpleDateFormat(pattern2);

            String dateTime = sdf2.format(date);
            
            
            ArrayList<CollatedTransaction> collatedTransactionList  = null;
            
            collatedTransactionList = UserDao.toggleStartDay(username, dateTime);
            
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonObject overall = new JsonObject();
            if(collatedTransactionList != null){
                overall.addProperty("status", "ended");
                JsonArray transactionArray = new JsonArray();
                for(CollatedTransaction ct : collatedTransactionList){
                    JsonObject transactionObj = new JsonObject();
                    
                    String[] nameArr = ct.collatedTransactionName.split("_");
                    transactionObj.addProperty("name", nameArr[0]);
                    transactionObj.addProperty("category", nameArr[1]);
                    transactionObj.addProperty("amount", ct.amount);
                    transactionObj.addProperty("count", ct.count);
                    transactionArray.add(transactionObj);
                }
                overall.add("result", transactionArray);
            }else{
                overall.addProperty("status", "started");
                overall.addProperty("result", "Started day");
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

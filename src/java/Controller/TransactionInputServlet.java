/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import dao.MenuDao;
import Entity.FoodItem;
import Entity.Transaction;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonParser;
import dao.TransactionDao;
import java.io.BufferedReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author moses
 */
public class TransactionInputServlet extends HttpServlet {

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
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
            } finally {
                reader.close();
            }
//            out.println(sb.toString());
            
            JsonParser parser = new JsonParser();
            if(sb != null){
                JsonObject jo = (JsonObject) parser.parse(sb.toString());
                
                String transactionType = jo.get("transactionType").getAsString();
                boolean dineIn = transactionType.equals("dinein");
                Double recievedTotal = jo.get("total").getAsDouble();
                String username = jo.get("username").getAsString();
                String unformattedDateTime = jo.get("dateTime").getAsString();
                String type = jo.get("type").getAsString();
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
                
                JsonArray purchases = jo.get("purchases").getAsJsonArray();
//                out.println(purchases);
                Transaction transaction = new Transaction(username, dateTime, type, recievedTotal, dineIn);
                for(JsonElement purchaseElement : purchases){
                    JsonObject purchaseObject = purchaseElement.getAsJsonObject();
                    String foodName = purchaseObject.get("food_name").getAsString();
                    int quantity = purchaseObject.get("quantity").getAsInt();
                    double price = MenuDao.getFoodPrice(username, foodName);
                    System.out.println("Food Price: " + price);
                    double totalPrice = quantity * price;
                    FoodItem foodItem = new FoodItem(foodName, quantity, totalPrice);
                    transaction.addFoodItem(foodItem);
                }
                double totalPrice = transaction.getTotalPrice();
                boolean result = TransactionDao.addTransaction(transaction);
                if(result){
                    response.setStatus(HttpServletResponse.SC_ACCEPTED);
                }else{
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
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

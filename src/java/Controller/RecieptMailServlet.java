/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.FoodItem;
import Entity.Transaction;
import dao.DiscountDao;
import dao.MailDao;
import dao.TransactionDao;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author moses
 */
public class RecieptMailServlet extends HttpServlet {

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
            try{
                String email = request.getParameter("email");
                String name = request.getParameter("name");
                String subtotal = request.getParameter("subtotal");
                String id = request.getParameter("id");
                
                if(email == null || name == null || email.length() == 0 || name.length() == 0 ){
                    throw new RuntimeException("Name or email is invalid");
                }
                Transaction t = TransactionDao.getTransaction(id);

                String subject = "E-receipt from " + t.companyName + "@" + t.outletName;
                String message = "Issued to: " + name + "\nDate: " + t.dateTime + "\n\n";
                message += "Items:\n";
                for(FoodItem f : t.foodList){
                    message += f.quantity + " x " + f.foodName + " ($" + f.totalPrice + ")\n";
                }
                message += "\nSubtotal: $" + subtotal + "\n";
                if(t.discountName.equals("null")){
                    message += "\nDiscount: None\n"; 
                }else{
                    message += "\nDiscount: " + t.discountName + " (" + DiscountDao.getDiscountAmount(t.companyName, t.outletName, t.discountName)*100 + "%)\n";
                }
                message += "Total: $" + t.totalPrice + "\n\n";
                message += "Thank you for your purchase!\nSee you again :)";

                try{
                    MailDao.sendCustomMail(email, subject, message);
                    out.println("{\"status\":\"success\"}");
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    out.println("{\"status\":\"fail\"}");
                }
            }catch(Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage());
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

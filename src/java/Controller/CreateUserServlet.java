/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import dao.CreateDao;
import dao.DiscountDao;
import dao.LoginDao;
import dao.MailDao;
import dao.UserDao;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

/**
 *
 * @author Moses
 */
public class CreateUserServlet extends HttpServlet {

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
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String companyName = request.getParameter("companyName");
            String outletName = request.getParameter("outletName");
            String creator = request.getParameter("creator");
            String subCreator = request.getParameter("subCreator");
            String type = request.getParameter("type");
            
            boolean directlyCreating = subCreator != null;
            HashSet<String> accessSet = new HashSet<>();
            String menuRights = request.getParameter("menuRights");
            if(menuRights.equals("1")){
                accessSet.add("menu_right");
            }
            
            String menu = request.getParameter("menu");
            if(menu.equals("1")){
                accessSet.add("menu");
            }
            
            String financeRights = request.getParameter("financeRights");
            if(financeRights.equals("1")){
                accessSet.add("payment_right");
            }
            
            String finance = request.getParameter("finance");
            if(finance.equals("1")){
                accessSet.add("payment");
            }
            
            String refund = request.getParameter("refund");
            if(refund.equals("1")){
                accessSet.add("refund");
            }else if(type.equals("1") || type.equals("2")){
                accessSet.add("refund");
            }
            
            String email = request.getParameter("email");
            
            try{
                double gst = Double.parseDouble(request.getParameter("gst"))/100;
                double svc = Double.parseDouble(request.getParameter("svc"))/100;

                UserDao.setSurcharge(gst, svc, companyName, outletName);
            }catch(Exception e){
                e.printStackTrace();
                System.out.println("No gst and svc sent");
            }
            
            if(directlyCreating){
                creator = subCreator;
                type = "" + (Integer.parseInt(type) + 1);
            }
            boolean result = CreateDao.create(username, password, email, companyName, outletName, creator, type, accessSet);
            System.out.println("Create " + username + " result: " + result);
            if(result){
                DiscountDao.addSnapCoinDiscount(companyName, outletName);
                
                MailDao.sendMail(username, "Welcome " + username, "Welcome to snapcoin-pos. Your username is: " + username + " and your temporary password is: " + password + "\nThank you for signing up with us :)\nDon't forget to reset your password as soon as you log in!");
                response.setStatus(HttpServletResponse.SC_ACCEPTED); //202
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            }
            
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }
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

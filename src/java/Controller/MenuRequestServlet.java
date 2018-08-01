/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
public class MenuRequestServlet extends HttpServlet {

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
            String outletName = request.getParameter("outletName");
            if(outletName != null && outletName.equals("1")){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject overall = new JsonObject();
                JsonArray menuArray = new JsonArray();
                JsonObject menuItem1 = new JsonObject();
                menuItem1.addProperty("name", "chicken rice");
                menuItem1.addProperty("price", 3.5);
                menuItem1.addProperty("description", "Chicken rice, if you are Singaporean, you don't need a description.");
                menuItem1.addProperty("url", "<fake chicken rice url to be inserted>");
                JsonArray categoryArray = new JsonArray();
                categoryArray.add("main");
                categoryArray.add("rice");
                categoryArray.add("chicken");
                categoryArray.add("singaporean");
                menuItem1.add("category", categoryArray);
                menuArray.add(menuItem1);

                JsonObject menuItem2 = new JsonObject();
                menuItem2.addProperty("name", "coke");
                menuItem2.addProperty("price", 1.2);
                menuItem2.addProperty("description", "Cock");
                menuItem2.addProperty("url", "<fake coke url to be inserted>");
                categoryArray = new JsonArray();
                categoryArray.add("drink");
                categoryArray.add("gassy drink");
                categoryArray.add("F&N");
                categoryArray.add("NOT SUGAR FREE");
                menuItem2.add("category", categoryArray);
                menuArray.add(menuItem2);

                overall.add("menu",menuArray);

                out.println(overall);
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

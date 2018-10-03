/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import Entity.Category;
import Entity.MenuItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.MenuDao;
import java.io.File;
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
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
        
        try (PrintWriter out = response.getWriter()) {
            String outletName = request.getParameter("outletName");
            String companyName = request.getParameter("companyName");
            String menuImageDirectory =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + File.separator + "API" + File.separator + "Menu_Images";
            String categoryImageDirectory =  request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + File.separator + "API" + File.separator + "Category_Images";
            
            ArrayList<MenuItem> menuItemList = MenuDao.getMenuItems(companyName, outletName);
            
            if(menuItemList != null){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject overall = new JsonObject();
                JsonArray categoryArray = new JsonArray();
                
                for(Category category : MenuDao.getCategory(companyName, outletName)){
                    JsonObject categoryObj = new JsonObject();
                    categoryObj.addProperty("categoryName", category.categoryName);
                    categoryObj.addProperty("url", categoryImageDirectory + File.separator + category.image + ".png");
                    categoryArray.add(categoryObj);
                }
                overall.add("categoryList", categoryArray);
                
                JsonArray menuArray = new JsonArray();
                
                for(MenuItem menuItem : menuItemList){
                    JsonObject menuObject = new JsonObject();
                    menuObject.addProperty("name", menuItem.itemName);
                    menuObject.addProperty("desc", menuItem.desc);
                    menuObject.addProperty("price", menuItem.price);
                    menuObject.addProperty("cost", menuItem.cost);
//                    menuObject.addProperty("description", "Chicken rice, if you are Singaporean, you don't need a description.");
                    menuObject.addProperty("url", menuImageDirectory + File.separator + menuItem.imageName + ".png");
                    JsonArray foodCategoryArray = new JsonArray();
                    ArrayList<String> foodCategory = menuItem.categoryList;
                    for(String category : foodCategory){
                        foodCategoryArray.add(category);
                    }
                    menuObject.add("categories", foodCategoryArray);
                    menuArray.add(menuObject);
                }
                
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import dao.LoginDao;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dao.MailDao;
import dao.UserDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Moses
 */
public class LoginServlet extends HttpServlet {

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
            String enteredUsername = request.getParameter("username");
            String enteredPassword = request.getParameter("password");
            ArrayList<String> user = LoginDao.validate(enteredUsername, enteredPassword);
            if(user != null){
                String type = user.get(0);
                String username = user.get(1);
                String companyName = user.get(2);
                String outletName = LoginDao.getOutlets(username);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonObject overall = new JsonObject();
                if(type != null && type.length() != 0 && type.equals("0")){
                    overall.addProperty("username", username);
                    overall.addProperty("type", type);
                    overall.addProperty("companyName", companyName);
                    overall.addProperty("outletName", outletName);
                    JsonArray arr = new JsonArray();
                    arr.add("all");
                    overall.add("access", arr);
                    overall.add("employees", arr);
                    overall.add("roles", arr);
                    out.println(overall);
                }else if(type != null && type.length() != 0 && !type.equals("1")){
                    String employees = LoginDao.getEmployees(enteredUsername).trim();
                    HashSet<String> accessSet = LoginDao.getAccess(enteredUsername);
                    ArrayList<String> roleList = LoginDao.getRoles(companyName);
                    ArrayList<String> categoryList = LoginDao.getCategories(companyName, outletName);
                    String[] surchargeArr = UserDao.getSurcharge(companyName, outletName);
                    ArrayList<String> sisterOutlets = LoginDao.getSisterOutlets(companyName, outletName);
                    
//                    if(type.equals(1)){
//                        accessSet = new HashSet<>();
//                        accessSet.add("0");
//                    }
                    overall.addProperty("username", enteredUsername);
                    overall.addProperty("type", type);
                    overall.addProperty("companyName", companyName);
                    overall.addProperty("outletName", outletName);
                    overall.addProperty("gst", surchargeArr[0]);
                    overall.addProperty("svc", surchargeArr[1]);
                    
                    String started = UserDao.isStarted(username);
                    boolean isStarted = started != null;
                    overall.addProperty("started", isStarted);

                    JsonArray accessArray = new JsonArray();
                    for(String access : accessSet){
                        accessArray.add(access);
                    }
                    overall.add("access", accessArray);
                    
                    overall.addProperty("employees", employees);
                    
                    JsonArray roleArray = new JsonArray();
                    for(String role : roleList){
                        roleArray.add(role);
                    }
                    overall.add("roles", roleArray);
                    
                    JsonArray categoryArray = new JsonArray();
                    for(String category : categoryList){
                        categoryArray.add(category);
                    }
                    overall.add("category", categoryArray);
                    
                    JsonArray sisterArray = new JsonArray();
                    for(String sisterName : sisterOutlets){
                        sisterArray.add(sisterName);
                    }
                    overall.add("sisterOutlets", sisterArray);
                    
                    System.out.println("Overall: " + overall);
                    out.println(overall);
                    
                    
                }else if(type != null && type.length() != 0){
                    String employees = LoginDao.getEmployees(enteredUsername).trim();
                    HashSet<String> accessSet = LoginDao.getAccess(enteredUsername);
                    ArrayList<String> roleList = LoginDao.getRoles(companyName);
                    ArrayList<String> categoryList = LoginDao.getCategories(companyName, outletName);
//                    String[] surchargeArr = UserDao.getSurcharge(companyName, outletName);
                    ArrayList<String> sisterOutlets = LoginDao.getSisterOutlets(companyName, outletName);
                    
//                    if(type.equals(1)){
//                        accessSet = new HashSet<>();
//                        accessSet.add("0");
//                    }
                    overall.addProperty("username", enteredUsername);
                    overall.addProperty("type", type);
                    overall.addProperty("companyName", companyName);
                    overall.addProperty("outletName", outletName);
                    overall.addProperty("gst", 0);
                    overall.addProperty("svc", 0);

                    JsonArray accessArray = new JsonArray();
                    for(String access : accessSet){
                        accessArray.add(access);
                    }
                    overall.add("access", accessArray);
                    
                    overall.addProperty("employees", employees);
                    
                    JsonArray roleArray = new JsonArray();
                    for(String role : roleList){
                        roleArray.add(role);
                    }
                    overall.add("roles", roleArray);
                    
                    JsonArray categoryArray = new JsonArray();
                    for(String category : categoryList){
                        categoryArray.add(category);
                    }
                    overall.add("category", categoryArray);
                    
                    JsonArray sisterArray = new JsonArray();
                    for(String sisterName : sisterOutlets){
                        sisterArray.add(sisterName);
                    }
                    overall.add("sisterOutlets", sisterArray);
                    
                    System.out.println("Overall: " + overall);
                    out.println(overall);
                    
                    
                }else{
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            
        }catch(Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    
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

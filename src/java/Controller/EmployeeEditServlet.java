package Controller;

import dao.UserDao;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class EmployeeEditServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response){
        try (PrintWriter out = response.getWriter()) {
            String employeeUsername = request.getParameter("EmployeeUsername");
            String employeeAccess = request.getParameter("EmployeeAccess");
            String employeePassword = request.getParameter("EmployeePassword");
            
            boolean passwordUpdate = true;
            if(employeePassword != ""){
                passwordUpdate = UserDao.changePassword(employeeUsername, employeePassword);
            }
            
            ArrayList<String> employeeAccessList = new ArrayList<>(Arrays.asList(employeeAccess.split(",")));
            boolean accessUpdate = UserDao.changeEmployeeAccess(employeeUsername, employeeAccessList);
            
            if(passwordUpdate && accessUpdate){
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
                       
        }catch(Exception e){
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>        
}
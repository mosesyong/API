/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import dao.MenuDao;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

/**
 *
 * @author moses
 */
public class EditMenuItemServlet extends HttpServlet {
    
    
    private static final int MEMORY_THRESHOLD = 1024 * 1024 * 3;
    private static final int MAX_FILE_SIZE = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE = 1024 * 1024 * 50; // 50MB
    
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
            System.out.println("Enters edit menu servlet");
            HashMap<String,String> parameterMap = new HashMap<>();
            String directory = getServletContext().getRealPath("") + "Menu_Images";
            DiskFileItemFactory factory = new DiskFileItemFactory();
            factory.setSizeThreshold(MEMORY_THRESHOLD);
            
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setFileSizeMax(MAX_FILE_SIZE);
            upload.setSizeMax(MAX_REQUEST_SIZE);
            
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            System.out.println("Is multipart: " + isMultipart);
            if(!isMultipart){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            List<FileItem> formItems = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(new ServletRequestContext(request));
            if (formItems != null && formItems.size() > 0) {
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    
                    String fieldName = item.getFieldName();
                    String data = item.getString();
                    parameterMap.put(fieldName, data);
                }
                
                for(FileItem item : formItems){
                    if (!item.isFormField()) {
                        String fileName = new File(item.getName()).getName();
                        String storedName = fileName.substring(0,fileName.indexOf('.'));
                        String filePath = directory + File.separator + parameterMap.get("companyName") + "_" + parameterMap.get("outletName") + "_" + fileName;
                        File storeFile = new File(filePath);
                        item.write(storeFile);
                        parameterMap.put("image",storedName);
                        System.out.println("Image name: " + storedName);
                    }
                }
            }
            if(parameterMap.containsKey("image") || MenuDao.exists(parameterMap.get("name"), parameterMap.get("outletName"), parameterMap.get("companyName"))){
                System.out.println("Parameter Map: " + parameterMap);
                boolean result = MenuDao.editMenuItem(parameterMap);
                if(result){
                    response.setStatus(HttpServletResponse.SC_ACCEPTED);
                }else{
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
            
                
            
        } catch (Exception ex) {
            System.out.println("error: " + ex.getMessage());
            ex.printStackTrace();
            Logger.getLogger(AddMenuItemServlet.class.getName()).log(Level.SEVERE, null, ex);
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

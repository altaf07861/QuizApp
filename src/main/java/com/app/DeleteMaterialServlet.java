package com.app;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/DeleteMaterialServlet")
public class DeleteMaterialServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    Connection con;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
    	// TODO Auto-generated method stub
    	super.init(config);
    	con =(Connection) config.getServletContext().getAttribute("CONN");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userid");
        int materialId = Integer.parseInt(request.getParameter("id"));

        try {

            // Step 1: Get file path from DB
            PreparedStatement ps = con.prepareStatement("SELECT file_path FROM study_material WHERE material_id=? AND userid=?");
            ps.setInt(1, materialId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String filePath = rs.getString("file_path");
                File file = new File(filePath);

                // Step 2: Delete file from system
                if (file.exists()) {
                    file.delete();
                }

                // Step 3: Delete record from DB
                String deleteSql = "DELETE FROM study_material WHERE material_id=? AND userid=?";
                PreparedStatement deleteStmt = con.prepareStatement(deleteSql);
                deleteStmt.setInt(1, materialId);
                deleteStmt.setInt(2, userId);
                deleteStmt.executeUpdate();
                deleteStmt.close();
            }

            rs.close();
            ps.close();
    

            response.sendRedirect("viewMaterials.jsp?deleted=1");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error deleting file: " + e.getMessage());
        }
    }
}
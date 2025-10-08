package com.app;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@MultipartConfig
public class UploadMaterialServlet extends HttpServlet {

	Connection con;

	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);

		con=(Connection)config.getServletContext().getAttribute("CONN");
	}


    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userid") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userid");
        String subject = request.getParameter("title");  // subject/title field from JSP
        Part filePart = request.getPart("file");

        // Save uploaded file
        String fileName = filePart.getSubmittedFileName();
        String uploadPath = getServletContext().getRealPath("") + File.separator + "/uploads";
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
			uploadDir.mkdir();
		}

        String filePath = uploadPath + File.separator + fileName;
        filePart.write(filePath);

        // Extract text content from uploaded file
        String extractedText = "";
        if (fileName.endsWith(".pdf")) {
            try (PDDocument document = Loader.loadPDF(new File(filePath))) {
                PDFTextStripper pdfStripper = new PDFTextStripper();
                extractedText = pdfStripper.getText(document);
            }
        } else if (fileName.endsWith(".txt")) {
            extractedText = new String(java.nio.file.Files.readAllBytes(new File(filePath).toPath()));
        } else {
            extractedText = "[Unsupported file format for text extraction]";
        }

        // Store in DB
        int materilaId=0;
        try {


            PreparedStatement ps = con.prepareStatement("INSERT INTO study_material(userid, subject, content, file_path) VALUES (?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, userId);
            ps.setString(2, subject);
            ps.setString(3, extractedText);
            ps.setString(4, "uploads/" + fileName);

            int rows = ps.executeUpdate();

            if (rows > 0) {
            	ResultSet rs = ps.getGeneratedKeys();
            		if(rs.next())
            		{
            			materilaId=rs.getInt(1);
            		}
                response.sendRedirect("studentDashboard.jsp?msg=Upload Successful");
            } else {
                response.sendRedirect("uploadMaterial.jsp?error=Upload Failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }

        if(materilaId > 0 && !extractedText.isEmpty())
        {
        	try
        		{
        			QuizGenerationServlet.generateQuiz(materilaId, extractedText);
        		}
        		catch (Exception e) {
					// TODO: handle exception
        			e.printStackTrace();
				}

        }
        else
        	{
        		System.out.println("quiz generation skiped.. for "+materilaId);
        		QuestionGenerate.generateQuizFromMaterial(materilaId, extractedText);
        	}
        response.encodeRedirectURL("studentDashboard.jsp");

    }
}
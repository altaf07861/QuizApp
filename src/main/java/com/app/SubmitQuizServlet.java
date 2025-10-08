package com.app;

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


@WebServlet("/SubmitQuizServlet")
public class SubmitQuizServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

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

        // ✅ Step 1: Session check
    	 HttpSession session = request.getSession(false);
         if (session == null || session.getAttribute("userid") == null) {
             response.sendRedirect("login.jsp");
             return;
         }

        int userId = (int) session.getAttribute("userid");
        int materialId = Integer.parseInt(request.getParameter("material_id"));

        try
        {

            // ✅ Step 2: Get all questions for this material
            String sql = "SELECT quizid, correct_answer FROM quizs WHERE material_id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();

            // ✅ Step 3: Insert result for each question
            String insertSql = "INSERT INTO result(userid, quizid, score) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = con.prepareStatement(insertSql);

            while (rs.next()) {
                int quizId = rs.getInt("quizid");
                String correctAnswer = rs.getString("correct_answer");

                String studentAnswer = request.getParameter("q" + quizId);

                int score = 0;
                if (studentAnswer != null && studentAnswer.equalsIgnoreCase(correctAnswer)) {
                    score = 1; // correct
                }

                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, quizId);
                insertStmt.setInt(3, score);
                insertStmt.addBatch();  // batch insert
            }

            insertStmt.executeBatch();
            insertStmt.close();
            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Step 4: Redirect to result page
        response.sendRedirect("viewResults.jsp?material_id=" + materialId);
    }
}
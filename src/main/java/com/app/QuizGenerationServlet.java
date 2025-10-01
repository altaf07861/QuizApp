package com.app;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;


import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class QuizGenerationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

   

    private static final String GEMINI_API_KEY = "AIzaSyC1A-QjOKM9Gp4RbJ1972Smf4_EfHFawcA"; // 👈 set your key
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-05-20:generateContent?key=" + GEMINI_API_KEY;

    // ✅ Static method so UploadMaterialServlet can call it directly
    public static void generateQuiz(int materialId, String extractedText) throws Exception {
        // Step 1: Build prompt
        String prompt = "Generate 5 multiple-choice quiz questions from the text below. " +
        	    "Each question must have 4 options (A, B, C, D) and specify the correct answer. " +
        	    "Return ONLY a valid JSON array like: " +
        	    "[{\"question\":\"...\",\"option_a\":\"...\",\"option_b\":\"...\",\"option_c\":\"...\",\"option_d\":\"...\",\"correct_answer\":\"A\"}]. " +
        	    "Do not include any explanation or extra text.\n\n" + extractedText;

        // Step 2: Build request body for Gemini
        JSONObject requestBody = new JSONObject()
            .put("contents", new JSONArray()
                .put(new JSONObject()
                    .put("parts", new JSONArray()
                        .put(new JSONObject().put("text", prompt)))));

        // Step 3: Call Gemini API
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GEMINI_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API error: " + response.body());
        }

        JSONObject jsonResponse = new JSONObject(response.body());
        String quizText = jsonResponse
                .getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text");

       
        // Step 4: Parse AI response safely
        
        String cleanedText = quizText.trim();

     // Check if it's a quoted JSON string
     if (cleanedText.startsWith("\"[") && cleanedText.endsWith("]\"")) {
         cleanedText = cleanedText.substring(1, cleanedText.length() - 1); // remove outer quotes
         cleanedText = cleanedText.replace("\\\"", "\""); // unescape quotes
     }
        
        JSONArray quizArray;
        try {
            quizArray = new JSONArray(cleanedText);
        } catch (Exception e) {
            System.out.println("⚠️ Gemini returned non-JSON text:\n" +cleanedText);
            throw new RuntimeException("Failed to parse Gemini response.");
        }

        // Step 5: Insert quizs into DB
        try(Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/quizProject", "root", "boot"))  {
        	
            String sql = "INSERT INTO quizs(material_id, question, option_a, option_b, option_c, option_d, correct_answer) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            for (int i = 0; i < quizArray.length(); i++) {
                JSONObject q = quizArray.getJSONObject(i);

                ps.setInt(1, materialId);
                ps.setString(2, q.getString("question"));
                ps.setString(3, q.getString("option_a"));
                ps.setString(4, q.getString("option_b"));
                ps.setString(5, q.getString("option_c"));
                ps.setString(6, q.getString("option_d"));
                ps.setString(7, q.getString("correct_answer"));
                ps.executeUpdate();
            }

            ps.close();
        }
        catch(SQLException e)
        	{
        	 e.printStackTrace();
        	}
    }

    // ✅ Servlet endpoint (manual trigger)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int materialId = Integer.parseInt(request.getParameter("material_id"));

        // Step 1: Get text from DB
        String extractedText = "";
        try (Connection con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/quizProject", "root", "boot")) {
            PreparedStatement ps = con.prepareStatement("SELECT content FROM study_material WHERE material_id=?");
            ps.setInt(1, materialId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                extractedText = rs.getString("content");
                
            }
            ps.close();
        } catch (Exception e) {
            throw new ServletException("DB error: " + e.getMessage(), e);
        }

        // Step 2: Generate quiz
        try {
            generateQuiz(materialId, extractedText);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"success\",\"message\":\"Quiz generated\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("application/json");
            response.getWriter().write("{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
}
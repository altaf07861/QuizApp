package com.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionGenerate {

    public static void generateQuizFromMaterial(int materialId, String extractedText) {
        try {
            // Step 1: Split into sentences
            String[] sentences = extractedText.split("(?<=[.!?])\\s+");

            // Step 2: Select some sentences for questions
            List<String> sentenceList = new ArrayList<>(Arrays.asList(sentences));
            Collections.shuffle(sentenceList);

            int questionCount = Math.min(3, sentenceList.size());

            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/quizProject", "root", "boot");

            String sql = "INSERT INTO quizs (material_id, question, option_a, option_b, option_c, option_d, correct_answer) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);

            Random rand = new Random();

            for (int i = 0; i < questionCount; i++) {
                String sentence = sentenceList.get(i).trim();

                // Step 3: Pick a keyword (longest word in sentence for simplicity)
                String[] words = sentence.split("\\s+");
                String keyword = "";
                for (String w : words) {
                    if (w.length() > keyword.length() && w.length() > 4) {
                        keyword = w.replaceAll("[^a-zA-Z]", "");
                    }
                }

                if (keyword.isEmpty()) {
					continue;
				}

                // Step 4: Form a fill-in-the-blank question
                String question = sentence.replaceFirst("(?i)" + keyword, "___");

                // Step 5: Generate options
                List<String> options = new ArrayList<>();
                options.add(keyword); // correct
                options.add("Concept" + rand.nextInt(100));
                options.add("Theory" + rand.nextInt(100));
                options.add("None of the above");

                Collections.shuffle(options);

                // Step 6: Find correct option letter
                String correctAnswer = "A";
                for (int j = 0; j < options.size(); j++) {
                    if (options.get(j).equals(keyword)) {
                        correctAnswer = String.valueOf((char) ('A' + j));
                        break;
                    }
                }

                // Step 7: Save in DB
                stmt.setInt(1, materialId);
                stmt.setString(2, question);
                stmt.setString(3, options.get(0));
                stmt.setString(4, options.get(1));
                stmt.setString(5, options.get(2));
                stmt.setString(6, options.get(3));
                stmt.setString(7, correctAnswer);
                stmt.executeUpdate();
            }

            stmt.close();
            conn.close();

            System.out.println("✅ Improved fill-in-the-blank quiz generated!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
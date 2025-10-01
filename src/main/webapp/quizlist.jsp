<%@ page import="java.sql.*" %>


<%
	Connection con=(Connection)application.getAttribute("CONN");

    HttpSession session1 = request.getSession(false);
    if (session1 == null || session1.getAttribute("userid") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    int userId = (int) session1.getAttribute("userid");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Quiz List</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="container mt-4">

    <h2>📝 Available Quizzes</h2>
    <hr/>

    <table class="table table-bordered table-striped">
        <thead class="table-dark">
            <tr>
                <th>Material ID</th>
                <th>Subject</th>
                <th>Total Questions</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
        <%
            try {
                String sql = "SELECT m.material_id, m.subject, COUNT(q.quizid) AS question_count " +
                             "FROM study_material m " +
                             "LEFT JOIN quizs q ON m.material_id = q.material_id " +
                             "WHERE m.userid=? " +
                             "GROUP BY m.material_id, m.subject";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                boolean hasData = false;
                while (rs.next()) {
                    hasData = true;
        %>
                    <tr>
                        <td><%= rs.getInt("material_id") %></td>
                        <td><%= rs.getString("subject") %></td>
                        <td><%= rs.getInt("question_count") %></td>
                        <td>
                            <% if (rs.getInt("question_count") > 0) { %>
                                <a href="takeQuiz.jsp?material_id=<%= rs.getInt("material_id") %>" 
                                   class="btn btn-primary btn-sm">Start Quiz</a>
                            <% } else { %>
                                <span class="text-muted">No Quiz Available</span>
                            <% } %>
                        </td>
                    </tr>
        <%
                }
                if (!hasData) {
        %>
                    <tr>
                        <td colspan="4" class="text-center text-muted">No study materials found yet.</td>
                    </tr>
        <%
                }
               
            } catch (Exception e) {
                out.println("<tr><td colspan='4'>Error: " + e.getMessage() + "</td></tr>");
            }
        %>
        </tbody>
    </table>

    <a href="studentDashboard.jsp" class="btn btn-secondary">⬅ Back to Dashboard</a>

</body>
</html>
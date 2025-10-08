<%@ page import="java.sql.*" %>


<%
	Connection con=(Connection)application.getAttribute("CONN");
	HttpSession session1 = request.getSession(false);
	if(session1 == null || session1.getAttribute("userid") == null) 
	{
		response.sendRedirect("login.jsp"); // redirect if not logged in
		return;
	}

	int userId = (int) session1.getAttribute("userid");
%>

<!DOCTYPE html>
<html>
<head>
    <title>My Quiz Results</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
</head>
<body class="container mt-5">

    <h2>📊 My Quiz Results</h2>
    <hr/>

    <table class="table table-bordered table-striped">
        <thead class="table-dark">
            <tr>
                <th>Subject</th>
                <th>Score</th>
                <th>Date</th>
                <th>View Details</th>
            </tr>
        </thead>
        <tbody>
        <%
            try {
                

                String sql = "SELECT m.material_id, m.subject, " +
                             "SUM(r.score) AS total_score, COUNT(r.result_id) AS total_questions, " +
                             "MAX(r.attempted_on) AS last_attempt " +
                             "FROM result r " +
                             "JOIN quizs q ON r.quizid = q.quizid " +
                             "JOIN study_material m ON q.material_id = m.material_id " +
                             "WHERE r.userid = ? " +
                             "GROUP BY m.material_id, m.subject";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
        %>
                    <tr>
                        <td><%= rs.getString("subject") %></td>
                        <td><%= rs.getInt("total_score") %> / <%= rs.getInt("total_questions") %></td>
                        <td><%= rs.getTimestamp("last_attempt") %></td>
                        <td>
                            <a href="viewResults.jsp?material_id=<%= rs.getInt("material_id") %>" 
                               class="btn btn-sm btn-info">View</a>
                        </td>
                    </tr>
        <%
                }
                if (!hasResults) {
                    out.println("<tr><td colspan='4' class='text-center text-danger'>No results found</td></tr>");
                }

                rs.close();
                ps.close();
             
            } catch (Exception e) {
                out.println("<tr><td colspan='4' class='text-danger'>Error: " + e.getMessage() + "</td></tr>");
            }
        %>
        </tbody>
    </table>

    <a href="studentDashboard.jsp" class="btn btn-primary">⬅ Back to Dashboard</a>

</body>
</html>
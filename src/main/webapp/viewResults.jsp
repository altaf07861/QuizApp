<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%
Connection con = (Connection) application.getAttribute("CONN");
HttpSession session1 = request.getSession(false);
if (session1 == null || session1.getAttribute("userid") == null) {
	response.sendRedirect("login.jsp");
	return;
}
int userId = (int) session.getAttribute("userid");
int materialId = Integer.parseInt(request.getParameter("material_id"));
%>

<!DOCTYPE html>
<html>
<head>
<title>Quiz Result</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<style>
body {
	background-color: #f8f9fa;
}

.result-card {
	max-width: 500px;
	margin: auto;
}
</style>
</head>
<body class="container py-5">

	<div class="text-center mb-4">
		<h2 class="fw-bold text-primary">
			<i class="bi bi-bar-chart-line-fill"></i> Quiz Summary
		</h2>
		<hr class="w-25 mx-auto" />
	</div>

	<div class="card shadow result-card">
		<div class="card-body">
			<%
			try {
				String sql = "SELECT SUM(r.score) AS total_score, COUNT(r.result_id) AS total_questions, "
				+ "MAX(r.attempted_on) AS last_attempt " + "FROM result r " + "JOIN quizs q ON r.quizid = q.quizid "
				+ "WHERE r.userid=? AND q.material_id=?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, userId);
				ps.setInt(2, materialId);
				ResultSet rs = ps.executeQuery();

				if (rs.next() && rs.getInt("total_questions") > 0) {

					int totalScore = rs.getInt("total_score");
					int totalQuestions = rs.getInt("total_questions");
					int percentage = (int) ((totalScore * 100.0f) / totalQuestions);
			%>
			
			<!-- 🎯 Progress Bar -->
			<div class="mt-4">
				<label class="form-label"><strong>Performance:</strong></label>
				<div class="progress" style="height: 25px;">
					<div
						class="progress-bar progress-bar-striped progress-bar-animated bg-success"
						role="progressbar" style="width: <%=percentage%>%;"
						aria-valuenow="<%=percentage%>" aria-valuemin="0"
						aria-valuemax="100">
						<%=percentage%>%
					</div>
				</div>
			</div>


			<h5 class="card-title text-success">
				<i class="bi bi-check-circle-fill"></i> Quiz Completed
			</h5>
			<p class="mb-2">
				<i class="bi bi-award-fill text-warning"></i> <strong>Score:</strong>
				<%=rs.getInt("total_score")%>
				/
				<%=rs.getInt("total_questions")%></p>
			<p>
				<i class="bi bi-calendar-event-fill text-info"></i> <strong>Last
					Attempt:</strong>
				<%=rs.getTimestamp("last_attempt")%></p>
			<%
			} else {
			out.println("<p class='text-danger'><i class='bi bi-exclamation-circle-fill'></i> No results found for this quiz.</p>");
			}
			rs.close();
			ps.close();
			} catch (Exception e) {
			out.println("<p class='text-danger'><i class='bi bi-bug-fill'></i> Error: " + e.getMessage() + "</p>");
			}
			%>
		</div>
		<div class="card-footer text-center">
			<a href="studentDashboard.jsp" class="btn btn-outline-primary"> <i
				class="bi bi-arrow-left-circle"></i> Back to Dashboard
			</a>
		</div>
	</div>

</body>
</html>
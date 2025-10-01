
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%
// Check if user is logged in
		Connection con=(Connection)application.getAttribute("CONN");
		HttpSession session1 = request.getSession(false);
		if(session1 == null || session1.getAttribute("userid") == null) 
		{
			response.sendRedirect("login.jsp"); // redirect if not logged in
			return;
		}

		int userId = (int) session1.getAttribute("userid");
		String userName = (String) session1.getAttribute("name");
		
		
%>

<!DOCTYPE html>
<html>
<head>
<title>Student Dashboard</title>
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="container mt-4">

	<h2>
		Welcome, <%=userName%></h2>
	<hr />

	<!-- Dashboard Menu -->
	<div class="mb-4 d-flex flex-wrap gap-2">
		<a href="uploadMaterial.jsp" class="btn btn-primary"> 
		<i class="bi bi-cloud-upload-fill me-1"></i> Upload Study Material
		</a> 
		<a href="quizlist.jsp" class="btn btn-success"> 
		<i class="bi bi-question-circle-fill me-1"></i> Take Quiz
		</a>
		 <!-- --<a href="viewResults.jsp" class="btn btn-info"> 
		<i class="bi bi-bar-chart-fill me-1"></i> View Results
		</a> -->
		 <a href="logout" class="btn btn-danger"> 
		<i class="bi bi-box-arrow-right me-1"></i> Logout
		</a>
		<a href="viewMaterials.jsp" class="btn btn-warning">
   			 <i class="bi bi-folder-fill me-1"></i> View Uploaded Materials
		</a>
	</div>

	<!-- Show Recent Results -->
	<h3>Your Recent Quiz Attempts</h3>
	<table class="table table-bordered">
		<thead>
			<tr>
				<th>Quiz ID</th>
				<th>Score</th>
				<th>Date</th>
			</tr>
		</thead>
		<tbody>
			<%
			// Fetch recent results from DB
			try {
				
				PreparedStatement ps = con.prepareStatement("SELECT quizid, score, attempted_on FROM result WHERE userid=? ORDER BY attempted_on DESC LIMIT 5");
				ps.setInt(1, userId);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
			%>
			<tr>
				<td><%=rs.getInt("quizid")%></td>
				<td><%=rs.getInt("score")%></td>
				<td><%=rs.getTimestamp("attempted_on")%></td>
			</tr>
			<%
			}
			} catch (Exception e) {
			out.println("<tr><td colspan='3'>Error: " + e.getMessage() + "</td></tr>");
			}
			%>
		</tbody>
	</table>

</body>
</html>
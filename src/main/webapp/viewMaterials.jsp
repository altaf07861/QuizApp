

<%@page import="java.sql.Connection"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
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
    <title>View Study Materials</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="container mt-4">

    <h2 >
    	<i class="bi bi-folder-fill me-2 text-warning"></i> Your Uploaded Study Materials
	</h2>
    <hr/>

    <table class="table table-bordered table-striped">
        <thead>
            <tr>
                <th>Material ID</th>
                <th>Subject</th>
                <th>Uploaded On</th>
                <th>Download</th>
                 <th>Delete</th>
            </tr>
        </thead>
        <tbody>
        <%
            try {
                PreparedStatement stmt = con.prepareStatement("SELECT material_id, subject, file_path, upload_on FROM study_material WHERE userid=?");
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
        %>
                    <tr>
                        <td><%= rs.getInt("material_id") %></td>
                        <td><%= rs.getString("subject") %></td>
                        <td><%= rs.getTimestamp("upload_on") %></td>
                        <td>
                            <a href="<%= rs.getString("file_path") %>" target="_blank" class="btn btn-success btn-sm">
                                Download
                            </a>
                        </td>
                        <td>
   							<a href="DeleteMaterialServlet?id=<%= rs.getInt("material_id") %>"  class="btn btn-danger btn-sm"
     								 onclick="return confirm('Are you sure you want to delete this material?');">
    							   Delete
  							 </a>
						</td>
                    </tr>
        <%
                }
              
            } catch (Exception e) {
                out.println("<tr><td colspan='4'>Error: " + e.getMessage() + "</td></tr>");
            }
        %>
        </tbody>
    </table>

   <a href="studentDashboard.jsp" class="btn btn-secondary">
    	<i class="bi bi-arrow-left-circle me-1"></i> Back to Dashboard
	</a>

</body>
</html>


<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Connection"%>
<%
	Connection con=(Connection)application.getAttribute("CONN");

    HttpSession session2 = request.getSession(false);
    if (session2 == null || session2.getAttribute("userid") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    int userId = (int) session.getAttribute("userid");
    int materialId = Integer.parseInt(request.getParameter("material_id"));
%>

<!DOCTYPE html>
<html>
<head>
    <title>Take Quiz</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
     <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
</head>
<body class="container mt-4">

    <h2>📝 Quiz</h2>
    <hr/>

    <form action="SubmitQuizServlet" method="post">
        <input type="hidden" name="material_id" value="<%= materialId %>"/>

        <%
            try {
               
                String sql = "SELECT * FROM quizs WHERE material_id = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, materialId);
                ResultSet rs = ps.executeQuery();

                int qNum = 1;
                while (rs.next()) {
        %>
                    <div class="mb-4">
                        <h5>Q<%= qNum %>. <%= rs.getString("question") %></h5>
                        <div class="form-check">
                            <input type="radio" class="form-check-input" 
                                   name="q<%= rs.getInt("quizid") %>" value="A" required>
                            <label class="form-check-label"><%= rs.getString("option_a") %></label>
                        </div>
                        <div class="form-check">
                            <input type="radio" class="form-check-input" 
                                   name="q<%= rs.getInt("quizid") %>" value="B">
                            <label class="form-check-label"><%= rs.getString("option_b") %></label>
                        </div>
                        <div class="form-check">
                            <input type="radio" class="form-check-input" 
                                   name="q<%= rs.getInt("quizid") %>" value="C">
                            <label class="form-check-label"><%= rs.getString("option_c") %></label>
                        </div>
                        <div class="form-check">
                            <input type="radio" class="form-check-input" 
                                   name="q<%= rs.getInt("quizid") %>" value="D">
                            <label class="form-check-label"><%= rs.getString("option_d") %></label>
                        </div>
                    </div>
        <%
                    qNum++;
                }
               
            } catch (Exception e) {
                out.println("<p class='text-danger'>Error: " + e.getMessage() + "</p>");
            }
        %>

        <button type="submit" class="btn btn-success">Submit Quiz</button>
    </form>

    <a href="quizList.jsp" class="btn btn-secondary mt-3">⬅ Back to Quiz List</a>

</body>
</html>
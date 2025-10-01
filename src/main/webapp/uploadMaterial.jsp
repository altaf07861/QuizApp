<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    HttpSession session1 = request.getSession(false);
    if (session1 == null || session1.getAttribute("userid") == null) {
        response.sendRedirect("login.jsp"); // Redirect if not logged in
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>📚 Upload Study Material</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body {
            background-color: #f8f9fa;
        }
        .upload-card {
            max-width: 600px;
            margin: auto;
            margin-top: 50px;
            border-radius: 12px;
        }
        .form-label {
            font-weight: 500;
        }
        .btn-upload {
            background-color: #0d6efd;
            border: none;
        }
        .btn-upload:hover {
            background-color: #0b5ed7;
        }
    </style>
</head>
<body>

    <div class="upload-card card shadow p-4">
        <h3 class="mb-3 text-center text-primary">📂 Upload Study Material</h3>
      <p class="text-muted text-center">
  		  <i class="bi bi-stars me-2 text-warning"></i> Make your learning smooth
	</p>
        <hr>

        <form action="UploadMaterialServlet" method="post" enctype="multipart/form-data">
            <div class="mb-3">
                <label for="title" class="form-label">📌 Material Title</label>
                <input type="text" name="title" id="title" class="form-control" placeholder="e.g. Java Basics Notes" required>
            </div>

            <div class="mb-3">
                <label for="file" class="form-label">📎 Upload File</label>
                <input type="file" name="file" id="file" class="form-control" accept=".pdf,.txt" required>
                <small class="text-muted">Accepted formats: PDF, TXT</small>
            </div>

            <div class="d-flex justify-content-between">
                <button type="submit" class="btn btn-upload text-white">🚀 Upload</button>
                <a href="studentDashboard.jsp" class="btn btn-outline-secondary">⬅️ Back to Dashboard</a>
            </div>
        </form>
    </div>

</body>
</html>
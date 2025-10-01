<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
		<link rel="stylesheet" href="css/registerform.css">
</head>
<body>
	<form class="form-container" action="loginServlet" method="post">
		<h2>Login Account</h2>

		<div class="form-group">
			<label for="email">Email</label> <input type="email" name="email"
				id="email" required />
		</div>

		<div class="form-group">
			<label for="password">Password</label> <input type="password"
				name="password" id="password" required />
		</div>

		<button type="submit">Login</button>
	</form>
</body>
</html>
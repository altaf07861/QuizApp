package com.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class loginServlet extends HttpServlet {
	
	Connection con;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		super.init(config);
		con=(Connection)config.getServletContext().getAttribute("CONN");
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		resp.setContentType("text/html");
		PrintWriter out=resp.getWriter();
		
		try {
			
			String _email=req.getParameter("email");
			String _password=req.getParameter("password");
			
			PreparedStatement ps=con.prepareStatement("select * from users where email=? and password=?");
			ps.setString(1, _email);
			ps.setString(2, _password);
			
			ResultSet rs=ps.executeQuery();
			
			if(rs.next())
			{
				HttpSession session=req.getSession();
				session.setAttribute("userid",rs.getInt("userid"));
				session.setAttribute("name", rs.getString("name"));
				session.setAttribute("password", rs.getString("password"));
				session.setAttribute("role", rs.getString("role"));
				
				if("admin".equals("role"))
				{
					resp.sendRedirect("adminDashboard.jsp");
					
				}
				else
					{
						resp.sendRedirect("studentDashboard.jsp");
						
					}
			}
			else
				{
					out.println("<h2>Invalid email or password <a href='login.jsp'>Try again</a></h2>");
				}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}

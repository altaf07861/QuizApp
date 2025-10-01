package com.app;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.swing.JOptionPane;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class registerServlet extends HttpServlet {
	
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
			
			String _name=req.getParameter("name");
			String _email=req.getParameter("email");
			String _password=req.getParameter("password");
			String _role="student";
			PreparedStatement ps=con.prepareStatement("insert into users(name,email,password,role) values(?,?,?,?)");
			ps.setString(1, _name);
			ps.setString(2, _email);
			ps.setString(3, _password);
			ps.setString(4,_role);
			
			
			int a=ps.executeUpdate();
			
			if(a>0)
			{
				//JOptionPane.showMessageDialog(null, "record inserteed");
				out.println("<h3>Registration successful.. <a href='login.jsp'> Login here.....</a></h3>");
			}
			else
				{
					//JOptionPane.showMessageDialog(null, "fail");
						out.println("<h3>Registration failed..</h3>");
				}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}

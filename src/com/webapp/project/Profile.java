package com.webapp.project;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	Entity user = new Entity("Login", 400);
	Query q = new Query("Login1");

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		HttpSession session = req.getSession(false);
		if (session != null) {

			String name = (String) session.getAttribute("name");
			String password = (String) session.getAttribute("password");

			String ename = req.getParameter("ename");
			String mobilenum = req.getParameter("number");
			String email = req.getParameter("email");
			String city = req.getParameter("city");
			String gender = req.getParameter("gender");
			user.setProperty("sname", name);
			user.setProperty("password", password);
			user.setProperty("reg name", ename);
			user.setProperty("Email", email);
			user.setProperty("mobilenum", mobilenum);
			user.setProperty("city", city);
			user.setProperty("Gender", gender);
			ds.put(user);
			Key k = KeyFactory.createKey("Login", 400);
			

					try {
						Entity e = ds.get(k);
						out.println(" <br> Welcome to your profile " + name);
						out.println(" <br>Your Email id: " + e.getProperty("sname"));
						out.println("<br>Your Password: " + e.getProperty("password"));
						out.println("<br>Your Registered name: " + e.getProperty("reg name"));
						out.println("<br>Mobile num: " + e.getProperty("mobilenum"));
						out.println("<br>Email: " + e.getProperty("Email"));
						out.println("<br>city:  " + e.getProperty("city"));
						out.println("<br>Gender: " + e.getProperty("Gender"));

					} catch (EntityNotFoundException e) {

						e.printStackTrace();
					}	
		out.println("<br> <html>");
		out.println("<br> <body>");
		out.println("<br><a href='update.html'>change password</a>");
		out.println("<br>");
		out.println("<br><a href='delete.html'>delete name</a>");
		out.println("<br>");
		out.println("<br><a href='Logout'> Logout </a>");
		out.println("<br>");
		out.println("<br><a href='index.html'>Home page</a>");
		out.println("<br></body>");
		out.println("<br></html>");
	}
	}
	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String name = req.getParameter("name");
		HttpSession session = req.getSession(false);
		if (session != null) {
			
				try {
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
					Filter propertyFilter = new FilterPredicate("sname", FilterOperator.EQUAL, name);
					q.setFilter(propertyFilter);
					PreparedQuery pq = datastore.prepare(q);
					Entity customer = pq.asSingleEntity();
					datastore.delete(customer.getKey());
					out.println("Name succesfully deleted");
					out.println("<br><a href='/Logout'> Logout </a>");
					out.println("<br><a href='details.html'>Profile</a>");
					out.println("<a href='index.html'>Home page </a>");
					out.println("</html></body>");
				} catch (Exception e) {
					out.println("No name found please enter valid name");
					req.getRequestDispatcher("delete.html").include(req, res);
					out.println("<html><body>");
					out.println("<a href='index.html'>Home page </a>");
					out.println("<a href='details.html'>Profile</a>");
					out.println("<br><a href='/Logout'> Logout </a>");
					out.println("</html></body>");
				}

		} else {
			out.println("please login first");
			req.getRequestDispatcher("index.html").include(req, res);
		}
	}
}

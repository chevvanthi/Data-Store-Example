package com.webapp.project;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.*;

public class Login extends HttpServlet {

	Entity user = new Entity("Login1");
	private static final long serialVersionUID = 1L;
	DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
	Query q = new Query("Login1");
	int count = 0;
	HttpSession session;

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String name = req.getParameter("name");
		String pass = req.getParameter("pass");
		try {
			Filter propertyFilter = new FilterPredicate("sname", FilterOperator.EQUAL, name);
			q.setFilter(propertyFilter);
			PreparedQuery pq = ds.prepare(q);
			Entity customer = pq.asSingleEntity();
			Key l = customer.getKey();
			try {
				Filter propertyFilter1 = new FilterPredicate("password", FilterOperator.EQUAL, pass);
				q.setFilter(propertyFilter1);
				PreparedQuery pq1 = ds.prepare(q);
				Entity customer1 = pq1.asSingleEntity();
				Key l1 = customer1.getKey();
			} catch (Exception e) {
				count++;
				if (count == 3) {
					out.println(
							"please enter the correct password or change the password otherwise your account wil be deleted");
					req.getRequestDispatcher("index.html").forward(req, res);
				}
				if (count > 3) {
					out.println("<br> Sorry!! your account has been deleted please register again ");
					DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
					Filter pf = new FilterPredicate("sname", FilterOperator.EQUAL, name);
					q.setFilter(pf);
					PreparedQuery p = datastore.prepare(q);
					Entity c = p.asSingleEntity();
					datastore.delete(c.getKey());
					req.getRequestDispatcher("register.html").forward(req, res);
				}

				out.println("password does not match please enter correct password ");
				req.getRequestDispatcher("index.html").forward(req, res);
			}

			if (pass.length() < 5) {
				out.println("<br><h4> Please enter min 5 character in password </h4>");
				RequestDispatcher rd = req.getRequestDispatcher("index.html");
				rd.include(req, res);
			} else {
				out.println("<br> You  are logged in successfully ");

				session = req.getSession();

				session.setAttribute("name", name);
				session.setAttribute("password", pass);

				out.println("<html><body>");
				out.println("<br><h4><a href='details.html'> Register</a></h4>");
				out.println("<br><h4><a href='update.html'>change password</a><h4>");
				out.println("<br><h4><a href='delete.html'>Delete name</a></h4>");
			}

		} catch (Exception e) {
			out.println("<br>Please Register First");
			req.getRequestDispatcher("register.html").include(req, res);
		}
	}

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
		res.setContentType("text/html");
		PrintWriter out = res.getWriter();
		String name = req.getParameter("name");
		String newpass = req.getParameter("newpass");
		session = req.getSession(false);
		if (session != null) {

			if (newpass.length() < 5) {
				out.println("<br><h4> Please enter min 5 character in password </h4>");
				RequestDispatcher rd = req.getRequestDispatcher("update.html");
				rd.include(req, res);
			} else {
				try {

					Filter propertyFilter = new FilterPredicate("sname", FilterOperator.EQUAL, name);
					q.setFilter(propertyFilter);
					PreparedQuery pq = ds.prepare(q);
					Entity c = pq.asSingleEntity();
					c.setProperty("password", newpass);
					ds.put(c);
					out.println("<br> <h3>updated succesfully </h3>");
					out.println("<html><body>");
					out.println("<br><a href='index.html'>Login </a>");
					out.println("<br>");
					out.println("<br><a href='details.html'>Register </a>");
					out.println("<br>");
					out.println("<br><a href='/Logout'>Logout</a>");
					out.println("</html></body>");
				} catch (Exception e) {
					out.println("Email doesn't exits please provide valid Email id");
					req.getRequestDispatcher("update.html").include(req, res);
				}
			}
		} else {
			out.println("Sorry name and password must be entered");
			req.getRequestDispatcher("update.html").include(req, res);

		}

	}

}

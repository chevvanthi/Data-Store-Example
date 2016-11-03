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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PreparedQuery.TooManyResultsException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	Query q = new Query("Login1");
	Entity e = new Entity("Login1");

	public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter t = res.getWriter();
		t.println("<html><body>");
		t.println("<h2> Click Login to Login</h2>");
		t.println("<br> <a href='index.html'>Login</a>");
		t.println("</body></html>");

		HttpSession session2 = req.getSession(false);

		if (session2 != null) {
			session2.invalidate();
			t.println(" <br> You are logged out succesfully");
		} else {
			t.println(" <br> please login first");
			RequestDispatcher rd = req.getRequestDispatcher("index.html");
			rd.include(req, res);
		}

	}

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		res.setContentType("text/html");
		PrintWriter p = res.getWriter();
		String name = req.getParameter("name");
		String password = req.getParameter("password");


			if (password.length() < 5) {
				p.println("<br><h4> Please enter min 5 character in password </h4>");
				req.getRequestDispatcher("register.html").forward(req, res);
			} 
			else {
				try {
					Filter propertyFilter = new FilterPredicate("sname", FilterOperator.EQUAL, name);
					q.setFilter(propertyFilter);
					PreparedQuery pq = datastore.prepare(q);
					Entity customer = pq.asSingleEntity();
					Key l = customer.getKey();
					p.println("<br>already a registered user");
					req.getRequestDispatcher("index.html").include(req, res);
				} catch (Exception e1) {

					e.setProperty("sname", name);
					e.setProperty("password", password);
					datastore.put(e);
					p.println("<br>Registered succesfully now you can login");
					req.getRequestDispatcher("index.html").include(req, res);
				}
			}

	}
}

package controller;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/my-reservations")
public class MyReservationsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
        	response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            ListingDAO dao = new ListingDAO();
            request.setAttribute("reservations", dao.getMyReservations(u.getId()));
            request.getRequestDispatcher("/secure/my-reservations.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

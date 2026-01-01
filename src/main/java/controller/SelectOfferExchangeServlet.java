package controller;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/exchange/select-offer")
public class SelectOfferExchangeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String targetIdStr = request.getParameter("targetId");
        if (targetIdStr == null || targetIdStr.isBlank()) {
            response.sendError(400, "Missing targetId");
            return;
        }

        try {
            int targetId = Integer.parseInt(targetIdStr);

            ListingDAO dao = new ListingDAO();
            request.setAttribute("targetId", targetId);
            request.setAttribute("myExchangeListings", dao.getMyAvailableExchangeListings(u.getId()));

            request.getRequestDispatcher("/secure/select-offer.jsp").forward(request, response);

        } catch (NumberFormatException ex) {
            response.sendError(400, "Invalid targetId");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

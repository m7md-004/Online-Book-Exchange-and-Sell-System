package controller;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/reserve-sell")
public class ReserveSellServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendError(400, "Missing listing id");
            return;
        }

        try {
            int listingId = Integer.parseInt(idStr);

            ListingDAO dao = new ListingDAO();
            boolean ok = dao.reserveSell(listingId, u.getId());

            if (!ok) {
                request.getSession().setAttribute("flash", "This listing is not available for reservation.");
                response.sendRedirect(request.getContextPath() + "/secure/browse");
                return;
            }

            request.getSession().setAttribute("flash", "Reserved successfully ✅");
            response.sendRedirect(request.getContextPath() + "/secure/my-reservations");

        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid listing id");
        } catch (IllegalArgumentException ex) {
            response.sendError(400, ex.getMessage());
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

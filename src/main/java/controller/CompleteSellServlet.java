package controller;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/sell/complete")
public class CompleteSellServlet extends HttpServlet {

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
            response.sendError(400, "Missing id");
            return;
        }

        try {
            int listingId = Integer.parseInt(idStr);

            ListingDAO dao = new ListingDAO();
            boolean ok = dao.completeSell(listingId, u.getId());

            if (!ok) {
                response.sendError(403, "Not allowed or listing not RESERVED SELL.");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/secure/my-listings");

        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid id");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

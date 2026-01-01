package controller;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/delete-listing")
public class DeleteListingServlet extends HttpServlet {

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
            int id = Integer.parseInt(idStr);

            ListingDAO dao = new ListingDAO();
            boolean ok = dao.deleteMyAvailableListing(id, u.getId());

            if (!ok) {
                response.sendError(400, "Delete failed (only AVAILABLE listing can be deleted).");
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

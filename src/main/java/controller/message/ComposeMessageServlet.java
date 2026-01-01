package controller.message;

import dao.ListingLookupDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;

@WebServlet("/secure/messages/compose")
public class ComposeMessageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String listingIdStr = request.getParameter("listingId");
        if (listingIdStr == null || listingIdStr.isBlank()) {
            response.sendError(400, "Missing listingId");
            return;
        }

        int listingId;
        try {
            listingId = Integer.parseInt(listingIdStr);
        } catch (Exception e) {
            response.sendError(400, "Invalid listingId");
            return;
        }

        try {
            ListingLookupDAO ldao = new ListingLookupDAO();
            int ownerId = ldao.getOwnerIdByListingId(listingId);

            if (ownerId == -1) {
                response.sendError(404, "Listing not found");
                return;
            }

            if (ownerId == u.getId()) {
                response.sendError(400, "You cannot message yourself.");
                return;
            }

            request.setAttribute("listingId", listingId);
            request.setAttribute("receiverId", ownerId);

            request.getRequestDispatcher("/secure/messages/compose.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

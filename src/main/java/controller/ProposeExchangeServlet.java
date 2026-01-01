package controller;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/exchange/propose")
public class ProposeExchangeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String targetIdStr = request.getParameter("targetId");
        String offeredIdStr = request.getParameter("offeredId");

        if (targetIdStr == null || offeredIdStr == null ||
                targetIdStr.isBlank() || offeredIdStr.isBlank()) {
            response.sendError(400, "Missing ids");
            return;
        }

        try {
            int targetId = Integer.parseInt(targetIdStr);
            int offeredId = Integer.parseInt(offeredIdStr);

            if (targetId == offeredId) {
                response.sendError(400, "You cannot offer the same listing.");
                return;
            }

            ListingDAO dao = new ListingDAO();

            if (dao.isOwnerOfListing(targetId, u.getId())) {
                response.sendError(400, "You cannot propose exchange on your own listing.");
                return;
            }

            if (!dao.isOwnerOfListing(offeredId, u.getId())) {
                response.sendError(403, "You can only offer your own listing.");
                return;
            }

            dao.createExchangeProposal(targetId, offeredId, u.getId());

            response.sendRedirect(request.getContextPath() + "/secure/browse");

        } catch (NumberFormatException ex) {
            response.sendError(400, "Invalid ids");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

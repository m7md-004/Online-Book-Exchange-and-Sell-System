package controller.exchange;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/exchange/accept")
public class AcceptExchangeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String proposalIdStr = request.getParameter("proposalId");
        if (proposalIdStr == null || proposalIdStr.isBlank()) {
            response.sendError(400, "Missing proposalId");
            return;
        }

        int proposalId;
        try {
            proposalId = Integer.parseInt(proposalIdStr);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid proposalId");
            return;
        }

        try {
            ListingDAO dao = new ListingDAO();

            boolean ok = dao.acceptExchangeProposal(proposalId, u.getId());
            if (!ok) {
                response.sendError(403, "Not allowed or proposal not pending");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/secure/exchange/incoming");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

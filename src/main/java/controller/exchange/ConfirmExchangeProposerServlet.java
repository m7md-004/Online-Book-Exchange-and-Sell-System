package controller.exchange;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/exchange/confirm-proposer")
public class ConfirmExchangeProposerServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String idStr = request.getParameter("proposalId");
        if (idStr == null || idStr.isBlank()) {
            response.sendError(400, "Missing proposalId");
            return;
        }

        try {
            int proposalId = Integer.parseInt(idStr);

            ListingDAO dao = new ListingDAO();
            boolean ok = dao.confirmExchangeAsProposer(proposalId, u.getId());

            if (!ok) {
                response.sendError(403, "Not allowed or proposal not ACCEPTED.");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/secure/exchange/sent");

        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid proposalId");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

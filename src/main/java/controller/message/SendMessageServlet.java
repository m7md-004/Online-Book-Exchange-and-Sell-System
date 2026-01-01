package controller.message;

import dao.ListingDAO;
import dao.MessageDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;

@WebServlet("/secure/message/send")
public class SendMessageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String listingIdStr = request.getParameter("listingId");
        String subject = request.getParameter("subject");
        String body = request.getParameter("body");

        if (listingIdStr == null || listingIdStr.isBlank()
                || subject == null || subject.isBlank()
                || body == null || body.isBlank()) {
            response.sendError(400, "Missing fields");
            return;
        }

        try {
            int listingId = Integer.parseInt(listingIdStr);

            ListingDAO ldao = new ListingDAO();
            int receiverId = ldao.getOwnerIdForListing(listingId);   

            MessageDAO mdao = new MessageDAO();
            mdao.sendMessage(u.getId(), receiverId, listingId, subject.trim(), body.trim());

            response.sendRedirect(request.getContextPath() + "/secure/messages/outbox");     
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid listingId");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

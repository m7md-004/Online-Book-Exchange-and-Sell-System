package controller.admin;

import dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;

@WebServlet("/secure/admin/toggle-block")
public class AdminToggleBlockServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        if (!"ADMIN".equals(String.valueOf(u.getRole()))) {
            response.sendError(403, "Admins only");
            return;
        }

        String idStr = request.getParameter("id");
        String action = request.getParameter("action");

        if (idStr == null || idStr.isBlank() || action == null || action.isBlank()) {
            response.sendError(400, "Missing fields");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid id");
            return;
        }

        boolean block = "block".equalsIgnoreCase(action);

        try {
            AdminDAO dao = new AdminDAO();
            dao.toggleBlockUser(userId, block);

            request.getSession().setAttribute("flash", block ? "User blocked." : "User unblocked.");

            response.sendRedirect(request.getContextPath() + "/secure/admin/users");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

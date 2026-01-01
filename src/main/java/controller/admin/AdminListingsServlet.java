package controller.admin;

import dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;

@WebServlet("/secure/admin/listings")
public class AdminListingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        try {
            AdminDAO dao = new AdminDAO();
            request.setAttribute("listings", dao.getAllListings());
            request.getRequestDispatcher("/secure/admin/listings.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

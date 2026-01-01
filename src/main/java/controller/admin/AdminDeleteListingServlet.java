package controller.admin;

import dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/admin/delete-listing")
public class AdminDeleteListingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest requset, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) requset.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(requset.getContextPath() + "/index.jsp");
            return;
        }

        if (!"ADMIN".equals(String.valueOf(u.getRole()))) {
            response.sendError(403, "Admins only");
            return;
        }

        String idStr = requset.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendError(400, "Missing id");
            return;
        }

        int listingId;
        try {
            listingId = Integer.parseInt(idStr);
        } catch (NumberFormatException ex) {
            response.sendError(400, "Invalid id");
            return;
        }

        try {
            AdminDAO dao = new AdminDAO();
            dao.deleteListingHard(listingId);  
            response.sendRedirect(requset.getContextPath() + "/secure/admin/listings");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

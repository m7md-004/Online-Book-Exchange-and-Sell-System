package controller.admin;

import dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;

@WebServlet("/secure/admin/users")
public class AdminUsersServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest requset, HttpServletResponse response)
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

        try {
            AdminDAO dao = new AdminDAO();
            requset.setAttribute("users", dao.getAllUsers());
            requset.getRequestDispatcher("/secure/admin/users.jsp").forward(requset, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}


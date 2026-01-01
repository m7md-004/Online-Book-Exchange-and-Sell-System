package controller.admin;

import dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;

@WebServlet("/secure/admin/categories")
public class AdminCategoriesServlet extends HttpServlet {

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
            request.setAttribute("categories", dao.getAllCategories());
            request.getRequestDispatcher("/secure/admin/categories.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

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

        String action = request.getParameter("action"); 
        try {
            AdminDAO dao = new AdminDAO();

            if ("add".equalsIgnoreCase(action)) {
                String name = request.getParameter("name");
                if (name == null || name.isBlank()) {
                    response.sendError(400, "Missing name");
                    return;
                }
                dao.addCategory(name.trim());

            } else if ("delete".equalsIgnoreCase(action)) {
                String idStr = request.getParameter("id");
                int id = Integer.parseInt(idStr);

                boolean ok = dao.deleteCategorySafe(id);
                request.getSession().setAttribute("flash",
                        ok ? "Category deleted successfully."
                           : "Cannot delete category because it is used by existing listings.");
            }
else {
                response.sendError(400, "Unknown action");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/secure/admin/categories");

        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid id");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

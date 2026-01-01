package controller.admin;

import dao.AdminDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;

@WebServlet("/secure/admin/course-codes")
public class AdminCourseCodesServlet extends HttpServlet {

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
            request.setAttribute("courseCodes", dao.getAllCourseCodes());
            request.getRequestDispatcher("/secure/admin/course-codes.jsp").forward(request, response);
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

                String code = request.getParameter("code");
                String dept = request.getParameter("department");

                if (code == null || dept == null ||
                    code.isBlank() || dept.isBlank()) {
                    response.sendError(400, "Missing fields");
                    return;
                }

                if (dao.courseCodeExists(code.trim())) {
                    request.getSession().setAttribute(
                            "flash_error",
                            "Course code already exists."
                    );
                } else {
                    dao.addCourseCode(code.trim(), dept.trim());
                    request.getSession().setAttribute(
                            "flash_success",
                            "Course code added successfully."
                    );
                }

            } else if ("delete".equalsIgnoreCase(action)) {
                String idStr = request.getParameter("id");
                int id = Integer.parseInt(idStr);

                boolean deleted = dao.deleteCourseCodeSafe(id);
                if (!deleted) {
                    request.getSession().setAttribute(
                            "flash_error",
                            "Cannot delete this course code because it is used by listings."
                    );
                } else {
                    request.getSession().setAttribute(
                            "flash_success",
                            "Course code deleted successfully."
                    );
                }
            }

            response.sendRedirect(request.getContextPath() + "/secure/admin/course-codes");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

}

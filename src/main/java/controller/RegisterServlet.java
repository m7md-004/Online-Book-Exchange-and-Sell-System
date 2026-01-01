package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String yearStr = request.getParameter("studyYear");

        if (name == null || email == null || password == null || yearStr == null ||
            name.isBlank() || email.isBlank() || password.isBlank()) {

            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing fields");
            return;
        }

        int studyYear;
        try {
            studyYear = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid study year");
            return;
        }

        try {
            UserDAO dao = new UserDAO();

            if (dao.findByEmail(email) != null) {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "Email already exists");
                return;
            }

            dao.create(name, email, password, studyYear, "STUDENT");

            resp.sendRedirect(request.getContextPath() + "/index.jsp");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

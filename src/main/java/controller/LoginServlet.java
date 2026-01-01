package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || password == null || email.isBlank() || password.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/index.jsp?error=missing");
            return;
        }

        try {
            UserDAO dao = new UserDAO();
            User user = dao.findByEmail(email);

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=invalid");
                return;
            }

            if (user.isBlocked()) {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=blocked");
                return;
            }

            if (!password.equals(user.getPasswordHash())) {
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=invalid");
                return;
            }

            HttpSession session = request.getSession(true);
            session.setAttribute("user", user);

            response.sendRedirect(request.getContextPath() + "/secure/dashboard.jsp");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

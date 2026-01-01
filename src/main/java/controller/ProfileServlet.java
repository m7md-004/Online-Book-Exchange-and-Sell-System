package controller;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;

import java.io.IOException;

@WebServlet("/secure/profile")
public class ProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.getRequestDispatcher("/secure/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String name = request.getParameter("name");
        String studyYearStr = request.getParameter("studyYear");

        if (name == null || name.isBlank() || studyYearStr == null || studyYearStr.isBlank()) {
            response.sendError(400, "Missing fields");
            return;
        }

        int studyYear;
        try {
            studyYear = Integer.parseInt(studyYearStr);
        } catch (NumberFormatException e) {
        	request.getSession().setAttribute("flash", "Study year must be a number");
        	request.getSession().setAttribute("flash_type", "error");
        	response.sendRedirect(request.getContextPath() + "/secure/profile");
            return;
        }

        if (studyYear < 1 || studyYear > 6) {
        	request.getSession().setAttribute("flash", "Study year must be between 1 and 6");
            request.getSession().setAttribute("flash_type", "error");
            response.sendRedirect(request.getContextPath() + "/secure/profile");
            return;
        }


        try {
            UserDAO dao = new UserDAO();
            dao.updateProfile(u.getId(), name.trim(), studyYear);

            u.setName(name.trim());
            u.setStudyYear(studyYear);
            request.getSession().setAttribute("user", u);

            request.setAttribute("msg", "Profile updated successfully.");
            request.getRequestDispatcher("/secure/profile.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

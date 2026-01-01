package controller.message;

import dao.MessageDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.User;

import java.io.IOException;

@WebServlet("/secure/messages/outbox")
public class OutboxServlet extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    User u = (User) request.getSession().getAttribute("user");
    if (u == null) {
      response.sendRedirect(request.getContextPath() + "/index.jsp");
      return;
    }

    try {
      MessageDAO dao = new MessageDAO();
      request.setAttribute("messages", dao.getOutbox(u.getId()));
      request.getRequestDispatcher("/secure/messages/outbox.jsp").forward(request, response);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}

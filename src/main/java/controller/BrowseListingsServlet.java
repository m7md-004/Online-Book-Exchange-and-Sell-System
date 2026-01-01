package controller;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import strategy.search.SearchCriteria;
import strategy.search.SearchService;
import strategy.search.SearchStrategy;

import java.io.IOException;

@WebServlet("/secure/browse")
public class BrowseListingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String q = request.getParameter("q");
        String filter = request.getParameter("filter");

        try {
            ListingDAO dao = new ListingDAO();
            dao.expireOldListings();

            if (q == null || q.isBlank()) {
                request.setAttribute("listings", dao.getAvailableListings());
            } else {
                SearchService service = new SearchService(dao);
                SearchStrategy strategy = service.pick(filter);
                request.setAttribute("listings", strategy.search(new SearchCriteria(q.trim())));
            }

            request.getRequestDispatcher("/secure/browse.jsp").forward(request, response);

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

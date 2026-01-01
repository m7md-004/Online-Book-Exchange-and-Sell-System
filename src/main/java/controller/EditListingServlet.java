package controller;

import dao.ListingDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Listing;
import model.ListingCondition;
import model.ListingStatus;
import model.ListingType;
import model.User;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/secure/edit-listing")
public class EditListingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        String idStr = request.getParameter("id");
        if (idStr == null || idStr.isBlank()) {
            response.sendError(400, "Missing id");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            ListingDAO listingDao = new ListingDAO();
            Listing l = listingDao.getListingById(id);

            if (l == null) {
                response.sendError(404, "Listing not found");
                return;
            }

            if (l.getOwnerId() != u.getId()) {
                response.sendError(403, "Not allowed");
                return;
            }

            if (l.getStatus() != ListingStatus.AVAILABLE) {
                response.sendError(400, "Listing is locked (not AVAILABLE).");
                return;
            }

            dao.AdminDAO adminDao = new dao.AdminDAO();
            request.setAttribute("categories", adminDao.getAllCategories());
            request.setAttribute("courseCodes", adminDao.getAllCourseCodes());

            request.setAttribute("listing", l);

            request.getRequestDispatcher("/secure/edit-listing.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(400, "Invalid id");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User u = (User) req.getSession().getAttribute("user");
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        try {
            String idStr = req.getParameter("id");
            if (idStr == null || idStr.isBlank()) {
                resp.sendError(400, "Missing id");
                return;
            }
            int id = Integer.parseInt(idStr);

            ListingDAO listingDao = new ListingDAO();
            Listing existing = listingDao.getListingById(id);

            if (existing == null) {
                resp.sendError(404, "Listing not found");
                return;
            }
            if (existing.getOwnerId() != u.getId()) {
                resp.sendError(403, "Not allowed");
                return;
            }
            if (existing.getStatus() != ListingStatus.AVAILABLE) {
                resp.sendError(400, "Listing is locked (not AVAILABLE).");
                return;
            }

            String title = req.getParameter("title");
            String author = req.getParameter("author");
            String edition = req.getParameter("edition");
            String conditionStr = req.getParameter("condition");
            String categoryIdStr = req.getParameter("categoryId");
            String courseCodeIdStr = req.getParameter("courseCodeId");

            if (title == null || title.isBlank() ||
                author == null || author.isBlank() ||
                edition == null || edition.isBlank() ||
                conditionStr == null || conditionStr.isBlank() ||
                categoryIdStr == null || categoryIdStr.isBlank() ||
                courseCodeIdStr == null || courseCodeIdStr.isBlank()) {
                resp.sendError(400, "Missing fields");
                return;
            }

            int categoryId = Integer.parseInt(categoryIdStr);
            int courseCodeId = Integer.parseInt(courseCodeIdStr);

            // تعبئة التعديل
            existing.setTitle(title.trim());
            existing.setAuthor(author.trim());
            existing.setEdition(edition.trim());
            existing.setCondition(ListingCondition.valueOf(conditionStr));
            existing.setCategoryId(categoryId);
            existing.setCourseCodeId(courseCodeId);

            if (existing.getType() == ListingType.SELL) {
                String priceStr = req.getParameter("price");
                if (priceStr == null || priceStr.isBlank()) {
                    resp.sendError(400, "Missing price for SELL listing");
                    return;
                }
                existing.setPrice(new BigDecimal(priceStr));
            }

            boolean ok = listingDao.updateMyAvailableListing(existing, u.getId());
            if (!ok) {
                resp.sendError(400, "Update failed (only AVAILABLE listing can be updated).");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/secure/my-listings");

        } catch (NumberFormatException e) {
            resp.sendError(400, "Invalid number in form");
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

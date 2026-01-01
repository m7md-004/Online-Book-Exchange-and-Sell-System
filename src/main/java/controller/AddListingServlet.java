package controller;

import dao.ListingDAO;
import factory.ListingFactory;
import factory.ListingFactoryProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Listing;
import model.User;

import java.io.File;
import java.io.IOException;

@WebServlet("/secure/add-listing")
@MultipartConfig(
	    maxFileSize = 5 * 1024 * 1024,      
	    maxRequestSize = 5 * 1024 * 1024
	)

public class AddListingServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        User u = (User) request.getSession().getAttribute("user");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        try {
            String type = request.getParameter("type");
            String title = request.getParameter("title");
            String author = request.getParameter("author");
            String conditionStr = request.getParameter("condition");
            String priceStr = request.getParameter("price");

            String edition = request.getParameter("edition");
            String categoryIdStr = request.getParameter("categoryId");
            String courseCodeIdStr = request.getParameter("courseCodeId");

            if (type == null || title == null || author == null || conditionStr == null ||
                    type.isBlank() || title.isBlank() || author.isBlank() || conditionStr.isBlank()) {

                request.getSession().setAttribute("flash", "Missing fields");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            if (edition == null || edition.isBlank() ||
                    categoryIdStr == null || categoryIdStr.isBlank() ||
                    courseCodeIdStr == null || courseCodeIdStr.isBlank()) {

                request.getSession().setAttribute("flash", "Missing edition/category/course code");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            int categoryId;
            int courseCodeId;
            try {
                categoryId = Integer.parseInt(categoryIdStr);
                courseCodeId = Integer.parseInt(courseCodeIdStr);
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("flash", "Invalid category/course code");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            Part imagePart = request.getPart("image");
            if (imagePart == null || imagePart.getSize() == 0) {
                request.getSession().setAttribute("flash", "Image is required");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            long maxSize = 5L * 1024 * 1024;
            if (imagePart.getSize() > maxSize) {
                request.getSession().setAttribute("flash", "Image must be <= 5MB");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }


            String contentType = imagePart.getContentType(); 
            boolean okType = "image/jpeg".equals(contentType) || "image/png".equals(contentType);
            if (!okType) {
                request.getSession().setAttribute("flash", "Only JPG/PNG allowed");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            String submitted = imagePart.getSubmittedFileName();
            if (submitted == null) {
                request.getSession().setAttribute("flash", "Invalid file");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            submitted = new File(submitted).getName();
            if (!submitted.contains(".")) {
                request.getSession().setAttribute("flash", "Invalid file name");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            String ext = submitted.substring(submitted.lastIndexOf(".")).toLowerCase();
            if (!ext.equals(".jpg") && !ext.equals(".jpeg") && !ext.equals(".png")) {
                request.getSession().setAttribute("flash", "Only JPG/PNG allowed");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            ListingFactory factory = ListingFactoryProvider.getFactory(type);
            Listing listing = factory.create(
                    u.getId(),
                    title.trim(),
                    author.trim(),
                    conditionStr.trim(),
                    priceStr
            );

            listing.setEdition(edition.trim());
            listing.setCategoryId(categoryId);
            listing.setCourseCodeId(courseCodeId);

            ListingDAO dao = new ListingDAO();
            int listingId = dao.insertListing(listing);
            if (listingId <= 0) {
                request.getSession().setAttribute("flash", "Failed to create listing");
                request.getSession().setAttribute("flash_type", "error");
                response.sendRedirect(request.getContextPath() + "/secure/add-listing");
                return;
            }

            String uploadDirPath = getServletContext().getRealPath("/uploads/listings");
            File uploadDir = new File(uploadDirPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            String fileName = "listing_" + listingId + "_" + System.currentTimeMillis() + ext;
            String fullPath = uploadDirPath + File.separator + fileName;
            imagePart.write(fullPath);

            String imagePathForDb = "uploads/listings/" + fileName;
            dao.updateListingImagePath(listingId, imagePathForDb);

            request.getSession().setAttribute("flash", "Listing created successfully ✅");
            request.getSession().setAttribute("flash_type", "success");
            response.sendRedirect(request.getContextPath() + "/secure/dashboard.jsp");

        } catch (IllegalStateException ex) {
            request.getSession().setAttribute("flash", "Upload too large. Max 5MB.");
            request.getSession().setAttribute("flash_type", "error");
            response.sendRedirect(request.getContextPath() + "/secure/add-listing");

        } catch (IllegalArgumentException ex) {
            request.getSession().setAttribute("flash", ex.getMessage());
            request.getSession().setAttribute("flash_type", "error");
            response.sendRedirect(request.getContextPath() + "/secure/add-listing");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        User u = (User) req.getSession().getAttribute("user");
        if (u == null) {
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            return;
        }

        try {
            dao.AdminDAO adminDao = new dao.AdminDAO();
            req.setAttribute("categories", adminDao.getAllCategories());
            req.setAttribute("courseCodes", adminDao.getAllCourseCodes());
            req.getRequestDispatcher("/secure/add-listing.jsp").forward(req, resp);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}

package dao;

import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO {

    public static class UserRow {
        public int id;
        public String name;
        public String email;
        public int studyYear;
        public String role;
        public boolean isBlocked;
        public String createdAt;
    }

    public List<UserRow> getAllUsers() throws Exception {
        String sql = "SELECT id, name, email, study_year, role, is_blocked, created_at " +
                     "FROM users ORDER BY created_at DESC";

        List<UserRow> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                UserRow u = new UserRow();
                u.id = rs.getInt("id");
                u.name = rs.getString("name");
                u.email = rs.getString("email");
                u.studyYear = rs.getInt("study_year");
                u.role = rs.getString("role");
                u.isBlocked = rs.getBoolean("is_blocked");
                u.createdAt = String.valueOf(rs.getTimestamp("created_at"));
                list.add(u);
            }
        }
        return list;
    }

    public boolean toggleBlockUser(int userId, boolean block) throws Exception {
        String sql = "UPDATE users SET is_blocked=? WHERE id=? AND role <> 'ADMIN'";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, block);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        }
    }


    public static class ListingRow {
        public int id;
        public int ownerId;
        public String ownerEmail;
        public String type;
        public String title;
        public String status;
        public String createdAt;
        public String expiresAt;
    }

    public List<ListingRow> getAllListings() throws Exception {
        String sql =
            "SELECT l.id, l.owner_id, u.email AS owner_email, l.type, l.title, l.status, l.created_at, l.expires_at " +
            "FROM listings l JOIN users u ON u.id = l.owner_id " +
            "ORDER BY l.created_at DESC";

        List<ListingRow> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ListingRow r = new ListingRow();
                r.id = rs.getInt("id");
                r.ownerId = rs.getInt("owner_id");
                r.ownerEmail = rs.getString("owner_email");
                r.type = rs.getString("type");
                r.title = rs.getString("title");
                r.status = rs.getString("status");
                r.createdAt = String.valueOf(rs.getTimestamp("created_at"));
                r.expiresAt = String.valueOf(rs.getTimestamp("expires_at"));
                list.add(r);
            }
        }

        return list;
    }


    public boolean deleteListing(int listingId) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM exchange_proposals WHERE target_listing_id=? OR offered_listing_id=?")) {
                    ps.setInt(1, listingId);
                    ps.setInt(2, listingId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM sell_reservations WHERE listing_id=?")) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM listing_images WHERE listing_id=?")) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                int affected;
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM listings WHERE id=?")) {
                    ps.setInt(1, listingId);
                    affected = ps.executeUpdate();
                }

                con.commit();
                return affected > 0;

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public void deleteListingHard(int listingId) throws Exception {
        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try {
                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM messages WHERE listing_id=?")) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM sell_reservations WHERE listing_id=?")) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM exchange_proposals WHERE target_listing_id=? OR offered_listing_id=?")) {
                    ps.setInt(1, listingId);
                    ps.setInt(2, listingId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = con.prepareStatement(
                        "DELETE FROM listings WHERE id=?")) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                con.commit();
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }


    public static class CategoryRow {
        public int id;
        public String name;
    }

    public List<CategoryRow> getAllCategories() throws Exception {
        String sql = "SELECT id, name FROM categories ORDER BY name ASC";
        List<CategoryRow> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CategoryRow c = new CategoryRow();
                c.id = rs.getInt("id");
                c.name = rs.getString("name");
                list.add(c);
            }
        }
        return list;
    }

    public boolean addCategory(String name) throws Exception {
        String sql = "INSERT INTO categories(name) VALUES (?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean deleteCategory(int id) throws Exception {
        String sql = "DELETE FROM categories WHERE id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    public boolean deleteCategorySafe(int id) throws Exception {
        String check = "SELECT 1 FROM listings WHERE category_id=? LIMIT 1";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(check)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return false; 
            }
        }

        return deleteCategory(id);
    }


    public static class CourseCodeRow {
        public int id;
        public String code;
        public String department;
    }

    public List<CourseCodeRow> getAllCourseCodes() throws Exception {
        String sql = "SELECT id, code, department FROM course_codes ORDER BY department ASC, code ASC";
        List<CourseCodeRow> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CourseCodeRow c = new CourseCodeRow();
                c.id = rs.getInt("id");
                c.code = rs.getString("code");
                c.department = rs.getString("department");
                list.add(c);
            }
        }
        return list;
    }

    public boolean addCourseCode(String code, String dept) throws Exception {
        String sql = "INSERT INTO course_codes(code, department) VALUES (?, ?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, dept);
            return ps.executeUpdate() > 0;
        }
    }
   
    public boolean isCourseCodeUsed(int courseCodeId) throws Exception {
        String sql = "SELECT COUNT(*) AS cnt FROM listings WHERE course_code_id = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, courseCodeId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("cnt") > 0;
            }
        }
    }

    public boolean deleteCourseCodeSafe(int id) throws Exception {
        if (isCourseCodeUsed(id)) {
            return false;
        }

        String sql = "DELETE FROM course_codes WHERE id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    public Integer getCategoryIdByName(String name) throws Exception {
        String sql = "SELECT id FROM categories WHERE name = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
                return null;
            }
        }
    }

    public Integer getCourseCodeIdByCode(String code) throws Exception {
        String sql = "SELECT id FROM course_codes WHERE code = ?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
                return null;
            }
        }
    }
    
    public boolean courseCodeExists(String code) throws Exception {
        String sql = "SELECT 1 FROM course_codes WHERE code=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


}

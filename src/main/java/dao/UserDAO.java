package dao;

import model.User;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

	public User findByEmail(String email) throws Exception {
	    String sql = "SELECT id, name, email, password_hash, study_year, role, is_blocked " +
	                 "FROM users WHERE email=?";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, email);

	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next()) return null;

	            User u = new User();
	            u.setId(rs.getInt("id"));
	            u.setName(rs.getString("name"));
	            u.setEmail(rs.getString("email"));
	            u.setPasswordHash(rs.getString("password_hash"));
	            u.setStudyYear(rs.getInt("study_year"));
	            u.setRole(rs.getString("role"));
	            u.setBlocked(rs.getBoolean("is_blocked"));
	            return u;
	        }
	    }
	}

    
    public int create(String name, String email, String passwordHash, int studyYear, String role) throws Exception {
        String sql = "INSERT INTO users (name, email, password_hash, study_year, role) VALUES (?,?,?,?,?)";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setInt(4, studyYear);
            ps.setString(5, role);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        }
    }
    
    public void updateProfile(int userId, String name, int studyYear) throws Exception {

        String sql = "UPDATE users SET name=?, study_year=? WHERE id=?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setInt(2, studyYear);
            ps.setInt(3, userId);

            ps.executeUpdate();
        }
    }


}

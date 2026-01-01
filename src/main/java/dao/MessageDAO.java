package dao;

import model.Message;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public void sendMessage(int senderId, int receiverId, Integer listingId,
                            String subject, String body) throws Exception {

        String sql = "INSERT INTO messages(sender_id, receiver_id, listing_id, subject, body) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);

            if (listingId == null) ps.setNull(3, Types.INTEGER);
            else ps.setInt(3, listingId);

            ps.setString(4, subject);
            ps.setString(5, body);

            ps.executeUpdate();
        }
    }

    public List<Message> getInbox(int userId) throws Exception {
        String sql = "SELECT * FROM messages WHERE receiver_id=? ORDER BY created_at DESC";
        return run(sql, ps -> ps.setInt(1, userId));
    }

    public List<Message> getOutbox(int userId) throws Exception {
        String sql = "SELECT * FROM messages WHERE sender_id=? ORDER BY created_at DESC";
        return run(sql, ps -> ps.setInt(1, userId));
    }

    private interface ParamSetter { void set(PreparedStatement ps) throws Exception; }

    private List<Message> run(String sql, ParamSetter setter) throws Exception {
        List<Message> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (setter != null) setter.set(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Message m = new Message();
                    m.setId(rs.getInt("id"));
                    m.setSenderId(rs.getInt("sender_id"));
                    m.setReceiverId(rs.getInt("receiver_id"));

                    int lid = rs.getInt("listing_id");
                    m.setListingId(rs.wasNull() ? null : lid);

                    m.setSubject(rs.getString("subject"));
                    m.setBody(rs.getString("body"));
                    m.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(m);
                }
            }
        }
        return list;
    }
}

package dao;

import model.ExchangeProposalRow;
import model.Listing;
import model.ListingCondition;
import model.ListingType;
import model.ListingStatus;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ListingDAO {

    public int insertListing(Listing listing) throws Exception {

    	String sql = "INSERT INTO listings " +
    		    "(owner_id, type, title, author, edition, category_id, course_code_id, condition_enum, price, status, expires_at, image_path) " +
    		    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'AVAILABLE', ?, NULL)";


        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        	ps.setInt(1, listing.getOwnerId());
        	ps.setString(2, listing.getType().name());
        	ps.setString(3, listing.getTitle());
        	ps.setString(4, listing.getAuthor());
        	ps.setString(5, listing.getEdition());
        	ps.setInt(6, listing.getCategoryId());
        	ps.setInt(7, listing.getCourseCodeId());

        	ps.setString(8, listing.getCondition().name());

        	if (listing.getType() == ListingType.SELL) ps.setBigDecimal(9, listing.getPrice());
        	else ps.setNull(9, java.sql.Types.DECIMAL);

        	ps.setObject(10, expiresAt);

            ps.executeUpdate();

            try (var rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
            return -1;
        }
    }


    public void updateListingImagePath(int listingId, String imagePath) throws Exception {
        String sql = "UPDATE listings SET image_path=? WHERE id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setInt(2, listingId);
            ps.executeUpdate();
        }
    }


    public void expireOldListings() throws Exception {
        String sql = "UPDATE listings " +
                "SET status='EXPIRED' " +
                "WHERE status IN ('AVAILABLE','RESERVED') AND expires_at < NOW()";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }


    public List<Listing> getAvailableListings() throws Exception {
        expireOldListings();

        String sql = "SELECT id, owner_id, type, title, author, condition_enum, price, image_path " +
                "FROM listings " +
                "WHERE status='AVAILABLE' " +
                "ORDER BY created_at DESC";

        return runListingQuery(sql, null);
    }

    public List<Listing> searchByTitle(String q) throws Exception {
        expireOldListings();

        String sql = "SELECT id, owner_id, type, title, author, condition_enum, price, image_path " +
                "FROM listings " +
                "WHERE status='AVAILABLE' AND title LIKE ? " +
                "ORDER BY created_at DESC";

        return runListingQuery(sql, ps -> ps.setString(1, "%" + q + "%"));
    }


    public List<Listing> searchByType(String q) throws Exception {
        expireOldListings();

        String sql = "SELECT id, owner_id, type, title, author, condition_enum, price, image_path " +
                "FROM listings " +
                "WHERE status='AVAILABLE' AND type = ? " +
                "ORDER BY created_at DESC";

        String type = q.trim().toUpperCase();
        return runListingQuery(sql, ps -> ps.setString(1, type));
    }


    public List<Listing> searchByCondition(String q) throws Exception {
        expireOldListings();

        String sql = "SELECT id, owner_id, type, title, author, condition_enum, price, image_path " +
                "FROM listings " +
                "WHERE status='AVAILABLE' AND condition_enum = ? " +
                "ORDER BY created_at DESC";

        String cond = q.trim().toUpperCase();
        return runListingQuery(sql, ps -> ps.setString(1, cond));
    }


    private interface ParamSetter {
        void set(PreparedStatement ps) throws Exception;
    }

    private List<Listing> runListingQuery(String sql, ParamSetter setter) throws Exception {
        List<Listing> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (setter != null) setter.set(ps);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Listing l = new Listing();
                    l.setId(rs.getInt("id"));
                    l.setOwnerId(rs.getInt("owner_id"));
                    l.setType(ListingType.valueOf(rs.getString("type")));
                    l.setTitle(rs.getString("title"));
                    l.setAuthor(rs.getString("author"));
                    l.setCondition(ListingCondition.valueOf(rs.getString("condition_enum")));
                    l.setPrice(rs.getBigDecimal("price"));
                    l.setImagePath(rs.getString("image_path")); // ✅ important
                    list.add(l);
                }
            }
        }

        return list;
    }


    public boolean reserveSell(int listingId, int buyerId) throws Exception {
        expireOldListings();

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try {
                String lockSql = "SELECT owner_id, type, status FROM listings WHERE id = ? FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                    ps.setInt(1, listingId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        int ownerId = rs.getInt("owner_id");
                        String type = rs.getString("type");
                        String status = rs.getString("status");

                        if (ownerId == buyerId) {
                            con.rollback();
                            throw new IllegalArgumentException("You cannot reserve your own listing.");
                        }

                        if (!"SELL".equals(type) || !"AVAILABLE".equals(status)) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                String insSql = "INSERT INTO sell_reservations(listing_id, buyer_id) VALUES (?, ?)";
                try (PreparedStatement ps = con.prepareStatement(insSql)) {
                    ps.setInt(1, listingId);
                    ps.setInt(2, buyerId);
                    ps.executeUpdate();
                }

                String updSql = "UPDATE listings SET status='RESERVED' WHERE id=?";
                try (PreparedStatement ps = con.prepareStatement(updSql)) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean completeSell(int listingId, int sellerId) throws Exception {
        expireOldListings();

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try {
                String lockSql = "SELECT owner_id, type, status FROM listings WHERE id=? FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                    ps.setInt(1, listingId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        int ownerId = rs.getInt("owner_id");
                        String type = rs.getString("type");
                        String status = rs.getString("status");

                        if (ownerId != sellerId) {
                            con.rollback();
                            return false;
                        }
                        if (!"SELL".equals(type) || !"RESERVED".equals(status)) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                String updSql = "UPDATE listings SET status='SOLD' WHERE id=?";
                try (PreparedStatement ps = con.prepareStatement(updSql)) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean cancelSellReservation(int listingId, int sellerId) throws Exception {
        expireOldListings();

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try {
                String lockSql = "SELECT owner_id, type, status FROM listings WHERE id=? FOR UPDATE";
                try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                    ps.setInt(1, listingId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        int ownerId = rs.getInt("owner_id");
                        String type = rs.getString("type");
                        String status = rs.getString("status");

                        if (ownerId != sellerId) {
                            con.rollback();
                            return false;
                        }
                        if (!"SELL".equals(type) || !"RESERVED".equals(status)) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                String delSql = "DELETE FROM sell_reservations WHERE listing_id=?";
                try (PreparedStatement ps = con.prepareStatement(delSql)) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                String updSql = "UPDATE listings SET status='AVAILABLE' WHERE id=?";
                try (PreparedStatement ps = con.prepareStatement(updSql)) {
                    ps.setInt(1, listingId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }


    public List<Listing> getMyListings(int ownerId) throws Exception {
        expireOldListings();

        String sql = "SELECT id, owner_id, type, title, author, condition_enum, price, status, image_path " +
                "FROM listings " +
                "WHERE owner_id=? AND status <> 'EXPIRED' " +
                "ORDER BY created_at DESC";

        List<Listing> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Listing l = new Listing();
                    l.setId(rs.getInt("id"));
                    l.setOwnerId(rs.getInt("owner_id"));
                    l.setType(ListingType.valueOf(rs.getString("type")));
                    l.setTitle(rs.getString("title"));
                    l.setAuthor(rs.getString("author"));
                    l.setCondition(ListingCondition.valueOf(rs.getString("condition_enum")));
                    l.setPrice(rs.getBigDecimal("price"));
                    l.setStatus(ListingStatus.valueOf(rs.getString("status")));
                    l.setImagePath(rs.getString("image_path"));
                    list.add(l);
                }
            }
        }

        return list;
    }

    public List<Listing> getMyAvailableExchangeListings(int ownerId) throws Exception {
        expireOldListings();

        String sql = "SELECT id, owner_id, type, title, author, condition_enum, price, image_path " +
                "FROM listings " +
                "WHERE status='AVAILABLE' AND type='EXCHANGE' AND owner_id=? " +
                "ORDER BY created_at DESC";

        return runListingQuery(sql, ps -> ps.setInt(1, ownerId));
    }


    public void createExchangeProposal(int targetListingId, int offeredListingId, int proposerId) throws Exception {
        if (targetListingId == offeredListingId) {
            throw new IllegalArgumentException("Target and offered listing cannot be the same.");
        }

        String sql = "INSERT INTO exchange_proposals(target_listing_id, offered_listing_id, proposer_id) " +
                "VALUES (?, ?, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, targetListingId);
            ps.setInt(2, offeredListingId);
            ps.setInt(3, proposerId);
            ps.executeUpdate();
        }
    }


    public List<ExchangeProposalRow> getIncomingExchangeProposals(int ownerId) throws Exception {
        String sql =
                "SELECT ep.id, ep.target_listing_id, ep.offered_listing_id, ep.proposer_id, ep.status, ep.created_at, " +
                "ep.target_confirmed, ep.proposer_confirmed, " +
                "t.title AS target_title, o.title AS offered_title " +
                "FROM exchange_proposals ep " +
                "JOIN listings t ON t.id = ep.target_listing_id " +
                "JOIN listings o ON o.id = ep.offered_listing_id " +
                "WHERE t.owner_id = ? AND ep.status IN ('PENDING','ACCEPTED') " +
                "ORDER BY ep.created_at DESC";

        List<ExchangeProposalRow> rows = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ownerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExchangeProposalRow r = new ExchangeProposalRow();
                    r.setId(rs.getInt("id"));
                    r.setTargetListingId(rs.getInt("target_listing_id"));
                    r.setOfferedListingId(rs.getInt("offered_listing_id"));
                    r.setProposerId(rs.getInt("proposer_id"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setTargetTitle(rs.getString("target_title"));
                    r.setOfferedTitle(rs.getString("offered_title"));
                    r.setTargetConfirmed(rs.getBoolean("target_confirmed"));
                    r.setProposerConfirmed(rs.getBoolean("proposer_confirmed"));
                    rows.add(r);
                }
            }
        }

        return rows;
    }


    public List<ExchangeProposalRow> getSentExchangeProposals(int proposerId) throws Exception {
        String sql =
                "SELECT ep.id, ep.target_listing_id, ep.offered_listing_id, ep.proposer_id, ep.status, ep.created_at, " +
                "ep.target_confirmed, ep.proposer_confirmed, " +
                "t.title AS target_title, o.title AS offered_title " +
                "FROM exchange_proposals ep " +
                "JOIN listings t ON t.id = ep.target_listing_id " +
                "JOIN listings o ON o.id = ep.offered_listing_id " +
                "WHERE ep.proposer_id = ? AND ep.status IN ('PENDING','ACCEPTED') " +
                "ORDER BY ep.created_at DESC";

        List<ExchangeProposalRow> rows = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, proposerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ExchangeProposalRow r = new ExchangeProposalRow();
                    r.setId(rs.getInt("id"));
                    r.setTargetListingId(rs.getInt("target_listing_id"));
                    r.setOfferedListingId(rs.getInt("offered_listing_id"));
                    r.setProposerId(rs.getInt("proposer_id"));
                    r.setStatus(rs.getString("status"));
                    r.setCreatedAt(rs.getTimestamp("created_at"));
                    r.setTargetTitle(rs.getString("target_title"));
                    r.setOfferedTitle(rs.getString("offered_title"));
                    r.setTargetConfirmed(rs.getBoolean("target_confirmed"));
                    r.setProposerConfirmed(rs.getBoolean("proposer_confirmed"));
                    rows.add(r);
                }
            }
        }

        return rows;
    }


    public boolean isOwnerOfListing(int listingId, int ownerId) throws Exception {
        String sql = "SELECT 1 FROM listings WHERE id=? AND owner_id=?";
        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, listingId);
            ps.setInt(2, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }


    public boolean acceptExchangeProposal(int proposalId, int targetOwnerId) throws Exception {
        expireOldListings();

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try {
                String lockSql =
                    "SELECT ep.target_listing_id, ep.offered_listing_id, ep.status, " +
                    "t.owner_id AS target_owner, t.status AS target_status, t.type AS target_type, " +
                    "o.status AS offered_status, o.type AS offered_type " +
                    "FROM exchange_proposals ep " +
                    "JOIN listings t ON t.id = ep.target_listing_id " +
                    "JOIN listings o ON o.id = ep.offered_listing_id " +
                    "WHERE ep.id = ? FOR UPDATE";

                int targetListingId;
                int offeredListingId;

                try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                    ps.setInt(1, proposalId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        String proposalStatus = rs.getString("status");
                        int targetOwner = rs.getInt("target_owner");

                        String targetStatus = rs.getString("target_status");
                        String offeredStatus = rs.getString("offered_status");

                        String targetType = rs.getString("target_type");
                        String offeredType = rs.getString("offered_type");

                        if (targetOwner != targetOwnerId) {
                            con.rollback();
                            return false;
                        }

                        if (!"PENDING".equals(proposalStatus)) {
                            con.rollback();
                            return false;
                        }

                        if (!"EXCHANGE".equals(targetType) || !"EXCHANGE".equals(offeredType)) {
                            con.rollback();
                            return false;
                        }
                        if (!"AVAILABLE".equals(targetStatus) || !"AVAILABLE".equals(offeredStatus)) {
                            con.rollback();
                            return false;
                        }

                        targetListingId = rs.getInt("target_listing_id");
                        offeredListingId = rs.getInt("offered_listing_id");
                    }
                }

                String updProposal =
                    "UPDATE exchange_proposals " +
                    "SET status='ACCEPTED' " +
                    "WHERE id=? AND status='PENDING'";

                try (PreparedStatement ps = con.prepareStatement(updProposal)) {
                    ps.setInt(1, proposalId);
                    int updated = ps.executeUpdate();
                    if (updated == 0) {
                        con.rollback();
                        return false;
                    }
                }

                String reserveListings =
                    "UPDATE listings SET status='RESERVED' " +
                    "WHERE id IN (?, ?) AND status='AVAILABLE'";

                int n;
                try (PreparedStatement ps = con.prepareStatement(reserveListings)) {
                  ps.setInt(1, targetListingId);
                  ps.setInt(2, offeredListingId);
                  n = ps.executeUpdate();
                }
                if (n != 2) {
                  con.rollback();
                  return false;
                }


                String rejectOthers =
                    "UPDATE exchange_proposals " +
                    "SET status='REJECTED' " +
                    "WHERE status='PENDING' AND id<>? " +
                    "AND (target_listing_id IN (?, ?) OR offered_listing_id IN (?, ?))";

                try (PreparedStatement ps = con.prepareStatement(rejectOthers)) {
                    ps.setInt(1, proposalId);
                    ps.setInt(2, targetListingId);
                    ps.setInt(3, offeredListingId);
                    ps.setInt(4, targetListingId);
                    ps.setInt(5, offeredListingId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean rejectExchangeProposal(int proposalId, int targetOwnerId) throws Exception {
        try (Connection con = DBUtil.getConnection()) {

            String sql =
                    "UPDATE exchange_proposals ep " +
                    "JOIN listings t ON t.id = ep.target_listing_id " +
                    "SET ep.status='REJECTED' " +
                    "WHERE ep.id=? AND t.owner_id=? AND ep.status='PENDING'";

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, proposalId);
                ps.setInt(2, targetOwnerId);
                return ps.executeUpdate() > 0;
            }
        }
    }


    public boolean confirmExchangeAsProposer(int proposalId, int proposerId) throws Exception {
        expireOldListings();

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try {
                String lockSql =
                        "SELECT ep.status, ep.proposer_id, ep.target_confirmed, ep.proposer_confirmed, " +
                        "ep.target_listing_id, ep.offered_listing_id, " +
                        "t.status AS target_status, o.status AS offered_status " +
                        "FROM exchange_proposals ep " +
                        "JOIN listings t ON t.id = ep.target_listing_id " +
                        "JOIN listings o ON o.id = ep.offered_listing_id " +
                        "WHERE ep.id=? FOR UPDATE";

                int targetListingId;
                int offeredListingId;
                boolean targetConfirmed;

                try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                    ps.setInt(1, proposalId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        String status = rs.getString("status");
                        int propId = rs.getInt("proposer_id");
                        targetConfirmed = rs.getBoolean("target_confirmed");
                        boolean proposerConfirmed = rs.getBoolean("proposer_confirmed");

                        if (propId != proposerId) {
                            con.rollback();
                            return false;
                        }

                        if (!"ACCEPTED".equals(status)) {
                            con.rollback();
                            return false;
                        }

                        if (proposerConfirmed) {
                            con.commit();
                            return true;
                        }

                        String ts = rs.getString("target_status");
                        String os = rs.getString("offered_status");
                        if (!"RESERVED".equals(ts) || !"RESERVED".equals(os)) {
                            con.rollback();
                            return false;
                        }

                        targetListingId = rs.getInt("target_listing_id");
                        offeredListingId = rs.getInt("offered_listing_id");
                    }
                }

                String upd = "UPDATE exchange_proposals SET proposer_confirmed=1 WHERE id=? AND status='ACCEPTED'";
                try (PreparedStatement ps = con.prepareStatement(upd)) {
                    ps.setInt(1, proposalId);
                    int n = ps.executeUpdate();
                    if (n == 0) {
                        con.rollback();
                        return false;
                    }
                }

                if (targetConfirmed) {
                    String updListings =
                            "UPDATE listings SET status='EXCHANGED' " +
                            "WHERE id IN (?, ?) AND status='RESERVED'";

                    try (PreparedStatement ps = con.prepareStatement(updListings)) {
                        ps.setInt(1, targetListingId);
                        ps.setInt(2, offeredListingId);
                        ps.executeUpdate();
                    }
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public boolean cancelExchangeProposal(int proposalId, int proposerId) throws Exception {
        expireOldListings();

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            try {
                String lockSql =
                        "SELECT ep.status, ep.proposer_id, ep.target_listing_id, ep.offered_listing_id, " +
                        "t.status AS target_status, o.status AS offered_status " +
                        "FROM exchange_proposals ep " +
                        "JOIN listings t ON t.id = ep.target_listing_id " +
                        "JOIN listings o ON o.id = ep.offered_listing_id " +
                        "WHERE ep.id=? FOR UPDATE";

                int targetListingId;
                int offeredListingId;
                String status;

                try (PreparedStatement ps = con.prepareStatement(lockSql)) {
                    ps.setInt(1, proposalId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return false;
                        }

                        status = rs.getString("status");
                        int propId = rs.getInt("proposer_id");

                        if (propId != proposerId) {
                            con.rollback();
                            return false;
                        }

                        if (!"PENDING".equals(status) && !"ACCEPTED".equals(status)) {
                            con.rollback();
                            return false;
                        }

                        targetListingId = rs.getInt("target_listing_id");
                        offeredListingId = rs.getInt("offered_listing_id");

                        String ts = rs.getString("target_status");
                        String os = rs.getString("offered_status");
                        if ("EXCHANGED".equals(ts) || "EXCHANGED".equals(os)) {
                            con.rollback();
                            return false;
                        }
                    }
                }

                String upd = "UPDATE exchange_proposals SET status='CANCELED' WHERE id=? AND proposer_id=?";
                try (PreparedStatement ps = con.prepareStatement(upd)) {
                    ps.setInt(1, proposalId);
                    ps.setInt(2, proposerId);
                    int n = ps.executeUpdate();
                    if (n == 0) {
                        con.rollback();
                        return false;
                    }
                }

                if ("ACCEPTED".equals(status)) {
                    String back =
                            "UPDATE listings SET status='AVAILABLE' " +
                            "WHERE id IN (?, ?) AND status='RESERVED'";

                    try (PreparedStatement ps = con.prepareStatement(back)) {
                        ps.setInt(1, targetListingId);
                        ps.setInt(2, offeredListingId);
                        ps.executeUpdate();
                    }
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
    
    public List<Listing> getMyReservedListings(int buyerId) throws Exception {

        String sql =
            "SELECT l.id, l.owner_id, l.type, l.title, l.author, l.condition_enum, " +
            "l.price, l.status, l.image_path " +
            "FROM sell_reservations sr " +
            "JOIN listings l ON l.id = sr.listing_id " +
            "WHERE sr.buyer_id = ? " +
            "ORDER BY sr.reserved_at DESC";

        List<Listing> list = new ArrayList<>();

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, buyerId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Listing l = new Listing();
                    l.setId(rs.getInt("id"));
                    l.setOwnerId(rs.getInt("owner_id"));
                    l.setType(ListingType.valueOf(rs.getString("type")));
                    l.setTitle(rs.getString("title"));
                    l.setAuthor(rs.getString("author"));
                    l.setCondition(ListingCondition.valueOf(rs.getString("condition_enum")));
                    l.setPrice(rs.getBigDecimal("price"));
                    l.setStatus(model.ListingStatus.valueOf(rs.getString("status")));
                    l.setImagePath(rs.getString("image_path"));
                    list.add(l);
                }
            }
        }
        return list;
    }


 public static class ReservationRow {
     public int reservationId;
     public int listingId;
     public int buyerId;
     public java.sql.Timestamp reservedAt;

     public String title;
     public String author;
     public String condition;
     public java.math.BigDecimal price;
     public String status;
     public String imagePath;
 }

 public List<ReservationRow> getMyReservations(int buyerId) throws Exception {

     String sql =
         "SELECT sr.id AS reservation_id, sr.listing_id, sr.buyer_id, sr.reserved_at, " +
         "l.title, l.author, l.condition_enum, l.price, l.status, l.image_path " +
         "FROM sell_reservations sr " +
         "JOIN listings l ON l.id = sr.listing_id " +
         "WHERE sr.buyer_id = ? " +
         "ORDER BY sr.reserved_at DESC";

     List<ReservationRow> list = new ArrayList<>();

     try (Connection con = DBUtil.getConnection();
          PreparedStatement ps = con.prepareStatement(sql)) {

         ps.setInt(1, buyerId);

         try (ResultSet rs = ps.executeQuery()) {
             while (rs.next()) {
                 ReservationRow r = new ReservationRow();
                 r.reservationId = rs.getInt("reservation_id");
                 r.listingId = rs.getInt("listing_id");
                 r.buyerId = rs.getInt("buyer_id");
                 r.reservedAt = rs.getTimestamp("reserved_at");

                 r.title = rs.getString("title");
                 r.author = rs.getString("author");
                 r.condition = rs.getString("condition_enum");
                 r.price = rs.getBigDecimal("price");
                 r.status = rs.getString("status");
                 r.imagePath = rs.getString("image_path");

                 list.add(r);
             }
         }
     }

     return list;
 }
 public boolean confirmExchangeAsTargetOwner(int proposalId, int targetOwnerId) throws Exception {
	    expireOldListings();

	    try (Connection con = DBUtil.getConnection()) {
	        con.setAutoCommit(false);

	        try {
	            String lockSql =
	                "SELECT ep.status, ep.target_confirmed, ep.proposer_confirmed, " +
	                "ep.target_listing_id, ep.offered_listing_id, " +
	                "t.owner_id AS target_owner, " +
	                "t.status AS target_status, o.status AS offered_status " +
	                "FROM exchange_proposals ep " +
	                "JOIN listings t ON t.id = ep.target_listing_id " +
	                "JOIN listings o ON o.id = ep.offered_listing_id " +
	                "WHERE ep.id=? FOR UPDATE";

	            int targetListingId;
	            int offeredListingId;
	            boolean proposerConfirmed;

	            try (PreparedStatement ps = con.prepareStatement(lockSql)) {
	                ps.setInt(1, proposalId);
	                try (ResultSet rs = ps.executeQuery()) {
	                    if (!rs.next()) {
	                        con.rollback();
	                        return false;
	                    }

	                    String status = rs.getString("status");
	                    boolean targetConfirmed = rs.getBoolean("target_confirmed");
	                    proposerConfirmed = rs.getBoolean("proposer_confirmed");

	                    int targetOwner = rs.getInt("target_owner");
	                    if (targetOwner != targetOwnerId) {
	                        con.rollback();
	                        return false;
	                    }

	                    if (!"ACCEPTED".equals(status)) {
	                        con.rollback();
	                        return false;
	                    }

	                    if (targetConfirmed) {
	                        con.commit();
	                        return true; 
	                    }

	                    String ts = rs.getString("target_status");
	                    String os = rs.getString("offered_status");
	                    if (!"RESERVED".equals(ts) || !"RESERVED".equals(os)) {
	                        con.rollback();
	                        return false;
	                    }

	                    targetListingId = rs.getInt("target_listing_id");
	                    offeredListingId = rs.getInt("offered_listing_id");
	                }
	            }

	            String upd = "UPDATE exchange_proposals SET target_confirmed=1 WHERE id=? AND status='ACCEPTED'";
	            try (PreparedStatement ps = con.prepareStatement(upd)) {
	                ps.setInt(1, proposalId);
	                int n = ps.executeUpdate();
	                if (n == 0) {
	                    con.rollback();
	                    return false;
	                }
	            }

	            if (proposerConfirmed) {
	                String updListings =
	                    "UPDATE listings SET status='EXCHANGED' " +
	                    "WHERE id IN (?, ?) AND status='RESERVED'";

	                try (PreparedStatement ps = con.prepareStatement(updListings)) {
	                    ps.setInt(1, targetListingId);
	                    ps.setInt(2, offeredListingId);
	                    ps.executeUpdate();
	                }
	            }

	            con.commit();
	            return true;

	        } catch (Exception e) {
	            con.rollback();
	            throw e;
	        } finally {
	            con.setAutoCommit(true);
	        }
	    }
	}

 public Listing getListingById(int id) throws Exception {
	    String sql = "SELECT id, owner_id, type, title, author, edition, category_id, course_code_id, " +
	                 "condition_enum, price, status, image_path " +
	                 "FROM listings WHERE id=?";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setInt(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next()) return null;

	            Listing l = new Listing();
	            l.setId(rs.getInt("id"));
	            l.setOwnerId(rs.getInt("owner_id"));
	            l.setType(ListingType.valueOf(rs.getString("type")));
	            l.setTitle(rs.getString("title"));
	            l.setAuthor(rs.getString("author"));
	            l.setEdition(rs.getString("edition"));
	            l.setCategoryId(rs.getInt("category_id"));
	            l.setCourseCodeId(rs.getInt("course_code_id"));
	            l.setCondition(ListingCondition.valueOf(rs.getString("condition_enum")));
	            l.setPrice(rs.getBigDecimal("price"));
	            l.setStatus(ListingStatus.valueOf(rs.getString("status")));
	            l.setImagePath(rs.getString("image_path"));
	            return l;
	        }
	    }
	}

 
 public boolean updateMyAvailableListing(Listing listing, int ownerId) throws Exception {
	    String sql =
	        "UPDATE listings SET title=?, author=?, edition=?, category_id=?, course_code_id=?, " +
	        "condition_enum=?, price=? " +
	        "WHERE id=? AND owner_id=? AND status='AVAILABLE'";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, listing.getTitle());
	        ps.setString(2, listing.getAuthor());
	        ps.setString(3, listing.getEdition());
	        ps.setInt(4, listing.getCategoryId());
	        ps.setInt(5, listing.getCourseCodeId());
	        ps.setString(6, listing.getCondition().name());

	        if (listing.getType() == ListingType.SELL) ps.setBigDecimal(7, listing.getPrice());
	        else ps.setNull(7, java.sql.Types.DECIMAL);

	        ps.setInt(8, listing.getId());
	        ps.setInt(9, ownerId);

	        return ps.executeUpdate() > 0;
	    }
	}

 
 public boolean deleteMyAvailableListing(int listingId, int ownerId) throws Exception {
	    String sql = "DELETE FROM listings WHERE id=? AND owner_id=? AND status='AVAILABLE'";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, listingId);
	        ps.setInt(2, ownerId);
	        return ps.executeUpdate() > 0;
	    }
	}

 public int getOwnerIdForListing(int listingId) throws Exception {
	    String sql = "SELECT owner_id FROM listings WHERE id=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        ps.setInt(1, listingId);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (!rs.next()) return -1;
	            return rs.getInt("owner_id");
	        }
	    }
	}

 

public List<Listing> searchByCourseCode(String q) throws Exception {
  expireOldListings();

  String sql =
      "SELECT l.id, l.owner_id, l.type, l.title, l.author, l.condition_enum, l.price, l.image_path " +
      "FROM listings l " +
      "JOIN course_codes cc ON cc.id = l.course_code_id " +
      "WHERE l.status='AVAILABLE' AND cc.code LIKE ? " +
      "ORDER BY l.created_at DESC";

  return runListingQuery(sql, ps -> ps.setString(1, "%" + q + "%"));
}


public List<Listing> searchByDepartment(String q) throws Exception {
  expireOldListings();

  String sql =
      "SELECT l.id, l.owner_id, l.type, l.title, l.author, l.condition_enum, l.price, l.image_path " +
      "FROM listings l " +
      "JOIN course_codes cc ON cc.id = l.course_code_id " +
      "WHERE l.status='AVAILABLE' AND cc.department LIKE ? " +
      "ORDER BY l.created_at DESC";

  return runListingQuery(sql, ps -> ps.setString(1, "%" + q + "%"));
}



}

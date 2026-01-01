package dao;

import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ListingLookupDAO {

    public int getOwnerIdByListingId(int listingId) throws Exception {
        String sql = "SELECT owner_id FROM listings WHERE id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, listingId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("owner_id");
                return -1;
            }
        }
    }
}

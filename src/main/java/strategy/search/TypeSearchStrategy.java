package strategy.search;

import dao.ListingDAO;
import model.Listing;

import java.util.List;

public class TypeSearchStrategy implements SearchStrategy {
    private final ListingDAO dao;

    public TypeSearchStrategy(ListingDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Listing> search(SearchCriteria c) throws Exception {
        return dao.searchByType(c.getQ());
    }
}

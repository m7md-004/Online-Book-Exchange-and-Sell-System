package strategy.search;

import dao.ListingDAO;
import model.Listing;

import java.util.List;

public class TitleSearchStrategy implements SearchStrategy {
    private final ListingDAO dao;

    public TitleSearchStrategy(ListingDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Listing> search(SearchCriteria c) throws Exception {
        return dao.searchByTitle(c.getQ());
    }
}

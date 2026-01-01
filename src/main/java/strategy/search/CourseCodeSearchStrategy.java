package strategy.search;

import dao.ListingDAO;
import model.Listing;
import java.util.List;

public class CourseCodeSearchStrategy implements SearchStrategy {
    private final ListingDAO dao;

    public CourseCodeSearchStrategy(ListingDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Listing> search(SearchCriteria c) throws Exception {
        return dao.searchByCourseCode(c.getQ());
    }
}

package strategy.search;

import dao.ListingDAO;
import model.Listing;
import java.util.List;

public class DepartmentSearchStrategy implements SearchStrategy {
    private final ListingDAO dao;

    public DepartmentSearchStrategy(ListingDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<Listing> search(SearchCriteria c) throws Exception {
        return dao.searchByDepartment(c.getQ());
    }
}

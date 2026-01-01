package strategy.search;

import dao.ListingDAO;

public class SearchService {

    private final ListingDAO dao;

    public SearchService(ListingDAO dao) {
        this.dao = dao;
    }

    public SearchStrategy pick(String filter) {

        if (filter == null) {
            return new TitleSearchStrategy(dao);
        }

        return switch (filter) {
            case "TITLE"  -> new TitleSearchStrategy(dao);
            case "TYPE"   -> new TypeSearchStrategy(dao);
            case "COND"   -> new ConditionSearchStrategy(dao);
            case "COURSE" -> new CourseCodeSearchStrategy(dao);
            case "DEPT"   -> new DepartmentSearchStrategy(dao);
            default       -> new TitleSearchStrategy(dao);
        };
    }
}

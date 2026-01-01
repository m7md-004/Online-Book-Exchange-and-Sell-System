package strategy.search;

import model.Listing;
import java.util.List;

public interface SearchStrategy {
    List<Listing> search(SearchCriteria c) throws Exception;
}

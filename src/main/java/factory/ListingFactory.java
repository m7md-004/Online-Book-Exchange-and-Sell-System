package factory;

import model.Listing;

public interface ListingFactory {
    Listing create(
            int ownerId,
            String title,
            String author,
            String conditionStr,
            String priceStr
    );
}

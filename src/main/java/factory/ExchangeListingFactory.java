package factory;

import model.Listing;
import model.ListingCondition;
import model.ListingType;

public class ExchangeListingFactory implements ListingFactory {

    @Override
    public Listing create(
            int ownerId,
            String title,
            String author,
            String conditionStr,
            String priceStr
    ) {
        ListingCondition condition = ListingCondition.valueOf(conditionStr);

        Listing l = Listing.createCommon(ownerId, title, author, condition);
        l.setType(ListingType.EXCHANGE);
        l.setPrice(null);

        return l;
    }
}

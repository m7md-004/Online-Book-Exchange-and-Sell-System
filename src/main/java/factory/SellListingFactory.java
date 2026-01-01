package factory;

import model.Listing;
import model.ListingCondition;
import model.ListingType;

import java.math.BigDecimal;

public class SellListingFactory implements ListingFactory {

    @Override
    public Listing create(
            int ownerId,
            String title,
            String author,
            String conditionStr,
            String priceStr
    ) {
        if (priceStr == null || priceStr.isBlank()) {
            throw new IllegalArgumentException("Price is required for SELL listing.");
        }

        ListingCondition condition = ListingCondition.valueOf(conditionStr);

        Listing l = Listing.createCommon(ownerId, title, author, condition);
        l.setType(ListingType.SELL);
        l.setPrice(new BigDecimal(priceStr));

        return l;
    }
}

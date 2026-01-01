package factory;

public class ListingFactoryProvider {

    public static ListingFactory getFactory(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Listing type is required.");
        }

        if ("SELL".equalsIgnoreCase(type)) {
            return new SellListingFactory();
        }

        if ("EXCHANGE".equalsIgnoreCase(type)) {
            return new ExchangeListingFactory();
        }

        throw new IllegalArgumentException("Invalid listing type: " + type);
    }
}

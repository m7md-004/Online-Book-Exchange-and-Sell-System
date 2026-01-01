package model;

import java.math.BigDecimal;

public class Listing {

    private int id;
    private int ownerId;

    private ListingType type;
    private String title;
    private String author;

    private int categoryId;
    private int courseCodeId;
    private String edition;

    private ListingCondition condition;
    private BigDecimal price;

    private ListingStatus status;
    private String imagePath;

    public static Listing createCommon(
            int ownerId,
            String title,
            String author,
            ListingCondition condition
    ) {
        Listing l = new Listing();
        l.ownerId = ownerId;
        l.title = title;
        l.author = author;
        l.condition = condition;
        return l;
    }


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOwnerId() { return ownerId; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }

    public ListingType getType() { return type; }
    public void setType(ListingType type) { this.type = type; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getCourseCodeId() { return courseCodeId; }
    public void setCourseCodeId(int courseCodeId) { this.courseCodeId = courseCodeId; }

    public ListingCondition getCondition() { return condition; }
    public void setCondition(ListingCondition condition) { this.condition = condition; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus status) { this.status = status; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}

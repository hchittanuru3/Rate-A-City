package cs4400gui;

/**
 * DAVE THE KNAVE
 */
public class PendingAttractionView {

    private final String name;
    private final String city;
    private final String location;
    private final String category;
    private final String descr;
    private final String hours;
    private final String contact;
    private final String email;
    private final String rating;
    private final String comment;
    private final int id;

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public String getDescr() {
        return descr;
    }

    public String getHours() {
        return hours;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public int getId() {
        return id;
    }

    public PendingAttractionView(String name, String city, String location, String category, String descr, String hours, String contact, String email, String rating, String comment, int id) {
        this.name = name;
        this.city = city;
        this.location = location;
        this.category = category;
        this.descr = descr;
        this.hours = hours;
        this.contact = contact;
        this.email = email;
        this.rating = rating;
        this.comment = comment;
        this.id = id;
    }


}

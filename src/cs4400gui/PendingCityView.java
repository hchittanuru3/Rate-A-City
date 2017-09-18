package cs4400gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by hemanthc98 on 7/21/17.
 */
public class PendingCityView {
    private final StringProperty name;
    private final StringProperty country;
    private final StringProperty email;
    private final StringProperty rating;
    private final StringProperty comment;
    private final int city_id;

    public PendingCityView(String name, String country, String email, String rating, String comment, int city_id) {
        this.name = new SimpleStringProperty(name);
        this.country = new SimpleStringProperty(country);
        this.email = new SimpleStringProperty(email);
        this.rating = new SimpleStringProperty(rating);
        this.comment = new SimpleStringProperty(comment);
        this.city_id = city_id;
    }

    public String getName() { return name.get(); }
    public String getCountry() { return country.get(); }
    public String getEmail() { return email.get(); }
    public String getRating() { return rating.get(); }
    public String getComment() { return comment.get(); }
    public int getCity_id() {
        return city_id;
    }
}

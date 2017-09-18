package cs4400gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by dayynn on 7/24/17.
 */
public class CityReviewRow {
    private final StringProperty username;
    private final StringProperty rating;
    private final StringProperty comment;

    public CityReviewRow(String username, String rating, String comment){
        this.username = new SimpleStringProperty(username);
        this.rating = new SimpleStringProperty(rating);
        this.comment = new SimpleStringProperty(comment);
    }

    public String getUsername() {
        return username.get();
    }
    public String getRating() {
        return rating.get();
    }
    public String getComment() {
        return comment.get();
    }

}

package cs4400gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by hemanthc98 on 7/24/17.
 */
public class AttractionReviewRow {

    private final StringProperty email;
    private final StringProperty rating;
    private final StringProperty comment;

    public AttractionReviewRow(String email, String rating, String comment) {
        this.email = new SimpleStringProperty(email);
        this.rating = new SimpleStringProperty(rating);
        this.comment = new SimpleStringProperty(comment);
    }

    public String getEmail() {
        return email.get();
    }

    public String getRating() {
        return rating.get();
    }

    public String getComment() {
        return comment.get();
    }
}

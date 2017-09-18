package cs4400gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by dhuynh38 on 7/15/17. This class represents a view of a review. These objects allow for temporary
 * storage of data obtained from queries for convenience
 */
public class ReviewView {
    private final StringProperty entity;
    private final StringProperty rating;
    private final StringProperty comment;
    private final StringProperty id;

    public ReviewView(String id, String entity, String rating, String comment) {
        this.id = new SimpleStringProperty(id);
        this.entity = new SimpleStringProperty(entity);
        this.rating = new SimpleStringProperty(rating);
        this.comment = new SimpleStringProperty(comment);
    }

    public String getId() { return id.get(); }
    public String getEntity() {
        return entity.get();
    }
    public String getRating() {
        return rating.get();
    }
    public String getComment() { return comment.get(); }

}

package cs4400gui;

import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by dhuynh38 on 7/15/17. This class represents a view of an attraction. These objects allow for temporary
 * storage of data obtained from queries for convenience
 */
public class AttractionView {
    private final StringProperty name;
    private final StringProperty category;
    private final StringProperty location;
    private final StringProperty avgRating;
    private final StringProperty numRating;
    private final StringProperty hyperlink;

    public AttractionView(String name, String categoryList, String location, String avgRating, String numRating, String hyperlink){
        this.category = new SimpleStringProperty(categoryList);
        this.name = new SimpleStringProperty(name);
        this.location = new SimpleStringProperty(location);
        this.avgRating = new SimpleStringProperty(avgRating);
        this.numRating = new SimpleStringProperty(numRating);
        this.hyperlink = new SimpleStringProperty(hyperlink);
    }

    public String getName() {
        return name.get();
    }
    public String getCategory() {
        return category.get();
    }
    public String getLocation() {
        return location.get();
    }
    public String getAvgRating() {
        return avgRating.get();
    }
    public String getNumRating() {
        return numRating.get();
    }
    public String getHyperlink() {return hyperlink.get();}

}

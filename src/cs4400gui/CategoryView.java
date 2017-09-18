package cs4400gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by hemanthc98 on 7/23/17.
 */
public class CategoryView {
    private final StringProperty name;
    private final StringProperty numAttr;

    public CategoryView(String name, String numAttr) {
        this.name = new SimpleStringProperty(name);
        this.numAttr = new SimpleStringProperty(numAttr);
    }

    public String getName() {
        return name.get();
    }

    public String getNumAttr() {
        return numAttr.get();
    }
}

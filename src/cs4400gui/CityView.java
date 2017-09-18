package cs4400gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by dhuynh38 on 7/15/17. This class represents a view of a city. These objects allow for temporary
 * storage of data obtained from queries for convenience
 */
public class CityView {
    private final StringProperty name;
    private final StringProperty avgRate;
    private final StringProperty numRate;
    private final StringProperty numAttr;
    private final StringProperty hyperlink;

    public CityView(String name, String avgRate, String numRate, String numAttr, String hyperlink) {
        this.name = new SimpleStringProperty(name);
        this.avgRate = new SimpleStringProperty(avgRate);
        this.numRate = new SimpleStringProperty(numRate);
        this.numAttr = new SimpleStringProperty(numAttr);
        this.hyperlink = new SimpleStringProperty(hyperlink);
    }

    public String getName() { return name.get(); }
    public String getAvgRate() { return avgRate.get(); }
    public String getHyperlink() { return hyperlink.get(); }
    public String getNumRate() { return numRate.get(); }
    public String getNumAttr() { return numAttr.get(); }

}

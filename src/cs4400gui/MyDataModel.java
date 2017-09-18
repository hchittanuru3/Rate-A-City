package cs4400gui;

import javafx.beans.property.SimpleStringProperty;

public class MyDataModel {
    private final SimpleStringProperty ID;
    private final SimpleStringProperty AVGRATING;
    private final SimpleStringProperty NUMRATING;

    public MyDataModel(String ID, String AVGRATING, String NUMRATING) {
        this.ID = new SimpleStringProperty(ID);
        this.AVGRATING = new SimpleStringProperty(AVGRATING);
        this.NUMRATING = new SimpleStringProperty(NUMRATING);
    }

    public String getID() {
        return ID.get();
    }
    public void setID(String id) {
        ID.set(id);
    }

    public String getAVGRATING() {
        return AVGRATING.get();
    }
    public void setAVGRATING(String fName) {
        AVGRATING.set(fName);
    }

    public String getNUMRATING() {
        return NUMRATING.get();
    }
    public void setNUMRATING(String fName) {
        NUMRATING.set(fName);
    }

}
package cs4400gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * DAVE THE KNAVE
 */
public class PendingAttractionsController implements Initializable {

    @FXML
    private ChoiceBox dropSort;
    @FXML
    private TableView<PendingAttractionView> attracList;
    @FXML
    private TableColumn<PendingAttractionView, String> name;
    @FXML
    private TableColumn<PendingAttractionView, String> city;
    @FXML
    private TableColumn<PendingAttractionView, String> location;
    @FXML
    private TableColumn<PendingAttractionView, String> category;
    @FXML
    private TableColumn<PendingAttractionView, String> descr;
    @FXML
    private TableColumn<PendingAttractionView, String> hours;
    @FXML
    private TableColumn<PendingAttractionView, String> contact;
    @FXML
    private TableColumn<PendingAttractionView, String> email;
    @FXML
    private TableColumn<PendingAttractionView, String> rating;
    @FXML
    private TableColumn<PendingAttractionView, String> comment;
    private static ObservableList<String> sortOptions;
    private static ObservableList<PendingAttractionView> list;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list = FXCollections.observableArrayList();
        sortOptions = FXCollections.observableArrayList("NAME ASC", "NAME DESC", "CITY ASC", "CITY DESC", "LOCATION ASC", "LOCATION DESC", "DESCRIPTION ASC", "DESCRIPTION DESC", "HOURS ASC", "HOURS DESC", "CONTACT ASC", "CONTACT DESC", "EMAIL ASC", "EMAIL DESC", "RATING ASC", "RATING DESC");
        dropSort.setItems(sortOptions);
        dropSort.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //reSort("newValue");)
                if (oldValue == (null) || (newValue == null)) {
                    return;
                }
                if (newValue.contains("NAME")) {
                    if (newValue.contains("ASC")) {
                        reSort("name", "ASC");
                    } else {
                        reSort("name", "DESC");
                    }
                } else if (newValue.contains("CITY")) {
                    if (newValue.contains("ASC")) {
                        reSort("city", "ASC");
                    } else {
                        reSort("city", "DESC");
                    }
                } else if (newValue.contains("LOCATION")) {
                    if (newValue.contains("ASC")) {
                        reSort("location", "ASC");
                    } else {
                        reSort("location", "DESC");
                    }
                } else if (newValue.contains("DESCRIPTION")) {
                    if (newValue.contains("ASC")) {
                        reSort("descr", "ASC");
                    } else {
                        reSort("descr", "DESC");
                    }
                } else if (newValue.contains("HOURS")) {
                    if (newValue.contains("ASC")) {
                        reSort("hours", "ASC");
                    } else {
                        reSort("hours", "DESC");
                    }
                } else if (newValue.contains("CONTACT")) {
                    if (newValue.contains("ASC")) {
                        reSort("contact", "ASC");
                    } else {
                        reSort("contact", "DESC");
                    }
                } else if (newValue.contains("EMAIL")) {
                    if (newValue.contains("ASC")) {
                        reSort("email", "ASC");
                    } else {
                        reSort("email", "DESC");
                    }
                } else if (newValue.contains("RATING")) {
                    if (newValue.contains("ASC")) {
                        reSort("rating", "ASC");
                    } else {
                        reSort("rating", "DESC");
                    }
                }

            }
        });
        dropSort.getSelectionModel().selectFirst();

        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        city.setCellValueFactory(new PropertyValueFactory<>("city"));
        location.setCellValueFactory(new PropertyValueFactory<>("location"));
        category.setCellValueFactory(new PropertyValueFactory<>("category"));
        descr.setCellValueFactory(new PropertyValueFactory<>("descr"));
        hours.setCellValueFactory(new PropertyValueFactory<>("hours"));
        contact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        email.setCellValueFactory(new PropertyValueFactory<>("email"));
        rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        comment.setCellValueFactory(new PropertyValueFactory<>("comment"));


        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            statement.executeUpdate("CREATE OR REPLACE VIEW one AS SELECT street_address as location, Attraction.attraction_id, Attraction.name as name, group_concat(c_Name) as category, City.name as city, description as descr, submitter_email as email, group_concat(info) as contact, group_concat(hours) as hours " +
                    "FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id JOIN City on Attraction.city_id=City.city_id LEFT JOIN Hours_Of_Operation ON Attraction.attraction_id=Hours_Of_Operation.attraction_id LEFT JOIN Contact_Info ON Attraction.attraction_id=Contact_Info.attraction_id " +
                    "WHERE is_Pending=1 " +
                    "GROUP BY attraction_id;");
            statement.executeUpdate("CREATE OR REPLACE VIEW two AS SELECT group_concat(rating) as rating, group_concat(comment) as comment, entity_id " +
                    "FROM Review " +
                    "GROUP BY entity_id;");
            ResultSet tuple = statement.executeQuery("SELECT * FROM one JOIN two ON one.attraction_id=two.entity_id;");


            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new PendingAttractionView(tuple.getString("name"),tuple.getString("city"), tuple.getString("location"),
                        tuple.getString("category"), tuple.getString("descr"), tuple.getString("hours"), tuple.getString("contact"),
                        tuple.getString("email"),tuple.getString("rating"), tuple.getString("comment"), tuple.getInt("attraction_id") ));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }




        attracList.setItems(list);

        /*
        CREATE OR REPLACE VIEW one AS SELECT Attraction.attraction_id, Attraction.name, group_concat(c_Name) as Categories, City.name as city, description,  submitter_email, group_concat(info) as contact_info, group_concat(hours) as hours_of_operation
FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id JOIN City on Attraction.city_id=City.city_id LEFT JOIN Hours_of_Operation ON Attraction.attraction_id=Hours_Of_Operation.attraction_id LEFT JOIN Contact_Info ON Attraction.attraction_id=Contact_Info.attraction_id
WHERE is_Pending=1
GROUP BY attraction_id;

CREATE OR REPLACE VIEW two AS SELECT group_concat(rating) as rating, group_concat(comment) as comment, entity_id
FROM Review
GROUP BY entity_id;

SELECT * FROM one JOIN two ON one.attraction_id=two.entity_id;




         */






    }
    public void reSort(String option, String direction) {
        list.clear();
        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            statement.executeUpdate("CREATE OR REPLACE VIEW one AS SELECT street_address as location, Attraction.attraction_id, Attraction.name as name, group_concat(c_Name) as category, City.name as city, description as descr, submitter_email as email, group_concat(info) as contact, group_concat(hours) as hours " +
                    "FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id JOIN City on Attraction.city_id=City.city_id LEFT JOIN Hours_Of_Operation ON Attraction.attraction_id=Hours_Of_Operation.attraction_id LEFT JOIN Contact_Info ON Attraction.attraction_id=Contact_Info.attraction_id " +
                    "WHERE is_Pending=1 " +
                    "GROUP BY attraction_id;");
            statement.executeUpdate("CREATE OR REPLACE VIEW two AS SELECT group_concat(rating) as rating, group_concat(comment) as comment, entity_id " +
                    "FROM Review " +
                    "GROUP BY entity_id;");
            ResultSet tuple = statement.executeQuery("SELECT * FROM one JOIN two ON one.attraction_id=two.entity_id ORDER BY " + option + " " + direction);


            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new PendingAttractionView(tuple.getString("name"),tuple.getString("city"), tuple.getString("location"),
                        tuple.getString("category"), tuple.getString("descr"), tuple.getString("hours"), tuple.getString("contact"),
                        tuple.getString("email"),tuple.getString("rating"), tuple.getString("comment"), tuple.getInt("attraction_id") ));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }




        attracList.setItems(list);

    }
    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }

    @FXML protected void approveSelected(ActionEvent event) {
        PendingAttractionView row = attracList.getSelectionModel().getSelectedItem();
        Alert approvealert = new Alert(Alert.AlertType.CONFIRMATION, "Approve selected attraction?");
        Optional<ButtonType> result = approvealert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Statement statement = DatabaseDriver.getStmnt();
                statement.executeUpdate("UPDATE Reviewable_Entity SET is_Pending=false WHERE entity_id=" + row.getId());
                initialize(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML protected void deleteSelected(ActionEvent event) {
        PendingAttractionView row = attracList.getSelectionModel().getSelectedItem();
        Alert approvealert = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected attraction?");
        Optional<ButtonType> result = approvealert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Statement statement = DatabaseDriver.getStmnt();
                statement.executeUpdate("DELETE FROM Attraction WHERE attraction_id=" + row.getId());
                initialize(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML protected void newAttraction(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "NewAttraction.fxml");
    }

}

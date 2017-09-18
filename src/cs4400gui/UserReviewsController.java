package cs4400gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Created by dhuynh38 on 7/15/17.
 */
public class UserReviewsController implements Initializable {
    @FXML private TableView<ReviewView> reviewsList;
    @FXML private TableColumn<ReviewView, String> entity;
    @FXML private TableColumn<ReviewView, String> rating;
    @FXML private TableColumn<ReviewView, String> comment;
    @FXML private ChoiceBox<String> dropSort;
    @FXML private Label email;
    private static ObservableList<String> sortOptions;
    private static ObservableList<ReviewView> list;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //set text on top

        sortOptions = FXCollections.observableArrayList("LOCATION ASC", "LOCATION DESC", "RATING ASC", "RATING DESC");
        dropSort.setItems(sortOptions);
        dropSort.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == (null)) {
                    return;
                }
                if (newValue.contains("RATING")) {
                    if (newValue.contains("ASC")) {
                        reSort("rating", "ASC");
                    } else {
                        reSort("rating", "DESC");
                    }
                } else if (newValue.contains("LOCATION")) {
                    if (newValue.contains("ASC")) {
                        reSort("name", "ASC");
                    } else {
                        reSort("name", "DESC");
                    }
                }
            }
        });


        dropSort.getSelectionModel().selectFirst();
        email.setText("" + SessionInfo.getInstance().getEmail() + "'s Reviews");
        list = FXCollections.observableArrayList();
        try {
            //make more statements as one statment can only be used to query at a time. This allows for
            //many queries to happen at the same time
            Statement statement = DatabaseDriver.getConnection().createStatement();

            //gets the name of entity, comment, and rating of all reviews
            ResultSet tuple = statement.executeQuery("SELECT name, comment, rating, Review.entity_id "
                    + "FROM (Review JOIN (SELECT city_id, name FROM City UNION "
                    + "SELECT attraction_id, name FROM Attraction) AS entity ON Review.entity_id=entity.city_id) "
                    + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                    + "ORDER BY name ASC");

            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                //adds it to the list to be displayed
                list.add(new ReviewView(tuple.getString("Review.entity_id"), tuple.getString("name"), tuple.getString("rating"),
                        tuple.getString("comment")));
            }

            //link columns to attributes
            entity.setCellValueFactory(new PropertyValueFactory<>("entity"));
            comment.setCellValueFactory(new PropertyValueFactory<>("comment"));
            rating.setCellValueFactory(new PropertyValueFactory<>("rating"));

            //set table view to array list and displays everything in the array
            reviewsList.setItems(null);
            reviewsList.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //check for double clicks on any row
        reviewsList.setRowFactory(a -> {
            TableRow<ReviewView> tuple = new TableRow<>();
            tuple.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !tuple.isEmpty()) {
                    SessionInfo.getInstance().setStorage(tuple.getItem().getId());
                    SceneSwitcher.getInstance().switchScene(e, "UpdateReview.fxml");
                }
            });
            return tuple;
        });
    }

    @FXML
    protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }

    private void reSort(String option, String direction) {
        list.clear();
        try {
            //make more statements as one statment can only be used to query at a time. This allows for
            //many queries to happen at the same time
            Statement statement = DatabaseDriver.getConnection().createStatement();

            //gets the name of entity, comment, and rating of all reviews
            ResultSet tuple = statement.executeQuery("SELECT name, comment, rating, Review.entity_id "
                    + "FROM (Review JOIN (SELECT city_id, name FROM City UNION "
                    + "SELECT attraction_id, name FROM Attraction) AS entity ON Review.entity_id=entity.city_id) "
                    + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                    + "ORDER BY " + option + " " + direction);

            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                //adds it to the list to be displayed
                list.add(new ReviewView(tuple.getString("Review.entity_id"), tuple.getString("name"), tuple.getString("rating"),
                        tuple.getString("comment")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        reviewsList.setItems(list);
    }
}

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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by hemanthc98 on 7/23/17.
 */
public class AttractionReviewsController implements Initializable {
    @FXML private Label title;
    @FXML private TableView<AttractionReviewRow> revList;
    @FXML private TableColumn<AttractionReviewRow, String> email;
    @FXML private TableColumn<AttractionReviewRow, String> rating;
    @FXML private TableColumn<AttractionReviewRow, String> comment;
    private static ObservableList<AttractionReviewRow> list = FXCollections.observableArrayList();
    @FXML private ChoiceBox<String> dropSort;
    private static ObservableList<String> sortOptions;
    @FXML private Button del;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortOptions = FXCollections.observableArrayList("USERNAME ASC", "USERNAME DESC", "RATING ASC", "RATING DESC", "COMMENT ASC", "COMMENT DESC");
        dropSort.setItems(sortOptions);
        dropSort.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == (null) || newValue == (null)) {
                    list.clear();
                    return;
                }
                if (newValue.contains("USERNAME")) {
                    if (newValue.contains("ASC")) {
                        reSort("author_email", "ASC");
                    } else {
                        reSort("author_email", "DESC");
                    }
                } else if (newValue.contains("RATING")) {
                    if (newValue.contains("ASC")) {
                        reSort("rating", "ASC");
                    } else {
                        reSort("rating", "DESC");
                    }
                } else if (newValue.contains("COMMENT")) {
                    if (newValue.contains("ASC")) {
                        reSort("Review.comment", "ASC");
                    } else {
                        reSort("Review.comment", "DESC");
                    }
                }
            }
        });
        dropSort.getSelectionModel().selectFirst();
        try {
            Statement statement = DatabaseDriver.getStmnt();
            ResultSet set = statement.executeQuery("SELECT is_manager FROM App_User WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
            set.next();
            if(set.getBoolean("is_manager")) {
                del.setVisible(true);
            }
            ResultSet tuple = statement.executeQuery("SELECT name FROM Attraction WHERE attraction_id='"
                    + SessionInfo.getInstance().getAttractionIdStorage() + "'");
            tuple.next();
            title.setText(tuple.getString("name") + " Reviews");
            ResultSet data = statement.executeQuery("SELECT author_email, rating, Review.comment "
                    + "FROM Attraction JOIN Review ON attraction_id=entity_id "
                    + "WHERE attraction_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'"
                    + "ORDER by author_email ASC");
            while (data.next()) {
                list.add(new AttractionReviewRow(data.getString("author_email"), data.getString("rating"), data.getString("Review.comment")));
            }
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
            rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
            comment.setCellValueFactory(new PropertyValueFactory<>("comment"));
            revList.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void review(ActionEvent event) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            ResultSet tuple = statement.executeQuery("SELECT comment, rating, attraction_id "
                    + "FROM Review JOIN Attraction ON entity_id=attraction_id "
                    + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                    + "AND entity_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'");
            if (tuple.next()) {
                SceneSwitcher.getInstance().switchScene(event, "UpdateReview.fxml");
            } else {
                SceneSwitcher.getInstance().switchScene(event, "NewReview.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }

    private void reSort(String order, String direction) {
        list.clear();
        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            ResultSet tuple = statement.executeQuery("SELECT author_email, rating, Review.comment FROM Review JOIN Attraction ON entity_id=attraction_id WHERE attraction_id=" + SessionInfo.getInstance().getAttractionIdStorage() + " ORDER BY "+ order + " " + direction);


            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new AttractionReviewRow(tuple.getString("author_email"), tuple.getString("rating"),
                        tuple.getString("Review.comment")));
            }
            revList.setItems(list);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void delete(ActionEvent event) {
        Statement statement = DatabaseDriver.getStmnt();
        AttractionReviewRow row = revList.getSelectionModel().getSelectedItem();
        String mail = row.getEmail();
        Alert delete_alert = new Alert(Alert.AlertType.WARNING,"This will delete selected review, are you sure?");
        Optional<ButtonType> result = delete_alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                statement.executeUpdate("DELETE FROM Review WHERE author_email='" + mail
                + "'AND entity_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'");
                //initialize(null, null);
                list.clear();
                reSort("author_email", "ASC");
            } catch (Exception d) {
                d.printStackTrace();
            }
        }
}}

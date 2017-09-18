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
 * Created by dayynn on 7/23/17.
 */
public class CityReviewsController implements Initializable {

    @FXML private Label title;
    @FXML private TableView<CityReviewRow> reviewtable;
    @FXML private TableColumn<CityReviewRow, String> username;
    @FXML private TableColumn<CityReviewRow, String> rating;
    @FXML private TableColumn<CityReviewRow, String> comment;
    @FXML private ChoiceBox sortbox;
    private static ObservableList<CityReviewRow> list;
    private static ObservableList<String> sortOptions;
    @FXML private Button deleteButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list = FXCollections.observableArrayList();
        sortOptions = FXCollections.observableArrayList("USERNAME ASC", "USERNAME DESC", "RATING ASC", "RATING DESC", "COMMENT ASC", "COMMENT DESC");
        sortbox.setItems(sortOptions);
        sortbox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == (null) || newValue == null) {
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
                        reSort("comment", "ASC");
                    } else {
                        reSort("comment", "DESC");
                    }
                }
            }
        });
        sortbox.getSelectionModel().selectFirst();
        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT is_manager FROM App_User WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
            resultSet.next();
            if (resultSet.getBoolean("is_manager")) {
                deleteButton.setVisible(true);
            }
            ResultSet tuple = statement.executeQuery("SELECT author_email, comment, rating FROM Review WHERE entity_id=" + SessionInfo.getInstance().getCityIdStorage());

            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new CityReviewRow(tuple.getString("author_email"), tuple.getString("rating"),
                        tuple.getString("comment")));
            }
            tuple = statement.executeQuery("SELECT name FROM City WHERE city_id=" + SessionInfo.getInstance().getCityIdStorage());
            tuple.next();
            title.setText(tuple.getString("name") + " Reviews");
            //link columns to attributes
            username.setCellValueFactory(new PropertyValueFactory<>("username"));
            rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
            comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

            //set table to array list
            reviewtable.setItems(list);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }

    @FXML protected void review(ActionEvent event) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            ResultSet tuple = statement.executeQuery("SELECT city_id "
                    + "FROM Review JOIN City ON entity_id=city_id "
                    + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                    + "AND entity_id='" + SessionInfo.getInstance().getCityIdStorage() + "'");
            if (tuple.next()) {
                SceneSwitcher.getInstance().switchScene(event, "UpdateReview.fxml");
            } else {
                SceneSwitcher.getInstance().switchScene(event, "NewReview.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reSort(String order, String direction) {
        list.clear();
        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            ResultSet tuple = statement.executeQuery("SELECT author_email, rating, comment FROM Review WHERE entity_id=" + SessionInfo.getInstance().getCityIdStorage() + " ORDER BY "+ order + " " + direction);


            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new CityReviewRow(tuple.getString("author_email"), tuple.getString("rating"),
                        tuple.getString("comment")));
            }
            reviewtable.setItems(list);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void deleteReview(ActionEvent event) {
        CityReviewRow row = reviewtable.getSelectionModel().getSelectedItem();
        try {
            Statement statement = DatabaseDriver.getStmnt();
            Alert alert = new Alert(Alert.AlertType.WARNING, "Delete selected Review?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                statement.executeUpdate("DELETE FROM Review WHERE entity_id=" + SessionInfo.getInstance().getCityIdStorage() + " AND author_email='" + row.getUsername() + "'");
                initialize(null, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

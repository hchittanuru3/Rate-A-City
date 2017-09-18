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
 * Created by hemanthc98 on 7/21/17.
 */
public class PendingCitiesController implements Initializable {

    @FXML private TableView<PendingCityView> pendingCitiesList;
    @FXML private TableColumn<PendingCityView, String> pendingCities;
    @FXML private TableColumn<PendingCityView, String> rating;
    @FXML private TableColumn<PendingCityView, String> email;
    @FXML private TableColumn<PendingCityView, String> comment;
    @FXML private TableColumn<PendingCityView, String> country;
    @FXML private ChoiceBox<String> dropSort;
    private static ObservableList<PendingCityView> list;
    private static ObservableList<String> sortOptions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list = FXCollections.observableArrayList();
        sortOptions = FXCollections.observableArrayList("NAME ASC", "NAME DESC", "RATING ASC", "RATING DESC", "EMAIL ASC", "EMAIL DESC", "LOCATION ASC", "LOCATION DESC");
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
                } else if (newValue.contains("NAME")) {
                    if (newValue.contains("ASC")) {
                        reSort("name", "ASC");
                    } else {
                        reSort("name", "DESC");
                    }
                } else if (newValue.contains("EMAIL")) {
                    if (newValue.contains("ASC")) {
                        reSort("email", "ASC");
                    } else {
                        reSort("email", "DESC");
                    }
                } else if (newValue.contains("LOCATION")) {
                    if (newValue.contains("ASC")) {
                        reSort("country", "ASC");
                    } else {
                        reSort("country", "DESC");
                    }
                }
            }
        });
        dropSort.getSelectionModel().selectFirst();
        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            statement.executeUpdate("CREATE OR REPLACE VIEW pendingCities AS SELECT name, country, submitter_email as email, rating, comment, city_id " +
                    "FROM City JOIN Review ON city_id=Review.entity_id JOIN Reviewable_Entity ON city_id=Reviewable_Entity.entity_id " +
                    "WHERE is_Pending=1;");
            ResultSet tuple = statement.executeQuery("SELECT DISTINCT * FROM pendingCities ORDER BY name ASC");
            while (tuple.next()) {

                list.add(new PendingCityView(tuple.getString("name"), tuple.getString("country"),
                        tuple.getString("email"), tuple.getString("rating"), tuple.getString("comment"), tuple.getInt("city_id")));
            }
            pendingCities.setCellValueFactory(new PropertyValueFactory<>("name"));
            country.setCellValueFactory(new PropertyValueFactory<>("country"));
            email.setCellValueFactory(new PropertyValueFactory<>("email"));
            rating.setCellValueFactory(new PropertyValueFactory<>("rating"));
            comment.setCellValueFactory(new PropertyValueFactory<>("comment"));

            pendingCitiesList.setItems(null);
            pendingCitiesList.setItems(list);
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
            statement.executeUpdate("CREATE OR REPLACE VIEW pendingCities AS SELECT name, country, submitter_email as email, rating, comment, city_id " +
                    "FROM City JOIN Review ON city_id=Review.entity_id JOIN Reviewable_Entity ON city_id=Reviewable_Entity.entity_id " +
                    "WHERE is_Pending=1; ");
            ResultSet tuple = statement.executeQuery("SELECT DISTINCT * FROM pendingCities ORDER BY " + order + " " + direction);
            while (tuple.next()) {

                list.add(new PendingCityView(tuple.getString("name"), tuple.getString("country"),
                        tuple.getString("email"), tuple.getString("rating"), tuple.getString("comment"), tuple.getInt("city_id")));
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void delete(ActionEvent event) {
        PendingCityView tuple = pendingCitiesList.getSelectionModel().getSelectedItem();
        Alert deletealert = new Alert(Alert.AlertType.CONFIRMATION, "Delete selected city?");
        Optional<ButtonType> result = deletealert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Statement statement = DatabaseDriver.getStmnt();
                statement.executeUpdate("DELETE FROM City WHERE city_id='" + tuple.getCity_id() + "'");
                initialize(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML protected void approve(ActionEvent event) {
        PendingCityView tuple = pendingCitiesList.getSelectionModel().getSelectedItem();
        Alert approvealert = new Alert(Alert.AlertType.CONFIRMATION, "Approve selected city?");
        Optional<ButtonType> result = approvealert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Statement statement = DatabaseDriver.getStmnt();
                statement.executeUpdate("UPDATE Reviewable_Entity SET is_Pending=false WHERE entity_id=" + tuple.getCity_id());
                initialize(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML protected void newCity(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "NewCity.fxml");
    }
}


package cs4400gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.sql.ResultSet;


/**
 * Created by dayynn on 7/10/17.
 */
public class WelcomeController implements Initializable {

    @FXML private Label welcome;
    @FXML private TextField attraction;
    @FXML private ChoiceBox<CityPair> cityChoices;
    @FXML private ChoiceBox<String> categoryChoices;
    @FXML private ChoiceBox<String> sort;
    @FXML private static ObservableList<CityPair> cityList;
    @FXML private static ObservableList<String> categoryList;
    private static ObservableList<String> sortOptions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cityList = FXCollections.observableArrayList();
            cityList.add(new CityPair("None", "None"));
            categoryList = FXCollections.observableArrayList();
            categoryList.add("None");
            welcome.setText("Welcome " + SessionInfo.getInstance().getEmail() + "!");
            Statement statement = DatabaseDriver.getStmnt();
            ResultSet list1 = statement.executeQuery("SELECT name, city_id, country " +
                                                       "FROM City JOIN Reviewable_Entity ON city_id=entity_id " +
                                                       "WHERE is_Pending=0");
            while (list1.next()) {
                cityList.add(new CityPair (list1.getString("city_id"), list1.getString("name") + ", " + list1.getString("country")));
            }
            cityChoices.setItems(cityList);
            cityChoices.getSelectionModel().selectFirst();

            ResultSet list2 = statement.executeQuery("SELECT category_name " +
                                                       "FROM Category");
            while (list2.next()) {
                categoryList.add(list2.getString("category_name"));
            }
            categoryChoices.setItems(categoryList);
            categoryChoices.getSelectionModel().selectFirst();

            sortOptions = FXCollections.observableArrayList("None", "LOCATION ASC", "LOCATION DESC", "NAME ASC", "NAME DESC", "AVG RATING ASC", "AVG RATING DESC", "NUM RATING ASC", "NUM RATING DESC");
            sort.setItems(sortOptions);
            sort.getSelectionModel().selectFirst();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML protected void logout(ActionEvent event) {
        Alert logout_alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to logout?", ButtonType.YES, ButtonType.NO);
        logout_alert.setTitle("Logout?");
        Optional<ButtonType> result = logout_alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            //log user out
            SessionInfo.getInstance().updateLoginInfo(null, null);
            SceneSwitcher.getInstance().switchScene(event, "Login.fxml");
        }
    }

    @FXML protected void allCities(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "CitiesList.fxml");
    }

    @FXML protected void allAttractions(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "AttractionsList.fxml");
    }

    @FXML protected void goMyReviews(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "UserReviews.fxml");
    }

    @FXML protected void deleteAccount(ActionEvent event) {
        Alert delete_alert = new Alert(Alert.AlertType.WARNING, "This will delete all of your reviews, " +
                "pending cities, and pending attractions. Do you still wish to proceed?", ButtonType.YES, ButtonType.NO);
        delete_alert.setTitle("Delete Account?");
        Optional<ButtonType> result = delete_alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            //delete user account
            Statement statement = DatabaseDriver.getStmnt();
            try {
                statement.executeUpdate("DELETE FROM App_User " +
                                          "WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
                statement.executeUpdate("DELETE FROM Reviewable_Entity " +
                                            "WHERE submitter_email IS NULL AND is_Pending=TRUE");
                SceneSwitcher.getInstance().switchScene(event, "Login.fxml");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML protected void search(ActionEvent event) {
        if (!attraction.getText().equals("")) {
            try {
                Statement statement = DatabaseDriver.getStmnt();
                ResultSet tuple = statement.executeQuery("SELECT attraction_id "
                                                         + "FROM Attraction JOIN Reviewable_Entity ON attraction_id=entity_id "
                                                         + "WHERE name='" + attraction.getText() + "' "
                                                         + "AND is_Pending=0");
                if (!tuple.first()) { //no tuples
                    Alert wrong_attraction = new Alert(Alert.AlertType.ERROR, "No Such Attraction");
                    wrong_attraction.showAndWait();
                } else if (tuple.first() && !tuple.next()) { //1 tuple
                    tuple.first();
                    SessionInfo.getInstance().setAttractionIdStorage(tuple.getString("attraction_id"));
                    SceneSwitcher.getInstance().switchScene(event, "AttractionPage.fxml");
                } else if (tuple.first() && tuple.next() ) { //2 tuple
                    if (!cityChoices.getSelectionModel().getSelectedItem().equals("None")) {
                        tuple = statement.executeQuery("SELECT attraction_id "
                                + "FROM Attraction JOIN City ON Attraction.city_id=City.city_id "
                                + "WHERE Attraction.name='" + attraction.getText() + "' "
                                + "AND City.name='" + cityChoices.getSelectionModel().getSelectedItem() + "' ");
                        tuple.next();
                        if (!tuple.next()) {
                            tuple.first();
                            SessionInfo.getInstance().setAttractionIdStorage(tuple.getString("attraction_id"));
                            SceneSwitcher.getInstance().switchScene(event, "AttractionPage.fxml");
                        }
                    } else if (!categoryChoices.getSelectionModel().getSelectedItem().equals("None")) {
                        tuple = statement.executeQuery("SELECT Attraction.attraction_id "
                                + "FROM Attraction JOIN Attraction_Category_List ON Attraction.attraction_id=Attraction_Category_List.attraction_id "
                                + "WHERE Attraction.name='" + attraction.getText() + "' "
                                + "AND c_Name='" + categoryChoices.getSelectionModel().getSelectedItem() + "' ");
                        tuple.next();
                        if (!tuple.next()) {
                            tuple.first();
                            SessionInfo.getInstance().setAttractionIdStorage(tuple.getString("Attraction.attraction_id"));
                            SceneSwitcher.getInstance().switchScene(event, "AttractionPage.fxml");
                        }
                    } else if (!cityChoices.getSelectionModel().getSelectedItem().equals("None")
                            && !categoryChoices.getSelectionModel().getSelectedItem().equals("None")) {
                        tuple = statement.executeQuery("SELECT Attraction.attraction_id "
                                + "FROM Attraction JOIN Attraction_Category_List ON Attraction.attraction_id=Attraction_Category_List.attraction_id "
                                + "JOIN City ON Attraction.city_id=City.city_id "
                                + "WHERE c_Name='" + categoryChoices.getSelectionModel().getSelectedItem() + "' "
                                + "AND Attraction.name='" + attraction.getText() + "' "
                                + "AND City.name='" + cityChoices.getSelectionModel().getSelectedItem() + "' ");
                        tuple.next();
                        if (!tuple.next()) {
                            tuple.first();
                            SessionInfo.getInstance().setAttractionIdStorage(tuple.getString("Attraction.attraction_id"));
                            SceneSwitcher.getInstance().switchScene(event, "AttractionPage.fxml");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (!cityChoices.getSelectionModel().getSelectedItem().getKey().equals("None")) {
            SessionInfo.getInstance().setCityIdStorage(cityChoices.getSelectionModel().getSelectedItem().getKey());
            SessionInfo.getInstance().setCategoryStorage(null);
            if (!categoryChoices.getSelectionModel().getSelectedItem().equals("None")) {
                SessionInfo.getInstance().setCategoryStorage(categoryChoices.getSelectionModel().getSelectedItem());
            }
            SessionInfo.getInstance().setSortStorage(null);
            if (!sort.getSelectionModel().getSelectedItem().equals("None")) {
                SessionInfo.getInstance().setSortStorage(sort.getSelectionModel().getSelectedItem());
            }
            SceneSwitcher.getInstance().switchScene(event, "CityPage.fxml");
        } else {
            SessionInfo.getInstance().setCityIdStorage(null);
            SessionInfo.getInstance().setCategoryStorage(null);
            System.out.println(categoryChoices.getSelectionModel().getSelectedItem());
            if (!categoryChoices.getSelectionModel().getSelectedItem().equals("None")) {
                SessionInfo.getInstance().setCategoryStorage(categoryChoices.getSelectionModel().getSelectedItem());
            }
            SessionInfo.getInstance().setSortStorage(null);
            if (!sort.getSelectionModel().getSelectedItem().equals("None")) {
                SessionInfo.getInstance().setSortStorage(sort.getSelectionModel().getSelectedItem());
            }
            SceneSwitcher.getInstance().switchScene(event, "AttractionsList.fxml");
        }

    }

}

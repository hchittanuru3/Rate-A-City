package cs4400gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by hemanthc98 on 7/12/17.
 */
public class ManagerWelcomeController implements Initializable {
    @FXML private Label welcome;
    @FXML private TextField attraction;
    @FXML private TextField user;
    @FXML private ChoiceBox<CityPair> cityChoices;
    @FXML private ChoiceBox<String> categoryChoices;
    @FXML private ChoiceBox<String> sort1;
    @FXML private ChoiceBox<String> sort2;
    @FXML private static ObservableList<CityPair> cityList;
    @FXML private static ObservableList<String> categoryList;
    private static ObservableList<String> sortOptions1;
    private static ObservableList<String> sortOptions2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcome.setText("Welcome " + SessionInfo.getInstance().getEmail() + "!");
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

            sortOptions1 = FXCollections.observableArrayList("None", "LOCATION ASC", "LOCATION DESC", "NAME ASC", "NAME DESC", "AVG RATING ASC", "AVG RATING DESC", "NUM RATING ASC", "NUM RATING DESC");
            sort1.setItems(sortOptions1);
            sort1.getSelectionModel().selectFirst();

            sortOptions2 = FXCollections.observableArrayList("None", "EMAIL ASC", "EMAIL DESC", "DATE JOINED ASC", "DATE JOINED DESC", "USER CLASS ASC", "USER CLASS DESC", "SUSPENDED ASC", "SUSPENDED DESC");
            sort2.setItems(sortOptions2);
            sort2.getSelectionModel().selectFirst();
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

    @FXML protected void viewCategories(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "CategoriesList.fxml");
    }

    @FXML protected void viewUsers(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "UsersList.fxml");
    }

    @FXML protected void viewPendingAttractions(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "PendingAttractions.fxml");
    }

    @FXML protected void viewPendingCities(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "PendingCities.fxml");
    }

    @FXML protected void searchAttraction(ActionEvent event) {
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
                    System.out.println("Here");
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
            if (!sort1.getSelectionModel().getSelectedItem().equals("None")) {
                SessionInfo.getInstance().setSortStorage(sort1.getSelectionModel().getSelectedItem());
            }
            SceneSwitcher.getInstance().switchScene(event, "CityPage.fxml");
        } else {
            SessionInfo.getInstance().setCityIdStorage(null);
            SessionInfo.getInstance().setCategoryStorage(null);
            if (!categoryChoices.getSelectionModel().getSelectedItem().equals("None")) {
                SessionInfo.getInstance().setCategoryStorage(categoryChoices.getSelectionModel().getSelectedItem());
            }
            SessionInfo.getInstance().setSortStorage(null);
            if (!sort1.getSelectionModel().getSelectedItem().equals("None")) {
                SessionInfo.getInstance().setSortStorage(sort1.getSelectionModel().getSelectedItem());
            }
            SceneSwitcher.getInstance().switchScene(event, "AttractionsList.fxml");
        }
    }

    @FXML protected void searchUser(ActionEvent event) {
        if (user.getText().equals("")) {
            SessionInfo.getInstance().setUserStorage(null);
            SessionInfo.getInstance().setSortStorage(null);
            System.out.println(sort2.getSelectionModel().getSelectedItem());
            if (!sort2.getSelectionModel().getSelectedItem().equals("None")) {
                SessionInfo.getInstance().setSortStorage(sort2.getSelectionModel().getSelectedItem());
            }
            SceneSwitcher.getInstance().switchScene(event, "UsersList.fxml");
        } else {
            try {
                Statement statement = DatabaseDriver.getStmnt();
                ResultSet tuple = statement.executeQuery("SELECT email "
                        + "FROM App_User "
                        + "WHERE email='" + user.getText() + "' ");
                if (!tuple.first()) { //no tuples
                    Alert wrong_user = new Alert(Alert.AlertType.ERROR, "No Such User");
                    wrong_user.showAndWait();
                } else {
                    tuple.first();
                    SessionInfo.getInstance().setUserStorage(tuple.getString("email"));
                    SessionInfo.getInstance().setSortStorage(null);
                    if (!sort2.getSelectionModel().getSelectedItem().equals("None")) {
                        SessionInfo.getInstance().setSortStorage(sort2.getSelectionModel().getSelectedItem());
                    }
                    SceneSwitcher.getInstance().switchScene(event, "UsersList.fxml");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                ResultSet check = statement.executeQuery("SELECT * FROM App_User WHERE is_manager=true");
                if (check.next()) {
                    if (check.next()) {
                        statement.executeUpdate("DELETE FROM App_User " +
                                "WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
                        statement.executeUpdate("DELETE FROM Reviewable_Entity " +
                                "WHERE submitter_email IS NULL AND is_Pending=TRUE");
                        SceneSwitcher.getInstance().switchScene(event, "Login.fxml");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}

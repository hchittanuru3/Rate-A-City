package cs4400gui;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.controlsfx.control.CheckComboBox;


import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * Created by harambe420 on 7/12/17.
 */
public class NewAttractionController implements Initializable {
    @FXML private TextField name;
    @FXML private TextField address;
    @FXML private ChoiceBox city;
    //add choice box for city and category
    @FXML private CheckComboBox category;
    @FXML private TextField rating;
    @FXML private TextField description;
    @FXML private TextField contactInfo;
    @FXML private TextField hoursOfOperation;
    @FXML private TextArea comment;
    ObservableList<String> chosen_categories;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Statement statement = DatabaseDriver.getStmnt();
        try {
            //populate city dropdown
            ResultSet city_list = statement.executeQuery("SELECT name FROM City");
            while (city_list.next()) {
                city.getItems().add(city_list.getString("name"));
            }
            //populate category drop down
            ResultSet category_list = statement.executeQuery("SELECT category_name FROM Category");
            while(category_list.next()) {
                category.getItems().add(category_list.getString("category_name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void submitAttraction(ActionEvent event) {
        Statement statement = DatabaseDriver.getStmnt();
        String attraction_name = name.getText();
        String attraction_address = address.getText();

        String attraction_description = description.getText();
        String attraction_contactInfo = contactInfo.getText();
        String attraction_hoursOfOperation = hoursOfOperation.getText();
        String attraction_comment = comment.getText();
        String submitterEmail = SessionInfo.getInstance().getEmail();
        int city_id;


        int id = EntityIDGenerator.newID();
        try {
            if (!rating.getText().equals("1") && !rating.getText().equals("2")
                    && !rating.getText().equals("3") && !rating.getText().equals("4") && !rating.getText().equals("5")) {
                Alert wrong_rating = new Alert(Alert.AlertType.ERROR, "You rating is invalid!");
                wrong_rating.showAndWait();
                return;
            }
            //set up city_id as int
            String selected_city = (String) city.getSelectionModel().getSelectedItem();
            ResultSet selectedcityid = statement.executeQuery("SELECT city_id FROM City WHERE name='" + selected_city + "'");
            selectedcityid.next();
            city_id = selectedcityid.getInt("city_id");
            //System.out.println(city_id);
            //set up added_category
            /*category.getCheckModel()..addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(ListChangeListener.Change<? extends String> c) {
                    //chosen_categories.setAll(category.getCheckModel().getCheckedItems());
                    chosen_categories = category.getCheckModel().getCheckedItems();
                    System.out.println(chosen_categories);
                }
            });
            */
            System.out.println(category.getCheckModel().getCheckedItems().get(0));
            int attraction_rating = Integer.parseInt(rating.getText());
            ResultSet checkManager = statement.executeQuery("SELECT is_manager "
                    + "FROM App_User "
                    + "WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
            checkManager.next();
            if (!checkManager.getBoolean("is_manager")) {
                statement.executeUpdate("INSERT INTO Reviewable_Entity(entity_id, is_Pending, is_City, submitter_email, date_submitted) "
                        + "VALUES (" + id + ", " + 1 + ", " + 0 + ", '" + submitterEmail + "', now())");
            } else {
                statement.executeUpdate("INSERT INTO Reviewable_Entity(entity_id, is_Pending, is_City, submitter_email, date_submitted) "
                        + "VALUES (" + id + ", " + 0 + ", " + 0 + ", '" + submitterEmail + "', now())");
            }
            statement.executeUpdate("INSERT INTO Attraction(attraction_id, name, street_address, description, city_id) " //have to pull city id from choice box
                    + "VALUES (" + id + ", '" + attraction_name + "', '" + attraction_address + "', '" + attraction_description + "', " + city_id + ")");
            statement.executeUpdate("INSERT INTO Review(entity_id, author_email, comment, rating, date_created) "
                    + "VALUES (" + id + ", '" + submitterEmail + "', '" + attraction_comment + "', " + attraction_rating + ", now())");
            if (!contactInfo.getText().isEmpty()) {
                statement.executeUpdate("INSERT INTO Contact_Info(attraction_id, info)"
                        + "VALUES (" + id + ", '" + attraction_contactInfo + "')");
            }
            if (!hoursOfOperation.getText().isEmpty()) {
                statement.executeUpdate("INSERT INTO Hours_Of_Operation(attraction_id, hours) "
                        + "VALUES (" + id + ", '" + attraction_hoursOfOperation + "')");
            }
            for (int i = 0; i < category.getCheckModel().getCheckedItems().toArray().length; i++) {
                String s = category.getCheckModel().getCheckedItems().get(i).toString();
                statement.executeUpdate("INSERT INTO Attraction_Category_List(c_Name, attraction_id) VALUES ('" + s + "', " + id + ")");
            }
            goBack(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }
}

// to do: add categories and city_id, have to figure out how to pull data from choice box
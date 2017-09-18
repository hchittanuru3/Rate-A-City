package cs4400gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by harambe420 on 7/12/17.
 */
public class NewCityController {

    @FXML private TextField name;
    @FXML private TextField country;
    @FXML private TextField state;
    @FXML private TextField rating;
    @FXML private TextArea comment;

    @FXML protected void submitCity(ActionEvent event) {
        Statement statement = DatabaseDriver.getStmnt();
        String city_name = name.getText();
        String city_country = country.getText();
        String city_state = state.getText();
        String city_comment = comment.getText();
        String submitterEmail = SessionInfo.getInstance().getEmail();
        try {
            //check to make sure no required fields are empty
            if (city_name.equals("") || city_country.equals("") || city_comment.equals("") || rating.getText().equals("")) {
                Alert not_enough_data = new Alert(Alert.AlertType.ERROR, "You did not enter all the required fields!");
                not_enough_data.showAndWait();
                return;
            }
            //check to see if input rating is 1-5
            if (!rating.getText().equals("1") && !rating.getText().equals("2")
                    && !rating.getText().equals("3") && !rating.getText().equals("4") && !rating.getText().equals("5")) {
                Alert wrong_rating = new Alert(Alert.AlertType.ERROR, "You rating is invalid!");
                wrong_rating.showAndWait();
                return;
            }
            if (city_state.equals("") && city_country.equals("United States")) {
                Alert null_state = new Alert(Alert.AlertType.ERROR, "State cannot be null.");
                null_state.showAndWait();
                return;
            }
            int rateValue = Integer.parseInt(rating.getText());
            int id = EntityIDGenerator.newID();
            //inserting city into all the tables to store data
            //first it checks to see if the user is a manager or regular user
            //manager that submits a city does not need to go to the review process
            ResultSet checkManager = statement.executeQuery("SELECT is_manager "
                                                            + "FROM App_User "
                                                            + "WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
            checkManager.next();
            if (!checkManager.getBoolean("is_manager")) {
                statement.executeUpdate("INSERT INTO Reviewable_Entity(entity_id, is_Pending, is_City, submitter_email, date_submitted) "
                        + "VALUES (" + id + ", " + 1 + ", " + 1 + ", '" + submitterEmail + "', now())");
            } else {
                statement.executeUpdate("INSERT INTO Reviewable_Entity(entity_id, is_Pending, is_City, submitter_email, date_submitted) "
                        + "VALUES (" + id + ", " + 0 + ", " + 1 + ", '" + submitterEmail + "', now())");
            }
            statement.executeUpdate("INSERT INTO City(city_id, name, country, state) "
                                    + "VALUES (" + id + ", '" + city_name + "', '" + city_country + "', '" + city_state + "')");
            statement.executeUpdate("INSERT INTO Review(entity_id, author_email, comment, rating, date_created) "
                                    + "VALUES (" + id + ", '" + submitterEmail + "', '" + city_comment + "', " + rateValue + ", now())");
            System.out.println("City Submitted!");
            SceneSwitcher.getInstance().switchScene(event, "CitiesList.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }
}

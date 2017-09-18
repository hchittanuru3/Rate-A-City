package cs4400gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.scene.control.*;


import java.sql.ResultSet;

/**
 * Created by dhuynh38 on 7/15/17.
 */
public class NewReviewController implements Initializable {

    @FXML private Label label;
    @FXML private TextField rating;
    @FXML private TextArea comment;

    @Override
    public void initialize (URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            String prevFxml = SceneSwitcher.getInstance().getPreviousFXML();
            System.out.println(prevFxml);

            if (prevFxml.equals("CityReviews.fxml") || prevFxml.equals("CityPage.fxml")) {
                ResultSet tuple = statement.executeQuery("SELECT name "
                        + "FROM City "
                        + "WHERE city_id='" + SessionInfo.getInstance().getCityIdStorage() + "'");
                if (tuple.next()) {
                    label.setText("New " + tuple.getString("name") + " Review");
                }
            } else {
                System.out.println(SessionInfo.getInstance().getAttractionIdStorage());
                ResultSet tuple = statement.executeQuery("SELECT name "
                        + "FROM Attraction "
                        + "WHERE attraction_id='" + SessionInfo.getInstance().getAttractionIdStorage() +"' ");
                tuple.next();
                label.setText("New " + tuple.getString("name") + " Review");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML protected void submitReview (ActionEvent event) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            if (!rating.getText().equals("1") && !rating.getText().equals("2")
                    && !rating.getText().equals("3") && !rating.getText().equals("4") && !rating.getText().equals("5")) {
                Alert wrong_rating = new Alert(Alert.AlertType.ERROR, "Your rating is invalid!");
                wrong_rating.showAndWait();
                return;
            }

            if (SceneSwitcher.getInstance().getPreviousFXML().equals("CityPage.fxml") || SceneSwitcher.getInstance().getPreviousFXML().equals("CityReviews.fxml")) {;
                statement.executeUpdate("INSERT INTO Review (entity_id, author_email, comment, rating, date_created) "
                        + "VALUES ('" + SessionInfo.getInstance().getCityIdStorage() + "','" + SessionInfo.getInstance().getEmail()
                        + "','" + comment.getText() + "','" + Integer.parseInt(rating.getText()) + "', now())");
            } else {
                statement.executeUpdate("INSERT INTO Review (entity_id, author_email, comment, rating, date_created) "
                        + "VALUES ('" + SessionInfo.getInstance().getAttractionIdStorage() + "','" + SessionInfo.getInstance().getEmail()
                        + "','" + comment.getText() + "','" + Integer.parseInt(rating.getText()) + "', now())");
            }
            SceneSwitcher.getInstance().goBack(event);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid input.");
            alert.showAndWait();
            return;
        }
    }
    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }
}

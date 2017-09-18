package cs4400gui;

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
 * Created by dhuynh38 on 7/15/17.
 */
public class UpdateReviewController implements Initializable {
    @FXML private Label entity;
    @FXML private TextField ratingField;
    @FXML private TextArea commentField;
    private int id;
    private String str;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            if (!SceneSwitcher.getInstance().getPreviousFXML().equals("UserReviews.fxml")) {
                //Get the rating and comment of the previous page and load them into the two fields
                ResultSet tuple = statement.executeQuery("SELECT comment, rating, city_id, name "
                        + "FROM (Review JOIN City ON entity_id=city_id) "
                        + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                        + "AND city_id='" + SessionInfo.getInstance().getCityIdStorage() + "'");
                if (tuple.next()) {

                    id = tuple.getInt("city_id");
                    ratingField.setText(tuple.getString("rating"));
                    commentField.setText(tuple.getString("comment"));
                    str = tuple.getString("name");
                    entity.setText("Update " + str + "'s Review");
                } else {

                    ResultSet data = statement.executeQuery("SELECT comment, rating, attraction_id, name "
                            + "FROM (Review JOIN Attraction ON entity_id=attraction_id) "
                            + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                            + "AND attraction_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'");
                    data.next();
                    id = data.getInt("attraction_id");
                    ratingField.setText(data.getString("rating"));
                    commentField.setText(data.getString("comment"));
                    str = data.getString("name");
                    entity.setText("Update " + str + "'s Review");
                }
            } else {
                ResultSet data = statement.executeQuery("SELECT comment, rating, Review.entity_id "
                        + "FROM Review JOIN Reviewable_Entity ON Review.entity_id=Reviewable_Entity.entity_id "
                        + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                        + "AND Review.entity_id='" + SessionInfo.getInstance().getStorage() + "' ");
                data.next();
                id = data.getInt("Review.entity_id");
                ratingField.setText(data.getString("rating"));
                commentField.setText(data.getString("comment"));
                data = statement.executeQuery("SELECT is_City "
                        + "FROM Reviewable_Entity "
                        + "WHERE entity_id='" + SessionInfo.getInstance().getStorage() + "' ");
                data.next();
                String s = "";
                if (data.getBoolean("is_City")) {
                    data = statement.executeQuery("SELECT name "
                            + "FROM City "
                            + "WHERE city_id='" + SessionInfo.getInstance().getStorage() + "' ");
                    data.next();
                    s = data.getString("name");
                } else {
                    data = statement.executeQuery("SELECT name "
                            + "FROM Attraction "
                            + "WHERE attraction_id='" + SessionInfo.getInstance().getStorage() + "' ");
                    data.next();
                    s = data.getString("name");
                }

                entity.setText("Update " + s + "'s Review");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void updateReview(ActionEvent event) {
        try {
            Alert update_alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to update your review?", ButtonType.YES, ButtonType.NO);
            update_alert.setTitle("Update Review?");
            Optional<ButtonType> result = update_alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                Statement statement = DatabaseDriver.getStmnt();
                //checks to see if the user inputs invalid rating
                if (!ratingField.getText().equals("1") && !ratingField.getText().equals("2")
                        && !ratingField.getText().equals("3") && !ratingField.getText().equals("4") && !ratingField.getText().equals("5")) {
                    Alert wrong_rating = new Alert(Alert.AlertType.ERROR, "You rating is invalid!");
                    wrong_rating.showAndWait();
                    return;
                }

                //use queries to locate the current review and update it
                int rateValue = Integer.parseInt(ratingField.getText());
                statement.executeUpdate("UPDATE Review "
                        + "SET comment='" + commentField.getText() + "', rating='" + rateValue + "' "
                        + "WHERE entity_id='" + id + "' AND "
                        + "author_email='" + SessionInfo.getInstance().getEmail() + "'");
                SceneSwitcher.getInstance().goBack(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void deleteReview(ActionEvent event) {
        try {
            Alert delete_alert = new Alert(Alert.AlertType.WARNING, "Are you sure you want to delete your review?", ButtonType.YES, ButtonType.NO);
            delete_alert.setTitle("Delete Review?");
            Optional<ButtonType> result = delete_alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                Statement statement = DatabaseDriver.getStmnt();
                //locates the current review in Review and delete it
                statement.executeUpdate("DELETE FROM Review "
                        + "WHERE entity_id='" + id + "' AND "
                        + "author_email='" + SessionInfo.getInstance().getEmail() + "'");
                SceneSwitcher.getInstance().goBack(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }
}

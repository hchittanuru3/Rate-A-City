package cs4400gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import javax.swing.plaf.nimbus.State;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Created by hemanthc98 on 7/22/17.
 */
public class AttractionPageController implements Initializable {

    @FXML private Label attraction;
    @FXML private ListView<String> titleList;
    @FXML private ListView valueList;
    private ObservableList<String> headers = FXCollections.observableArrayList();
    private ObservableList<String> values = FXCollections.observableArrayList();
    @FXML private Button del;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            ResultSet set = statement.executeQuery("SELECT is_manager FROM App_User WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
            set.next();
            if(set.getBoolean("is_manager")) {
                del.setVisible(true);
            }
            ResultSet tuple = statement.executeQuery("SELECT name FROM Attraction "
                                                    + "WHERE attraction_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'");
            tuple.next();
            attraction.setText(tuple.getString("name"));
            ResultSet numRate = statement.executeQuery("SELECT attraction_id, COUNT(rating)"
                                                    + "FROM Attraction JOIN Review ON attraction_id=entity_id "
                                                    + "WHERE attraction_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'"
                                                    + "GROUP BY attraction_id");
            numRate.next();
            String str = numRate.getString("COUNT(rating)");
            headers.add("Address:");
            headers.add("Description:");
            if(str == null) {
                headers.add("Average Rating (based on 0 ratings):");
            } else {
                headers.add("Average Rating (based on " + str + " ratings):");
            }
            headers.add("Hours of Operation:");
            headers.add("Contact Info:");
            headers.add("Category:");
            titleList.setItems(headers);
            ResultSet data = statement.executeQuery("SELECT  Attraction.attraction_id, street_address, description, AVG(rating), "
                    + "group_concat(DISTINCT hours) as hours, group_concat(DISTINCT info) as info, group_concat(DISTINCT c_Name) as Categories "
                    + "FROM Attraction LEFT JOIN Review ON attraction_id=entity_id NATURAL JOIN Attraction_Category_List LEFT JOIN Contact_Info "
                    + "ON Attraction.attraction_id=Contact_Info.attraction_id LEFT JOIN Hours_Of_Operation ON Attraction.attraction_id=Hours_Of_Operation.attraction_id "
                    + "WHERE Attraction.attraction_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'"
                    + "GROUP BY attraction_id");
            data.next();
            values.add(data.getString("street_address"));
            values.add(data.getString("description"));
            String rate = data.getString("AVG(rating)");
            if(rate == null) {
                rate = "";
            }
            values.add(rate);
            String hours = data.getString("hours");
            if (hours == null) {
                hours = "";
            }
            values.add(hours);
            String contactinfostr = data.getString("info");
            if (contactinfostr == null) {
                contactinfostr = "";
            }
            values.add(contactinfostr);
            values.add(data.getString("Categories"));
            valueList.setItems(values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void review(ActionEvent event) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            ResultSet tuple = statement.executeQuery("SELECT attraction_id "
                    + "FROM Review JOIN Attraction ON entity_id=attraction_id "
                    + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                    + "AND attraction_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'");
            if (tuple.next()) {
                SceneSwitcher.getInstance().switchScene(event, "UpdateReview.fxml");
            } else {
                SceneSwitcher.getInstance().switchScene(event, "NewReview.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void viewReviews(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "AttractionReviews.fxml");
    }

    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }

    @FXML protected void delete(ActionEvent event) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            Alert delete_alert = new Alert(Alert.AlertType.WARNING, "This will delete an attraction and all of its reviews, is that okay?");
            Optional<ButtonType> result = delete_alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                statement.executeUpdate("DELETE FROM Reviewable_Entity WHERE entity_id='" + SessionInfo.getInstance().getAttractionIdStorage() + "'");
            }
            SceneSwitcher.getInstance().goBack(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

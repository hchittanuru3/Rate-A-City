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

import javax.swing.plaf.nimbus.State;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by hemanthc98 on 7/12/17.
 */
public class UsersListController implements Initializable {

    @FXML private ChoiceBox sort_choicebox;
    @FXML private TableView<UserListRow> usertable;
    @FXML private TableColumn<UserListRow, String> emailcol;
    @FXML private TableColumn<UserListRow, String> datecol;
    @FXML private TableColumn<UserListRow, String> userclasscol;
    @FXML private TableColumn<UserListRow, String> suspendedcol;
    @FXML private TableColumn<UserListRow, String> deletecol;
    private static ObservableList<String> sortOptions;
    private static ObservableList<UserListRow> tuples;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tuples = FXCollections.observableArrayList();
        sortOptions = FXCollections.observableArrayList("EMAIL ASC", "EMAIL DESC", "DATE JOINED ASC", "DATE JOINED DESC", "USER CLASS ASC", "USER CLASS DESC", "SUSPENDED ASC", "SUSPENDED DESC");
        sort_choicebox.setItems(sortOptions);
        sort_choicebox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //reSort("newValue");)
                if (oldValue == (null) || (newValue == null)) {
                    return;
                }
                if (newValue.contains("EMAIL")) {
                    if (newValue.contains("ASC")) {
                        reSort("email", "ASC");
                    } else {
                        reSort("email", "DESC");
                    }
                } else if (newValue.contains("DATE JOINED")) {
                    if (newValue.contains("ASC")) {
                        reSort("date_joined", "ASC");
                    } else {
                        reSort("date_joined", "DESC");
                    }
                } else if (newValue.contains("USER CLASS")) {
                    if (newValue.contains("ASC")) {
                        reSort("is_manager", "ASC");
                    } else {
                        reSort("is_manager", "DESC");
                    }
                } else if (newValue.contains("SUSPENDED")) {
                    if (newValue.contains("ASC")) {
                        reSort("is_suspended", "ASC");
                    } else {
                        reSort("is_suspended", "DESC");
                    }
                }

            }
        });
        sort_choicebox.getSelectionModel().selectFirst();
        try {
            Statement statement = DatabaseDriver.getStmnt();
            String previousinput = SessionInfo.getInstance().getUserStorage();
            ResultSet resultSet = null;
            if (previousinput == null) {
                resultSet = statement.executeQuery("SELECT email, date_joined, is_manager, is_suspended FROM App_User");
            } else {
                resultSet = statement.executeQuery("SELECT email, date_joined, is_manager, is_suspended FROM App_User WHERE email='" + previousinput + "'");
            }
            while (resultSet.next()) {
                String resultemail = resultSet.getString("email");
                String resultdate = resultSet.getTimestamp("date_joined").toString();
                String resultclass;
                if (resultSet.getBoolean("is_manager")) {
                    resultclass = "Manager";
                } else {
                    resultclass = "User";
                }
                String resultsuspend;
                if (resultSet.getBoolean("is_suspended")) {
                    resultsuspend = "Yes";
                } else {
                    resultsuspend = "No";
                }
                tuples.add(new UserListRow(resultemail, resultdate, resultclass, resultsuspend));
            }
            emailcol.setCellValueFactory(new PropertyValueFactory<>("email"));
            datecol.setCellValueFactory(new PropertyValueFactory<>("date"));
            userclasscol.setCellValueFactory(new PropertyValueFactory<>("userclass"));
            suspendedcol.setCellValueFactory(new PropertyValueFactory<>("suspended"));

            usertable.setItems(tuples);

            if (SessionInfo.getInstance().getSortStorage() != null) {
                String newValue = SessionInfo.getInstance().getSortStorage();
                if (newValue.contains("EMAIL")) {
                    if (newValue.contains("ASC")) {
                        reSort("email", "ASC");
                    } else {
                        reSort("email", "DESC");
                    }
                } else if (newValue.contains("DATE JOINED")) {
                    if (newValue.contains("ASC")) {
                        reSort("date_joined", "ASC");
                    } else {
                        reSort("date_joined", "DESC");
                    }
                } else if (newValue.contains("USER CLASS")) {
                    if (newValue.contains("ASC")) {
                        reSort("is_manager", "ASC");
                    } else {
                        reSort("is_manager", "DESC");
                    }
                } else if (newValue.contains("SUSPENDED")) {
                    if (newValue.contains("ASC")) {
                        reSort("is_suspended", "ASC");
                    } else {
                        reSort("is_suspended", "DESC");
                    }
                }
                sort_choicebox.getSelectionModel().select(newValue);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }

    @FXML protected void addUser(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "Registration.fxml");
    }

    @FXML protected void deleteUser(ActionEvent event) {
        UserListRow row = usertable.getSelectionModel().getSelectedItem();
        if (row.getUserclass().equals("Manager")) {
            Alert error_alert = new Alert(Alert.AlertType.ERROR, "Cannot delete a manager!");
            error_alert.showAndWait();
            return;
        }
        Alert delete_alert = new Alert(Alert.AlertType.WARNING, "This will delete a user, all of their reviews, " +
                "pending cities, and pending attractions. Is this okay?");
        Optional<ButtonType> result = delete_alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Statement statement = DatabaseDriver.getStmnt();
            try {
                statement.executeUpdate("DELETE FROM App_User WHERE email ='" + row.getEmail() + "'");
                statement.executeUpdate("DELETE FROM Reviewable_Entity " +
                        "WHERE submitter_email IS NULL AND is_Pending=TRUE");
                initialize(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML protected void toggleSuspended(ActionEvent event) {
        UserListRow row = usertable.getSelectionModel().getSelectedItem();
        Statement statement = DatabaseDriver.getStmnt();
        if (row.getUserclass().equals("Manager")) {
            Alert error_alert = new Alert(Alert.AlertType.ERROR, "Cannot suspend a manager!");
            error_alert.showAndWait();
            return;
        }
        if (row.getSuspended().equals("Yes")) {
            Alert unbanalert = new Alert(Alert.AlertType.CONFIRMATION, "Unban this user?");
            Optional<ButtonType> result = unbanalert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    statement.executeUpdate("UPDATE App_User SET is_suspended=false WHERE email='" + row.getEmail() + "'");
                    initialize(null, null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            Alert banalert = new Alert(Alert.AlertType.CONFIRMATION, "Suspend this user?");
            Optional<ButtonType> result = banalert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    statement.executeUpdate("UPDATE App_User SET is_suspended=true WHERE email='" + row.getEmail() + "'");
                    initialize(null, null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML protected void promoteUser(ActionEvent event) {
        UserListRow row = usertable.getSelectionModel().getSelectedItem();
        if (row.getSuspended().equals("Yes")) {
            Alert erroralert = new Alert(Alert.AlertType.ERROR, "Cannot promote a suspended user!");
            erroralert.showAndWait();
            return;
        }
        Statement statement = DatabaseDriver.getStmnt();
        if (row.getUserclass().equals("Manager")) {
            Alert promotealert = new Alert(Alert.AlertType.CONFIRMATION, "This will demote the selected user to user status.");
            Optional<ButtonType> result = promotealert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    statement.executeUpdate("UPDATE App_User SET is_manager=false WHERE email='" + row.getEmail() + "'");
                    initialize(null, null);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return;
        }
        Alert promotealert = new Alert(Alert.AlertType.CONFIRMATION, "This will promote the selected user to manager status.");
        Optional<ButtonType> result = promotealert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                statement.executeUpdate("UPDATE App_User SET is_manager=true WHERE email='" + row.getEmail() + "'");
                initialize(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void reSort(String order, String direction) {
        try {
            System.out.println(order);
            System.out.println(direction);
            System.out.println("SELECT * FROM App_User ORDER BY " + order + " " + direction);
            Statement statement = DatabaseDriver.getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM App_User ORDER BY " + order + " " + direction);


            //traverse the database and store tuples in the arraylist
            tuples.clear();
            while (resultSet.next()) {
                String resultemail = resultSet.getString("email");
                String resultdate = resultSet.getTimestamp("date_joined").toString();
                String resultclass;
                if (resultSet.getBoolean("is_manager")) {
                    resultclass = "Manager";
                } else {
                    resultclass = "User";
                }
                String resultsuspend;
                if (resultSet.getBoolean("is_suspended")) {
                    resultsuspend = "Yes";
                } else {
                    resultsuspend = "No";
                }
                tuples.add(new UserListRow(resultemail, resultdate, resultclass, resultsuspend));
            }
            usertable.setItems(tuples);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}

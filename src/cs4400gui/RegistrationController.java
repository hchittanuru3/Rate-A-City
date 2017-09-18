package cs4400gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.Statement;


/**
 * Created by dayynn on 7/10/17.
 */
public class RegistrationController {

    @FXML private TextField email;
    @FXML private PasswordField password;
    @FXML private PasswordField password_confirm;

    @FXML protected void createAccount(ActionEvent event) {
        Statement statement = DatabaseDriver.getStmnt();
        //check if user data is valid
        String new_email = email.getText();
        String new_password = password.getText();
        String confirm_password = password_confirm.getText();
        if (!new_email.matches(".*@.*\\....")) {
            //throw dialog box
            Alert email_alert = new Alert(Alert.AlertType.ERROR, "Email field must contain a valid email!");
            email_alert.showAndWait();
            return;
        }
        if (new_password == null || confirm_password == null || new_password.length() < 3 || confirm_password.length() < 3) {
            //throw dialog box
            Alert password_alert = new Alert(Alert.AlertType.ERROR, "Password must have at least 4 characters!");
            password_alert.showAndWait();
            return;
        }
        if (!new_password.equals(confirm_password)) {
            //throw dialog box
            Alert password_alert = new Alert(Alert.AlertType.ERROR, "Passwords must match!");
            password_alert.showAndWait();
            return;
        }

        //hashpassword and then
        //add user data to database
        try {
            //check to see if email is already in the database
            ResultSet loginTable = statement.executeQuery("SELECT email " +
                                                            "FROM App_User " +
                                                            "WHERE email='" + email + "'");
            if (loginTable.next()) {
                Alert password_alert = new Alert(Alert.AlertType.ERROR, "Email already exists!");
                password_alert.showAndWait();
                return;
            }
            //create the new user
            statement.executeUpdate("INSERT INTO App_User(email,password,date_joined,is_manager,is_suspended) " +
                                      "VALUES ('" + new_email + "','" + hashPass(new_email, new_password) + "',now(),false,false)");
            System.out.println("Created new account!");
            SceneSwitcher.getInstance().goBack(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }

    //uses the username as the salt to hash the password into the database
    //simplified
    protected static int hashPass(String salt, String password) {
        int hash = 3;
        String saltypass = salt + password;
        for (int i = 0; i < saltypass.length(); i++) {
            hash += hash*7 + saltypass.charAt(i);
        }
        return hash;
    }
}

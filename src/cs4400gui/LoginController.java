package cs4400gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.Statement;

public class LoginController {
    @FXML private TextField user;
    @FXML private PasswordField passwd;

    @FXML protected void attemptLogin(ActionEvent event) {
        Statement statement = DatabaseDriver.getStmnt();
        String email = user.getText();
        String password = passwd.getText();
        ResultSet loginTable = null;
        try {
            //check if email and password matches what is in the system
            loginTable = statement.executeQuery("SELECT email, is_suspended, is_manager " +
                                                  "FROM App_User " +
                                                  "WHERE email='" + email + "' AND password='" +
                                                    RegistrationController.hashPass(email, password) + "'");

            if (loginTable.next()) {
                System.out.println("LOGIN!");
                //check to see if manager or regular user
                if (loginTable.getBoolean("is_suspended")) {
                    Alert suspended = new Alert(Alert.AlertType.ERROR, "Your account has been suspended.");
                    suspended.showAndWait();
                } else if (!loginTable.getBoolean("is_manager")) {
                    SessionInfo.getInstance().updateLoginInfo(email, password);
                    SceneSwitcher.getInstance().switchScene(event, "WelcomePage.fxml");
                } else {
                    SessionInfo.getInstance().updateLoginInfo(email, password);
                    SceneSwitcher.getInstance().switchScene(event, "ManagerWelcomePage.fxml");
                }
            } else {
                Alert wrong_pass = new Alert(Alert.AlertType.ERROR, "Incorrect Login Credentials");
                wrong_pass.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception Error in Login Controller!");
        }

    }

    @FXML protected void switchToRegister(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "Registration.fxml");
    }
}

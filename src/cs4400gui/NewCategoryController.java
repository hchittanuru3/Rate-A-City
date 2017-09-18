package cs4400gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.sql.Statement;

/**
 * Created by harambe420 on 7/12/17.
 */
public class NewCategoryController {
    @FXML private TextField category_name;

    @FXML protected void createCategory(ActionEvent event) {
        Statement statement = DatabaseDriver.getStmnt();
        String new_category_name = category_name.getText();
        try {
            statement.executeUpdate("insert into Category(category_name) values (" + new_category_name + ")");
            System.out.println("Created new Category!");
            SceneSwitcher.getInstance().switchScene(event, "CategoriesList.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }
}

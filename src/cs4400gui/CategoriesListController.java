package cs4400gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;




/**
 * Created by hemanthc98 on 7/23/17.
 */
public class CategoriesListController implements Initializable {

    private static ObservableList<CategoryView> list = FXCollections.observableArrayList();
    @FXML private TableColumn<CategoryView, String> name;
    @FXML private TableColumn<CategoryView, String> numAttr;
    @FXML private TableView<CategoryView> categoryList;
    @FXML private ChoiceBox<String> sort;
    private static ObservableList<String> sortOptions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortOptions = FXCollections.observableArrayList("NAME ASC", "NAME DESC", "NUM ATTRACTIONS ASC", "NUM ATTRACTIONS DESC");
        sort.setItems(sortOptions);
        sort.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == (null) || newValue == null) {
                    list.clear();
                    return;
                }
                if (newValue.contains("NAME")) {
                    if (newValue.contains("ASC")) {
                        reSort("category_name", "ASC");
                    } else {
                        reSort("category_name", "DESC");
                    }
                } else if (newValue.contains("NUM ATTRACTIONS")) {
                    if (newValue.contains("ASC")) {
                        reSort("numAttr", "ASC");
                    } else {
                        reSort("numAttr", "DESC");
                    }
                }
            }
        });
        sort.getSelectionModel().selectFirst();
        try {
            Statement statement = DatabaseDriver.getStmnt();
            statement.executeUpdate("CREATE OR REPLACE VIEW one AS SELECT category_Name, COUNT(attraction_id) as numAttr " +
                                    "FROM Attraction_Category_List RIGHT JOIN Category ON c_Name=category_name " +
                                    "GROUP BY category_name;");
            ResultSet tuple = statement.executeQuery("SELECT * FROM one ORDER BY category_name ASC;");
            while (tuple.next()) {
                list.add(new CategoryView(tuple.getString("category_name"), tuple.getString("numAttr")));
            }
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            numAttr.setCellValueFactory(new PropertyValueFactory<>("numAttr"));
            categoryList.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML protected void addCategory(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "NewCategory.fxml");
    }

    @FXML protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
    }

    @FXML protected void deleteCategory(ActionEvent event) {
        CategoryView category = categoryList.getSelectionModel().getSelectedItem();
        Alert delete_alert = new Alert(Alert.AlertType.WARNING, "This will delete a category and its instances in all attractions");
        Optional<ButtonType> result = delete_alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Statement statement = DatabaseDriver.getStmnt();
            try {
                statement.executeUpdate("DELETE FROM Category WHERE category_name='" + category.getName() + "'");
                initialize(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML protected void editCategory(ActionEvent event) {
        Statement statement = DatabaseDriver.getStmnt();
        CategoryView category = categoryList.getSelectionModel().getSelectedItem();
        TextInputDialog dialog = new TextInputDialog("Edit Category Name");
        dialog.setTitle("Edit Category Name");
        dialog.setHeaderText("Change the name of " + category.getName() + ".");
        dialog.setContentText("Enter what you want to change the category name to.");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                statement.executeUpdate("UPDATE Category SET category_name='" + result.get() + "'"
                        + "WHERE category_name='" + category.getName() + "'");
                for (int i = 0; i<categoryList.getItems().size(); i++) {
                    categoryList.getItems().clear();
                }
                initialize(null, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void reSort(String order, String direction) {
        list.clear();
        try {
            System.out.println(order);
            System.out.println(direction);
            Statement statement = DatabaseDriver.getStmnt();
            statement.executeUpdate("CREATE OR REPLACE VIEW one AS SELECT category_name, COUNT(attraction_id) as numAttr " +
                    "FROM Attraction_Category_List RIGHT JOIN Category ON c_Name=category_name " +
                    "GROUP BY category_name;");
            ResultSet data = statement.executeQuery("SELECT * FROM one ORDER BY " + order + " " + direction);
            System.out.println(order);
            System.out.println(direction);

            while (data.next()) {
                list.add(new CategoryView(data.getString("category_name"), data.getString("numAttr")));
            }
            categoryList.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

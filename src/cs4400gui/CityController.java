package cs4400gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * DAVE THE KNAVE
 * TODO: set label_avgRATING LABEL TO BE AVG RATING OF THE CITY(query to get avg rating of city)
 * TODO: set city_name label to be city name (query to get name from city_id)
 */
public class CityController implements Initializable {
    @FXML private Button deleteButton;
    @FXML private TableView<AttractionView> attracList;
    @FXML private TableColumn<AttractionView, String> name;
    @FXML private TableColumn<AttractionView, String> avgRating;
    @FXML private TableColumn<AttractionView, String> numRating;
    @FXML private TableColumn<AttractionView, String> category;
    @FXML private TableColumn<AttractionView, String> location;
    @FXML private TableColumn<AttractionView, String> hyperlink;
    @FXML private Label label_avgRating;
    @FXML private Label city_name;
    @FXML private Label header;
    @FXML private ChoiceBox<String> dropSort;
    private static ObservableList<AttractionView> list;
    private static ObservableList<String> sortOptions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list = FXCollections.observableArrayList();
        sortOptions = FXCollections.observableArrayList("NAME ASC", "NAME DESC", "AVG RATING ASC", "AVG RATING DESC", "NUM RATING ASC", "NUM RATING DESC", "LOCATION ASC", "LOCATION DESC");
        dropSort.setItems(sortOptions);
        dropSort.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (oldValue == (null)) {
                    return;
                }
                if (newValue.contains("AVG RATING")) {
                    if (newValue.contains("ASC")) {
                        reSort("avgRating", "ASC");
                    } else {
                        reSort("avgRating", "DESC");
                    }
                } else if (newValue.contains("NAME")) {
                    if (newValue.contains("ASC")) {
                        reSort("name", "ASC");
                    } else {
                        reSort("name", "DESC");
                    }
                } else if (newValue.contains("NUM RATING")) {
                    if (newValue.contains("ASC")) {
                        reSort("numRating", "ASC");
                    } else {
                        reSort("numRating", "DESC");
                    }
                } else if (newValue.contains("LOCATION")) {
                    if (newValue.contains("ASC")) {
                        reSort("location", "ASC");
                    } else {
                        reSort("location", "DESC");
                    }
                }
            }
        });

        dropSort.getSelectionModel().selectFirst();
        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            ResultSet result = statement.executeQuery("SELECT is_manager FROM App_User WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
            result.next();
            if (result.getBoolean("is_manager")) {
                deleteButton.setVisible(true);
            }
            statement.executeUpdate("CREATE OR REPLACE VIEW attr1 AS SELECT attraction_id, group_concat(c_Name) as category, Attraction.name as name, street_address as location " +
                    "FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id " +
                    "WHERE is_Pending=0 AND Attraction.city_id="+SessionInfo.getInstance().getCityIdStorage() + " "+
                    " GROUP BY attraction_id;");
            statement.executeUpdate("CREATE OR REPLACE VIEW attr2 AS SELECT entity_id, COUNT(*) as numRating, AVG(rating) as avgRating " +
                    "FROM Reviewable_Entity NATURAL JOIN Review " +
                    "WHERE is_Pending=0 " +
                    "GROUP BY entity_id;");
            ResultSet tuple = statement.executeQuery("SELECT * FROM attr1 JOIN attr2 ON attr1.attraction_id=attr2.entity_id" + (SessionInfo.getInstance().getCategoryStorage() == null ? "" : (" AND category LIKE '%" + SessionInfo.getInstance().getCategoryStorage()+"%' ")));




            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new AttractionView(tuple.getString("name"), tuple.getString("category"),
                        tuple.getString("location"), tuple.getString("avgRating"), tuple.getString("numRating"),
                        tuple.getString("attraction_id")));
            }
            tuple = statement.executeQuery("SELECT * FROM City WHERE city_id = " + SessionInfo.getInstance().getCityIdStorage());
            while(tuple.next()) {
                city_name.setText(tuple.getString("name") + ", " + tuple.getString("country"));
                header.setText(SessionInfo.getInstance().getCategoryStorage() == null ? "": SessionInfo.getInstance().getCategoryStorage() + " Attractions In: " + tuple.getString("name"));
            }

            tuple  = statement.executeQuery("SELECT AVG(rating) AS avgRating, is_Pending " +
                    "FROM City JOIN Review ON city_id=entity_id JOIN Reviewable_Entity ON city_id=Reviewable_Entity.entity_id " +
                    "GROUP BY city_id having is_Pending=0 AND city_id = "+ SessionInfo.getInstance().getCityIdStorage());
            while(tuple.next()) {
                label_avgRating.setText(tuple.getString("avgRating"));
            }
            //link columns to attributes
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            avgRating.setCellValueFactory(new PropertyValueFactory<>("avgRating"));
            numRating.setCellValueFactory(new PropertyValueFactory<>("numRating"));
            location.setCellValueFactory(new PropertyValueFactory<>("location"));
            category.setCellValueFactory(new PropertyValueFactory<>("category"));
            hyperlink.setCellValueFactory(new PropertyValueFactory<>("hyperlink"));
            hyperlink.setCellFactory(tc -> {
                TableCell<AttractionView, String> cell = new TableCell<AttractionView, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty) ;
                        setText(empty ? null : item);
                    }
                };



                cell.setOnMouseClicked(e -> {
                    if (!cell.isEmpty()) {
                        String attraction_id = cell.getItem();
                        System.out.println(attraction_id);
                        SessionInfo.getInstance().setAttractionIdStorage(attraction_id);
                        //FIX THIS LINKING
                        SceneSwitcher.getInstance().switchScene(e,"AttractionPage.fxml");
                        try {

                        }catch (Exception d) {
                            d.printStackTrace();
                        }



                    }
                });
                return cell ;
            });
            //set table to array list
            attracList.setItems(list);

            //takes in sort option from storage and sort by that
            if (SessionInfo.getInstance().getSortStorage() != null) {
                String newValue = SessionInfo.getInstance().getSortStorage();
                if (newValue.contains("AVG RATING")) {
                    if (newValue.contains("ASC")) {
                        reSort("avgRating", "ASC");
                    } else {
                        reSort("avgRating", "DESC");
                    }
                } else if (newValue.contains("NAME")) {
                    if (newValue.contains("ASC")) {
                        reSort("name", "ASC");
                    } else {
                        reSort("name", "DESC");
                    }
                } else if (newValue.contains("NUM RATING")) {
                    if (newValue.contains("ASC")) {
                        reSort("numRating", "ASC");
                    } else {
                        reSort("numRating", "DESC");
                    }
                } else if (newValue.contains("LOCATION")) {
                    if (newValue.contains("ASC")) {
                        reSort("location", "ASC");
                    } else {
                        reSort("location", "DESC");
                    }
                }
                dropSort.getSelectionModel().select(newValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void addReview(ActionEvent event) {
        try {
            Statement statement = DatabaseDriver.getStmnt();
            System.out.println(SessionInfo.getInstance().getCityIdStorage());
            ResultSet tuple = statement.executeQuery("SELECT city_id "
                    + "FROM Review JOIN City ON entity_id=city_id "
                    + "WHERE author_email='" + SessionInfo.getInstance().getEmail() + "' "
                    + "AND entity_id='" + SessionInfo.getInstance().getCityIdStorage() + "' ");
            if (tuple.next()) {
                SceneSwitcher.getInstance().switchScene(event, "UpdateReview.fxml");
            } else {
                SceneSwitcher.getInstance().switchScene(event, "NewReview.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void viewReviews(ActionEvent event) {
        //TODO: MAKE THIS LINK CORRECTLY
        SceneSwitcher.getInstance().switchScene(event, "CityReviews.fxml");
    }


    @FXML
    protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
        /*try {
            Statement statement = DatabaseDriver.getStmnt();
            ResultSet testManager = statement.executeQuery("SELECT is_manager "
                    + "FROM App_User "
                    + "WHERE email='" + SessionInfo.getInstance().getEmail() + "'");
            testManager.next();
            if (testManager.getBoolean("is_manager")) {
                SceneSwitcher.getInstance().switchScene(event, "ManagerWelcomePage.fxml");
            } else {
                SceneSwitcher.getInstance().switchScene(event, "WelcomePage.fxml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }




    //check for double clicks on any row
        /*citiesList.setRowFactory( a -> {
            TableRow<CityView> tuple = new TableRow<>();
            tuple.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !tuple.isEmpty()) {
                    SessionInfo.getInstance().setStorage(tuple.getItem().getName());
                    SceneSwitcher.getInstance().switchScene(e, "NewCity.fxml");
                }
            });
            return tuple;
        });*/



    private void reSort(String order, String direction) {
        list.clear();
        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            statement.executeUpdate("CREATE OR REPLACE VIEW attr1 AS SELECT attraction_id, group_concat(c_Name) as category, Attraction.name as name, street_address as location " +
                    "FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id " +
                    "WHERE is_Pending=0 AND Attraction.city_id="+SessionInfo.getInstance().getCityIdStorage() + " "+
                    "GROUP BY attraction_id;");
            statement.executeUpdate("CREATE OR REPLACE VIEW attr2 AS SELECT entity_id, COUNT(*) as numRating, AVG(rating) as avgRating " +
                    "FROM Reviewable_Entity NATURAL JOIN Review " +
                    "WHERE is_Pending=0 " +
                    "GROUP BY entity_id;");
            ResultSet tuple = statement.executeQuery("SELECT * FROM attr1 JOIN attr2 ON attr1.attraction_id=attr2.entity_id" + (SessionInfo.getInstance().getCategoryStorage() == null ? "" : (" AND category LIKE '%" + SessionInfo.getInstance().getCategoryStorage()+"%'")) +"ORDER BY " + order + " " + direction);


            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new AttractionView(tuple.getString("name"), tuple.getString("category"),
                        tuple.getString("location"), tuple.getString("avgRating"), tuple.getString("numRating"), tuple.getString("attraction_id")));
            }
            attracList.setItems(list);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void deleteCity(ActionEvent event) {
        try {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Delete this City?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Statement statement = DatabaseDriver.getStmnt();
                statement.executeUpdate("DELETE FROM City WHERE city_id=" + SessionInfo.getInstance().getCityIdStorage());
                goBack(event);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



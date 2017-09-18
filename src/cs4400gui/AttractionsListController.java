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

import java.net.URL;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;

/**
 * DAVE THE KNAVE
 */
public class AttractionsListController implements Initializable {
    @FXML private TableView<AttractionView> attrList;
    @FXML private TableColumn<AttractionView, String> name;
    @FXML private TableColumn<AttractionView, String> category;
    @FXML private TableColumn<AttractionView, String> location;
    @FXML private TableColumn<AttractionView, String> avgRating;
    @FXML private TableColumn<AttractionView, String> numRating;
    @FXML private TableColumn<AttractionView, String> hyperlink;
    @FXML private ChoiceBox<String> dropSort;
    private static ObservableList<AttractionView> list;
    private static ObservableList<String> sortOptions;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list = FXCollections.observableArrayList();
        sortOptions = FXCollections.observableArrayList("LOCATION ASC", "LOCATION DESC", "NAME ASC", "NAME DESC", "AVG RATING ASC", "AVG RATING DESC", "NUM RATING ASC", "NUM RATING DESC" );
        dropSort.setItems(sortOptions);
        dropSort.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                //reSort("newValue");)
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

        ////////////////////

        //TESTING AREA SessionInfo.getInstance().setCategoryStorage("Shop");

        ////////////////////

        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            statement.executeUpdate("CREATE OR REPLACE VIEW one AS SELECT attraction_id, group_concat(c_Name) as category, Attraction.name as name , City.name as location FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id JOIN City on Attraction.city_id=City.city_id WHERE is_Pending=0 GROUP BY attraction_id;");
                    statement.executeUpdate("CREATE OR REPLACE VIEW two AS SELECT entity_id, COUNT(*) as numRating, AVG(rating) as avgRating FROM Reviewable_Entity NATURAL JOIN Review WHERE is_Pending=0 GROUP BY entity_id;");
                            ResultSet tuple = statement.executeQuery("SELECT * FROM one JOIN two ON one.attraction_id=two.entity_id "+ (SessionInfo.getInstance().getCategoryStorage() == null ? "" : ("WHERE category LIKE '%" + SessionInfo.getInstance().getCategoryStorage()+"%' ")) +"ORDER BY location ASC");


//(SessionInfo.getInstance().getCategoryStorage() == null ? "" : ("WHERE category LIKE '%" + SessionInfo.getInstance().getCategoryStorage()+"%' ")) +

            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new AttractionView(tuple.getString("name"), tuple.getString("category"),
                        tuple.getString("location"), tuple.getString("avgRating"), tuple.getString("numRating"), tuple.getString("attraction_id")));
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
            //citiesList.setItems(null);
            attrList.setItems(list);

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
    protected void addAttraction(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "NewAttraction.fxml");
    }

    @FXML
    protected void goBack(ActionEvent event) {
        SceneSwitcher.getInstance().goBack(event);
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
            statement.executeUpdate("CREATE OR REPLACE VIEW one AS SELECT attraction_id, group_concat(c_Name) as category, Attraction.name as name , City.name as location FROM Attraction_Category_List NATURAL JOIN Attraction JOIN Reviewable_Entity ON attraction_id=entity_id JOIN City on Attraction.city_id=City.city_id WHERE is_Pending=0 GROUP BY attraction_id;");
            statement.executeUpdate("CREATE OR REPLACE VIEW two AS SELECT entity_id, COUNT(*) as numRating, AVG(rating) as avgRating FROM Reviewable_Entity NATURAL JOIN Review WHERE is_Pending=0 GROUP BY entity_id;");
            //ResultSet tuple = statement.executeQuery("SELECT * FROM one JOIN two ON one.attraction_id=two.entity_id ORDER BY " + order + " " + direction);
            ResultSet tuple = statement.executeQuery("SELECT * FROM one JOIN two ON one.attraction_id=two.entity_id "+ (SessionInfo.getInstance().getCategoryStorage() == null ? "" : ("WHERE category LIKE '%" + SessionInfo.getInstance().getCategoryStorage()+"%' ")) +"ORDER BY " + order + " " + direction);


            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                list.add(new AttractionView(tuple.getString("name"), tuple.getString("category"), tuple.getString("location"),
                        tuple.getString("avgRating"), tuple.getString("numRating"), tuple.getString("attraction_id")));
            }
            attrList.setItems(list);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}



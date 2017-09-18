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
import java.util.ResourceBundle;

/**
 * DAVE THE KNAVE
 */
public class CitiesListController implements Initializable {
    @FXML private TableView<CityView> citiesList;
    @FXML private TableColumn<CityView, String> cities;
    @FXML private TableColumn<CityView, String> avgRatings;
    @FXML private TableColumn<CityView, String> numRatings;
    @FXML private TableColumn<CityView, String> numAttractions;
    @FXML private TableColumn<CityView, String> hyperlink;
    @FXML private ChoiceBox<String> dropSort;
    private static ObservableList<CityView> list;
    private static ObservableList<String> sortOptions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        list = FXCollections.observableArrayList();
        sortOptions = FXCollections.observableArrayList("NAME ASC", "NAME DESC", "AVG RATING ASC", "AVG RATING DESC", "NUM RATING ASC", "NUM RATING DESC", " NUM ATTRACTIONS ASC", "NUM ATTRACTIONS DESC" );
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
                } else if (newValue.contains("NUM ATTRACTIONS")) {
                    if (newValue.contains("ASC")) {
                        reSort("numAttrac", "ASC");
                    } else {
                        reSort("numAttrac", "DESC");
                    }
                }
            }
        });
        dropSort.getSelectionModel().selectFirst();
        try {
            Statement statement = DatabaseDriver.getConnection().createStatement();
            statement.executeUpdate("CREATE OR REPLACE VIEW one AS SELECT city_id, is_Pending, name, COUNT(*) AS numRating, AVG(rating)AS avgRating\n" +
                    "                                      FROM City JOIN Review ON city_id=entity_id JOIN Reviewable_Entity ON city_id=Reviewable_Entity.entity_id\n" +
                    "                                      WHERE is_Pending = 0 GROUP BY city_id;");
            statement.executeUpdate("CREATE OR REPLACE VIEW two AS SELECT City.city_id, is_Pending,  COUNT(*) AS numAttrac\n" +
                    "                                      FROM City JOIN Attraction ON City.city_id=Attraction.city_id JOIN Reviewable_Entity ON attraction_id=Reviewable_Entity.entity_id\n" +
                    "                                      WHERE is_Pending=0\n" +
                    "                                      GROUP BY City.city_id;");
            ResultSet tuple = statement.executeQuery("SELECT * " +
                                                       "FROM one LEFT JOIN two ON one.city_id=two.city_id " +
                                                       "ORDER BY name ASC;");




            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                String numAttrac = tuple.getString("numAttrac");
                if (numAttrac == null || numAttrac.equals("")) {
                    numAttrac = "0";
                }
                list.add(new CityView(tuple.getString("name"), tuple.getString("avgRating"),
                        tuple.getString("numRating"), numAttrac, tuple.getString("city_id")));
            }

            //link columns to attributes
            cities.setCellValueFactory(new PropertyValueFactory<>("name"));
            avgRatings.setCellValueFactory(new PropertyValueFactory<>("avgRate"));
            numRatings.setCellValueFactory(new PropertyValueFactory<>("numRate"));
            numAttractions.setCellValueFactory(new PropertyValueFactory<>("numAttr"));
            hyperlink.setCellValueFactory(new PropertyValueFactory<>("hyperlink"));
            hyperlink.setCellFactory(tc -> {
                TableCell<CityView, String> cell = new TableCell<CityView, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty) ;
                        setText(empty ? null : item);
                    }
                };



                cell.setOnMouseClicked(e -> {
                    if (!cell.isEmpty()) {
                        String city_id = cell.getItem();
                        System.out.println(city_id);
                        SessionInfo.getInstance().setCityIdStorage(city_id);
                        SceneSwitcher.getInstance().switchScene(e,"CityPage.fxml");
                        try {

                        }catch (Exception d) {
                            d.printStackTrace();
                        }



                    }
                });
                return cell ;
            });
            //set table to array list
            citiesList.setItems(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void addCity(ActionEvent event) {
        SceneSwitcher.getInstance().switchScene(event, "NewCity.fxml");
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
            statement.executeUpdate("CREATE OR REPLACE VIEW one AS SELECT city_id, is_Pending, name, COUNT(*) AS numRating, AVG(rating)AS avgRating\n" +
                    "                                      FROM City JOIN Review ON city_id=entity_id JOIN Reviewable_Entity ON city_id=Reviewable_Entity.entity_id\n" +
                    "                                      WHERE is_Pending = 0 GROUP BY city_id;");
            statement.executeUpdate("CREATE OR REPLACE VIEW two AS SELECT City.city_id, is_Pending,  COUNT(*) AS numAttrac\n" +
                    "                                      FROM City JOIN Attraction ON City.city_id=Attraction.city_id JOIN Reviewable_Entity ON attraction_id=Reviewable_Entity.entity_id\n" +
                    "                                      WHERE is_Pending=0\n" +
                    "                                      GROUP BY City.city_id;");
            ResultSet tuple = statement.executeQuery("SELECT * " +
                    "FROM one LEFT JOIN two ON one.city_id=two.city_id " +
                    "ORDER BY " + order + " " + direction);


            //traverse the database and store tuples in the arraylist
            while (tuple.next()) {
                String numAttrac = tuple.getString("numAttrac");
                if (numAttrac == null || numAttrac.equals("")) {
                    numAttrac = "0";
                }
                list.add(new CityView(tuple.getString("name"), tuple.getString("avgRating"),
                        tuple.getString("numRating"), numAttrac, tuple.getString("city_id")));
            }
            citiesList.setItems(list);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}



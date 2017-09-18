package cs4400gui;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

/**
 * Created by dayynn on 7/12/17.
 */
public class SceneSwitcher {

    private static SceneSwitcher instance = new SceneSwitcher();
    private FXMLLoader loader;
    private Stack<String> sceneStack;

    private SceneSwitcher() {
        this.loader = new FXMLLoader();
        this.sceneStack = new Stack<>();
        sceneStack.add("Login.fxml");
    }

    public static SceneSwitcher getInstance() {
        return instance;
    }

    public void switchScene(Event event, String fxml) {
        Node node = (Node) event.getSource();
        Stage primaryStage = (Stage) node.getScene().getWindow();
        SessionInfo session = SessionInfo.getInstance();

        Parent parent;
        try {
            sceneStack.add(session.getCurrentFxml());
            session.setCurrentFxml(fxml);
            parent = this.loader.load(getClass().getResource(fxml));

            Scene scene = new Scene(parent);

            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goBack(Event event) {
        Node node = (Node) event.getSource();
        Stage primaryStage = (Stage) node.getScene().getWindow();
        Parent parent;
        SessionInfo session = SessionInfo.getInstance();
        try {
            String popped = sceneStack.pop();
            parent = this.loader.load(getClass().getResource(popped));
            session.setCurrentFxml(popped);
            Scene scene = new Scene(parent);
            primaryStage.setScene(scene);
            primaryStage.sizeToScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPreviousFXML() {
        return sceneStack.peek();
    }
}

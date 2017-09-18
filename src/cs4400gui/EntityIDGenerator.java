package cs4400gui;

import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by dhuynh38 on 7/14/17.
 */
public class EntityIDGenerator {

    public static int newID() {
        Statement statement = DatabaseDriver.getStmnt();
        ResultSet tuple = null;
        int id = 0;
        try {
            //get the max id from revieable entity and adds one to make new entity_id
            tuple = statement.executeQuery("SELECT MAX(entity_id) " +
                                             "FROM Reviewable_Entity");
            if (!tuple.next()) {
                id = 1;
            } else {
                id = tuple.getInt("MAX(entity_id)") + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

}

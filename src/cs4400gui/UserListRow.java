package cs4400gui;

/**
 * Created by dayynn on 7/23/17.
 */
public class UserListRow {

    private final String email;
    private final String date;
    private final String userclass;
    private final String suspended;


    public UserListRow(String email, String date, String userclass, String suspended) {
        this.email = email;
        this.date = date;
        this.userclass = userclass;
        this.suspended = suspended;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getSuspended() {
        return suspended;
    }

    public String getUserclass() {
        return userclass;
    }
}

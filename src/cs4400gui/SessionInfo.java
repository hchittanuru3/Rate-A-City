package cs4400gui;

/**
 * Created by dayynn on 7/12/17.
 */
public class SessionInfo {
    /*
        Class to hold data about the current users session rather than keeping track on the database side
     */

    private static SessionInfo instance = new SessionInfo();

    private String user_email;
    private String user_password;
    private String storage;
    private String sortStorage;
    private String userStorage;
    private String attractionIdStorage;
    private String cityIdStorage;
    private String categoryStorage;
    private String previousFxml;
    private String currentFxml;

    private SessionInfo() {
        this.user_email = null;
        this.user_password = null;
        this.storage = null;
        this.sortStorage = null;
        this.userStorage = null;
        this.attractionIdStorage = null;
        this.cityIdStorage = null;
        this.categoryStorage = null;
        this.previousFxml = null;
        this.currentFxml = null;
    }

    public static SessionInfo getInstance() {
        return instance;
    }

    public void updateLoginInfo(String email, String password) {
        this.user_email = email;
        this.user_password = password;
    }

    public void setStorage(String data) { storage = data; }

    public void setSortStorage(String data) { sortStorage = data; }

    public void setUserStorage(String data) { userStorage = data; }

    public void setAttractionIdStorage(String data) { attractionIdStorage = data; }

    public void setCityIdStorage(String data) { cityIdStorage = data; }

    public void setCategoryStorage(String data) { categoryStorage = data; }

    public String getEmail() {
        return user_email;
    }

    public String getPassword() {
        return user_password;
    }

    public String getStorage() { return storage; }

    public String getSortStorage() { return sortStorage; }

    public String getUserStorage() { return userStorage; }

    public String getAttractionIdStorage() { return attractionIdStorage; }

    public String getCityIdStorage() { return cityIdStorage; }

    public String getCategoryStorage() { return categoryStorage; }

    public String getPreviousFxml() {
        return previousFxml;
    }

    public void setPreviousFxml(String previous) {
        this.previousFxml = previous;
    }

    public String getCurrentFxml() {
        return currentFxml;
    }

    public void setCurrentFxml(String current) {
        this.currentFxml = current;
    }
}


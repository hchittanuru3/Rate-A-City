package cs4400gui;

/**
 * Created by dhuynh38 on 7/24/17.
 */
public class CityPair {
    String key;
    String value;

    public CityPair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}

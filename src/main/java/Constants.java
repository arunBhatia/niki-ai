package main.java;

import java.util.HashMap;
import java.util.Map;

/**
 * Class which has all the constants stored
 *
 * Created by Arun bhatia on 22/3/17.
 */
public class Constants {

    public static final String SEPARATOR = "<niki>";
    private static Map<Integer, String> intlabelMap = new HashMap<Integer, String>();

    public Constants(){
        this.intlabelMap = getIntLabelMap();
    }

    /**
     * Preserves mapping for each class type
     * @return
     */
    public static Map<Integer, String> getIntLabelMap() {
        intlabelMap.put(1, "who");
        intlabelMap.put(2, "what");
        intlabelMap.put(3, "when");
        intlabelMap.put(4, "affirmation");
        intlabelMap.put(5, "unknown");

        return intlabelMap;
    }
}

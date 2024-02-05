package org.example.map;

import org.debug.print.DebugPrint;
import org.json.JSONObject;
import org.model.Application;

public class MyApplicationMap {

    static Long id;
    static Long ucdb_id;
    static Float requested_amount;
    static String product;
    static String stringThatContainJson;

    public static Long getLongFromJsonByKey(String stringThatContainJson, String key) {
        long result;

        try {
            result = getMyJsonObject(stringThatContainJson).getJSONObject("after").getLong(key);
        } catch (Exception e) {
            DebugPrint.deprint(e.getMessage());
            result = 0L;
        }
        return result;
    }

    public static Float getFloatFromJsonByKey(String stringThatContainJson, String key) {
        float result;
        try {
            result = getMyJsonObject(stringThatContainJson).getJSONObject("after").getFloat(key);
        } catch (Exception e) {
            DebugPrint.deprint(e.getMessage());
            result = 0f;
        }
        return result;
    }

    public static String getStringFromJsonByKey(String stringThatContainJson, String key) {
        String result;
        try {
            result = getMyJsonObject(stringThatContainJson).getJSONObject("after").getString(key);
        } catch (Exception e) {
            DebugPrint.deprint(e.getMessage());
            result = "";
        }
        return result;
    }

    public Application map(String stringThatContainJson) throws Exception {
        return new Application(
                getLongFromJsonByKey(stringThatContainJson, "id"),
                getLongFromJsonByKey(stringThatContainJson, "ucdb_id"),
                getFloatFromJsonByKey(stringThatContainJson, "requested_amount"),
                getStringFromJsonByKey(stringThatContainJson, "product")
        );
    }

    public static JSONObject getMyJsonObject(String stringThatContainJson) {
        JSONObject result;
        try {
            result = new JSONObject(stringThatContainJson);
        } catch (Exception e) {
            DebugPrint.deprint(e.getMessage());
            result = new JSONObject();
        }
        return result;
    }
}

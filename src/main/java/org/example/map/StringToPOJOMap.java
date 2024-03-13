package org.example.map;

import org.debug.print.DebugPrint;
import org.json.JSONException;
import org.json.JSONObject;
import org.model.Application;

import java.util.Objects;

public class StringToPOJOMap {

    public static Long getLongFromJsonByKey(String stringThatContainJson, String key) {
        long result;

        try {
            result = getMyJsonObjectFromString(stringThatContainJson).getJSONObject("after").getLong(key);
        } catch (JSONException je) {
            result = getMyJsonObjectFromString(stringThatContainJson).getJSONObject("before").getLong(key);
            DebugPrint.deprint(String.valueOf(result));

            if (Objects.isNull(getMyJsonObjectFromString(stringThatContainJson))) {
                DebugPrint.deprint("NULL!!!", "NULL!!!");
            } else {
                DebugPrint.deprint(getMyJsonObjectFromString(stringThatContainJson).toString(), "+++++++what I Search ");
                DebugPrint.deprint(getMyJsonObjectFromString(stringThatContainJson).getClass().toString(), "+++++++what I Search - Type");
                DebugPrint.deprint("NOT NULL!!!", "NOT NULL!!!");
            }
            DebugPrint.deprint(je.getMessage());
        } catch (Exception e) {
            DebugPrint.deprint(e.getMessage());
            result = 5555555555555L;
        }
        return result;
    }

    public static Float getFloatFromJsonByKey(String stringThatContainJson, String key) {
        float result;
        try {
            result = getMyJsonObjectFromString(stringThatContainJson).getJSONObject("after").getFloat(key);
        } catch (Exception e) {
            DebugPrint.deprint(e.getMessage());
            result = 55555555555555555555f;
        }
        return result;
    }

    public static String getStringFromJsonByKey(String stringThatContainJson, String key) {
        String result;
        try {
            result = getMyJsonObjectFromString(stringThatContainJson).getJSONObject("after").getString(key);
        } catch (Exception e) {
            DebugPrint.deprint(e.getMessage());
            result = "5555555555555555555555555555";
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

    public static JSONObject getMyJsonObjectFromString(String stringThatContainJson) {
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

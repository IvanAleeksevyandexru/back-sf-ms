package ru.gosuslugi.pgu.sp.adapter.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Класс утилита, для работы с JSON
 */
final public class JsonUtil {
    private JsonUtil() {}

    /**
     * Проверка строки на JSON
     * @param checkingString строка для теста
     * @return если проверяемая строка есть валидный JSON - вернет true, иначе false
     */
    public static boolean isJSONString(String checkingString) {
        try {
            new JSONObject(checkingString);
        } catch (JSONException ex) {
            try {
                new JSONArray(checkingString);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}

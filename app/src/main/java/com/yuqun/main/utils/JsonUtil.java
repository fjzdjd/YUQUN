package com.yuqun.main.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    public static int JSON_TYPE_OBJECT = 0;
    public static int JSON_TYPE_ARRAY = 1;
    public static int JSON_TYPE_ERROR = -1;

    /***
     * 获取JSON类型 判断规则 判断第一个字母是否为{或[ 如果都不是则不是一个JSON格式的文本
     *
     * @param str
     * @return
     */
    public static int getJSONType(String str) {
        if (TextUtils.isEmpty(str)) {
            return JSON_TYPE_ERROR;
        }

        final char[] strChar = str.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];
        LogN.d("getJSONType", "firstChar is      " + firstChar);
        if (firstChar == '{') {
            return JSON_TYPE_OBJECT;
        } else if (firstChar == '[') {
            return JSON_TYPE_ARRAY;
        } else {
            return JSON_TYPE_ERROR;
        }

    }

    public static String getDataJson(String json) {
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("Data");
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List parseFromJsonToList(String jsonData, Class<T> myClass) {
        String json = getDataJson(jsonData);
        if (null == json) {
            LogN.e("parseFromJsonToList", "jsonArray  is  null  ");
            return null;
        }
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonArray Jarray = parser.parse(json).getAsJsonArray();
        ArrayList<T> lcs = new ArrayList<T>();
        for (JsonElement obj : Jarray) {
            lcs.add((T) gson.fromJson(obj, myClass));
        }
        return lcs;
    }

    public static <T> Object parseFromJsonToObject(String jsonData,
                                                   Class<T> myClass) {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, myClass);
    }


}

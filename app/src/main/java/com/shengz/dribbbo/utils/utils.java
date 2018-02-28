package com.shengz.dribbbo.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.shengz.dribbbo.model.User;

/**
 * Created by shengzhong on 2017/11/19.
 */

public class utils {
    private static String PREF_NAME = "models";

    private static Gson gson = new Gson();

    public static <T> String toString(T object, TypeToken<T> typeToken){
        return gson.toJson(object, typeToken.getType());
    }
    public static <T> T toObject(String json, TypeToken<T> typeToken){
        return gson.fromJson(json,typeToken.getType());


    }

    public static void save(Context context, String keyUser, Object object) {
        SharedPreferences sp = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        sp.edit().putString(keyUser, toString(object, new TypeToken<Object>(){})).apply();
    }

    public static <T> T read(Context context, String keyUser, TypeToken<T> typeToken) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonString = sp.getString(keyUser, "");
        try{
            return gson.fromJson(jsonString, typeToken.getType());
        }catch (JsonSyntaxException e){
            e.printStackTrace();
            return null;
        }
    }
}


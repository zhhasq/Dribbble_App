package com.shengz.dribbbo.view.Template;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.shengz.dribbbo.model.Bucket;
import com.shengz.dribbbo.model.Like;
import com.shengz.dribbbo.model.Shot;
import com.shengz.dribbbo.model.User;
import com.shengz.dribbbo.utils.utils;
import com.shengz.dribbbo.view.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by shengzhong on 2017/11/21.
 */

public class Dribbble {

    private static final String SP_AUTH = "auth";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String API_URL = "https://api.dribbble.com/v1/";
    private static final String USER_END_POINT = API_URL + "user";
    private static final String SHOT_END_POINT = API_URL + "shots";
    private static final String BUCKET_END_POINT = API_URL + "buckets";

    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SHOT_ID = "shot_id";


    private static OkHttpClient client = new OkHttpClient();
    private static final TypeToken<User> USER_TYPE = new TypeToken<User>(){};
    private static final TypeToken<List<Shot>> SHOT_TYPE = new TypeToken<List<Shot>>(){};
    private static final TypeToken<List<Bucket>> BUCKET_TYPE = new TypeToken<List<Bucket>>(){};
    private static final TypeToken<List<Like>> LIKE_LIST_TYPE = new TypeToken<List<Like>>(){};

    private static final String KEY_USER = "user";
    public static final int COUNT_PER_PAGE = 12;



    private static String accessToken;
    private static User user;

    private static Request.Builder authRequestBuilder(String url){
        return new Request.Builder()
                .addHeader("Authorization", "Bearer " + accessToken)
                .url(url);
    }

    private static Response getResponse(Request request) throws IOException{
        Response response = client.newCall(request).execute();
        Log.d("api-rate", response.header("X-RateLimit-Remaining"));

        return response;
    }
    private static Response makeRequest(String url) throws IOException{
        Request request = authRequestBuilder(url).build();
        return getResponse(request);
    }
    private static <T> T parseResponse(Response response, TypeToken<T> typeToken)
            throws IOException, JsonSyntaxException{
        String responseString = response.body().string();
        return utils.toObject(responseString, typeToken);
    }



    //login function will store accesstoken and user information
    public static void login (@NonNull Context context, @NonNull String accessToken)
            throws IOException, JsonSyntaxException{
        Dribbble.accessToken = accessToken;
        storeAccessToken(context, accessToken);

        //get user from API first
        user = getUser();
        Log.i("get user","get uer from API");
        Log.i("user is", user.name);
        Log.i("user head", user.avatar_url);

        storeUser(context, user);
    }

    public static void storeAccessToken(@NonNull Context context, @NonNull String string){
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(SP_AUTH, Context.MODE_PRIVATE);
        sp.edit().putString(KEY_ACCESS_TOKEN,string).apply();
    }

    public static void init(@NonNull Context context) {
        accessToken = loadAccessToken(context);
        if(accessToken != null){
            Log.i("Load token is",accessToken);
            user = loadUser(context);
        }
    }

    public static boolean isLoggedIn(){
        return accessToken != null;
    }

    //load token from harddrive if exist else return null
    private static String loadAccessToken(Context context) {
        SharedPreferences sp = context.getApplicationContext().getSharedPreferences(
                SP_AUTH, Context.MODE_PRIVATE);
        //when KEY_ACCESS_TOKEN not exist return 2nd parameter null.
        return sp.getString(KEY_ACCESS_TOKEN, null);
    }

    private static User loadUser(Context context) {
        return utils.read(context, KEY_USER, new TypeToken<User>(){});
    }

    public static User getUser() throws IOException,JsonSyntaxException{
       return parseResponse(makeRequest(USER_END_POINT),USER_TYPE);
    }
    public static void storeUser(@NonNull Context context, @Nullable User user) {
        utils.save(context, KEY_USER, user);
        Log.i("user" ,"user saved");
    }
    public static User getCurrentUser() {
        return user;
    }

    public static void logOut(@NonNull Context context) {
        storeAccessToken(context, null);
        storeUser(context, null);
        accessToken = null;
        user = null;


    }

    public static List<Shot> getShot(int page) throws IOException,JsonSyntaxException {
        String url = SHOT_END_POINT + "?page=" + page;
        //List<Shot> shots = parseResponse(makeRequest(url),SHOT_TYPE);
        //Shot shot1 = shots.get(0);
        //Log.i("shot1", (Uri.parse(shot1.getImageUrl())).toString());
        return parseResponse(makeRequest(url),SHOT_TYPE);
    }

    public static List<Bucket> getUserBuckets(int page) throws IOException, JsonSyntaxException{
        String url = USER_END_POINT + "/" + "buckets?page=" + page;
        return parseResponse(makeRequest(url),BUCKET_TYPE);
    }

    public static List<Shot> getLikes(int page) throws IOException, JsonSyntaxException {
        String url = USER_END_POINT + "/" + "likes?page=" + page;
        List<Like> likes = parseResponse(makeRequest(url),LIKE_LIST_TYPE);
        List<Shot> likedShots = new ArrayList<>();
        for(Like like : likes){
            likedShots.add(like.shot);
        }
        return likedShots;
    }

    public static Void addShotToFavorite(String shotID) throws IOException, JsonSyntaxException{
        String url = SHOT_END_POINT + "/" + shotID + "/like";
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = authRequestBuilder(url)
                .post(formBody)
                .build();
        getResponse(request);
        return null;
    }

    public static Void deleteShotFavorite(String shotID) throws IOException, JsonSyntaxException{
        String url = SHOT_END_POINT + "/" + shotID + "/like";
        RequestBody formBody = new FormBody.Builder()
                .build();
        Request request = authRequestBuilder(url)
                .delete(formBody)
                .build();
        getResponse(request);
        return null;
    }

    public static List<Shot> getBucketShots(int page, int id) throws IOException, JsonSyntaxException {
        //Log.i("bucketid",""+id);
        String url = BUCKET_END_POINT + "/" + id + "/shots?page=" + page;
        return parseResponse(makeRequest(url),SHOT_TYPE);
    }

    public static Bucket createBucket(String bucketName, String buckDescription)
            throws IOException, JsonSyntaxException {
        String url = BUCKET_END_POINT;
        FormBody formBody = new FormBody.Builder()
                .add(KEY_NAME, bucketName)
                .add(KEY_DESCRIPTION, buckDescription)
                .build();
        Request request = authRequestBuilder(url)
                .post(formBody)
                .build();
        return parseResponse(getResponse(request), new TypeToken<Bucket>(){});

    }

    public static Void addShotToBucket(String shotID, String bucketID)throws IOException,JsonSyntaxException {
        String url = BUCKET_END_POINT + "/" + bucketID +"/shots" ;
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID,shotID)
                .build();
        Request request = authRequestBuilder(url)
                .put(formBody)
                .build();
        Response response = getResponse(request);
        return null;
    }

    public static Shot LoadSingleShot(String shotID) throws IOException, JsonSyntaxException {
        String url = SHOT_END_POINT + "/" + shotID;
        return parseResponse(makeRequest(url), new TypeToken<Shot>(){});
    }

    public static Void deleteShotFromBucket(String shotID, String bucketID) throws IOException{
        String url = BUCKET_END_POINT + "/" + bucketID + "/shots";
        FormBody formBody = new FormBody.Builder()
                .add(KEY_SHOT_ID,shotID)
                .build();
        Request request = authRequestBuilder(url)
                .delete(formBody)
                .build();
        Response response =  getResponse(request);
        return  null;

    }

}

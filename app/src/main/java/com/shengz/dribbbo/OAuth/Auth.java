package com.shengz.dribbbo.OAuth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by shengzhong on 2017/11/21.
 */

public class Auth {
    public static final int REQ_CODE =100;
    private static final String KEY_CLIENT_ID = "client_id";
    private static final String CLIENT_ID = "e6841c4729a53f9938b8f2472a760ef8ed9cf79a43c02fe7f550272e6af4612a";
    private static final String KEY_SCOPE = "scope";
    private static final String SCOPE = "public+write";
    private static final String URI_AUTHORIZE = "https://dribbble.com/oauth/authorize";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    public static final String REDIRECT_URI = "http://www.google.com";
    private static final String KEY_CODE = "code";
    private static final String KEY_CLIENT_SECRET = "client_secret";
    private static final String CLIENT_SECRET = "71bb58bdc9bc57e0153648a3c8c0b84f8a171733a751c9c285394aac15beb2bf";
    private static final String URI_TOKEN = "https://dribbble.com/oauth/token";

    private static final String KEY_ACCESS_TOKEN = "access_token";




    public static String getAuthorizeUrl(){
        String url = Uri.parse(URI_AUTHORIZE)
                .buildUpon()
                .appendQueryParameter(KEY_CLIENT_ID, CLIENT_ID)
                .build()
                .toString();

        // fix encode issue
        url += "&" + KEY_REDIRECT_URI + "=" + REDIRECT_URI;
        url += "&" + KEY_SCOPE + "=" + SCOPE;
        return url;
    }

    public static void openLoginWeb(@NonNull Activity activity){
        Intent intent = new Intent(activity,AuthActivity.class);
        intent.putExtra(AuthActivity.KEY_URL,getAuthorizeUrl());
        activity.startActivityForResult(intent,REQ_CODE);
    }

    public static String getAcessToken(String authCode)
        throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody post = new FormBody.Builder()
                .add(KEY_CLIENT_ID, CLIENT_ID)
                .add(KEY_CLIENT_SECRET, CLIENT_SECRET)
                .add(KEY_CODE, authCode)
                .add(KEY_REDIRECT_URI, REDIRECT_URI)
                .build();
        Request request = new Request.Builder()
                .url(URI_TOKEN)
                .post(post)
                .build();
        Response response = client.newCall(request).execute();

        String responseString = response.body().string();
        try{
            JSONObject obj = new JSONObject(responseString);
            Log.i("token" ,obj.getString(KEY_ACCESS_TOKEN));
            return obj.getString(KEY_ACCESS_TOKEN);

        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }

    }
}

package com.shengz.dribbbo.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.shengz.dribbbo.OAuth.Auth;
import com.shengz.dribbbo.OAuth.AuthActivity;
import com.shengz.dribbbo.R;
import com.shengz.dribbbo.view.Template.Dribbble;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Login extends AppCompatActivity {
    @BindView(R.id.activity_login_btn) TextView loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //load token from shared Preference
        Dribbble.init(this);

        if(!Dribbble.isLoggedIn()){
            Log.i("user log in ", "user is not logged in ");
            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Auth.openLoginWeb(Login.this);
                }
            });
        }else{
            Log.i("user log in ", "user logged in ");
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == Auth.REQ_CODE && resultCode == RESULT_OK){
            final String authCode = data.getStringExtra(AuthActivity.KEY_CODE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String token = Auth.getAcessToken(authCode);
                        Log.i("user","try to log in user");
                        Dribbble.login(Login.this, token);

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (IOException e) {
                        e.getMessage();
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
}

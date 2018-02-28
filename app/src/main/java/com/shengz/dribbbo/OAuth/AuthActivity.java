package com.shengz.dribbbo.OAuth;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shengz.dribbbo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AuthActivity extends AppCompatActivity {
    public static final String KEY_CODE = "code";
    public static final String KEY_URL = "url";
    @BindView(R.id.webview) WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith(Auth.REDIRECT_URI)){
                    Uri uri = Uri.parse(url);
                    Intent result = new Intent();
                    result.putExtra(KEY_CODE,uri.getQueryParameter(KEY_CODE));
                    setResult(RESULT_OK,result);
                    finish();
                }
                return super.shouldOverrideUrlLoading(view,url);
            }
        });
        String url = getIntent().getStringExtra(KEY_URL);
        webView.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

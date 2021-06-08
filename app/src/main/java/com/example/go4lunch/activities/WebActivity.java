package com.example.go4lunch.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.go4lunch.R;

public class WebActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        initView();
        configureToolbar();
        displayWebView();
    }

    private void initView() {
        mToolbar = findViewById(R.id.simple_toolbar);
        webView = findViewById(R.id.webview);
    }

    private void configureToolbar(){
        setSupportActionBar(mToolbar);
        getSupportActionBar();
    }




    private void displayWebView() {
        String url = getIntent().getStringExtra("Website");
        if (url != null){
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.loadUrl(url);
            webView.setWebViewClient(new WebViewClient());
        }else{
           finish();
        }

    }
}
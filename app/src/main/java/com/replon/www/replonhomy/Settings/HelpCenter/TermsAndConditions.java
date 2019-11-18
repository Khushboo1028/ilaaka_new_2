package com.replon.www.replonhomy.Settings.HelpCenter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class TermsAndConditions extends AppCompatActivity {

    private WebView webView;
    private TextView cancel;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), TermsAndConditions.this);

        setContentView(R.layout.terms_and_conditions);


            webView = (WebView) findViewById(R.id.webView1);
            cancel=(TextView) findViewById(R.id.cancel);
            webView.getSettings().setJavaScriptEnabled(true);
            type=getIntent().getStringExtra("type");
            if(type.equals("T&C")){
                webView.loadUrl("https://www.replon.com/terms-and-conditions.html");

            }else if(type.equals("privacy_policy")){
                webView.loadUrl("https://www.replon.com/privacy-policy.html");
            }


            webView.setWebViewClient(new WebViewClient(){
                public void onReceivedError(WebView view,int errorCode,String description,String failingUrl){
                    webView.loadUrl("file:///android_asset/error.html");
                }
        });

          cancel.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  onBackPressed();
              }
          });


    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

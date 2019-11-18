package com.replon.www.replonhomy.Settings.HelpCenter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class ContactUsActivity extends AppCompatActivity {

    ImageView back;
    EditText name,email,phone,message;
    Button send;
    TextView message_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ContactUsActivity.this);

        setContentView(R.layout.activity_contact_us);

        back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        name = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        message = (EditText) findViewById(R.id.message);
        send=(Button)findViewById(R.id.send);
        message_tv=(TextView)findViewById(R.id.message_text);


        email.setText(getIntent().getStringExtra("email"));
        email.setInputType(0);
        name.setText(getIntent().getStringExtra("currentName"));


//        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                message_tv.setVisibility(View.GONE);
//            }
//        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Replon Home Help");
                intent.putExtra(Intent.EXTRA_TEXT, "name: "+name.getText().toString()+"\nemail: "+email.getText().toString()+"\nContact: "+phone.getText().toString()+"\nmessage: "+message.getText().toString() );
                intent.setData(Uri.parse("mailto:support@replon.com")); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);
            }
        });






    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }
}

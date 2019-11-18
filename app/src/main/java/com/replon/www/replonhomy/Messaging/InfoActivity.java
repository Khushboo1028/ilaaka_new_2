package com.replon.www.replonhomy.Messaging;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class InfoActivity extends AppCompatActivity {

    public static final String TAG = "InfoActivity";
    TextView name,flat_no,phone,email;
    ImageView user_profile,back,profile_image;
    String rec_name,rec_flatno,rec_phno,rec_email,rec_img;
    RelativeLayout phone_rel,email_rel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), InfoActivity.this);
        setContentView(R.layout.activity_info);

       rec_name = getIntent().getExtras().getString("rec_name");
        rec_email = getIntent().getExtras().getString("rec_email");
        rec_phno = getIntent().getExtras().getString("rec_phone");
        rec_flatno = getIntent().getExtras().getString("rec_flatno");
        rec_img = getIntent().getStringExtra("rec_img");

        name = findViewById(R.id.name);
        flat_no = findViewById(R.id.flat_no);
        back = findViewById(R.id.back);
        profile_image = findViewById(R.id.user_profile);
        phone = findViewById(R.id.phone_number);
        email = findViewById(R.id.email);

        phone_rel = findViewById(R.id.phone_rel);
        email_rel = findViewById(R.id.email_rel);

        phone_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = phone.getText().toString();
                tel = "tel:"+tel;
                //Intent acCall = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));

                Intent acCall = new Intent(Intent.ACTION_DIAL);
                acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                acCall.setData(Uri.parse(tel));
                v.getContext().startActivity(acCall);
            }
        });

        email_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = email.getText().toString();
                mail = "mailto:"+mail;
                //Intent acCall = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));

                Intent acMail = new Intent(Intent.ACTION_VIEW);
                acMail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                acMail.setData(Uri.parse(mail));
                v.getContext().startActivity(acMail);

            }
        });


        name.setText(rec_name);
        flat_no.setText(rec_flatno);
        phone.setText("+91 " + rec_phno);
        email.setText(rec_email);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if(rec_img!= null && rec_img.equals("")){
            profile_image.setImageResource(R.drawable.ic_service_default);

        }else{
            Glide.with(getApplicationContext()).load(rec_img).placeholder(R.drawable.ic_service_default).into(profile_image);
        }

    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);
    }
}

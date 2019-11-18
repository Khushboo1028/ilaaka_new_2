package com.replon.www.replonhomy.Login;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";
    Fragment fragment;
    String isNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), RegisterActivity.this);
        setContentView(R.layout.activity_register);
        //isNewUser=getIntent().getStringExtra("isNewUSer");
        Log.i(TAG,"iNEwUser? "+isNewUser);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });



        FragmentManager fm = getSupportFragmentManager();

            FragmentTransaction ft = fm.beginTransaction();
            fragment =new SocIdFragment();
            Bundle args = new Bundle();
          //  args.putString("isNewUSer", isNewUser);
            fragment.setArguments(args);
            ft.replace(R.id.fragment_register,fragment);
            ft.commit();





    }




}

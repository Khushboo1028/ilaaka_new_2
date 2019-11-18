package com.replon.www.replonhomy.Login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.replon.www.replonhomy.Onboarding.OnboardingActivity;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class Login extends AppCompatActivity  {
    private static final String TAG = "Login";

//    Button cont;
//    private FirebaseAuth mAuth;
//
//    EditText et_email;
//    EditText et_password;
//    Button btn_login;
//    TextView invalid_text;
//
//    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), Login.this);

        setContentView(R.layout.activity_login);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("LOGIN"));
        tabLayout.addTab(tabLayout.newTab().setText("REGISTER"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

//        View root = tabLayout.getChildAt(0);
//        if (root instanceof LinearLayout) {
//            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
//            GradientDrawable drawable = new GradientDrawable();
//            drawable.setColor(getResources().getColor(R.color.white));
//            drawable.setSize(10, 1);
////            ((LinearLayout) root).setDividerPadding(10);
//            ((LinearLayout) root).setPadding(10,50,10,40);
//            ((LinearLayout) root).setDividerDrawable(drawable);
//        }


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

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



















//        FirebaseApp.initializeApp(this);
//        mAuth = FirebaseAuth.getInstance();
////        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
////        actionBar.hide();
//
//        et_email=(EditText)findViewById(R.id.username);
//        et_password=(EditText)findViewById(R.id.password);
//        btn_login=(Button)findViewById(R.id.login);
//        invalid_text = (TextView)findViewById(R.id.invalid_text);
//
//        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
//        mProgressBar.setVisibility(View.GONE);
//
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//
//
//        et_email.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                et_email.setBackgroundResource(R.drawable.edittext_borders);
//                et_password.setBackgroundResource(R.drawable.edittext_borders);
//                return false;
//            }
//        });
//
//        et_password.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                et_email.setBackgroundResource(R.drawable.edittext_borders);
//                et_password.setBackgroundResource(R.drawable.edittext_borders);
//                return false;
//            }
//        });
//
//
//
//        btn_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String email=et_email.getText().toString().toLowerCase().trim();
//                String password=et_password.getText().toString();
//
//                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
//                    invalid_text.setText("Please Enter all the Details");
//                    changeUI();
//
//                }else{
//                    mProgressBar.setVisibility(View.VISIBLE);
//                    v.getBackground().setAlpha(100);
//                    mAuth.signInWithEmailAndPassword(email,password)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//
//                                    if(task.isSuccessful()){
//
//                                        mProgressBar.setVisibility(View.GONE);
//
//                                        Intent login_intent = new Intent(com.replon.www.replonhomy.Login.Login.this, Home.class);
//                                        login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
//                                        startActivity(login_intent);
//                                        overridePendingTransition(0,0);
//                                        finish();
//
//                                    }
//                                    else{
//                                        mProgressBar.setVisibility(View.GONE);
//                                        invalid_text.setText("Oops! Your Username/Password is incorrect.");
//                                        changeUI();
//
//
//
//                                    }
//
//                                }
//                            });
//                }
//
//
//            }
//            private void changeUI() {
//
//                Vibrator vibrator = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
//                // Vibrate for 500 milliseconds
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
//                } else {
//                    //deprecated in API 26
//                    vibrator.vibrate(500);
//                }
//                et_email.setBackgroundResource(R.drawable.edittext_borders_red);
//                et_password.setBackgroundResource(R.drawable.edittext_borders_red);
//            }
//        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        //finish();
        //finishAffinity();
    }


    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onStart() {

        if (isFirstTimeAppStart()) {

            Log.i(TAG,"FIRST TIME USING THE APP");

            Intent intent = new Intent(Login.this, OnboardingActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            setFirstTimeStartStatus(false);

        }else{
            super.onStart();
        }
    }

    private boolean isFirstTimeAppStart(){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSlider", Context.MODE_PRIVATE);
        return ref.getBoolean("FirstTimeStartFlag",true);

    }

    private void setFirstTimeStartStatus(boolean stt){
        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSlider",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putBoolean("FirstTimeStartFlag",stt);
        editor.commit();
    }
}



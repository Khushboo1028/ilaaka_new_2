package com.replon.www.replonhomy.Onboarding;

import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class OnboardingActivity extends AppCompatActivity{

    private ViewPager viewPager;
    private LinearLayout dotLayout;
    private TextView[] dotstv;
    private int[] layouts;
    private TextView skip,next;

    private PagerAdapterOnboarding pagerAdapterOnboarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), OnboardingActivity.this);

        setStatusBarTransparent();
        setContentView(R.layout.activity_onboarding);

        viewPager = (ViewPager)findViewById(R.id.view_pager);
        dotLayout = (LinearLayout)findViewById(R.id.dotLayout);
        skip = (TextView)findViewById(R.id.skip);
        next = (TextView)findViewById(R.id.next);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
//               overridePendingTransition(0,0);

            }
        });

        layouts = new int[]{R.layout.slider_1,R.layout.slider_2,R.layout.slider_3,R.layout.slider_5,R.layout.slider_6};

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = viewPager.getCurrentItem()+1;
                if (currentPage <layouts.length){
                    viewPager.setCurrentItem(currentPage);
                }else {
                    finish();
//                    overridePendingTransition(0,0);
                }

            }
        });
        pagerAdapterOnboarding = new PagerAdapterOnboarding(layouts, getApplicationContext());
        viewPager.setAdapter(pagerAdapterOnboarding);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == layouts.length-1){
                    next.setText("START");
                    skip.setVisibility(View.GONE);
                }else {
                    next.setText("NEXT");
                    skip.setVisibility(View.VISIBLE);
                }
                setDotStatus(i);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        setDotStatus(0);



    }

//    private void startMainActivity(){
//        setFirstTimeStartStatus(false);
//        Intent intent = new Intent(OnboardingActivity.this, Home.class);
//        finish();
//        startActivity(intent);
//        overridePendingTransition(0,0);
//    }

//    private boolean isFirstTimeAppStart(){
//        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSlider", Context.MODE_PRIVATE);
//        return ref.getBoolean("FirstTimeStartFlag",true);
//    }
//
//    private void setFirstTimeStartStatus(boolean stt){
//        SharedPreferences ref = getApplicationContext().getSharedPreferences("IntroSlider",Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = ref.edit();
//        editor.putBoolean("FirstTimeStartFlag",stt);
//        editor.commit();
//    }

    private void setDotStatus(int page){

        dotLayout.removeAllViews();
        dotstv = new TextView[layouts.length];
        for (int i = 0;i<dotstv.length;i++){
            dotstv[i] = new TextView(this);
            dotstv[i].setText(Html.fromHtml("&#8226"));
            dotstv[i].setTextSize(30);
            dotstv[i].setTextColor(Color.parseColor("#A1EFD9"));
            dotLayout.addView(dotstv[i]);

        }

        if (dotstv.length > 0){
            dotstv[page].setTextColor(Color.parseColor("#00FF4E"));
        }


    }

    private void setStatusBarTransparent(){
        if (Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN );
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

    }
}

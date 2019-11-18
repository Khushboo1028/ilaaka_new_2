package com.replon.www.replonhomy.Accounting.MMT;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.replon.www.replonhomy.Accounting.SocietyAccounting.SocietyAccountingActivity;
import com.replon.www.replonhomy.Complaints.ComplaintsActivity;
import com.replon.www.replonhomy.Emergency.Emergency;
import com.replon.www.replonhomy.Messaging.MessagesMainActivity;
import com.replon.www.replonhomy.Nearby.Nearby;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Services.Services;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Settings.Settings;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class AccountingMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String TAG = "AccountingMainActivity";
    ImageView ham_icon, user_icon,back;
    String soc_name, flat_no,name,username,unique_id,admin;
    TextView tv_society_name,tv_flatno,tv_name,tv_username;
    Boolean dummy=false;

    LinearLayout mmt_rel,accounting_rel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AccountingMainActivity.this);

        setContentView(R.layout.activity_accounting_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ham_icon = (ImageView) findViewById(R.id.ham_out);
        user_icon = (ImageView)findViewById(R.id.user_profile);

        ham_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        tv_name = headerLayout.findViewById(R.id.name);
        tv_username=headerLayout.findViewById(R.id.username);

        tv_society_name=(TextView)findViewById(R.id.society_name);
        tv_flatno=(TextView)findViewById(R.id.flat_no);

        accounting_rel=(LinearLayout)findViewById(R.id.soc_accounts_rel);

        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        username = getIntent().getExtras().getString("username");
        unique_id=getIntent().getStringExtra("unique_id");
        admin=getIntent().getStringExtra("admin");

        if(unique_id.equals(getString(R.string.DUMMY_SOC))){
            dummy=true;
        }


        tv_society_name.setText(soc_name);
        tv_flatno.setText(flat_no);
        tv_username.setText(username);
        tv_name.setText(name);

        user_icon = (ImageView)findViewById(R.id.user_profile);
        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settings_intent = new Intent(AccountingMainActivity.this, Settings.class);
                settings_intent.putExtra("society_name",soc_name);
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",username);
                settings_intent.putExtra("name",name);
                settings_intent.putExtra("admin",admin);
                finish();
                startActivity(settings_intent);
                overridePendingTransition(0, 0);

            }
        });


        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mmt_rel=(LinearLayout) findViewById(R.id.mmt_rel);
        mmt_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MMTActivity.class);
                if(unique_id.equals(getString(R.string.DUMMY_SOC_ID))){
                    showMessageOptions("Oops!","You are not able to access MMT!\n Contact us for more information",R.drawable.ic_error_dialog);

                }else{
                    intent.putExtra("society_name",soc_name);
                    intent.putExtra("flat_no",flat_no);
                    intent.putExtra("username",username);
                    intent.putExtra("name",name);
                    intent.putExtra("admin",admin);
                    intent.putExtra("unique_id",unique_id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_start_activity, R.anim.exit_start_activity);

                }

            }
        });

        accounting_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SocietyAccountingActivity.class);
                if(unique_id.equals(getString(R.string.DUMMY_SOC_ID))){
                    showMessageOptions("Oops!","You are not able to access Accounting!\n Contact us for more information",R.drawable.ic_error_dialog);

                }else{
                    intent.putExtra("society_name",soc_name);
                    intent.putExtra("flat_no",flat_no);
                    intent.putExtra("username",username);
                    intent.putExtra("name",name);
                    intent.putExtra("admin",admin);
                    intent.putExtra("unique_id",unique_id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_start_activity, R.anim.exit_start_activity);
                }


            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (id){
            case R.id.home_menu:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.messages_menu:
                if(!dummy) {
                    Intent messages_intent = new Intent(getApplicationContext(), MessagesMainActivity.class);
                    finish();
                    messages_intent.putExtra("society_name", soc_name);
                    messages_intent.putExtra("flat_no", flat_no);
                    messages_intent.putExtra("username", username);
                    messages_intent.putExtra("name", name);
                    messages_intent.putExtra("admin", admin);
                    messages_intent.putExtra("unique_id", unique_id);
                    startActivity(messages_intent);
                    overridePendingTransition(0, 0);
                }else{
                    showMessageOptions("Oops!","You are not able to access Messaging!\n Contact us for more information",R.drawable.ic_error_dialog);
                }
                break;
//            case R.id.payments_menu:
//                Intent payments_intent = new Intent(ComplaintsActivity.this, Payments.class);
//                payments_intent.putExtra("society_name",soc_name);
//                payments_intent.putExtra("flat_no",flat_no);
//                payments_intent.putExtra("username",username);
//                payments_intent.putExtra("name",name);
//                payments_intent.putExtra("admin",admin);
//                payments_intent.putExtra("unique_id",unique_id);
//                finish();
//                startActivity(payments_intent);
//                overridePendingTransition(0, 0);
//
//                break;
            case R.id.complaints_menu:
                Intent complaints_intent = new Intent(getApplicationContext(), ComplaintsActivity.class);

                complaints_intent.putExtra("society_name",soc_name);
                complaints_intent.putExtra("flat_no",flat_no);
                complaints_intent.putExtra("username",username);
                complaints_intent.putExtra("name",name);
                complaints_intent.putExtra("admin",admin);
                complaints_intent.putExtra("unique_id",unique_id);
                startActivity(complaints_intent);
                overridePendingTransition(0,0);
                break;
            case R.id.services_menu:
                Intent services_intent = new Intent(getApplicationContext(), Services.class);
                finish();
                services_intent.putExtra("society_name",soc_name);
                services_intent.putExtra("flat_no",flat_no);
                services_intent.putExtra("username",username);
                services_intent.putExtra("name",name);
                services_intent.putExtra("admin",admin);
                services_intent.putExtra("unique_id",unique_id);
                startActivity(services_intent);
                overridePendingTransition(0,0);
                break;
            case R.id.settings_menu:
                Intent settings_intent = new Intent(getApplicationContext(), Settings.class);
                finish();
                settings_intent.putExtra("society_name",soc_name);
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",username);
                settings_intent.putExtra("name",name);
                settings_intent.putExtra("admin",admin);
                settings_intent.putExtra("unique_id",unique_id);
                startActivity(settings_intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.nearby_menu:
                Intent nearby_intent = new Intent(getApplicationContext(), Nearby.class);
                finish();
                nearby_intent.putExtra("society_name",soc_name);
                nearby_intent.putExtra("flat_no",flat_no);
                nearby_intent.putExtra("username",username);
                nearby_intent.putExtra("name",name);
                nearby_intent.putExtra("admin",admin);
                nearby_intent.putExtra("unique_id",unique_id);
                nearby_intent.putExtra("category_home", "");
                startActivity(nearby_intent);
                overridePendingTransition(0,0);
                break;

            case R.id.emergency_menu:
                Intent emergency_intent = new Intent(getApplicationContext(), Emergency.class);
                finish();
                emergency_intent.putExtra("society_name",soc_name);
                emergency_intent.putExtra("flat_no",flat_no);
                emergency_intent.putExtra("username",username);
                emergency_intent.putExtra("name",name);
                emergency_intent.putExtra("unique_id",unique_id);
                emergency_intent.putExtra("admin",admin);
                startActivity(emergency_intent);
                overridePendingTransition(0, 0);
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showMessageOptions(String title, String message, int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(AccountingMainActivity.this);
        dialog.setContentView(R.layout.dialog_new);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.i(TAG,"NEW DIALOG");

        Button btn_positive = dialog.findViewById(R.id.btn_positive);
        Button btn_negative = dialog.findViewById(R.id.btn_negative);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
        TextView dialog_message = dialog.findViewById(R.id.dialog_message);
        ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

        dialog_title.setText(title);
        dialog_message.setText(message);
        btn_negative.setVisibility(View.VISIBLE);
        btn_positive.setVisibility(View.VISIBLE);

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings_intent=new Intent(getApplicationContext(), ContactUsActivity.class);
                settings_intent.putExtra("society_name",soc_name);
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",username);
                settings_intent.putExtra("name",name);
                settings_intent.putExtra("admin",admin);
                startActivity(settings_intent);
                overridePendingTransition(0, 0);

            }
        });
        btn_positive.setText("Contact Us");

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        btn_negative.setText("OK");
        dialog_icon.setImageResource(image);
        dialog.show();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            overridePendingTransition(0, 0);
        }
    }
}

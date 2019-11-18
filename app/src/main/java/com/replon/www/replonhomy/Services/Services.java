package com.replon.www.replonhomy.Services;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.replon.www.replonhomy.Accounting.MMT.AccountingMainActivity;
import com.replon.www.replonhomy.Complaints.ComplaintsActivity;
import com.replon.www.replonhomy.Emergency.Emergency;
import com.replon.www.replonhomy.Messaging.MessagesMainActivity;
import com.replon.www.replonhomy.Nearby.Nearby;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.Settings;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Services extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "Services";
    GridView gridServices;
    ImageView back, user_icon, ham_icon;
    RelativeLayout relativeLayout;
    String admin;

    String soc_name, flat_no,name,username,unique_id;
    TextView tv_society_name,tv_flatno,tv_username,tv_name;
    Boolean dummy=FALSE;

    String servicesGrid[]={"Housekeeper","Electrician","Plumber","Grocery Delivery","Laundry Service","Milkman","Society HouseKeeping",
            "Society Security","GuestList","Vegetable Delivery", "Babysitter","Tuition Teacher","Fitness Instructor","Car Cleaner",
            "Gardener","Flower Delivery","Internet Service","Carpenter","AMC","Officials","Pest Control","Miscellaneous", "Society Camera"};

    int servicesGridImg[]={R.drawable.housekeepers,R.drawable.electricians,R.drawable.plumber,
            R.drawable.grocery_delivery, R.drawable.laundry,R.drawable.milkman,R.drawable.soc_housekeeping,
            R.drawable.security,R.drawable.soc_guestlist,R.drawable.vegetable_delivery,R.drawable.babysitter,R.drawable.tution_teacher,
            R.drawable.fitness_instructor,R.drawable.car_cleaner,R.drawable.gardner,R.drawable.flower_delivery,R.drawable.internet_services,
            R.drawable.carpenter,R.drawable.amc,R.drawable.officials,R.drawable.pestcontrol,R.drawable.misc,R.drawable.cctv};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), Services.this);

        setContentView(R.layout.activity_services);
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
        admin=getIntent().getExtras().getString("admin");

        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settings_intent = new Intent(Services.this, Settings.class);
                settings_intent.putExtra("society_name",soc_name);
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",username);
                settings_intent.putExtra("name",name);
                settings_intent.putExtra("admin",admin);
                settings_intent.putExtra("unique_id",unique_id);
                finish();
                startActivity(settings_intent);
                overridePendingTransition(0, 0);

            }
        });



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gridServices = (GridView) findViewById(R.id.gridViewServices);
        ServicesGridAdapter adapter = new ServicesGridAdapter(getApplicationContext(),servicesGridImg,servicesGrid);
        gridServices.setAdapter(adapter);
        gridServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(getApplicationContext(), "Clicked Picture: " + servicesGrid[position], Toast.LENGTH_SHORT).show();

                if(!servicesGrid[position].equals("Society Camera") && !servicesGrid[position].equals("GuestList")) {
                    Intent intent = new Intent(getApplicationContext(), ServicesItem.class);
                    intent.putExtra("service_title", servicesGrid[position]);
                    intent.putExtra("society_name",soc_name);
                    intent.putExtra("flat_no",flat_no);
                    intent.putExtra("username",username);
                    intent.putExtra("name",name);
                    intent.putExtra("admin",admin);
                    intent.putExtra("unique_id",unique_id);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                }

                else if (servicesGrid[position].equals("GuestList")){

                    Intent intent = new Intent(getApplicationContext(),ServicesGuestlistActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);

                }
                else {

//                    Snackbar snackbar = Snackbar
//                            .make(drawer, "Be Patient! "+servicesGrid[position]+" is Coming Soon !", Snackbar.LENGTH_LONG);
//                    snackbar.show();
                    showMessage("Be Patient!",servicesGrid[position]+" is Coming Soon !",R.drawable.ic_success_dialog);


                }
            }
        });

        back = (ImageView) findViewById(R.id.back_services);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home);
        tv_name = headerLayout.findViewById(R.id.name);
        tv_username=headerLayout.findViewById(R.id.username);

        tv_society_name=(TextView)findViewById(R.id.society_name);
        tv_flatno=(TextView)findViewById(R.id.flat_no);


        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        username = getIntent().getExtras().getString("username");
        unique_id=getIntent().getStringExtra("unique_id");

        if(soc_name.equals(getString(R.string.DUMMY_SOC))){
            dummy=TRUE;
        }


        tv_society_name.setText(soc_name);
        tv_flatno.setText(flat_no);
        tv_username.setText(username);
        tv_name.setText(name);


    }

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Services.this);
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
        btn_negative.setVisibility(View.GONE);
        btn_positive.setVisibility(View.GONE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    Intent messages_intent = new Intent(Services.this, MessagesMainActivity.class);
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
            case R.id.complaints_menu:
                Intent complaints_intent = new Intent(Services.this, ComplaintsActivity.class);
                finish();
                complaints_intent.putExtra("society_name",soc_name);
                complaints_intent.putExtra("flat_no",flat_no);
                complaints_intent.putExtra("username",username);
                complaints_intent.putExtra("name",name);
                complaints_intent.putExtra("admin",admin);
                complaints_intent.putExtra("unique_id",unique_id);
                startActivity(complaints_intent);
                overridePendingTransition(0, 0);
                break;
//            case R.id.payments_menu:
//                Intent payments_intent = new Intent(Services.this, Payments.class);
//                finish();
//                payments_intent.putExtra("society_name",soc_name);
//                payments_intent.putExtra("flat_no",flat_no);
//                payments_intent.putExtra("username",username);
//                payments_intent.putExtra("name",name);
//                payments_intent.putExtra("admin",admin);
//                payments_intent.putExtra("unique_id",unique_id);
//                startActivity(payments_intent);
//                overridePendingTransition(0, 0);
//                break;

            case R.id.accounting_menu:
                Intent acc_intent = new Intent(getApplicationContext(), AccountingMainActivity.class);
                finish();
                acc_intent.putExtra("society_name",soc_name);
                acc_intent.putExtra("flat_no",flat_no);
                acc_intent.putExtra("username",username);
                acc_intent.putExtra("name",name);
                acc_intent.putExtra("admin",  admin);
                acc_intent.putExtra("unique_id", unique_id);
                startActivity(acc_intent);
                overridePendingTransition(0,0);
                break;
            case R.id.settings_menu:
                Intent settings_intent = new Intent(Services.this,Settings.class);
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
                Intent nearby_intent = new Intent(Services.this, Nearby.class);
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
                emergency_intent.putExtra("admin",admin);
                emergency_intent.putExtra("unique_id",unique_id);
                startActivity(emergency_intent);
                overridePendingTransition(0, 0);
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showMessageOptions(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Services.this);
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
                Intent settings_intent=new Intent(getApplicationContext(), Services.class);
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


}

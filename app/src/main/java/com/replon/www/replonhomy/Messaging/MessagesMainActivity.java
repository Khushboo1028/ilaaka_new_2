package com.replon.www.replonhomy.Messaging;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.replonhomy.Accounting.MMT.AccountingMainActivity;
import com.replon.www.replonhomy.Complaints.ComplaintsActivity;
import com.replon.www.replonhomy.Emergency.Emergency;
import com.replon.www.replonhomy.Messaging.Fragments.ChatsFragment;
import com.replon.www.replonhomy.Messaging.Fragments.UsersFragment;
import com.replon.www.replonhomy.Messaging.Model.User;
import com.replon.www.replonhomy.Nearby.Nearby;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Services.Services;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.UserDataFirebase;
import com.replon.www.replonhomy.Settings.Settings;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG="MessagesMainActivity";

    TextView tv_society_name,tv_flatno,tv_username,tv_name;
    String soc_name, flat_no,name,username;

    ImageView user_icon,ham_icon;
    String admin;
    DrawerLayout drawer;

    ArrayList<UserDataFirebase> user_arraylist;

    //firebase and messaging
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    DocumentReference unique_id_reference;
    CircleImageView profile_image;
    String unique_id;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), MessagesMainActivity.this);

        setContentView(R.layout.activity_messages_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home);
        tv_name = headerLayout.findViewById(R.id.name);
        tv_username = headerLayout.findViewById(R.id.username);

        tv_society_name = (TextView) findViewById(R.id.society_name);
        tv_flatno = (TextView) findViewById(R.id.flat_no);


        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        username = getIntent().getExtras().getString("username");
        admin = getIntent().getStringExtra("admin");
        unique_id=getIntent().getStringExtra("unique_id");

        tv_society_name.setText(soc_name);
        tv_flatno.setText(flat_no);
        tv_username.setText(username);
        tv_name.setText(name);

        ham_icon = (ImageView) findViewById(R.id.ham_out);

        ham_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });

        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        user_icon = (ImageView) findViewById(R.id.user_profile);
        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settings_intent = new Intent(MessagesMainActivity.this, Settings.class);
                settings_intent.putExtra("society_name", soc_name);
                settings_intent.putExtra("flat_no", flat_no);
                settings_intent.putExtra("username", username);
                settings_intent.putExtra("name", name);
                settings_intent.putExtra("admin", admin);
                settings_intent.putExtra("unique_id",unique_id);
                finish();
                startActivity(settings_intent);
                overridePendingTransition(0, 0);

            }
        });

        //firebase references and main messaging content
        Log.i(TAG, "Society name is " + soc_name);
        Log.i(TAG, "unique_id is " + unique_id);

    if (unique_id != null && firebaseUser!=null) {


        reference = FirebaseDatabase.getInstance().getReference(unique_id).child(firebaseUser.getUid());
        profile_image = (CircleImageView) findViewById(R.id.profile_image);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                User user = dataSnapshot.getValue(User.class);

                if (user.getImageURL() != null && user.getImageURL().equals("")) {
                    profile_image.setImageResource(R.drawable.ic_service_default);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageURL()).placeholder(R.drawable.ic_service_default).into(profile_image);
                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        final ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        viewPagerAdapter.addFragment(new UsersFragment(), "Users");


        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();



        }


        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }


        public void addFragment(Fragment fragment,String title){
            Bundle bundle=new Bundle();
            bundle.putString("society_name",soc_name);
            bundle.putString("flat_no",flat_no);
            bundle.putString("name",name);
            bundle.putString("admin",admin);
            bundle.putString("username",username);
            bundle.putString("unique_id",unique_id);
            fragment.setArguments(bundle);
            fragments.add(fragment);
            titles.add(title);


        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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

        switch (id){
            case R.id.home_menu:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.complaints_menu:
                Intent complaints_intent = new Intent(MessagesMainActivity.this, ComplaintsActivity.class);
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
//                Intent payments_intent = new Intent(MessagesMainActivity.this, Payments.class);
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

            case R.id.services_menu:
                Intent services_intent = new Intent(MessagesMainActivity.this, Services.class);
                finish();
                services_intent.putExtra("society_name",soc_name);
                services_intent.putExtra("flat_no",flat_no);
                services_intent.putExtra("username",username);
                services_intent.putExtra("name",name);
                services_intent.putExtra("admin",admin);
                services_intent.putExtra("unique_id",unique_id);
                startActivity(services_intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.settings_menu:
                Intent settings_intent = new Intent(MessagesMainActivity.this, Settings.class);
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
                Intent nearby_intent = new Intent(MessagesMainActivity.this, Nearby.class);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void showSoonMessage(String title,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(MessagesMainActivity.this);
        dialog.setContentView(R.layout.dialog_new);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.i(TAG,"NEW DIALOG");

        Button btn_positive = dialog.findViewById(R.id.btn_positive);
        Button btn_negative = dialog.findViewById(R.id.btn_negative);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
        TextView dialog_message = dialog.findViewById(R.id.dialog_message);
        ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

        dialog_title.setText(title);
//        dialog_message.setText(message);
        dialog_message.setVisibility(View.GONE);
        btn_negative.setVisibility(View.GONE);
        btn_positive.setVisibility(View.GONE);
        dialog_icon.setImageResource(image);
        dialog.show();

    }
}

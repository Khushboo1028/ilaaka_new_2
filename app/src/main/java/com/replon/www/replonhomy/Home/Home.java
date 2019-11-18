package com.replon.www.replonhomy.Home;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.RemoteInput;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.replon.www.replonhomy.Accounting.MMT.AccountingMainActivity;
import com.replon.www.replonhomy.Accounting.MMT.MMTActivity;
import com.replon.www.replonhomy.Accounting.SocietyAccounting.SocietyAccountingActivity;
import com.replon.www.replonhomy.Complaints.ComplaintsActivity;
import com.replon.www.replonhomy.Complaints.NewComplaintsActivity;
import com.replon.www.replonhomy.Emergency.Emergency;
import com.replon.www.replonhomy.Login.Login;
import com.replon.www.replonhomy.Messaging.MessagesMainActivity;
import com.replon.www.replonhomy.Nearby.Nearby;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Services.ServicesGuestlistActivity;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Settings.PersonalInfo.ChangeEmailActivity;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.UserDataFirebase;
import com.replon.www.replonhomy.MinutesOfMeetings.MinutesOfMeetingActivity;
import com.replon.www.replonhomy.NoticeBoard.NoticeBoardActivity;
import com.replon.www.replonhomy.Services.Services;
import com.replon.www.replonhomy.Settings.Settings;
import com.replon.www.replonhomy.Voting.VotingActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG="Home";
    public static final String CHANNEL_ID="NOTIF";
    public static final String CHANNEL_NAME="Notifications";
    public static final String CHANNEL_DESC="This channel is for all notifications";


    DrawerLayout drawer;
    NavigationView navigationView;
    Toolbar toolbar=null;

    GridView gridQuick,gridNearby;

    ImageView ham_icon,user_icon;
    String soc_name, flat_no,name,username,wing,flat_no_withoutWing;
    ListenerRegistration getDataListener;

    Boolean isNewUser;
    //Complaint
    TextView complaint_date,complaint_subject,solved_unsolved;
    DocumentReference soc_ref;
    Boolean user_admin,dummy=FALSE;

    String token;
    //notices
    TextView home_notice_date,home_notice_subject;

    String replyLabel;
    RemoteInput remoteInput;
    private static String KEY_REPLY = "key_reply_message";

//    String quickActionsList[]={"Meetings","Electric Bill","Gas Bill","Voting","Messages","Emergency"};
    String quickActionsList[]={"Meetings","Voting","Messages","Emergency"};

//    int quickActionsListImg[]={R.drawable.ic_meetings,R.drawable.elec_bill_home,R.drawable.gas_bill_home,
//            R.drawable.voting_home, R.drawable.mess_home,R.drawable.sos_home};
    int quickActionsListImg[]={R.drawable.ic_meetings,
            R.drawable.voting_home, R.drawable.mess_home,R.drawable.sos_home};

    String nearbyHomeList[]={"Groceries","Restaurants","Pharmacies","Transport"};

    int nearbyHomeListImg[]={R.drawable.groc_home,R.drawable.rest_home,R.drawable.pharm_home, R.drawable.trans_home};

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    TextView generateComplaint;
    TextView tv_society_name,tv_flatno,tv_name,tv_username;
    RelativeLayout rel_complaints;

    ArrayList<UserDataFirebase> user_arraylist;

    UserDataFirebase obj = new UserDataFirebase();
    Boolean email_verified;

    //notice board
    RelativeLayout notice_board;
    String unique_id;

    //guestlist
    RelativeLayout relGuestlist;
    TextView guest_name,guest_in_time,guest_out_time;

     TextView checkedin_text;
    ImageView checkedin_image;
    String soc_unique_id;

    //Notifications
    Boolean goToNotice=false;
    Boolean goToMessaging=false;
    Boolean goToComplaints=false;
    Boolean goToMeetings=false;
    Boolean goToServices=false;
    Boolean goToVoting=false;
    Boolean goToGuestlist=false;
    Boolean goToMMT=false;
    Boolean goToAccounts=false;
    String actOnNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
//        adjustFontScale(getResources().getConfiguration());
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), Home.this);



        setContentView(R.layout.activity_home);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        isNewUser=getIntent().getBooleanExtra("isNewUSer",false);
        ham_icon = (ImageView) findViewById(R.id.ham_out);

        relGuestlist=(RelativeLayout)findViewById(R.id.relGuestlist);
        guest_name=(TextView)findViewById(R.id.guest_name);
        guest_in_time=(TextView)findViewById(R.id.guest_in_time);
        guest_out_time=(TextView)findViewById(R.id.guest_out_time);

        checkedin_text=(TextView)findViewById(R.id.checkedin_text);
        checkedin_image=(ImageView)findViewById(R.id.checkedin_image);
        checkedin_image.setVisibility(View.GONE);
        checkedin_text.setVisibility(View.GONE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,"Notifications",NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }



        relGuestlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nearby_intent = new Intent(Home.this, ServicesGuestlistActivity.class);
                nearby_intent.putExtra("society_name",obj.getSociety_name());
                nearby_intent.putExtra("flat_no",flat_no);
                nearby_intent.putExtra("username",obj.getUsername());
                nearby_intent.putExtra("name",obj.getName());
                nearby_intent.putExtra("admin",  user_admin.toString());
                startActivity(nearby_intent);
                overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
            }
        });

        if(isNewUser){
            showMessage("Success!", "A verification email has been sent to you. Please follow the instructions in the email to verify your email.", R.drawable.ic_success_dialog);
        }

        Log.i(TAG,"new User?:" +isNewUser);
        ham_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });
        user_icon = (ImageView)findViewById(R.id.user_profile);

        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settings_intent = new Intent(Home.this, Settings.class);
                settings_intent.putExtra("society_name",soc_name);
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",obj.getUsername());
                settings_intent.putExtra("name",obj.getName());
                settings_intent.putExtra("admin",user_admin.toString());
                settings_intent.putExtra("unique_id", obj.getUnique_id());
                startActivity(settings_intent);
                overridePendingTransition(0, 0);

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home);
        tv_name = headerLayout.findViewById(R.id.name);
        tv_username=headerLayout.findViewById(R.id.username);

        home_notice_date=(TextView)findViewById(R.id.home_notice_date);
        home_notice_subject=(TextView)findViewById(R.id.home_notice);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH );
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }



        gridQuick = (GridView) findViewById(R.id.gridView);
        QuickActionsGridAdapter adapter = new QuickActionsGridAdapter(getApplicationContext(),quickActionsListImg,quickActionsList);
        gridQuick.setAdapter(adapter);
        gridQuick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               // Toast.makeText(getApplicationContext(), "Clicked Picture: " + quickActionsList[position], Toast.LENGTH_SHORT).show();

                if (quickActionsList[position].equals("Meetings")){
                    Intent mom_intent = new Intent(getApplicationContext(), MinutesOfMeetingActivity.class);
                    mom_intent.putExtra("society_name",obj.getSociety_name());
                    mom_intent.putExtra("flat_no",flat_no);
                    mom_intent.putExtra("username",obj.getUsername());
                    mom_intent.putExtra("name",obj.getName());
                    mom_intent.putExtra("admin",  user_admin.toString());
                    startActivity(mom_intent);
                    overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                }

                if (quickActionsList[position].equals("Messages")) {
                    if(!dummy) {
                        Intent messages_intent = new Intent(Home.this, MessagesMainActivity.class);
                        messages_intent.putExtra("society_name", obj.getSociety_name());
                        messages_intent.putExtra("flat_no", flat_no);
                        messages_intent.putExtra("username", obj.getUsername());
                        messages_intent.putExtra("name", obj.getName());
                        messages_intent.putExtra("admin", user_admin.toString());
                        messages_intent.putExtra("unique_id", obj.getUnique_id());
                        startActivity(messages_intent);
                        overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                    }else{
                        showMessageOptions("Oops!","You are not able to access Messaging!\n Contact us for more information",R.drawable.ic_error_dialog);

                    }
                }

                if (quickActionsList[position].equals("Emergency")) {

                    Intent emergency_intent = new Intent(Home.this,Emergency.class);
                    emergency_intent.putExtra("society_name",obj.getSociety_name());
                    emergency_intent.putExtra("flat_no",flat_no);
                    emergency_intent.putExtra("username",obj.getUsername());
                    emergency_intent.putExtra("name",obj.getName());
                    emergency_intent.putExtra("admin",  user_admin.toString());
//                settings_intent.putExtra("admin",  "true");
                    emergency_intent.putExtra("unique_id", obj.getUnique_id());
                    startActivity(emergency_intent);
                    overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);



                }

                if (quickActionsList[position].equals("Voting")) {

                    Intent intent = new Intent(Home.this,VotingActivity.class);
                    intent.putExtra("society_name",obj.getSociety_name());
                    intent.putExtra("flat_no",flat_no);
                    intent.putExtra("username",obj.getUsername());
                    intent.putExtra("name",obj.getName());
                    intent.putExtra("admin",  user_admin.toString());
                    intent.putExtra("unique_id", obj.getUnique_id());
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                }

                if (quickActionsList[position].equals("Gas Bill")) {

                    showSoonMessage("Coming Soon",R.drawable.ic_success_dialog);

                }

                if (quickActionsList[position].equals("Electric Bill")) {

                    showSoonMessage("Coming Soon",R.drawable.ic_success_dialog);

                }
            }
        });

        gridNearby = (GridView) findViewById(R.id.gridNearby);
        QuickActionsGridAdapter adapter2 = new QuickActionsGridAdapter(getApplicationContext(),nearbyHomeListImg,nearbyHomeList);
        gridNearby.setAdapter(adapter2);
        gridNearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (!isNetworkAvailable()){

                    final Dialog dialog = new Dialog(Home.this);
                    dialog.setContentView(R.layout.dialog_new);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    Log.i(TAG,"NEW DIALOG");

                    Button btn_positive = dialog.findViewById(R.id.btn_positive);
                    Button btn_negative = dialog.findViewById(R.id.btn_negative);
                    TextView dialog_title = dialog.findViewById(R.id.dialog_title);
                    TextView dialog_message = dialog.findViewById(R.id.dialog_message);
                    ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

                    dialog_title.setText("Internet Unavailable");
                    dialog_message.setText("Poor network connectivity detected! Please check your internet connection");
//        btn_negative.setVisibility(View.GONE);
//        btn_positive.setVisibility(View.GONE);

                    btn_positive.setText("OK");
                    btn_negative.setText("Go to Settings");
                    btn_positive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    btn_negative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                            startActivity(myIntent);
                        }
                    });
                    dialog_icon.setImageResource(R.drawable.ic_no_internet);
                    dialog.show();
                }else {

                    Intent nearby_intent = new Intent(Home.this, Nearby.class);
                    nearby_intent.putExtra("society_name", obj.getSociety_name());
                    nearby_intent.putExtra("flat_no", flat_no);
                    nearby_intent.putExtra("username", obj.getUsername());
                    nearby_intent.putExtra("name", obj.getName());
                    nearby_intent.putExtra("admin", user_admin.toString());
                    nearby_intent.putExtra("unique_id",obj.getUnique_id());
                    nearby_intent.putExtra("category_home", nearbyHomeList[position]);
                    startActivity(nearby_intent);
                }


            }
        });





        rel_complaints=(RelativeLayout)findViewById(R.id.rel_complaints);
        rel_complaints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent complaints_intent = new Intent(Home.this, ComplaintsActivity.class);
                complaints_intent.putExtra("society_name",obj.getSociety_name());
                complaints_intent.putExtra("flat_no",flat_no);
                complaints_intent.putExtra("username",obj.getUsername());
                complaints_intent.putExtra("name",obj.getName());
                complaints_intent.putExtra("admin",  user_admin.toString());
                complaints_intent.putExtra("unique_id", obj.getUnique_id());
                startActivity(complaints_intent);
                overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
            }
        });


        setupFirebaseAuth();
        getData();


        generateComplaint=(TextView)findViewById(R.id.home_comp_gen);
        generateComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intent=new Intent(getApplicationContext(), NewComplaintsActivity.class);
//                intent.putExtra("admin",  user_admin.toString());
//                intent.putExtra("society_name",soc_name);
//                intent.putExtra("flat_no",flat_no);
//                intent.putExtra("username",username);
//                intent.putExtra("name",obj.getName());
//                startActivity(intent);
//                overridePendingTransition(0,0);
                Intent intent = new Intent(Home.this, NewComplaintsActivity.class);
                intent.putExtra("society_name",obj.getSociety_name());
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("username",obj.getUsername());
                intent.putExtra("name",obj.getName());
                intent.putExtra("admin",  user_admin.toString());
                intent.putExtra("unique_id", obj.getUnique_id());
                startActivity(intent);


            }
        });

        //notice_board
        notice_board=(RelativeLayout)findViewById(R.id.notice_board_rel);
        notice_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), NoticeBoardActivity.class);
                intent.putExtra("admin",  user_admin.toString());
                intent.putExtra("society_name",soc_name);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("username",username);
                intent.putExtra("name",obj.getName());
                startActivity(intent);
                overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
            }
        });

        this.setFinishOnTouchOutside(true);
        //mAuth.signOut();




    }



    private void getData() {

        tv_society_name=(TextView)findViewById(R.id.society_name);
        tv_flatno=(TextView)findViewById(R.id.flat_no);


        FirebaseFirestore db=FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

        if(currentFirebaseUser!=null) {


            final String user_id = currentFirebaseUser.getUid();

            String user = getString(R.string.USER);


            DocumentReference docRef = db.collection(user).document(user_id);

            user_arraylist = new ArrayList<>();

            getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                            ? "Local" : "Server";

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, source + " Our data is here data: " + snapshot.getData());

                        String username_fb = snapshot.get("username").toString();
                        Log.i(TAG, "Username is " + username_fb);

                        String flat_no_fb="",name="",society_name="",username="";
                        Timestamp date_created;
                        Boolean admin;

                        if(snapshot.get("flat_no")!=null) {
                             flat_no_fb=snapshot.getString("flat_no");
                        }
                        if(snapshot.get("name")!=null) {
                             name=snapshot.getString("name");
                        }
                        if(snapshot.get("date_created")!=null) {
                             date_created=snapshot.getTimestamp("date_created");
                        }
                        if(snapshot.get("admin")!=null) {
                             admin=snapshot.getBoolean("admin");
                        }
                        if(snapshot.get("society_name")!=null) {

                             society_name=snapshot.getString("society_name");
                             if(society_name.equals(getString(R.string.DUMMY_SOC))){
                                 dummy=TRUE;
                             }
                             Log.i(TAG,"dummy? "+dummy);
                        }

                        if(snapshot.get("username")!=null) {
                             username=snapshot.getString("username");
                        }
                        if(snapshot.get("society_id")!=null) {
                            DocumentReference society_id=snapshot.getDocumentReference("society_id");
                        }

                        if(snapshot.get("unique_id")!=null) {
                            String unique_id=snapshot.getString("unique_id");
                        }

                        obj = new UserDataFirebase(snapshot.getString("flat_no"),
                                 snapshot.get("name").toString(),
                                (Timestamp) snapshot.get("date_created"),
                                Boolean.parseBoolean(snapshot.get("admin").toString()),
                                snapshot.get("society_name").toString(),
                                snapshot.get("username").toString(),
                                (DocumentReference) snapshot.get("society_id"),
                                snapshot.getString("unique_id")
                        );


                        Log.i(TAG, "Soc_id is " + obj.getSoc_id().toString());

                        soc_name = obj.getSociety_name();
                        flat_no = obj.getFlat_no();
                        name = obj.getName();
                        username = obj.getUsername();
                        soc_ref = obj.getSoc_id();
                        user_admin = obj.getAdmin();
                        unique_id=obj.getUnique_id();

                        Log.i(TAG,"unique id is "+unique_id);


                        tv_society_name.setText(soc_name);
                        tv_flatno.setText(flat_no);
                        tv_name.setText(name);
                        tv_username.setText(currentFirebaseUser.getEmail());

                            if(getIntent().hasExtra("click_action")) {
                                actOnNotification = getIntent().getStringExtra("click_action");
                                Log.i(TAG, "ACTION Home: " + actOnNotification);

                                if (actOnNotification.equals("notice")) {
                                    goToNotice = true;
                                } else if (actOnNotification.equals("messages")) {
                                    goToMessaging = true;
                                } else if (actOnNotification.equals("services")) {
                                    goToServices = true;
                                } else if (actOnNotification.equals("meetings")) {
                                    goToMeetings = true;
                                } else if (actOnNotification.equals("voting")) {
                                    goToVoting = true;
                                } else if (actOnNotification.equals("service_guestlist")) {
                                    goToGuestlist = true;
                                }
                                else if (actOnNotification.equals("complaint")) {
                                    goToComplaints = true;
                                }
                                else if(actOnNotification.equals("mmt")){
                                    goToMMT=true;
                                }
                                else if(actOnNotification.equals("accounts")){
                                    goToAccounts=true;
                                }
                                getIntent().removeExtra("click_action");
                                goToWherever();
                            }





                        //for complaints
                        solved_unsolved = (TextView) findViewById(R.id.solved_unsolved);
                        complaint_date = (TextView) findViewById(R.id.complaints_date);
                        complaint_subject = (TextView) findViewById(R.id.complaint_subject);

                        if (user_admin) {
                        getDataListener=obj.getSoc_id().collection("complaints")
                                .orderBy("date", Query.Direction.DESCENDING)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                                        if (e != null) {
                                            Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }

                                        if (snapshot.getDocuments().isEmpty()) {
                                            solved_unsolved.setVisibility(View.GONE);
                                            complaint_date.setVisibility(View.GONE);
                                            complaint_subject.setText("No Complaints");

                                        } else {

                                            Log.d(TAG, " Index 0 data : " + snapshot.getDocuments().get(0).get("solved"));

                                            complaint_subject.setText(snapshot.getDocuments().get(0).get("subject").toString());

                                            if (snapshot.getDocuments().get(0).get("solved").toString().equals("false")) {
                                                solved_unsolved.setBackgroundResource(R.drawable.unsolved_border);
                                                solved_unsolved.setText("Unsolved");
                                                solved_unsolved.setTextColor(Color.RED);

                                            } else {
                                                solved_unsolved.setBackgroundResource(R.drawable.solved_border);
                                                solved_unsolved.setText("Solved");
                                                solved_unsolved.setTextColor(getResources().getColor(R.color.greenSolved));
                                            }


                                            Timestamp last_date_created = (Timestamp) snapshot.getDocuments().get(0).get("date");
                                            Date date_on_Last_complaint = last_date_created.toDate();
                                            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
                                            String date = sfd.format(date_on_Last_complaint);
                                            complaint_date.setText(date);
                                        }


                                    }
                                });
                        }else{
                            getDataListener=obj.getSoc_id().collection("complaints")
                                    .whereEqualTo("user_id",user_id)
                                    .orderBy("date", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                                            if (e != null) {
                                                Log.w(TAG, "Listen failed.", e);
                                                return;
                                            }

                                            if (snapshot.getDocuments().isEmpty()) {
                                                solved_unsolved.setVisibility(View.GONE);
                                                complaint_date.setVisibility(View.GONE);
                                                complaint_subject.setText("No Complaints");

                                            } else {

                                                Log.d(TAG, " Index 0 data : " + snapshot.getDocuments().get(0).get("solved"));

                                                complaint_subject.setText(snapshot.getDocuments().get(0).get("subject").toString());

                                                if (snapshot.getDocuments().get(0).get("solved").toString().equals("false")) {
                                                    solved_unsolved.setBackgroundResource(R.drawable.unsolved_border);
                                                    solved_unsolved.setText("Unsolved");
                                                    solved_unsolved.setTextColor(Color.RED);

                                                } else {
                                                    solved_unsolved.setBackgroundResource(R.drawable.solved_border);
                                                    solved_unsolved.setText("Solved");
                                                    solved_unsolved.setTextColor(Color.GREEN);
                                                }


                                                Timestamp last_date_created = (Timestamp) snapshot.getDocuments().get(0).get("date");
                                                Date date_on_Last_complaint = last_date_created.toDate();
                                                SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
                                                String date = sfd.format(date_on_Last_complaint);
                                                complaint_date.setText(date);
                                            }


                                        }
                                    });
                        }



                        getDataListener=obj.getSoc_id().collection("notices")
                                .orderBy("date", Query.Direction.DESCENDING)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                                        if (e != null) {
                                            Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }

                                        if (snapshot.getDocuments().isEmpty()) {
                                            home_notice_subject.setText("No Notices");
                                            home_notice_date.setVisibility(View.GONE);

                                        } else {


                                            home_notice_date.setVisibility(View.VISIBLE);
                                            home_notice_subject.setText(snapshot.getDocuments().get(0).get("subject").toString());



                                            Timestamp last_date_created=(Timestamp)snapshot.getDocuments().get(0).get("date");
                                            Date date_on_Last_complaint=last_date_created.toDate();
                                            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yyyy");
                                            String date = sfd.format(date_on_Last_complaint);
                                            home_notice_date.setText(date);
                                        }


                                    }
                                });



                        getDataListener=obj.getSoc_id().collection("guestlist")
                                .orderBy("date_created", Query.Direction.DESCENDING)
                                .whereArrayContains("flat_no",obj.getFlat_no())
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                                        if (e != null) {
                                            Log.w(TAG, "Listen failed.", e);
                                            return;
                                        }

                                        if (snapshot.getDocuments().isEmpty() || snapshot.isEmpty()) {
                                           guest_out_time.setVisibility(View.GONE);
                                           guest_in_time.setVisibility(View.GONE);
                                           guest_name.setVisibility(View.GONE);
                                           checkedin_text.setVisibility(View.GONE);
                                           checkedin_image.setVisibility(View.VISIBLE);


                                        } else {

                                            guest_name.setText(snapshot.getDocuments().get(0).get("name").toString());


                                            Timestamp last_date_created=(Timestamp)snapshot.getDocuments().get(0).get("date_created");
                                            Date date1=last_date_created.toDate();
                                            SimpleDateFormat sfd = new SimpleDateFormat("dd/MM/yy, hh:mm a");
                                            String date = sfd.format(date1);
                                            guest_in_time.setText(date);

                                            if(snapshot.getDocuments().get(0).get("checkout_time")==null){
                                                guest_out_time.setText("---NA---");
                                                checkedin_image.setVisibility(View.GONE);
                                                checkedin_text.setVisibility(View.VISIBLE);
                                            }else{
                                                Timestamp ts_checkout_time=(Timestamp)snapshot.getDocuments().get(0).get("checkout_time");
                                                Date checkout_time=ts_checkout_time.toDate();
                                                String date_out = sfd.format(checkout_time);
                                                guest_out_time.setText(date_out);
                                                checkedin_image.setVisibility(View.VISIBLE);
                                                checkedin_text.setVisibility(View.GONE);
                                                Log.i(TAG,"IN CHECKED OUT");
                                            }
                                        }


                                    }
                                });

                    } else {
                        Log.d(TAG, source + " data: null");
                    }
                }
            });



        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (id) {
            case R.id.messages_menu:
            if (!dummy) {


                Intent messages_intent = new Intent(Home.this, MessagesMainActivity.class);
                messages_intent.putExtra("society_name", obj.getSociety_name());
                messages_intent.putExtra("flat_no", flat_no);
                messages_intent.putExtra("username", obj.getUsername());
                messages_intent.putExtra("name", obj.getName());
                messages_intent.putExtra("admin", user_admin.toString());
                messages_intent.putExtra("unique_id", obj.getUnique_id());

                startActivity(messages_intent);
                overridePendingTransition(0, 0);
            }else{
                showMessageOptions("Oops!","You are not able to access Messaging!\n Contact us for more information",R.drawable.ic_error_dialog);

            }
                break;
            case R.id.complaints_menu:
                Intent complaints_intent = new Intent(Home.this, ComplaintsActivity.class);

                complaints_intent.putExtra("society_name",obj.getSociety_name());
                complaints_intent.putExtra("flat_no",flat_no);
                complaints_intent.putExtra("username",obj.getUsername());
                complaints_intent.putExtra("name",obj.getName());
                complaints_intent.putExtra("admin",  user_admin.toString());
                complaints_intent.putExtra("unique_id", obj.getUnique_id());
                startActivity(complaints_intent);
                overridePendingTransition(0,0);
                break;
//            case R.id.payments_menu:
//                Intent payments_intent = new Intent(Home.this, Payments.class);
//
//                payments_intent.putExtra("society_name",obj.getSociety_name());
//                payments_intent.putExtra("flat_no",flat_no);
//                payments_intent.putExtra("username",obj.getUsername());
//                payments_intent.putExtra("name",obj.getName());
//                payments_intent.putExtra("admin",  user_admin.toString());
//                payments_intent.putExtra("unique_id", obj.getUnique_id());
//
//                startActivity(payments_intent);
//                overridePendingTransition(0,0);
//                break;

            case R.id.accounting_menu:
                Intent acc_intent = new Intent(Home.this, AccountingMainActivity.class);

                acc_intent.putExtra("society_name",obj.getSociety_name());
                acc_intent.putExtra("flat_no",flat_no);
                acc_intent.putExtra("username",obj.getUsername());
                acc_intent.putExtra("name",obj.getName());
                acc_intent.putExtra("admin",  user_admin.toString());
                acc_intent.putExtra("unique_id", obj.getUnique_id());
                startActivity(acc_intent);
                overridePendingTransition(0,0);
                break;

            case R.id.services_menu:
                Intent services_intent = new Intent(Home.this, Services.class);

                services_intent.putExtra("society_name",obj.getSociety_name());
                services_intent.putExtra("flat_no",flat_no);
                services_intent.putExtra("username",obj.getUsername());
                services_intent.putExtra("name",obj.getName());
                services_intent.putExtra("admin",  user_admin.toString());
                services_intent.putExtra("unique_id", obj.getUnique_id());

                startActivity(services_intent);
                overridePendingTransition(0,0);
                break;
            case R.id.nearby_menu:

                    Intent nearby_intent = new Intent(Home.this, Nearby.class);
                    nearby_intent.putExtra("society_name", obj.getSociety_name());
                    nearby_intent.putExtra("flat_no", flat_no);
                    nearby_intent.putExtra("username", obj.getUsername());
                    nearby_intent.putExtra("name", obj.getName());
                    nearby_intent.putExtra("admin", user_admin.toString());
                    nearby_intent.putExtra("category_home", "");
                    startActivity(nearby_intent);

                    overridePendingTransition(0,0);

                break;
            case R.id.settings_menu:
                Intent settings_intent = new Intent(Home.this,Settings.class);
                settings_intent.putExtra("society_name",obj.getSociety_name());
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",obj.getUsername());
                settings_intent.putExtra("name",obj.getName());
                settings_intent.putExtra("admin",  user_admin.toString());
//                settings_intent.putExtra("admin",  "true");
                settings_intent.putExtra("unique_id", obj.getUnique_id());
                startActivity(settings_intent);
                overridePendingTransition(0, 0);
                break;

            case R.id.emergency_menu:
                Intent emergency_intent = new Intent(Home.this,Emergency.class);
                emergency_intent.putExtra("society_name",obj.getSociety_name());
                emergency_intent.putExtra("flat_no",flat_no);
                emergency_intent.putExtra("username",obj.getUsername());
                emergency_intent.putExtra("name",obj.getName());
                emergency_intent.putExtra("admin",  user_admin.toString());
//                settings_intent.putExtra("admin",  "true");
                emergency_intent.putExtra("unique_id", obj.getUnique_id());
                startActivity(emergency_intent);
                overridePendingTransition(0, 0);
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    //checks to see if the @param user is logged in
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG,"Checking if user is logged in");
        String user_key = getString(R.string.USER);
        if(user==null){
            Intent intent=new Intent(getApplicationContext(), Login.class);
            startActivity(intent);

        }else{
            FirebaseFirestore db=FirebaseFirestore.getInstance();
            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String user_id=currentFirebaseUser.getUid();

            DocumentReference doc_ref=db.collection(user_key).document(user_id);
            doc_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(!documentSnapshot.exists()){
                        Log.i(TAG,"User is not present");
                        mAuth.signOut();


                    }else{

                        if(!dummy) {


                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if (task.isSuccessful()) {
                                                token = task.getResult().getToken();

                                                Log.i(TAG, "token is " + token);

                                                final DocumentReference doc_id = FirebaseFirestore.getInstance().collection(getString(R.string.USER)).document(currentFirebaseUser.getUid());


                                                doc_id.update("token",FieldValue.arrayUnion(token)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.i(TAG,"Token updated");
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i(TAG,"An error occurred : "+e.getMessage());
                                                    }
                                                });

                                            } else {
                                                Log.i(TAG, "An error occurred " + task.getException().getMessage());
                                            }
                                        }
                                    });

                        }

                    }
                }
            });






        }

    }

    private void setupFirebaseAuth() {
        Log.d(TAG,"onAuthStateChanged:Setting up firebase auth");

        // Obtain the FirebaseAnalytics instance.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = mAuth.getCurrentUser();


                //checks if the user is logged in
                checkCurrentUser(currentUser);
                if (currentUser != null) {
                    //user is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in");
                    currentUser.reload();


                    email_verified = currentUser.isEmailVerified();
                    Log.i(TAG, "email verified: " + currentUser.isEmailVerified());

                    if (!email_verified) {
                        int count = 0;

                        String filename = "email_verify_counter1";
                        String fileContents = String.valueOf(count);
                        FileOutputStream outputStream;
                        //to read the file
                        BufferedReader input = null;
                        File file = null;
                        try {
                            file = new File(getFilesDir(), filename); // Pass getFilesDir() and "MyFile" to read file

                            input = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                            String line;
                            StringBuffer buffer = new StringBuffer();
                            while ((line = input.readLine()) != null) {
                                buffer.append(line);
                            }

                            Log.d(TAG, "File contents are " + buffer.toString());

                            String data = buffer.toString();
                            count = Integer.parseInt(data);
                            count++;

                            if (count >= 30) {
                                dialogShowWithAppClose();
                            } else if (count % 5 == 0) {
                                dialogShow();
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        //to write in file


                        try {
                            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                            outputStream.write(String.valueOf(count).getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }






                }
                else{
                    //user is signed out
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }
            }
        };

    }

      public void dialogShow() {

        String changeEmail = "Change Email";
        String resendVerificationEmail = "Resend verification email";
        String dismiss = "Dismiss";
        final CharSequence[] items = {changeEmail, resendVerificationEmail,dismiss};
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        //builder.setTitle(addPhoto);
        final String finalChangeEmail = changeEmail;
        final String finalResendVerificationEmail = resendVerificationEmail;
        final String finaldismiss=dismiss;

        builder.setTitle("Email Not Verified ! Please Verify Email or Choose from the following..");
//        builder.setMessage("Please Verify Email or Choose from the following options..");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(finalChangeEmail)) {
                    Intent intent=new Intent(getApplicationContext(), ChangeEmailActivity.class);
                    intent.putExtra("currentName",name);
                    intent.putExtra("admin",obj.getAdmin());
                    intent.putExtra("username",obj.getUsername());
                    intent.putExtra("society_name",soc_name);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }
                else if (items[item].equals(finalResendVerificationEmail)) {
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser=mAuth.getCurrentUser();
                    currentUser.sendEmailVerification()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG,"Verification email sent");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG,"An error occurred "+e.getMessage());
                                }
                            });

                }else if((items[item].equals(finaldismiss))){
                    dialog.dismiss();

                }
            }
        });

        builder.show();
//        builder.setCancelable(false);
//        this.setFinishOnTouchOutside(true);



    }

    public void dialogShowWithAppClose() {

        String changeEmail = "Change Email";
        String resendVerificationEmail = "Resend verification email";
        String dismiss = "Dismiss";
        final CharSequence[] items = {changeEmail, resendVerificationEmail,dismiss};
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        //builder.setTitle(addPhoto);
        final String finalChangeEmail = changeEmail;
        final String finalResendVerificationEmail = resendVerificationEmail;
        final String finaldismiss=dismiss;

        builder.setTitle("Email Not Verified ! Please Verify Email or Choose from the following..");

        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(finalChangeEmail)) {
                    Intent intent=new Intent(getApplicationContext(), ChangeEmailActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                }
                else if (items[item].equals(finalResendVerificationEmail)) {
                    mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser=mAuth.getCurrentUser();
                    currentUser.sendEmailVerification();

                }else if((items[item].equals(finaldismiss))){
                   // dialog.dismiss();
//                    finish();
//                    System.exit(0);
                }
            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                finish();
//                System.exit(0);
            }
        });
        builder.show();
//        builder.setCancelable(false);
//        this.setFinishOnTouchOutside(true);


    }


    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        checkCurrentUser(currentUser);





    }
    @Override
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if (getDataListener!= null) {
            getDataListener.remove();
            getDataListener = null;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG,"ON RESUME TRIGGERED");
        Log.i(TAG,"getting INTENT FROM NOTIF  "+ getIntent().hasExtra("action_notification"));

        getData();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();


    }

    public void showMessageOptions(String title, String message, int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Home.this);
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
                settings_intent.putExtra("admin",user_admin.toString());
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

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Home.this);
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

    public void showSoonMessage(String title,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Home.this);
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




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void goToWherever(){

        if(goToNotice){
            goToNotice=false;
            Intent intent=new Intent(getApplicationContext(), NoticeBoardActivity.class);
            intent.putExtra("admin",  user_admin.toString());
            intent.putExtra("society_name",soc_name);
            intent.putExtra("flat_no",flat_no);
            intent.putExtra("username",username);
            intent.putExtra("name",obj.getName());
            startActivity(intent);
            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);

        }
        else if(goToMessaging) {
            goToMessaging = false;
            Intent messages_intent = new Intent(Home.this, MessagesMainActivity.class);
            messages_intent.putExtra("society_name", obj.getSociety_name());
            messages_intent.putExtra("flat_no", flat_no);
            messages_intent.putExtra("username", obj.getUsername());
            messages_intent.putExtra("name", obj.getName());
            messages_intent.putExtra("admin",obj.getAdmin().toString());
            messages_intent.putExtra("unique_id", obj.getUnique_id());
            startActivity(messages_intent);
            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
        }
        else if(goToComplaints) {
            goToComplaints = false;
            Intent complaints_intent = new Intent(Home.this, ComplaintsActivity.class);
            complaints_intent.putExtra("society_name",obj.getSociety_name());
            complaints_intent.putExtra("flat_no",flat_no);
            complaints_intent.putExtra("username",obj.getUsername());
            complaints_intent.putExtra("name",obj.getName());
            complaints_intent.putExtra("admin",  user_admin.toString());
            complaints_intent.putExtra("unique_id", obj.getUnique_id());
            startActivity(complaints_intent);
            overridePendingTransition(0,0);
        }
        else if(goToMeetings){
            goToMeetings=false;
            Intent mom_intent = new Intent(getApplicationContext(), MinutesOfMeetingActivity.class);
            mom_intent.putExtra("society_name",obj.getSociety_name());
            mom_intent.putExtra("flat_no",flat_no);
            mom_intent.putExtra("username",obj.getUsername());
            mom_intent.putExtra("name",obj.getName());
            mom_intent.putExtra("admin",  user_admin.toString());
            startActivity(mom_intent);
            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
        }
        else if(goToGuestlist){
            goToGuestlist=false;
            Intent nearby_intent = new Intent(Home.this, ServicesGuestlistActivity.class);
            nearby_intent.putExtra("society_name",obj.getSociety_name());
            nearby_intent.putExtra("flat_no",flat_no);
            nearby_intent.putExtra("username",obj.getUsername());
            nearby_intent.putExtra("name",obj.getName());
            nearby_intent.putExtra("admin",  user_admin.toString());
            startActivity(nearby_intent);
            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
        }
        else if(goToVoting){
            goToVoting=false;
            Intent intent = new Intent(Home.this,VotingActivity.class);
            intent.putExtra("society_name",obj.getSociety_name());
            intent.putExtra("flat_no",flat_no);
            intent.putExtra("username",obj.getUsername());
            intent.putExtra("name",obj.getName());
            intent.putExtra("admin",  user_admin.toString());
            intent.putExtra("unique_id", obj.getUnique_id());
            startActivity(intent);
            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);

        }
        else if(goToServices){
            goToServices=false;
            Intent services_intent = new Intent(Home.this, Services.class);
            services_intent.putExtra("society_name",obj.getSociety_name());
            services_intent.putExtra("flat_no",flat_no);
            services_intent.putExtra("username",obj.getUsername());
            services_intent.putExtra("name",obj.getName());
            services_intent.putExtra("admin",  user_admin.toString());
            services_intent.putExtra("unique_id", obj.getUnique_id());
            startActivity(services_intent);
            overridePendingTransition(0,0);

        }
        else if(goToAccounts){
            goToAccounts=false;
            Intent intent = new Intent(Home.this, SocietyAccountingActivity.class);
            intent.putExtra("society_name",obj.getSociety_name());
            intent.putExtra("flat_no",flat_no);
            intent.putExtra("username",obj.getUsername());
            intent.putExtra("name",obj.getName());
            intent.putExtra("admin",  user_admin.toString());
            intent.putExtra("unique_id", obj.getUnique_id());
            startActivity(intent);
            overridePendingTransition(0,0);

        }
        else if (goToMMT){
            goToMMT=false;
            Intent intent = new Intent(Home.this, MMTActivity.class);
            intent.putExtra("society_name",obj.getSociety_name());
            intent.putExtra("flat_no",flat_no);
            intent.putExtra("username",obj.getUsername());
            intent.putExtra("name",obj.getName());
            intent.putExtra("admin",  user_admin.toString());
            intent.putExtra("unique_id", obj.getUnique_id());
            startActivity(intent);
            overridePendingTransition(0,0);

        }

    }


}
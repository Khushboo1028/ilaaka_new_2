package com.replon.www.replonhomy.Complaints;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.replon.www.replonhomy.Accounting.MMT.AccountingMainActivity;
import com.replon.www.replonhomy.Emergency.Emergency;
import com.replon.www.replonhomy.Messaging.MessagesMainActivity;
import com.replon.www.replonhomy.Nearby.Nearby;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Services.Services;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Settings.Settings;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.SwipeToDelete;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ComplaintsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String TAG="ComplaintsActivity";

    ImageView ham_icon, user_icon;
    String soc_name, flat_no,name,username;
    TextView tv_society_name,tv_flatno,tv_name,tv_username;

    //filters

    ImageView filter_icon;

    Calendar cldrStart;
    Date dateStart, dateEnd;
    DatePickerDialog pickerDialog;
    ListenerRegistration getDataListener;

    EditText searchView;

    private RecyclerView recyclerView;
    private ComplaintsAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView  generate;
    RelativeLayout relativeLayout;
    ImageView back,generate_img;
    Boolean dummy=FALSE;

//    String dayOfTheWeek,day,monthString,monthNumber,year;

    Date date;

    List<ContentsComplaints> complaintsList;
    List<ComplaintsFirebaseData> complaintsFirebaseData;


    private StorageReference mStorageRef;

    String user;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;

    String user_id;
    DocumentReference docRef;

    String complaints="complaints";
    DocumentReference soc_id_ref;
    String admin;
    View view_solved;
    TextView flat_no_users;
    DrawerLayout drawerLayout;
    String unique_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ComplaintsActivity.this);

        setContentView(R.layout.activity_complaints_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user=getString(R.string.USER);
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


        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        username = getIntent().getExtras().getString("username");
        unique_id=getIntent().getStringExtra("unique_id");

        Log.i(TAG,"unique_id is "+unique_id);


        tv_society_name.setText(soc_name);
        tv_flatno.setText(flat_no);
        tv_username.setText(username);
        tv_name.setText(name);

        //generate=(TextView)findViewById(R.id.generate);
        generate_img = findViewById(R.id.generate_img);
        back=(ImageView)findViewById(R.id.back);
        complaintsList =new ArrayList<>();

        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);

        drawerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical
        relativeLayout=findViewById(R.id.relativeLayout);

        db= FirebaseFirestore.getInstance();
        currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        user_id=currentFirebaseUser.getUid();
        docRef=db.collection(user).document(user_id);

        admin=getIntent().getExtras().getString("admin");
        Log.i(TAG,"Admin is :"+admin);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });

        user_icon = (ImageView)findViewById(R.id.user_profile);
        user_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent settings_intent = new Intent(ComplaintsActivity.this, Settings.class);
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
        searchView = findViewById(R.id.search_field);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString().toLowerCase());

            }
        });



        view_solved=(View)findViewById(R.id.view_solved);
        flat_no_users=(TextView)findViewById(R.id.flat_no_users);


        getData();

        mAdapter=new ComplaintsAdapter(getApplicationContext(), complaintsList);
        recyclerView.setAdapter(mAdapter);

        generate_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), NewComplaintsActivity.class);
                intent.putExtra("admin",admin);
                startActivity(intent);
            }
        });

        //filters
        filter_icon=(ImageView)findViewById(R.id.filter_icon);


        filter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Filter button pressed");
                showFilterDialogue();
            }
        });




        enableSwipeToDelete();
        enableSwipeToSolve();

    }

    private void filter(String text){
        List<ContentsComplaints> temp = new ArrayList();
        for(ContentsComplaints d: complaintsList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getFlat_no().toLowerCase().contains(text) || d.getSubject().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        mAdapter.updateList(temp);

    }

    private void showFilterDialogue() {

        final Boolean[] boolean_solved = {FALSE};
        final Boolean[] boolean_unsolved = {FALSE};
        final Boolean[] boolean_all = {TRUE};
        final Boolean[] boolean_date = {FALSE};
        final int[] dayStart = {0};
        final int[] monthStart = new int[1];
        final int[] yearStart = new int[1];
        final int[] dayEnd = new int[1];
        final int[] monthEnd = new int[1];
        final int[] yearEnd = new int[1];

        LayoutInflater factory = LayoutInflater.from(this);
        final View filterDialogView = factory.inflate(R.layout.complaints_filters, null);
        final AlertDialog filterDialog = new AlertDialog.Builder(this).create();
        filterDialog.setView(filterDialogView);

        final Button btn_solved=(Button)filterDialogView.findViewById(R.id.btn_solved);
        final Button btn_unsolved=(Button)filterDialogView.findViewById(R.id.btn_unsolved);
        final Button btn_all=(Button)filterDialogView.findViewById(R.id.btn_all);
        final Button btn_apply_filter=(Button)filterDialogView.findViewById(R.id.btn_apply_filter);
        final TextView start_date=(TextView) filterDialogView.findViewById(R.id.start_date);
        final TextView end_date=(TextView) filterDialogView.findViewById(R.id.end_date);


        btn_solved.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

           if( boolean_unsolved[0]){
               boolean_unsolved[0] =FALSE;
               btn_unsolved.setTextColor(Color.parseColor("#FF0000"));
               btn_unsolved.setBackgroundColor(Color.parseColor("#1A707070"));
           }
           if( boolean_all[0]){
               boolean_all[0] =FALSE;
               btn_all.setTextColor(Color.parseColor("#000000"));
               btn_all.setBackgroundColor(Color.parseColor("#1A707070"));
           }
            boolean_solved[0] =TRUE;
            Log.i(TAG,"Button pressed");
            btn_solved.setBackgroundColor(getResources().getColor(R.color.greenSolved));
            btn_solved.setTextColor(Color.parseColor("#FFFFFF"));

           }
       });

        btn_unsolved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( boolean_solved[0]){
                    boolean_solved[0] =FALSE;
                    btn_solved.setTextColor(getResources().getColor(R.color.greenSolved));
                    btn_solved.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                if( boolean_all[0]){
                    boolean_all[0] =FALSE;
                    btn_all.setTextColor(Color.parseColor("#000000"));
                    btn_all.setBackgroundColor(Color.parseColor("#1A707070"));
                }
                boolean_unsolved[0] =TRUE;
                Log.i(TAG,"Button pressed");
                btn_unsolved.setBackgroundColor(Color.parseColor("#FF0000"));
                btn_unsolved.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });

        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( boolean_solved[0]){
                    boolean_solved[0] =FALSE;
                    btn_solved.setTextColor(getResources().getColor(R.color.greenSolved));
                    btn_solved.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                if( boolean_unsolved[0]){
                    boolean_unsolved[0] =FALSE;
                    btn_unsolved.setTextColor(Color.parseColor("#FF0000"));
                    btn_unsolved.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                boolean_all[0]=TRUE;
                Log.i(TAG,"Button pressed");
                btn_all.setBackgroundColor(Color.parseColor("#000000"));
                btn_all.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });


        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cldrStart = Calendar.getInstance();
                dayStart[0] = cldrStart.get(Calendar.DAY_OF_MONTH);
                monthStart[0] = cldrStart.get(Calendar.MONTH);
                yearStart[0] = cldrStart.get(Calendar.YEAR);
                pickerDialog = new DatePickerDialog(ComplaintsActivity.this,R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                try {
                                    //All your parse Operations
                                    dateStart=sdf.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    dateEnd=sdf.parse(dayOfMonth+1 + "/" + (monthOfYear + 1) + "/" + year);
                                } catch (ParseException e) {
                                    //Handle exception here, most of the time you will just log it.
                                    e.printStackTrace();
                                }

                                start_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                end_date.setText("Set End Date");
                            }
                        }, yearStart[0], monthStart[0], dayStart[0]);

                pickerDialog.show();


            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dayStart[0] == 0) {
                    showMessage("Error!", "Please Enter Start Date First",R.drawable.ic_error_dialog);

                }
                else {

                    dayEnd[0] = dayStart[0] + 1;
                    monthEnd[0] = monthStart[0];
                    yearEnd[0] = yearStart[0];

                    pickerDialog = new DatePickerDialog(ComplaintsActivity.this, R.style.DialogTheme,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                    try {
                                        //All your parse Operations
                                        dateEnd=sdf.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    } catch (ParseException e) {
                                        //Handle exception here, most of the time you will just log it.
                                        e.printStackTrace();
                                    }

                                    if(dateEnd.before(dateStart)){
                                        showMessage("Error","Please Enter the Date after Start Date",R.drawable.ic_error_dialog);
                                    }
                                    else {
                                        end_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    }
                                }
                            }, yearEnd[0], monthEnd[0], dayEnd[0]);
                    pickerDialog.show();

                }
                boolean_date[0] =TRUE;
            }
        });

        btn_apply_filter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i(TAG,boolean_date[0].toString());

                if(boolean_solved[0] && boolean_date[0]){
                    Log.i(TAG,"In 1");
                    getDataWithDateAndSolved(TRUE);
                }

                else if(boolean_unsolved[0] && boolean_date[0]){
                    Log.i(TAG,"In 2");
                    getDataWithDateAndSolved(FALSE);

                }

               else if(boolean_all[0] && boolean_date[0]){
                    Log.i(TAG,"In 3");
                    getDataWithDate();

                }

               else if(boolean_solved[0]){
                    Log.i(TAG,"In 4");
                    getDataWithSolved(TRUE);

                }

               else if(boolean_unsolved[0]){
                    Log.i(TAG,"In 5");
                    getDataWithSolved(FALSE);
                }

               else if(boolean_all[0]){
                    Log.i(TAG,"In 6");
                    getData();
                }
                mAdapter=new ComplaintsAdapter(getApplicationContext(), complaintsList);
                recyclerView.setAdapter(mAdapter);
                filterDialog.dismiss();

            }

            private void getDataWithSolved(final Boolean solved) {

                complaintsFirebaseData =new ArrayList<ComplaintsFirebaseData>();

               getDataListener= docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable final DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                        if(e!=null){
                            Log.i(TAG,"Request failed");
                        }

                        String source=snapshot!=null && snapshot.getMetadata().hasPendingWrites()
                                ?"Local" : "Server";
                        if(snapshot!=null && snapshot.exists()){

                            Log.i(TAG,"Data is here "+snapshot.getData());

                            soc_id_ref=(DocumentReference)snapshot.get("society_id");
                            Log.i(TAG,"Society id is "+soc_id_ref.toString());
                            if(snapshot.get("society_name").toString().equals("My Society")){
//
                                dummy=TRUE;
                                generate_img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//                                        Snackbar snackbar = Snackbar.make(drawerLayout, "You are not able to generate a complaint. Please go to settings for more information.", Snackbar.LENGTH_LONG);
//                                        snackbar.setAction("Settings", new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                Intent settings_intent=new Intent(getApplicationContext(),Settings.class);
//                                                settings_intent.putExtra("society_name",soc_name);
//                                                settings_intent.putExtra("flat_no",flat_no);
//                                                settings_intent.putExtra("username",username);
//                                                settings_intent.putExtra("name",name);
//                                                settings_intent.putExtra("admin",admin);
//                                                startActivity(settings_intent);
//                                                overridePendingTransition(0, 0);
//
//                                            }
//                                        });
//                                        snackbar.show();
                                        showMessageOptions("Oops!","You are not able to generate a complaint!\n Contact us for more information",R.drawable.ic_error_dialog);

                                        //showMessage("Error!","You are not allowed to do this. Please go to settings for more information.",R.drawable.ic_error_dialog);
                                    }
                                });
                            }

                            if(!admin.equals("true")) {

                                Log.i(TAG,"I am in admin = false part");


                                getDataListener=soc_id_ref.collection(complaints).whereEqualTo("user_id", user_id)
                                        .whereEqualTo("solved",solved)
                                        .orderBy("date", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                                if (e != null) {
                                                    Log.d(TAG, "Error:" + e.getMessage());
                                                } else {


                                                    if (snapshots.getDocuments().isEmpty()) {
                                                        Log.i(TAG, "No complaints");
                                                        showMessage("Oops!","No data matches your filters",R.drawable.ic_error_dialog);

                                                    } else {

                                                        complaintsList.clear();
                                                        mAdapter.notifyDataSetChanged();
                                                        for (QueryDocumentSnapshot doc : snapshots) {


                                                            Timestamp date1 = (Timestamp) doc.get("date");

                                                            SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                            String day = sfd_day.format(date1.toDate());

                                                            SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                            String mon = sfd_mon.format(date1.toDate());

                                                            SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                            String year = sfd_year.format(date1.toDate());

                                                            SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                            String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                                            SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                                            String time = sfd_time.format(date1.toDate());



                                                            String solved_orNot = doc.get("solved").toString();
                                                            Log.i(TAG, "solved or not :" + solved_orNot);

                                                            if (solved_orNot == "false") {

                                                                solved_orNot = "Unsolved";
                                                            } else {
                                                                solved_orNot = "Solved";
                                                            }



                                                            complaintsList.add(
                                                                    new ContentsComplaints(
                                                                            solved_orNot,
                                                                            mon,
                                                                            day,
                                                                            year,
                                                                            doc.get("subject").toString(),
                                                                            doc.get("description").toString(),
                                                                            date_viewFormat,
                                                                            (ArrayList<String>) doc.get("image_url"),
                                                                            (DocumentReference) doc.get("document_id"),
                                                                            doc.get("flat").toString(),
                                                                            admin,
                                                                            doc.get("user_id").toString(),
                                                                            time
                                                                    )
                                                            );
                                                            recyclerView.smoothScrollToPosition(0);


                                                        }

                                                    }

                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            }

                                        });
                            }else{

                                Log.i(TAG,"I am in admin = true part");
                                getDataListener=soc_id_ref.collection(complaints)
                                        .whereEqualTo("solved",solved)
                                        .orderBy("date", Query.Direction.DESCENDING)
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                                if (e != null) {
                                                    Log.d(TAG, "Error:" + e.getMessage());
                                                } else {


                                                    if (snapshots.getDocuments().isEmpty()) {
                                                        Log.i(TAG, "No complaints");
                                                        showMessage("Oops!","No data matches your filters",R.drawable.ic_error_dialog);

                                                    } else {

                                                        complaintsList.clear();
                                                        mAdapter.notifyDataSetChanged();
                                                        for (QueryDocumentSnapshot doc : snapshots) {

                                                            Log.i(TAG, "solved or not " + doc.get("solved").toString());

                                                            Timestamp date1 = (Timestamp) doc.get("date");

                                                            SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                            String day = sfd_day.format(date1.toDate());

                                                            SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                            String mon = sfd_mon.format(date1.toDate());

                                                            SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                            String year = sfd_year.format(date1.toDate());

                                                            SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                            String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                                            SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                                            String time = sfd_time.format(date1.toDate());


                                                            String solved_orNot = doc.get("solved").toString();
                                                            Log.i(TAG, "solved or not :" + solved_orNot);

                                                            if (solved_orNot == "false") {

                                                                solved_orNot = "Unsolved";
                                                            } else {
                                                                solved_orNot = "Solved";
                                                            }


                                                            complaintsList.add(
                                                                    new ContentsComplaints(
                                                                            solved_orNot,
                                                                            mon,
                                                                            day,
                                                                            year,
                                                                            doc.get("subject").toString(),
                                                                            doc.get("description").toString(),
                                                                            date_viewFormat,
                                                                            (ArrayList<String>) doc.get("image_url"),
                                                                            (DocumentReference) doc.get("document_id"),
                                                                            doc.get("flat").toString(),
                                                                            admin,
                                                                            doc.get("user_id").toString(),
                                                                            time
                                                                    )
                                                            );


                                                        }

                                                    }

                                                    mAdapter.notifyDataSetChanged();
                                                }
                                            }

                                        });


                            }
                        }

                    }
                });
            }

            private void getDataWithDate() {

                Log.i(TAG,"getDataWithDate starting");

                complaintsFirebaseData =new ArrayList<ComplaintsFirebaseData>();

                getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable final DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        Log.i(TAG,"getDataListener starting");

                        if(e!=null){
                            Log.i(TAG,"Request failed");
                        }

                        String source=snapshot!=null && snapshot.getMetadata().hasPendingWrites()
                                ?"Local" : "Server";
                        if(snapshot!=null && snapshot.exists()){

                            Log.i(TAG,"Data is here "+snapshot.getData());

                            soc_id_ref=(DocumentReference)snapshot.get("society_id");
                            Log.i(TAG,"Society id is "+soc_id_ref.toString());
                            if(snapshot.get("society_name").toString().equals("My Society")){
                                dummy=TRUE;
                                generate_img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//
                                        showMessageOptions("Oops!","You are not able to generate a complaint!\n Contact us for more information",R.drawable.ic_error_dialog);
                                    }
                                });
                            }

                            if(!admin.equals("true")) {

                                Log.i(TAG,"I am in admin = false part");

                                Log.i(TAG,"Date start is "+ dateStart + dateEnd);


                                getDataListener=soc_id_ref.collection(complaints).whereEqualTo("user_id", user_id)
                                        .orderBy("date", Query.Direction.DESCENDING)
                                        .whereLessThanOrEqualTo("date",new Timestamp(dateEnd))
                                        .whereGreaterThanOrEqualTo("date",new Timestamp(dateStart))
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.d(TAG, "Error:" + e.getMessage());
                                        } else {


                                            if (snapshots.getDocuments().isEmpty()) {
                                                Log.i(TAG, "No complaints");
                                                showMessage("Oops!","No data matches your filters",R.drawable.ic_error_dialog);

                                            } else {

                                                complaintsList.clear();
                                                mAdapter.notifyDataSetChanged();
                                                for (QueryDocumentSnapshot doc : snapshots) {


                                                    Timestamp date1 = (Timestamp) doc.get("date");

                                                    SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                    String day = sfd_day.format(date1.toDate());

                                                    SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                    String mon = sfd_mon.format(date1.toDate());

                                                    SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                    String year = sfd_year.format(date1.toDate());

                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                    String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                                    SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                                    String time = sfd_time.format(date1.toDate());



                                                    String solved_orNot = doc.get("solved").toString();
                                                    Log.i(TAG, "solved or not :" + solved_orNot);

                                                    if (solved_orNot == "false") {

                                                        solved_orNot = "Unsolved";
                                                    } else {
                                                        solved_orNot = "Solved";
                                                    }


                                                    complaintsList.add(
                                                            new ContentsComplaints(
                                                                    solved_orNot,
                                                                    mon,
                                                                    day,
                                                                    year,
                                                                    doc.get("subject").toString(),
                                                                    doc.get("description").toString(),
                                                                    date_viewFormat,
                                                                    (ArrayList<String>) doc.get("image_url"),
                                                                    (DocumentReference) doc.get("document_id"),
                                                                    doc.get("flat").toString(),
                                                                    admin,
                                                                    doc.get("user_id").toString(),
                                                                    time
                                                            )
                                                    );
                                                    recyclerView.smoothScrollToPosition(0);


                                                }

                                            }

                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }

                                });
                            }else{

                                Log.i(TAG,"I am in admin = true part");
                                getDataListener=soc_id_ref.collection(complaints)
                                        .orderBy("date", Query.Direction.DESCENDING)
                                        .whereLessThanOrEqualTo("date",new Timestamp(dateEnd))
                                        .whereGreaterThanOrEqualTo("date",new Timestamp(dateStart))
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.d(TAG, "Error:" + e.getMessage());
                                        } else {

                                            Log.i(TAG,"Date start is "+ dateStart + dateEnd);



                                            if (snapshots.getDocuments().isEmpty()) {
                                                Log.i(TAG, "No complaints");
                                                showMessage("Note","There are no complaints for this selected time",R.drawable.ic_error_dialog);
                                            } else {

                                                complaintsList.clear();
                                                mAdapter.notifyDataSetChanged();
                                                for (QueryDocumentSnapshot doc : snapshots) {

                                                    Log.i(TAG, "solved or not " + doc.get("solved").toString());

                                                    Timestamp date1 = (Timestamp) doc.get("date");

                                                    SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                    String day = sfd_day.format(date1.toDate());

                                                    SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                    String mon = sfd_mon.format(date1.toDate());

                                                    SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                    String year = sfd_year.format(date1.toDate());

                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                    String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                                    SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                                    String time = sfd_time.format(date1.toDate());


                                                    String solved_orNot = doc.get("solved").toString();
                                                    Log.i(TAG, "solved or not :" + solved_orNot);

                                                    if (solved_orNot == "false") {

                                                        solved_orNot = "Unsolved";
                                                    } else {
                                                        solved_orNot = "Solved";
                                                    }


                                                    complaintsList.add(
                                                            new ContentsComplaints(
                                                                    solved_orNot,
                                                                    mon,
                                                                    day,
                                                                    year,
                                                                    doc.get("subject").toString(),
                                                                    doc.get("description").toString(),
                                                                    date_viewFormat,
                                                                    (ArrayList<String>) doc.get("image_url"),
                                                                    (DocumentReference) doc.get("document_id"),
                                                                    doc.get("flat").toString(),
                                                                    admin,
                                                                    doc.get("user_id").toString(),
                                                                    time
                                                            )
                                                    );


                                                }

                                            }

                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }

                                });


                            }
                        }

                    }
                });


            }

            private void getDataWithDateAndSolved(final Boolean solved) {

                complaintsFirebaseData =new ArrayList<ComplaintsFirebaseData>();
                getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable final DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                        if(e!=null){
                            Log.i(TAG,"Request failed");
                        }

                        String source=snapshot!=null && snapshot.getMetadata().hasPendingWrites()
                                ?"Local" : "Server";
                        if(snapshot!=null && snapshot.exists()){

                            Log.i(TAG,"Data is here "+snapshot.getData());

                            soc_id_ref=(DocumentReference)snapshot.get("society_id");
                            Log.i(TAG,"Society id is "+soc_id_ref.toString());
                            if(snapshot.get("society_name").toString().equals("My Society")){

                                dummy=TRUE;
                                generate_img.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Snackbar snackbar = Snackbar.make(drawerLayout, "You are not able to generate a complaint. Please go to settings for more information.", Snackbar.LENGTH_LONG);
                                        snackbar.setAction("Settings", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent settings_intent=new Intent(getApplicationContext(),Settings.class);
                                                settings_intent.putExtra("society_name",soc_name);
                                                settings_intent.putExtra("flat_no",flat_no);
                                                settings_intent.putExtra("username",username);
                                                settings_intent.putExtra("name",name);
                                                settings_intent.putExtra("admin",admin);
                                                settings_intent.putExtra("unique_id",unique_id);
                                                startActivity(settings_intent);
                                                overridePendingTransition(0, 0);

                                            }
                                        });
                                        snackbar.show();
                                        showMessageOptions("Oops!","You are not able to generate a complaint!\n Contact us for more information",R.drawable.ic_error_dialog);
                                        //showMessage("Error!","You are not allowed to do this. Please go to settings for more information.",R.drawable.ic_error_dialog);
                                    }
                                });
                            }

                            if(!admin.equals("true")) {

                                Log.i(TAG,"I am in admin = false part");


                               getDataListener= soc_id_ref.collection(complaints).whereEqualTo("user_id", user_id)
                                        .whereEqualTo("solved",solved)
                                        .orderBy("date", Query.Direction.DESCENDING)
                                        .whereGreaterThanOrEqualTo("date",new Timestamp(dateStart))
                                        .whereLessThanOrEqualTo("date",new Timestamp(dateEnd))
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.d(TAG, "Error:" + e.getMessage());
                                        } else {


                                            if (snapshots.getDocuments().isEmpty()) {
                                                Log.i(TAG, "No complaints");
                                                showMessage("Oops!","No data matches your filters",R.drawable.ic_error_dialog);

                                            } else {

                                                complaintsList.clear();
                                                mAdapter.notifyDataSetChanged();
                                                for (QueryDocumentSnapshot doc : snapshots) {


                                                    Timestamp date1 = (Timestamp) doc.get("date");

                                                    SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                    String day = sfd_day.format(date1.toDate());

                                                    SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                    String mon = sfd_mon.format(date1.toDate());

                                                    SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                    String year = sfd_year.format(date1.toDate());

                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                    String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                                    SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                                    String time = sfd_time.format(date1.toDate());



                                                    String solved_orNot = doc.get("solved").toString();
                                                    Log.i(TAG, "solved or not :" + solved_orNot);

                                                    if (solved_orNot == "false") {

                                                        solved_orNot = "Unsolved";
                                                    } else {
                                                        solved_orNot = "Solved";
                                                    }


                                                    complaintsList.add(
                                                            new ContentsComplaints(
                                                                    solved_orNot,
                                                                    mon,
                                                                    day,
                                                                    year,
                                                                    doc.get("subject").toString(),
                                                                    doc.get("description").toString(),
                                                                    date_viewFormat,
                                                                    (ArrayList<String>) doc.get("image_url"),
                                                                    (DocumentReference) doc.get("document_id"),
                                                                    doc.get("flat").toString(),
                                                                    admin,
                                                                    doc.get("user_id").toString(),
                                                                    time
                                                            )
                                                    );
                                                    recyclerView.smoothScrollToPosition(0);


                                                }

                                            }

                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }

                                });
                            }else{

                                Log.i(TAG,"I am in admin = true part");
                                Log.i(TAG,"Date start "+dateStart);
                                Log.i(TAG,"Date End "+dateEnd);
                                Log.i(TAG,"Date start timestamp "+ new Timestamp(dateStart));
                                Log.i(TAG,"Date End timestamp"+ new Timestamp(dateEnd));

                                getDataListener=soc_id_ref.collection(complaints)
                                        .whereEqualTo("solved",solved)
                                        .orderBy("date", Query.Direction.DESCENDING)
                                        .whereLessThanOrEqualTo("date",new Timestamp(dateEnd))
                                        .whereGreaterThanOrEqualTo("date",new Timestamp(dateStart))
                                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.d(TAG, "Error:" + e.getMessage());
                                        } else {




                                            if (snapshots.getDocuments().isEmpty()) {
                                                Log.i(TAG, "No complaints");
                                                showMessage("Oops!","No data matches your filters",R.drawable.ic_error_dialog);

                                            } else {

                                                complaintsList.clear();
                                                mAdapter.notifyDataSetChanged();
                                                for (QueryDocumentSnapshot doc : snapshots) {

                                                    Log.i(TAG, "solved or not " + doc.get("solved").toString());

                                                    Timestamp date1 = (Timestamp) doc.get("date");

                                                    SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                    String day = sfd_day.format(date1.toDate());

                                                    SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                    String mon = sfd_mon.format(date1.toDate());

                                                    SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                    String year = sfd_year.format(date1.toDate());

                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                    String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                                    SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                                    String time = sfd_time.format(date1.toDate());


                                                    String solved_orNot = doc.get("solved").toString();
                                                    Log.i(TAG, "solved or not :" + solved_orNot);

                                                    if (solved_orNot == "false") {

                                                        solved_orNot = "Unsolved";
                                                    } else {
                                                        solved_orNot = "Solved";
                                                    }


                                                    complaintsList.add(
                                                            new ContentsComplaints(
                                                                    solved_orNot,
                                                                    mon,
                                                                    day,
                                                                    year,
                                                                    doc.get("subject").toString(),
                                                                    doc.get("description").toString(),
                                                                    date_viewFormat,
                                                                    (ArrayList<String>) doc.get("image_url"),
                                                                    (DocumentReference) doc.get("document_id"),
                                                                    doc.get("flat").toString(),
                                                                    admin,
                                                                    doc.get("user_id").toString(),
                                                                    time
                                                            )
                                                    );

                                                }

                                            }

                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }

                                });


                            }
                        }

                    }
                });

            }
        });


        filterDialog.show();


    }


    private void getData() {
        complaintsFirebaseData =new ArrayList<ComplaintsFirebaseData>();

       getDataListener= docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable final DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                if(e!=null){
                    Log.i(TAG,"Request failed");
                }

                String source=snapshot!=null && snapshot.getMetadata().hasPendingWrites()
                        ?"Local" : "Server";
                if(snapshot!=null && snapshot.exists()){

                    Log.i(TAG,"Data is here "+snapshot.getData());

                    soc_id_ref=(DocumentReference)snapshot.get("society_id");
                    Log.i(TAG,"Society id is "+soc_id_ref.toString());

                    if(snapshot.get("society_name").toString().equals("My Society")){
                        dummy=TRUE;
                        generate_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Snackbar snackbar = Snackbar.make(drawerLayout, "You are not able to generate a complaint. Please go to settings for more information.", Snackbar.LENGTH_LONG);
//                                snackbar.setAction("Settings", new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View view) {
//                                        Intent settings_intent=new Intent(getApplicationContext(),Settings.class);
//                                        settings_intent.putExtra("society_name",soc_name);
//                                        settings_intent.putExtra("flat_no",flat_no);
//                                        settings_intent.putExtra("username",username);
//                                        settings_intent.putExtra("name",name);
//                                        settings_intent.putExtra("admin",admin);
//                                        settings_intent.putExtra("unique_id",unique_id);
//                                        startActivity(settings_intent);
//                                        overridePendingTransition(0, 0);
//
//                                    }
//                                });
//                                snackbar.show();
                                filter_icon.setVisibility(View.GONE);
                                showMessageOptions("Oops!","You are not able to generate a complaint!\n Contact us for more information",R.drawable.ic_error_dialog);


                            }
                        });
                    }

                    if(!admin.equals("true") && !dummy) {

                        Log.i(TAG,"I am in admin = false part");


                        getDataListener=soc_id_ref.collection(complaints).whereEqualTo("user_id", user_id)
                                .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                if (e != null) {
                                    Log.d(TAG, "Error:" + e.getMessage());
                                } else {

                                    if (snapshots.getDocuments().isEmpty()) {
                                        Log.i(TAG, "No complaints");
                                    } else {

                                        complaintsList.clear();
                                        mAdapter.notifyDataSetChanged();
                                        for (QueryDocumentSnapshot doc : snapshots) {

                                            Timestamp date1 = (Timestamp) doc.get("date");

                                            SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                            String day = sfd_day.format(date1.toDate());

                                            SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                            String mon = sfd_mon.format(date1.toDate());

                                            SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                            String year = sfd_year.format(date1.toDate());

                                            SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                            String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                            SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                            String time = sfd_time.format(date1.toDate());



                                            String solved_orNot = doc.get("solved").toString();
                                            Log.i(TAG, "solved or not :" + solved_orNot);

                                            if (solved_orNot == "false") {

                                                solved_orNot = "Unsolved";
                                            } else {
                                                solved_orNot = "Solved";
                                            }


                                            complaintsList.add(
                                                    new ContentsComplaints(
                                                            solved_orNot,
                                                            mon,
                                                            day,
                                                            year,
                                                            doc.get("subject").toString(),
                                                            doc.get("description").toString(),
                                                            date_viewFormat,
                                                            (ArrayList<String>) doc.get("image_url"),
                                                            (DocumentReference) doc.get("document_id"),
                                                            doc.get("flat").toString(),
                                                            admin,
                                                            doc.get("user_id").toString(),
                                                            time
                                                    )
                                            );
                                            recyclerView.smoothScrollToPosition(0);


                                        }

                                    }

                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                        });
                    }else{

                        Log.i(TAG,"I am in admin = true part");
                        getDataListener=soc_id_ref.collection(complaints)
                                .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                if (e != null) {
                                    Log.d(TAG, "Error:" + e.getMessage());
                                } else {


                                    if (snapshots.getDocuments().isEmpty()) {
                                        Log.i(TAG, "No complaints");
                                    } else {

                                        complaintsList.clear();
                                        mAdapter.notifyDataSetChanged();
                                        for (QueryDocumentSnapshot doc : snapshots) {

                                            Log.i(TAG, "solved or not " + doc.get("solved").toString());

                                            Timestamp date1 = (Timestamp) doc.get("date");

                                            SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                            String day = sfd_day.format(date1.toDate());

                                            SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                            String mon = sfd_mon.format(date1.toDate());

                                            SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                            String year = sfd_year.format(date1.toDate());

                                            SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                            String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                            SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                            String time = sfd_time.format(date1.toDate());


                                            String solved_orNot = doc.get("solved").toString();
                                            Log.i(TAG, "solved or not :" + solved_orNot);

                                            if (solved_orNot == "false") {

                                                solved_orNot = "Unsolved";
                                            } else {
                                                solved_orNot = "Solved";
                                            }


                                            complaintsList.add(
                                                    new ContentsComplaints(
                                                            solved_orNot,
                                                            mon,
                                                            day,
                                                            year,
                                                            doc.get("subject").toString(),
                                                            doc.get("description").toString(),
                                                            date_viewFormat,
                                                            (ArrayList<String>) doc.get("image_url"),
                                                            (DocumentReference) doc.get("document_id"),
                                                            doc.get("flat").toString(),
                                                            admin,
                                                            doc.get("user_id").toString(),
                                                            time
                                                    )
                                            );


                                        }

                                    }

                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                        });


                    }
                }
            }
        });

    }

    private void enableSwipeToDelete() {

        SwipeToDelete swipeToDelete = new SwipeToDelete(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();

                if(dummy){
//                    Snackbar snackbar = Snackbar.make(drawerLayout, "You are not able to delete a complaint. Please go to settings for more information.", Snackbar.LENGTH_LONG);
//                    snackbar.setAction("Settings", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent settings_intent=new Intent(getApplicationContext(),Settings.class);
//                            settings_intent.putExtra("society_name",soc_name);
//                            settings_intent.putExtra("flat_no",flat_no);
//                            settings_intent.putExtra("username",username);
//                            settings_intent.putExtra("name",name);
//                            settings_intent.putExtra("admin",admin);
//                            settings_intent.putExtra("unique_id",unique_id);
//                            startActivity(settings_intent);
//                            overridePendingTransition(0, 0);
//
//                        }
//                    });
//                    snackbar.show();
                    showMessageOptions("Oops!","You are not authorized to delete complaint!\n Contact us for more information",R.drawable.ic_error_dialog);

                    mAdapter.notifyDataSetChanged();
                }
                else if(complaintsList.get(position).getSolved_unsolved().equals("Solved")){
                    Snackbar snackbar = Snackbar
                            .make(drawerLayout, "Solved complaint cannot be deleted", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                    mAdapter.notifyDataSetChanged();
                }else {

                    new AlertDialog.Builder(ComplaintsActivity.this)
                            .setTitle("")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {



                                    if (user_id.equals(complaintsList.get(position).getUser_complaint())) {


                                       final DocumentReference complaint_id = complaintsList.get(position).getDocument_id();
                                        Log.i(TAG,"ref is "+complaint_id);

                                        ArrayList<String> image_url_delete = complaintsList.get(position).getFb_imageURl();
                                        Log.i(TAG, "Image_url after swiping " + image_url_delete.toString());
                                       // mAdapter.removeItem(position);
                                        Log.i(TAG,"image_url_delete is "+image_url_delete);

                                        if (image_url_delete.size() != 0) {

                                            Log.i(TAG,"inside if");

                                            for (int count = 0; count < image_url_delete.size(); count++) {
                                                Log.i(TAG,"inside for loop");

                                                mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image_url_delete.get(count));
                                                Log.i(TAG, "Image URl is " + mStorageRef.toString());
                                                mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "onSuccess: deleted image from storage");
                                                        complaintsList.get(position).getDocument_id().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {

                                                                Snackbar snackbar = Snackbar
                                                                        .make(drawerLayout, "Complaint deleted", Snackbar.LENGTH_LONG);
                                                                snackbar.setActionTextColor(Color.YELLOW);
                                                                snackbar.show();
                                                                 mAdapter.removeItem(position);
                                                                 mAdapter.notifyDataSetChanged();

                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception exception) {
                                                                // Uh-oh, an error occurred!
                                                                Log.d(TAG, "onFailure: did not delete image");
                                                                Snackbar snackbar = Snackbar
                                                                        .make(drawerLayout, "Unable to delete complaint", Snackbar.LENGTH_LONG);
                                                                snackbar.setActionTextColor(Color.RED);
                                                                snackbar.show();
                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i(TAG,"An error occurred "+e.getMessage());
                                                    }
                                                });
                                            }
                                        } else {
                                            complaint_id.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    Snackbar snackbar = Snackbar
                                                            .make(drawerLayout, "Complaint deleted", Snackbar.LENGTH_LONG);
                                                    snackbar.setActionTextColor(Color.YELLOW);
                                                    snackbar.show();
                                                    //  mAdapter.removeItem(position);
                                                }
                                            });
                                        }
                                    } else {
                                        Snackbar snackbar = Snackbar
                                                .make(drawerLayout, "You can only delete your own complaint", Snackbar.LENGTH_LONG);
                                        snackbar.setActionTextColor(Color.YELLOW);
                                        snackbar.show();
                                        mAdapter.notifyDataSetChanged();
                                    }
                                }


                            })
                            .setNegativeButton("Cancel", null).show();
                }

                mAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDelete);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    private void enableSwipeToSolve() {
        SwipeToSolve swipeToSolve = new SwipeToSolve(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();

                if(dummy){
                    Snackbar snackbar = Snackbar.make(drawerLayout, "You are not able to solve a complaint. Please go to settings for more information.", Snackbar.LENGTH_LONG);
//                    snackbar.setAction("Settings", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent settings_intent=new Intent(getApplicationContext(),Settings.class);
//                            settings_intent.putExtra("society_name",soc_name);
//                            settings_intent.putExtra("flat_no",flat_no);
//                            settings_intent.putExtra("username",username);
//                            settings_intent.putExtra("name",name);
//                            settings_intent.putExtra("admin",admin);
//                            startActivity(settings_intent);
//                            overridePendingTransition(0, 0);
//
//                        }
//                    });
//                    snackbar.show();

                    showMessageOptions("Oops!","You are not authorized to delete complaint!\n Contact us for more information",R.drawable.ic_error_dialog);

                    mAdapter.notifyDataSetChanged();
                }
                else if(complaintsList.get(position).getSolved_unsolved().equals("Solved")){


                    Snackbar snackbar = Snackbar
                            .make(drawerLayout, "Complaint already solved", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                    mAdapter.notifyDataSetChanged();
                }
                else {

                    new AlertDialog.Builder(ComplaintsActivity.this)
                            .setTitle("")
                            .setMessage("Are you sure the complaint is solved?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if (complaintsList.get(position).getSolved_unsolved() != "true") {

                                        DocumentReference complaint_id = complaintsList.get(position).getDocument_id();
                                        complaint_id.update("solved", TRUE);
                                    } else {
                                        Log.i(TAG, "Just swiped");
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null).show();

                    mAdapter.notifyDataSetChanged();
                }

            }

        };

        mAdapter.notifyDataSetChanged();

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToSolve);
        itemTouchhelper.attachToRecyclerView(recyclerView);

    }


//    public void showMessage(String title, String message){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.show();
//
//    }

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(ComplaintsActivity.this);
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


    public void showMessageOptions(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(ComplaintsActivity.this);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (id){
            case R.id.home_menu:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.messages_menu:
                if(!dummy) {
                    Intent messages_intent = new Intent(ComplaintsActivity.this, MessagesMainActivity.class);
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
                Intent services_intent = new Intent(ComplaintsActivity.this, Services.class);
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
                Intent settings_intent = new Intent(ComplaintsActivity.this,Settings.class);
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
                Intent nearby_intent = new Intent(ComplaintsActivity.this, Nearby.class);
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

    @Override
    protected void onStop() {
        super.onStop();

        if (getDataListener!= null) {
            getDataListener.remove();
            getDataListener = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}

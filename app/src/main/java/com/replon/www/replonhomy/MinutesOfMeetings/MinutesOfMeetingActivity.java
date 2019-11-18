package com.replon.www.replonhomy.MinutesOfMeetings;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class MinutesOfMeetingActivity extends AppCompatActivity {

    public static final String TAG = "MinutesOfMeetingActivit";
    private RecyclerView recyclerView;
    private MoMAdapter mAdapter;
    private TextView add_meeting;
    ImageView back, filters;

    List<ContentsMoM> momList;
    ListenerRegistration getDataListener, getGetDataListener2;

    String user;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;

    String user_id;
    DocumentReference docRef, soc_id_ref;
    String minutes_of_meeting = "minutes_of_meeting";

    EditText searchView;

    Calendar cldrStart;
    Date dateStart, dateEnd;
    DatePickerDialog pickerDialog;
    RelativeLayout relativeLayout;
    Boolean dummy = FALSE;

    String soc_name, flat_no, username, name, admin, unique_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), MinutesOfMeetingActivity.this);

        setContentView(R.layout.activity_minutes_of_meeting);

        user = getString(R.string.USER);
        add_meeting = (TextView) findViewById(R.id.add_meeting);
        back = (ImageView) findViewById(R.id.back);
        momList = new ArrayList<>();
        filters = findViewById(R.id.filters);

        recyclerView = (RecyclerView) findViewById(R.id.mom_recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        db = FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user_id = currentFirebaseUser.getUid();
        docRef = db.collection(user).document(user_id);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        getData();
        mAdapter = new MoMAdapter(this, momList);
        recyclerView.setAdapter(mAdapter);

        soc_name = getIntent().getStringExtra("society_name");
        flat_no = getIntent().getStringExtra("flat_no");
        username = getIntent().getStringExtra("username");
        name = getIntent().getStringExtra("name");
        admin = getIntent().getStringExtra("admin");
        unique_id = getIntent().getStringExtra("unique_id");

        if(admin.equals("false")){
            add_meeting.setVisibility(View.GONE);
        }else{
            add_meeting.setVisibility(View.VISIBLE);
        }

        add_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_intent = new Intent(getApplicationContext(), AddMeetingActivity.class);
                startActivity(add_intent);
                overridePendingTransition(0, 0);
                getData();
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

        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Filter button pressed");
                showFilterDialogue();
            }
        });
    }

    private void showFilterDialogue() {
        final Boolean[] boolean_date = {FALSE};
        final int[] dayStart = {0};
        final int[] monthStart = new int[1];
        final int[] yearStart = new int[1];
        final int[] dayEnd = new int[1];
        final int[] monthEnd = new int[1];
        final int[] yearEnd = new int[1];

        LayoutInflater factory = LayoutInflater.from(this);
        final View filterDialogView = factory.inflate(R.layout.notice_filters, null);
        final AlertDialog filterDialog = new AlertDialog.Builder(this).create();
        filterDialog.setView(filterDialogView);

        final Button btn_apply_filter = (Button) filterDialogView.findViewById(R.id.btn_apply_filter);
        final TextView start_date = (TextView) filterDialogView.findViewById(R.id.start_date);
        final TextView end_date = (TextView) filterDialogView.findViewById(R.id.end_date);


        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cldrStart = Calendar.getInstance();
                dayStart[0] = cldrStart.get(Calendar.DAY_OF_MONTH);
                monthStart[0] = cldrStart.get(Calendar.MONTH);
                yearStart[0] = cldrStart.get(Calendar.YEAR);
                pickerDialog = new DatePickerDialog(MinutesOfMeetingActivity.this, R.style.DialogTheme,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                try {
                                    //All your parse Operations
                                    dateStart = sdf.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    dateEnd = sdf.parse(dayOfMonth + 1 + "/" + (monthOfYear + 1) + "/" + year);
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

                } else {

                    dayEnd[0] = dayStart[0] + 1;
                    monthEnd[0] = monthStart[0];
                    yearEnd[0] = yearStart[0];

                    pickerDialog = new DatePickerDialog(MinutesOfMeetingActivity.this, R.style.DialogTheme,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

                                    try {
                                        //All your parse Operations
                                        dateEnd = sdf.parse(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    } catch (ParseException e) {
                                        //Handle exception here, most of the time you will just log it.
                                        e.printStackTrace();
                                    }

                                    if (dateEnd.before(dateStart)) {
                                        showMessage("Error", "Please Enter the Date after Start Date",R.drawable.ic_error_dialog);
                                    } else {
                                        end_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    }
                                }
                            }, yearEnd[0], monthEnd[0], dayEnd[0]);
                    pickerDialog.show();

                }
                boolean_date[0] = TRUE;
            }
        });

        btn_apply_filter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.i(TAG, boolean_date[0].toString());

                if (boolean_date[0]) {
                    getDataWithDate();
                }


                mAdapter = new MoMAdapter(getApplicationContext(), momList);
                recyclerView.setAdapter(mAdapter);
                filterDialog.dismiss();

            }

        });


        filterDialog.show();
    }

    private void getDataWithDate() {
        getDataListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, "Request failed");
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
                if (snapshot != null && snapshot.exists()) {


                    Log.i(TAG, "Data is here " + snapshot.getData());

                    soc_id_ref = (DocumentReference) snapshot.get("society_id");
                    Log.i(TAG, "Society id is " + soc_id_ref.toString());

                    if (snapshot.get("society_name").toString().equals("My Society")) {
                        //generate_img.setEnabled(false);
                        dummy = TRUE;
//
                        add_meeting.setEnabled(true);

                        add_meeting.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showMessageOptions("Oops!", "You are not able to generate a complaint!\n Contact us for more information", R.drawable.ic_error_dialog);

                            }
                        });

                    }

                    getGetDataListener2 = soc_id_ref.collection(minutes_of_meeting)
                            .orderBy("date_created", Query.Direction.DESCENDING)
                            .whereLessThanOrEqualTo("date_created", new Timestamp(dateEnd))
                            .whereGreaterThanOrEqualTo("date_created", new Timestamp(dateStart))
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                    if (e != null) {
                                        Log.d(TAG, "Error:" + e.getMessage());
                                    } else {
                                        if (snapshots.getDocuments().isEmpty()) {
                                            Log.i(TAG, "No MOM");
                                            showMessage("Oops!","No data matches your filters",R.drawable.ic_error_dialog);
                                        } else {
                                            momList.clear();
                                            mAdapter.notifyDataSetChanged();

                                            for (QueryDocumentSnapshot doc : snapshots) {
                                                Timestamp date1 = (Timestamp) doc.get("date_created");

                                                SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                String day = sfd_day.format(date1.toDate());

                                                SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                String mon = sfd_mon.format(date1.toDate());

                                                SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                String year = sfd_year.format(date1.toDate());

                                                SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                                SimpleDateFormat sfd_time = new SimpleDateFormat("h:mm a");
                                                String time = sfd_time.format(date1.toDate());

                                                Log.i(TAG, "Meeting name is " + doc.get("meeting_name").toString());

                                                ArrayList<String> fb_imageUrl=new ArrayList<>();

                                                if(doc.get("image_url")==null){
                                                    fb_imageUrl.add("");
                                                }else{
                                                    fb_imageUrl=(ArrayList)doc.get("image_url");
                                                }

                                                ArrayList<String> fb_pdfUrl=new ArrayList<>();

                                                if(doc.get("pdf_url")==null){
                                                    fb_pdfUrl.add("");
                                                }else{
                                                    fb_pdfUrl=(ArrayList)doc.get("pdf_url");
                                                }


                                                momList.add(new ContentsMoM(
                                                        doc.get("meeting_name").toString(),
                                                        (ArrayList<String>) doc.get("subjects"),
                                                        (ArrayList<String>) doc.get("descriptions"),
                                                        mon,
                                                        day,
                                                        year,
                                                        date_viewFormat,
                                                        time,
                                                        fb_pdfUrl,
                                                        fb_imageUrl

                                                ));
                                                mAdapter.notifyDataSetChanged();

                                            }

                                            mAdapter = new MoMAdapter(getApplicationContext(), momList);
//                                            mAdapter.notifyDataSetChanged();
                                            recyclerView.setAdapter(mAdapter);

                                        }
                                        mAdapter.notifyDataSetChanged();


                                    }
                                }
                            });

                }
            }
        });
    }

    private void filter(String text) {
        List<ContentsMoM> temp = new ArrayList();
        for (ContentsMoM d : momList) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getMeeting_name().contains(text) || d.getMeeting_subjects().contains(text)) {
                temp.add(d);
            }
        }
        //update recyclerview
        mAdapter.updateList(temp);

    }

    public void showMessageOptions(String title, String message, int image) {

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(MinutesOfMeetingActivity.this);
        dialog.setContentView(R.layout.dialog_new);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.i(TAG, "NEW DIALOG");

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
                Intent settings_intent = new Intent(getApplicationContext(), ContactUsActivity.class);
                settings_intent.putExtra("society_name", soc_name);
                settings_intent.putExtra("flat_no", flat_no);
                settings_intent.putExtra("username", username);
                settings_intent.putExtra("name", name);
                settings_intent.putExtra("admin", admin);
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

        final Dialog dialog = new Dialog(MinutesOfMeetingActivity.this);
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

    private void getData() {

        getDataListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.i(TAG, "Request failed");
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
                if (snapshot != null && snapshot.exists()) {


                    Log.i(TAG, "Data is here " + snapshot.getData());

                    soc_id_ref = (DocumentReference) snapshot.get("society_id");
                    Log.i(TAG, "Society id is " + soc_id_ref.toString());

                    if (snapshot.get("society_name").toString().equals("My Society")) {
                        //add_meeting.setEnabled(false);
                        dummy = TRUE;
//
                        filters.setVisibility(View.GONE);
                        add_meeting.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                showMessageOptions("Oops!", "You are not able to generate MOM!\n Contact us for more information", R.drawable.ic_error_dialog);

                            }
                        });

                    }

                    getGetDataListener2 = soc_id_ref.collection(minutes_of_meeting).orderBy("date_created", Query.Direction.DESCENDING)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                    if (e != null) {
                                        Log.d(TAG, "Error:" + e.getMessage());
                                    } else {
                                        if (snapshots.getDocuments().isEmpty()) {
                                            Log.i(TAG, "No MOM");
                                        } else {
                                            momList.clear();
                                            mAdapter.notifyDataSetChanged();

                                            for (QueryDocumentSnapshot doc : snapshots) {
                                                Timestamp date1 = (Timestamp) doc.get("date_created");

                                                SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                String day = sfd_day.format(date1.toDate());

                                                SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                String mon = sfd_mon.format(date1.toDate());

                                                SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                String year = sfd_year.format(date1.toDate());

                                                SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                                SimpleDateFormat sfd_time = new SimpleDateFormat("HH:mm a");
                                                String time = sfd_time.format(date1.toDate());

                                                Log.i(TAG, "Meeting name is " + doc.get("meeting_name").toString());

                                                ArrayList<String> fb_imageUrl=new ArrayList<>();

                                                if(doc.get("image_url")==null){
                                                    fb_imageUrl.add("");
                                                }else{
                                                    fb_imageUrl=(ArrayList)doc.get("image_url");
                                                }

                                                ArrayList<String> fb_pdfUrl=new ArrayList<>();

                                                if(doc.get("pdf_url")==null){
                                                    fb_pdfUrl.add("");
                                                }else{
                                                    fb_pdfUrl=(ArrayList)doc.get("pdf_url");
                                                }

                                                momList.add(new ContentsMoM(
                                                        doc.get("meeting_name").toString(),
                                                        (ArrayList<String>) doc.get("subjects"),
                                                        (ArrayList<String>) doc.get("descriptions"),
                                                        mon,
                                                        day,
                                                        year,
                                                        date_viewFormat,
                                                        time,
                                                        fb_pdfUrl,
                                                        fb_imageUrl
                                                ));
                                                mAdapter.notifyDataSetChanged();

                                            }

                                            mAdapter = new MoMAdapter(getApplicationContext(), momList);
                                            mAdapter.notifyDataSetChanged();
                                            recyclerView.setAdapter(mAdapter);


                                        }
                                        mAdapter.notifyDataSetChanged();


                                    }
                                }
                            });

                }
            }
        });


    }

    @Override
    protected void onStop() {
        super.onStop();

        if (getDataListener != null) {
            getDataListener.remove();
            getDataListener = null;
        }

        if (getGetDataListener2 != null) {
            getGetDataListener2.remove();
            getGetDataListener2 = null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
        overridePendingTransition(R.anim.enter_finish_activity, R.anim.exit_finish_activity);

    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}

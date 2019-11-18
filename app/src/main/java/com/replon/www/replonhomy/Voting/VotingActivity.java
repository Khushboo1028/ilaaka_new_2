package com.replon.www.replonhomy.Voting;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.replon.www.replonhomy.Complaints.ComplaintsActivity;
import com.replon.www.replonhomy.Home.Home;
import com.replon.www.replonhomy.MinutesOfMeetings.ContentsMoM;
import com.replon.www.replonhomy.MinutesOfMeetings.MinutesOfMeetingActivity;
import com.replon.www.replonhomy.MinutesOfMeetings.MoMAdapter;
import com.replon.www.replonhomy.MinutesOfMeetings.ViewMoMAdapter;
import com.replon.www.replonhomy.NoticeBoard.ContentsNotice;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Settings.Settings;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.SwipeToDelete;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class VotingActivity extends AppCompatActivity {

    public static final String TAG = "VotingActivity";
    private RecyclerView recyclerView;
    private PollsMainAdapter mAdapter;
    private TextView create_poll;
    ImageView back,filters;
    List <ContentsPolls> pollsList;
    EditText searchView;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DocumentReference docRef,soc_id_ref;
    String user_id, user;
    ListenerRegistration getDataListener,getGetDataListener2;
    Boolean dummy=FALSE;

    RelativeLayout relativeLayout;
    String soc_name,flat_no,username,name,admin;


    Calendar cldrStart;
    Date dateStart, dateEnd;
    DatePickerDialog pickerDialog;
    Boolean time_passed=FALSE;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), VotingActivity.this);

        setContentView(R.layout.activity_voting_main);

        user=getString(R.string.USER);
        create_poll=(TextView)findViewById(R.id.create_poll);
        back=(ImageView)findViewById(R.id.back);
        pollsList=new ArrayList<>();
        filters = findViewById(R.id.filters);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        create_poll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddPollActivity.class);
                startActivity(intent);
                getData();
            }
        });


        soc_name=getIntent().getStringExtra("society_name");
        flat_no=getIntent().getStringExtra("flat_no");
        username=getIntent().getStringExtra("username");
        name=getIntent().getStringExtra("name");
        admin=getIntent().getStringExtra("admin");

        if(admin.equals("false")){
            create_poll.setVisibility(View.GONE);
        }else{
           enableSwipeToDelete();

        }

        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialogue();
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
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        user_id=firebaseUser.getUid();
        docRef=db.collection(user).document(user_id);

        getData();
//        pollsList.add(new ContentsPolls("Should We Get Cameras in the passage area of each floor ? (Current Quotation - 98,456 ₹)",
//                "June",
//                "7",
//                "2019"
//        ,"6:30 PM June 8, 2019"));
//        pollsList.add(new ContentsPolls("What Theme Should be kept for 2019 Ganesh Utsav ?",
//                "May",
//                "30",
//                "2019"
//                ,"Poll Ended"));


        mAdapter = new PollsMainAdapter(getApplicationContext(), pollsList);
        recyclerView.setAdapter(mAdapter);



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

        final Button btn_apply_filter=(Button)filterDialogView.findViewById(R.id.btn_apply_filter);
        final TextView start_date=(TextView) filterDialogView.findViewById(R.id.start_date);
        final TextView end_date=(TextView) filterDialogView.findViewById(R.id.end_date);


        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cldrStart = Calendar.getInstance();
                dayStart[0] = cldrStart.get(Calendar.DAY_OF_MONTH);
                monthStart[0] = cldrStart.get(Calendar.MONTH);
                yearStart[0] = cldrStart.get(Calendar.YEAR);
                pickerDialog = new DatePickerDialog(VotingActivity.this,R.style.DialogTheme,
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

                    pickerDialog = new DatePickerDialog(VotingActivity.this, R.style.DialogTheme,
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

                Log.i(TAG, boolean_date[0].toString());

                if (boolean_date[0]) {
                    getDataWithDate();
                }


                mAdapter = new PollsMainAdapter(getApplicationContext(), pollsList);
                recyclerView.setAdapter(mAdapter);
                filterDialog.dismiss();

            }

            private void getDataWithDate(){

                Log.i(TAG,"In getDataWithDate");
                Log.i(TAG,"doc ref is "+docRef);
                Log.i(TAG,"start date is "+start_date);
                Log.i(TAG,"End date is "+end_date);

                getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.i(TAG,"Request failed");
                        }

                        Log.i(TAG,"In addSnapshotListener");
                        String source=snapshot!=null && snapshot.getMetadata().hasPendingWrites()
                                ?"Local" : "Server";
                        Log.i(TAG,"Snapshot is "+snapshot);
                        Log.i(TAG,"Snapshot exists "+snapshot.exists());
                        if(snapshot!=null && snapshot.exists()) {

                            Log.i(TAG,"InSnapshot");
                            Log.i(TAG,"Data is here "+snapshot.getData());

                            soc_id_ref=(DocumentReference)snapshot.get("society_id");
                            Log.i(TAG,"Society id is "+soc_id_ref.toString());

                            if(snapshot.get("society_name").toString().equals("My Society")){
//                        generate_img.setEnabled(false);
                                dummy=TRUE;

                                create_poll.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
//
                                        showMessageOptions("Unavailable","You are not able to generate poll",R.drawable.ic_error_dialog);


                                    }
                                });
                            }

                            getGetDataListener2=soc_id_ref.collection("voting_poll")
                                    .whereLessThanOrEqualTo("date_created",new Timestamp(dateEnd))
                                    .whereGreaterThanOrEqualTo("date_created",new Timestamp(dateStart))
                                    .orderBy("date_created", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                            if (e != null) {
                                                Log.d(TAG, "Error:" + e.getMessage());
                                            } else {
                                                if (snapshots.getDocuments().isEmpty()) {
                                                    Log.i(TAG, "No polls");
                                                }else{
                                                    pollsList.clear();
                                                    mAdapter.notifyDataSetChanged();

                                                    for (QueryDocumentSnapshot doc : snapshots) {
                                                        Timestamp date1 = (Timestamp) doc.get("date_created");

                                                        SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                        String day = sfd_day.format(date1.toDate());

                                                        SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                        String mon = sfd_mon.format(date1.toDate());

                                                        SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                        String year = sfd_year.format(date1.toDate());


                                                        SimpleDateFormat sfd_time = new SimpleDateFormat("h:mm a");
                                                        String time = sfd_time.format(date1.toDate());

                                                        Timestamp ts_time_poll_ends = (Timestamp) doc.get("time_poll_ends");
                                                        SimpleDateFormat sfd_timePollEnds = new SimpleDateFormat("h:mm a d MMM, yyyy");
                                                        String time_poll_ends = sfd_timePollEnds.format(ts_time_poll_ends.toDate());
                                                        Log.i(TAG,"time poll ends is "+ts_time_poll_ends);
                                                        Log.i(TAG,"String time poll ends is "+time_poll_ends);


                                                        Log.i(TAG,"Poll name is "+doc.get("question").toString());

                                                        Date currentDate=new Date();



                                                        if(currentDate.after(ts_time_poll_ends.toDate())){
                                                            time_passed=TRUE;


                                                        }

                                                        pollsList.add(new ContentsPolls(
                                                                        doc.get("question").toString(),
                                                                        mon,
                                                                        day,
                                                                        year,
                                                                        time_poll_ends,
                                                                        time,
                                                                        (ArrayList)doc.get("choices"),
                                                                        time_passed,
                                                                        (DocumentReference)doc.get("document_id"),
                                                                        flat_no
                                                                )
                                                        );
                                                        mAdapter.notifyDataSetChanged();

                                                    }

                                                    mAdapter = new PollsMainAdapter(getApplicationContext(), pollsList);
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

        });
        filterDialog.show();
    }








    private void getData() {

        Log.i(TAG,"In getData");
        Log.i(TAG,"doc ref is "+docRef);
        getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i(TAG,"Request failed");
                }

                Log.i(TAG,"In addSnapshotListener");
                String source=snapshot!=null && snapshot.getMetadata().hasPendingWrites()
                        ?"Local" : "Server";
                Log.i(TAG,"Snapshot is "+snapshot);

                if(snapshot!=null && snapshot.exists()) {

                    Log.i(TAG,"InSnapshot");
                    Log.i(TAG,"Data is here "+snapshot.getData());

                    soc_id_ref=(DocumentReference)snapshot.get("society_id");
                    Log.i(TAG,"Society id is "+soc_id_ref.toString());

                    if(snapshot.get("society_name").toString().equals("My Society")){
//                        generate_img.setEnabled(false);
                        dummy=TRUE;
                        filters.setVisibility(View.GONE);
                        create_poll.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//
                                showMessage("Unavailable","You are not able to generate poll",R.drawable.ic_error_dialog);


                            }
                        });
                    }

                    getGetDataListener2=soc_id_ref.collection("voting_poll")
                            .orderBy("date_created", Query.Direction.DESCENDING)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                                    if (e != null) {
                                        Log.d(TAG, "Error:" + e.getMessage());
                                    } else {
                                        if (snapshots.getDocuments().isEmpty()) {
                                            Log.i(TAG, "No polls");
                                        }else{
                                            pollsList.clear();
                                            mAdapter.notifyDataSetChanged();

                                            for (QueryDocumentSnapshot doc : snapshots) {
                                                Timestamp date1 = (Timestamp) doc.get("date_created");

                                                SimpleDateFormat sfd_day = new SimpleDateFormat("dd");
                                                String day = sfd_day.format(date1.toDate());

                                                SimpleDateFormat sfd_mon = new SimpleDateFormat("MMM");
                                                String mon = sfd_mon.format(date1.toDate());

                                                SimpleDateFormat sfd_year = new SimpleDateFormat("yyyy");
                                                String year = sfd_year.format(date1.toDate());


                                                SimpleDateFormat sfd_time = new SimpleDateFormat("h:mm a");
                                                String time = sfd_time.format(date1.toDate());

                                                Timestamp ts_time_poll_ends = (Timestamp) doc.get("time_poll_ends");
                                                SimpleDateFormat sfd_timePollEnds = new SimpleDateFormat("h:mm a d MMM, yyyy");
                                                String time_poll_ends = sfd_timePollEnds.format(ts_time_poll_ends.toDate());
                                                Log.i(TAG,"time poll ends is "+ts_time_poll_ends);
                                                Log.i(TAG,"String time poll ends is "+time_poll_ends);


                                                Log.i(TAG,"Poll name is "+doc.get("question").toString());

                                                Date currentDate=new Date();
                                                Boolean time_passed=FALSE;


                                                if(currentDate.after(ts_time_poll_ends.toDate())){
                                                    time_passed=TRUE;


                                                }

                                                pollsList.add(new ContentsPolls(
                                                        doc.get("question").toString(),
                                                        mon,
                                                        day,
                                                        year,
                                                        time_poll_ends,
                                                        time,
                                                        (ArrayList)doc.get("choices"),
                                                        time_passed,
                                                        (DocumentReference)doc.get("document_id"),
                                                        flat_no
                                                      )
                                                );
                                                mAdapter.notifyDataSetChanged();

                                            }

                                            mAdapter = new PollsMainAdapter(getApplicationContext(), pollsList);
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

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(VotingActivity.this);
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
                Intent settings_intent=new Intent(getApplicationContext(), Settings.class);
                settings_intent.putExtra("society_name",soc_name);
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",username);
                settings_intent.putExtra("name",name);
                settings_intent.putExtra("admin",admin);
                startActivity(settings_intent);
                overridePendingTransition(0, 0);
}
        });
        dialog_icon.setImageResource(image);
        dialog.show();

    }

    private void filter(String text){
        List<ContentsPolls> temp = new ArrayList();
        for(ContentsPolls d: pollsList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getPoll_name().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        mAdapter.updateList(temp);

    }


    private void enableSwipeToDelete() {

        SwipeToDelete swipeToDelete = new SwipeToDelete(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();

                if(dummy){
                    Snackbar snackbar = Snackbar.make(relativeLayout, "You are not able to delete a poll. Please go to settings for more information.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("Settings", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent settings_intent=new Intent(getApplicationContext(),Settings.class);
                            settings_intent.putExtra("society_name",soc_name);
                            settings_intent.putExtra("flat_no",flat_no);
                            settings_intent.putExtra("username",username);
                            settings_intent.putExtra("name",name);
                            settings_intent.putExtra("admin",admin);
                            startActivity(settings_intent);
                            overridePendingTransition(0, 0);

                        }
                    });
                    snackbar.show();
                    mAdapter.notifyDataSetChanged();

                }else if(pollsList.get(position).getTimePassed()){
                    Snackbar snackbar = Snackbar.make(relativeLayout, "This poll is expired. It cannot be deleted", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    mAdapter.notifyDataSetChanged();
                } else{

                    new AlertDialog.Builder(VotingActivity.this)
                            .setTitle("")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {

                                    final DocumentReference document_id = pollsList.get(position).getDoc_ref();
                                    Log.i(TAG,"ref is "+document_id);

                                    document_id.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Snackbar snackbar = Snackbar
                                                    .make(relativeLayout, "Poll deleted", Snackbar.LENGTH_LONG);
                                            snackbar.setActionTextColor(Color.YELLOW);
                                            snackbar.show();
                                            //  mAdapter.removeItem(position);


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG,"An error occurred in deleting poll " +e.getMessage());
                                            Snackbar snackbar = Snackbar
                                                    .make(relativeLayout, "An error occurred", Snackbar.LENGTH_LONG);
                                            snackbar.setActionTextColor(Color.YELLOW);
                                            snackbar.show();
                                        }
                                    });


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



    public void showMessageOptions(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(VotingActivity.this);
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
        finish();
        overridePendingTransition(0,0);
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (getDataListener!= null) {
            getDataListener.remove();
            getDataListener = null;
        }

        if (getGetDataListener2!= null) {
            getGetDataListener2.remove();
            getGetDataListener2 = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }


}

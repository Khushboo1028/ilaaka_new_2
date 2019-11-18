package com.replon.www.replonhomy.NoticeBoard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.storage.StorageReference;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Utility.CallUserData;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.SwipeToDelete;
import com.replon.www.replonhomy.Home.Home;
import com.replon.www.replonhomy.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class NoticeBoardActivity extends AppCompatActivity {

    public static final String TAG = "NoticeBoardActivity";
    private RecyclerView recyclerView;
    private NoticeAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private TextView generate,tv_time;
    RelativeLayout relativeLayout;
    ImageView back,filters_icon;
//    String dayOfTheWeek,day,monthString,monthNumber,year;

    Calendar cldrStart;
    Date dateStart, dateEnd;
    DatePickerDialog pickerDialog;
    ListenerRegistration getDataListener;

    List<ContentsNotice> noticeList;


    private StorageReference mStorageRef;

    String user;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;

    String user_id;
    DocumentReference docRef;

    String notice="notices";
    DocumentReference soc_id_ref;
    String admin;


    CallUserData callUserData;

    EditText searchView;
    String soc_name,flat_no,username,name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), NoticeBoardActivity.this);

        callUserData  = new CallUserData(getApplicationContext());

        setContentView(R.layout.activity_notice_board);

        user=getString(R.string.USER);
        generate=(TextView)findViewById(R.id.generate);
        generate.setVisibility(View.GONE);
        back=(ImageView)findViewById(R.id.back);
        noticeList =new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical
        relativeLayout=findViewById(R.id.relativeLayout);



        db=FirebaseFirestore.getInstance();
        currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        user_id=currentFirebaseUser.getUid();
        docRef=db.collection(user).document(user_id);

        tv_time=(TextView)findViewById(R.id.time);


        admin=getIntent().getStringExtra("admin");
//        admin = callUserData.userDataFirebase.getAdmin().toString();
        Log.i(TAG,"Admin is :"+admin);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0,0);
            }
        });



        getData();

        mAdapter=new NoticeAdapter(getApplicationContext(), noticeList);
        recyclerView.setAdapter(mAdapter);


        if(admin.equalsIgnoreCase("true")){
            generate.setVisibility(View.VISIBLE);
            generate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getApplicationContext(), NewNoticeActivity.class);
                    intent.putExtra("admin",admin);
                    startActivity(intent);
                }
            });
        }

       // enableSwipeToDelete();

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

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        soc_name=getIntent().getStringExtra("society_name");
        flat_no=getIntent().getStringExtra("flat_no");
        username=getIntent().getStringExtra("username");
        name=getIntent().getStringExtra("name");

        filters_icon = (ImageView) findViewById(R.id.filters);
        if(soc_name.equals(getString(R.string.DUMMY_SOC))){
            filters_icon.setVisibility(View.GONE);
        }

        filters_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Filter button pressed");
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
                pickerDialog = new DatePickerDialog(NoticeBoardActivity.this,R.style.DialogTheme,
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

                    pickerDialog = new DatePickerDialog(NoticeBoardActivity.this, R.style.DialogTheme,
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

                if(boolean_date[0]){
                    getDataWithDate();
                }


                mAdapter=new NoticeAdapter(getApplicationContext(), noticeList);
                recyclerView.setAdapter(mAdapter);
                filterDialog.dismiss();

            }

            private void getDataWithDate() {

               getDataListener= docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable final DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                        if(e!=null){
                            Log.i(TAG,"Request failed");
                        }

                        String source=snapshot!=null && snapshot.getMetadata().hasPendingWrites()
                                ?"Local" : "Server";
                        if(snapshot!=null && snapshot.exists()){

                            Log.i(TAG,"Data is here "+snapshot.getData());

                            soc_id_ref=(DocumentReference)snapshot.get("society_id");
                            Log.i(TAG,"Society id is "+soc_id_ref.toString());


                            getDataListener=soc_id_ref.collection("notices")
                                    .orderBy("date", Query.Direction.DESCENDING)
                                    .whereLessThanOrEqualTo("date",new Timestamp(dateEnd))
                                    .whereGreaterThanOrEqualTo("date",new Timestamp(dateStart))
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                                    if (e != null) {
                                        Log.d(TAG, "Error:" + e.getMessage());
                                    } else {


                                        if (snapshots.getDocuments().isEmpty()) {
                                            Log.i(TAG, "No Notices");
                                            showMessage("Oops!","No data matches your filters",R.drawable.ic_error_dialog);

                                        } else {

                                            noticeList.clear();
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

                                                noticeList.add(
                                                        new ContentsNotice(

                                                                doc.get("subject").toString(),
                                                                mon,
                                                                day,
                                                                year,
                                                                doc.get("description").toString(),
                                                                date_viewFormat,
                                                                (ArrayList<String>) doc.get("image_url"),
                                                                (DocumentReference)doc.get("document_id"),
                                                                time,
                                                                (ArrayList<String>) doc.get("pdf_url")

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
                });
            }
        });


        filterDialog.show();
    }
    public void showMessageOptions(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(NoticeBoardActivity.this);
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
    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(NoticeBoardActivity.this);
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


    private void filter(String text){
        List<ContentsNotice> temp = new ArrayList();
        for(ContentsNotice d: noticeList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getSubject().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        mAdapter.updateList(temp);

    }

    private void getData() {

        getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable final DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {

                if(e!=null){
                    Log.i(TAG,"Request failed");
                }

                String source=snapshot!=null && snapshot.getMetadata().hasPendingWrites()
                        ?"Local" : "Server";
                if(snapshot!=null && snapshot.exists()){

                    Log.i(TAG,"Data is here "+snapshot.getData());

                    soc_id_ref=(DocumentReference)snapshot.get("society_id");
                    Log.i(TAG,"Society id is "+soc_id_ref.toString());

                    getDataListener=soc_id_ref.collection("notices")
                            .orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.d(TAG, "Error:" + e.getMessage());
                            } else {


                                if (snapshots.getDocuments().isEmpty()) {
                                    Log.i(TAG, "No Notices");
                                } else {

                                    noticeList.clear();
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




                                        noticeList.add(
                                                new ContentsNotice(

                                                        doc.get("subject").toString(),
                                                        mon,
                                                        day,
                                                        year,
                                                        doc.get("description").toString(),
                                                        date_viewFormat,
                                                        (ArrayList<String>) doc.get("image_url"),
                                                        (DocumentReference)doc.get("document_id"),
                                                        time,
                                                        (ArrayList<String>) doc.get("pdf_url")

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
        });

    }

    private void enableSwipeToDelete() {

        SwipeToDelete swipeToDelete = new SwipeToDelete(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                if(admin.equals("true")) {
                    final DocumentReference ref = noticeList.get(position).getDocument_id();

                    ArrayList<String> image_url_delete = noticeList.get(position).getFb_imageURl();
                    Log.i(TAG, "Image_url after swiping " + image_url_delete.toString());
                    mAdapter.removeItem(position);

                    if (image_url_delete.size() != 0) {


                        for (int count = 0; count < image_url_delete.size(); count++) {

                            mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image_url_delete.get(count));
                            Log.i(TAG, "Image URl is " + mStorageRef.toString());
                            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: deleted image from storage");
                                    ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {


                                            Snackbar snackbar = Snackbar
                                                    .make(relativeLayout, "Notice deleted", Snackbar.LENGTH_LONG);
                                            snackbar.setActionTextColor(Color.YELLOW);
                                            snackbar.show();
                                            // mAdapter.removeItem(position);

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Uh-oh, an error occurred!
                                            Log.d(TAG, "onFailure: did not delete image");
                                            Snackbar snackbar = Snackbar
                                                    .make(relativeLayout, "An error occurred", Snackbar.LENGTH_LONG);
                                            snackbar.setActionTextColor(Color.RED);
                                            snackbar.show();
                                        }
                                    });

                                }
                            });
                        }
                    } else {
                        ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Snackbar snackbar = Snackbar
                                        .make(relativeLayout, "Notice deleted", Snackbar.LENGTH_LONG);
                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();
                                //  mAdapter.removeItem(position);


                            }
                        });


                    }
                }else{
                    Snackbar snackbar = Snackbar
                            .make(relativeLayout, "You are not authorized to delete notice", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                    mAdapter.notifyDataSetChanged();
                }
                mAdapter.notifyDataSetChanged();



            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDelete);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
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

package com.replon.www.replonhomy.Services;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.replon.www.replonhomy.NoticeBoard.ContentsNotice;
import com.replon.www.replonhomy.NoticeBoard.NoticeAdapter;
import com.replon.www.replonhomy.NoticeBoard.NoticeBoardActivity;
import com.replon.www.replonhomy.R;
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

public class ServicesGuestlistActivity extends AppCompatActivity {

    public static final String TAG = "ServicesGuestlistActivi";
    ImageView back;
    RecyclerView recyclerView;
    List<ContentsGuestlist> guestList;
    private GuestlistAdapter mAdapter;

    String user;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser currentFirebaseUser;

    String user_id;
    DocumentReference docRef;


    DocumentReference soc_id_ref;
    ListenerRegistration getDataListener;
    String flat_no;

    Calendar cldrStart;
    Date dateStart, dateEnd;
    DatePickerDialog pickerDialog;
    ImageView filters;
    EditText searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ServicesGuestlistActivity.this);

        setContentView(R.layout.activity_services_guestlist);

        user=getString(R.string.USER);
        back = findViewById(R.id.back);
        guestList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical

        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        currentFirebaseUser=mAuth.getCurrentUser();
        user_id=currentFirebaseUser.getUid();

        docRef=db.collection(user).document(user_id);
        getData();

       // guestList.add(new ContentsGuestlist("Rahul Gala", "Business","12:15 PM 12/06/19","---NA---","4","default"));
        mAdapter=new GuestlistAdapter(getApplicationContext(),guestList);
        recyclerView.setAdapter(mAdapter);



        filters=(ImageView)findViewById(R.id.filters);
        filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialogue();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    }

    private void filter(String text){
        List<ContentsGuestlist> temp = new ArrayList();
        for(ContentsGuestlist d: guestList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if( d.getName().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        mAdapter.updateList(temp);

    }
    private void getData(){
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

                    flat_no=snapshot.getString("flat_no");
                    soc_id_ref=(DocumentReference)snapshot.get("society_id");
                    Log.i(TAG,"Society id is "+soc_id_ref.toString());

                    Log.i(TAG,"Society name is "+snapshot.getString("society_name"));



                   getDataListener= soc_id_ref.collection("guestlist")
                            .orderBy("date_created",Query.Direction.DESCENDING)
                           .whereArrayContains("flat_no",flat_no)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.d(TAG, "Error:" + e.getMessage());
                            }else{

                                if(queryDocumentSnapshots.getDocuments().isEmpty()){
                                    Log.i(TAG,"No visitors");
                                }else{
                                    guestList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){

                                        Timestamp date1 = (Timestamp) doc.get("date_created");
                                        SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("hh:mm a, MMMM d, yyyy");
                                        String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                        SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                        String time = sfd_time.format(date1.toDate());

                                        String checkout_time;
                                        if(doc.get("checkout_time")==null){
                                            checkout_time="---NA---";

                                        }else{
                                            Timestamp out_time=(Timestamp)doc.get("checkout_time");
                                            checkout_time = sfd_viewFormat.format(out_time.toDate());
                                        }


                                        guestList.add(
                                                new ContentsGuestlist(
                                                        doc.getString("name"),
                                                        doc.getString("purpose"),
                                                        date_viewFormat,
                                                        checkout_time,
                                                        doc.getString("car_type"),
                                                        doc.getString("image_url")


                                                )
                                        );



                                    }

                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                }

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
                pickerDialog = new DatePickerDialog(ServicesGuestlistActivity.this,R.style.DialogTheme,
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

                    pickerDialog = new DatePickerDialog(ServicesGuestlistActivity.this, R.style.DialogTheme,
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


                mAdapter=new GuestlistAdapter(getApplicationContext(), guestList);
                recyclerView.setAdapter(mAdapter);
                filterDialog.dismiss();

            }




        });


        filterDialog.show();
    }

    private void getDataWithDate() {

        getDataListener= soc_id_ref.collection("guestlist")
                .orderBy("date_created")
                .whereLessThanOrEqualTo("date_created",new Timestamp(dateEnd))
                .whereGreaterThanOrEqualTo("date_created",new Timestamp(dateStart))
                .whereArrayContains("flat_no",flat_no)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.d(TAG, "Error:" + e.getMessage());
                        }else{

                            if(queryDocumentSnapshots.getDocuments().isEmpty()){
                                Log.i(TAG,"No visitors");
                                showMessage("Oops!","No data matches your filters",R.drawable.ic_error_dialog);

                            }else{
                                guestList.clear();
                                mAdapter.notifyDataSetChanged();

                                for(QueryDocumentSnapshot doc : queryDocumentSnapshots){

                                    Timestamp date1 = (Timestamp) doc.get("date_created");
                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("hh:mm a, MMMM d, yy");
                                    String date_viewFormat = sfd_viewFormat.format(date1.toDate());

                                    SimpleDateFormat sfd_time = new SimpleDateFormat("hh:mm a");
                                    String time = sfd_time.format(date1.toDate());

                                    String checkout_time;
                                    if(doc.get("checkout_time")==null){
                                        checkout_time="---NA---";
                                    }else{
                                        Timestamp out_time=(Timestamp)doc.get("checkout_time");
                                        checkout_time = sfd_viewFormat.format(out_time.toDate());
                                    }


                                    guestList.add(
                                            new ContentsGuestlist(
                                                    doc.getString("name"),
                                                    doc.getString("purpose"),
                                                    date_viewFormat,
                                                    checkout_time,
                                                    doc.getString("car_type"),
                                                    doc.getString("image_url")


                                            )
                                    );



                                }

                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(ServicesGuestlistActivity.this);
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
}

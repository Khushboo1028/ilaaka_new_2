package com.replon.www.replonhomy.Accounting.MMT;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.replonhomy.Utility.DownloadsAttachmentContent;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DownloadAdapter;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PayNowActivity extends AppCompatActivity implements Serializable {

    public static final String TAG = "PayNowActivity";
    String soc_name, flat_no,name,username,unique_id,admin;
    ProgressBar progressBar;

    ArrayList<ContentsMMT> payList;

    DatePickerDialog picker;

    TextView tv_flat,tv_amount,tv_month,tv_date,tv_date_paid,save,arrow_spinner;
    EditText et_reference;
    String paid_by;
    List<String>categories;
    Date date_paid;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<String> pdf_url, image_url;
    private List<DownloadsAttachmentContent> downloadList;

    //internet
    Boolean isConnected,monitoringConnectivity;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String status;
    int position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_now);
        Log.i(TAG,"On this page");

        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        username = getIntent().getExtras().getString("username");
        unique_id=getIntent().getStringExtra("unique_id");
        admin=getIntent().getStringExtra("admin");
        status=getIntent().getStringExtra("status");

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        db= FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

        payList=new ArrayList<>();

        payList = this.getIntent().getExtras().getParcelableArrayList("findList");
        position=getIntent().getIntExtra("position",0);


        Log.i(TAG,"amount is "+payList.get(position).getAmount());

        tv_amount=(TextView)findViewById(R.id.amount);
        tv_flat=(TextView)findViewById(R.id.flat_no);
        tv_month=(TextView)findViewById(R.id.month);
        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_date_paid=(TextView)findViewById(R.id.tv_date_paid);
        save=(TextView)findViewById(R.id.save);
        et_reference=(EditText)findViewById(R.id.reference_no);
        arrow_spinner=(TextView)findViewById(R.id.arrow_spinner);
        Log.i(TAG,"Status is "+status);

        if(status.equals("approved")){
            tv_amount.setText(payList.get(position).getAmount());
            tv_amount.setEnabled(false);
            tv_flat.setText(flat_no);
            tv_month.setText(payList.get(position).getMaintenance_month());
            tv_date.setText(payList.get(position).getDue_date());
            et_reference.setText(payList.get(position).getReference());
            et_reference.setEnabled(false);
            save.setVisibility(View.GONE);
            tv_date_paid.setText(payList.get(position).getPaid_on_date());
            tv_date_paid.setEnabled(false);



        }else{
            tv_amount.setText(payList.get(position).getAmount());
            tv_flat.setText(flat_no);
            tv_month.setText(payList.get(position).getMaintenance_month());
            tv_date.setText(payList.get(position).getDue_date());
            et_reference.setText(payList.get(position).getReference());
            tv_date_paid.setText(payList.get(position).getPaid_on_date());

        }




        final Spinner spinner = findViewById(R.id.spinner);
        categories=new ArrayList<>();

        if(status.equals("approved")){

            categories.add(payList.get(position).getPaid_by());
            arrow_spinner.setVisibility(View.GONE);
            spinner.setEnabled(false);

        }else if(status.equals("rejected")||status.equals("pending")){
            categories.add(payList.get(position).getPaid_by());
            arrow_spinner.setVisibility(View.VISIBLE);
            spinner.setEnabled(true);

            if (payList.get(position).getPaid_by().equals("Cheque")){
                categories.add("NEFT/IMPS");
                categories.add("Other");
            }else if(payList.get(position).getPaid_by().equals("NEFT/IMPS")){
                categories.add("Cheque");
                categories.add("Other");
            }else {
                categories.add("Cheque");
                categories.add("NEFT/IMPS");
            }

        }

        else{
            categories.add("Cheque");
            categories.add("NEFT/IMPS");
            categories.add("Other");
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paid_by = String.valueOf(spinner.getSelectedItem());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tv_date_paid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        downloadList =new ArrayList<>();
        image_url=new ArrayList<>();
        pdf_url=new ArrayList<>();


        image_url=getIntent().getStringArrayListExtra("image_url_array");
        pdf_url=getIntent().getStringArrayListExtra("pdf_url_array");


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        if(image_url!=null) {
            if (!image_url.isEmpty()) {
                for (int i = 0; i < image_url.size(); i++) {

                    Log.i(TAG,"in image_download");
                    //image_url.get(i);
                    Log.i(TAG,"image_url is"+image_url.get(i));
                    if (!image_url.get(i).equals("")) {
                        downloadList.add(new DownloadsAttachmentContent(image_url.get(i), "image", ""));

                    }


                }
            }
        }
        Log.i(TAG,"pdf_url : "+pdf_url);


        if(pdf_url!=null) {

            if (!pdf_url.isEmpty()) {
                for (int i = 0; i < pdf_url.size(); i++) {

                    //image_url.get(i);
                    Log.i(TAG,"I am here");
                    if(!pdf_url.get(i).equals("")){
                        downloadList.add(new DownloadsAttachmentContent("", "pdf", pdf_url.get(i)));

                    }
                    Log.i(TAG,pdf_url.get(i));

                }
            }
        }


        mAdapter=new DownloadAdapter(this, downloadList);
        recyclerView.setAdapter(mAdapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected){
                    final Dialog dialog = new Dialog(PayNowActivity.this);
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
                            Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                            startActivity(myIntent);
                        }
                    });
                    dialog_icon.setImageResource(R.drawable.ic_no_internet);
                    dialog.show();
                }else{
                    if(et_reference.getText().toString().trim().isEmpty() || date_paid==null ){
                        showMessage("Error","Please enter all fields",R.drawable.ic_error_dialog);

                    }else{
                        updateData();
                    }
                }
            }
        });


    }

    private void updateData() {
        tv_date_paid.setEnabled(false);
        et_reference.setEnabled(false);

        final String user = getString(R.string.USER);
        final String user_id = firebaseUser.getUid();
        DocumentReference docRef = db.collection(user).document(user_id);
        progressBar.setVisibility(View.VISIBLE);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        DocumentReference soc_id_ref =document.getDocumentReference("society_id");
                        Log.i(TAG, "Society id is " + soc_id_ref);

                        DocumentReference doc_id=soc_id_ref.collection("mmt").document(payList.get(0).getDocument_id());

                        int status;
                        if(admin.equals("true")){
                            status=3;
                        }else{
                            status=1;
                        }

                        Map<String, Object> data = new HashMap<>();
                        data.put("status",status);
                        data.put("reference_no",et_reference.getText().toString().trim());
                        data.put("paid_on",date_paid);
                        data.put("paid_by",paid_by);

                        doc_id.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                progressBar.setVisibility(View.GONE);
                                finish();



                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                progressBar.setVisibility(View.GONE);

                                showMessage("Error", "unable to generate invoice",R.drawable.ic_error_dialog);
                            }
                        });

                    } else {
                        Log.d(TAG, "No such document");
                        progressBar.setVisibility(View.GONE);
                        showMessage("Error", "An internal error occurred",R.drawable.ic_error_dialog);

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    progressBar.setVisibility(View.GONE);
                    showMessage("Error", "An internal error occurred",R.drawable.ic_error_dialog);

                }
            }
        });
    }

    private void showDatePicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(PayNowActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tv_date_paid.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String formatedDate = sdf.format(cldr.getTime());
                        try {
                            String sDate1=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            date_paid =new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);

        picker.show();
    }

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(PayNowActivity.this);
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

    //check for internet
    private ConnectivityManager.NetworkCallback connectivityCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            isConnected = true;
            Log.i(TAG, "INTERNET CONNECTED");
        }

        @Override
        public void onLost(Network network) {
            isConnected = false;
            Log.i(TAG,"Internet lost");
            final Dialog dialog = new Dialog(PayNowActivity.this);
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
                    Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog_icon.setImageResource(R.drawable.ic_no_internet);
            dialog.show();
        }
    };

    private void checkConnectivity() {
        // here we are getting the connectivity service from connectivity manager
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(
                Context.CONNECTIVITY_SERVICE);

        // Getting network Info
        // give Network Access Permission in Manifest
        final NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // isConnected is a boolean variable
        // here we check if network is connected or is getting connected
        isConnected = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

        if (!isConnected) {
            // SHOW ANY ACTION YOU WANT TO SHOW
            // WHEN WE ARE NOT CONNECTED TO INTERNET/NETWORK
            Log.i(TAG, " NO NETWORK!");
            // if Network is not connected we will register a network callback to  monitor network
            connectivityManager.registerNetworkCallback(
                    new NetworkRequest.Builder()
                            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .build(), connectivityCallback);
            monitoringConnectivity = true;

            final Dialog dialog = new Dialog(PayNowActivity.this);
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
                    Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog_icon.setImageResource(R.drawable.ic_no_internet);
            dialog.show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnectivity();
    }


}

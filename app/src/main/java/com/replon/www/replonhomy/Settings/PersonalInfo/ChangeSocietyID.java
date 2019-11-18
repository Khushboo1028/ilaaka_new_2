package com.replon.www.replonhomy.Settings.PersonalInfo;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Settings.Settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeSocietyID extends AppCompatActivity {

    public static final String TAG = "ChangeSocietyID";
    ImageView back;
    EditText et_soc_id;
    Button btn_confirm;

    FirebaseFirestore db;
    ListenerRegistration listenerRegistration;
    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    RelativeLayout enter_flatno_rel;
    List categories;
    Spinner spinner;
    String flat,name,phone_number;
    DatabaseReference reference;
    String flat_doc_id;
    DocumentReference doc_flat_ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_society_id);

        back=(ImageView)findViewById(R.id.back);
        et_soc_id=(EditText)findViewById(R.id.soc_id);
        btn_confirm=(Button)findViewById(R.id.btn_confirm);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        enter_flatno_rel=(RelativeLayout)findViewById(R.id.enter_flatno_rel);
        enter_flatno_rel.setVisibility(View.GONE);
        spinner=(Spinner)findViewById(R.id.spinner);

        categories = new ArrayList<String>();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_soc_id.getText().toString().trim().isEmpty()){
                    showMessage("Oops!","Society Id cannot be empty",R.drawable.ic_error_dialog);
                }else{
                    changeSocID();
                }
            }
        });


        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        name=getIntent().getStringExtra("currentName");
        phone_number=getIntent().getStringExtra("phone_number");




    }

    private void changeSocID() {


        btn_confirm.setEnabled(false);


        final String society_id=et_soc_id.getText().toString().trim();
        final Query flat_Ref= db.collection(getString(R.string.SOCIETIES)).whereEqualTo("unique_id",society_id);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                flat = String.valueOf(spinner.getSelectedItem());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        flat_Ref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Listen failed.", task.getException());
                    return;
                } else {

                    final QuerySnapshot snapshot = task.getResult();
                    if (snapshot.getDocuments().isEmpty()) {
                        progressBar.setVisibility(View.GONE);
                        btn_confirm.setEnabled(true);
                        Log.i(TAG, "no societies");
                        showMessage("Error!", "There is no society with such ID.", R.drawable.ic_error_dialog);

                    } else {

                        Log.i(TAG, "Society found with ID " + society_id);
                        enter_flatno_rel.setVisibility(View.VISIBLE);

                        if (snapshot.getDocuments().get(0).get("flats_available") != null) {

                            flat_doc_id = snapshot.getDocuments().get(0).getId();

                            categories = ((ArrayList) snapshot.getDocuments().get(0).get("flats_available"));
                            Collections.sort(categories);


                            // Creating adapter for spinner
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);

                            // Drop down layout style - list view with radio button
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            // attaching data adapter to spinner
                            spinner.setAdapter(dataAdapter);

                        } else {
                            spinner.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            showMessageOptions("Oops!", "There are no flats available! Please contact us for further information", R.drawable.ic_error_dialog);

                        }


                        Log.i(TAG, "categories is " + categories);

                        if (categories.isEmpty()) {
                            categories.add("No flats available");
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
                            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(dataAdapter);
                            progressBar.setVisibility(View.GONE);
                            btn_confirm.setVisibility(View.GONE);
                            showMessageOptions("Oops!", "There are no flats available! Please contact us for further information", R.drawable.ic_error_dialog);

                        } else {

                            btn_confirm.setEnabled(true);

                            btn_confirm.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    btn_confirm.setEnabled(false);
                                    final DocumentReference new_soc_ref = db.collection(getString(R.string.SOCIETIES)).document(snapshot.getDocuments().get(0).getId());
                                    DocumentReference docRef = db.collection(getString(R.string.USER)).document(firebaseUser.getUid());


                                    final String society_name_new = snapshot.getDocuments().get(0).get("society_name").toString();
                                    if (flat != null) {

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("flat_no", flat);
                                        data.put("society_id", new_soc_ref);
                                        data.put("society_name", society_name_new);
                                        data.put("unique_id", society_id);

                                        docRef.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.GONE);
                                                Log.i(TAG, "Data updated");
                                                showMessage("Success!", "Welcome to " + society_name_new, R.drawable.ic_success_dialog);
//
                                                //now remove flat from available array
                                                if (flat_doc_id != null) {
                                                    doc_flat_ref = db.collection(getString(R.string.SOCIETIES)).document(flat_doc_id);
                                                }


                                                if (doc_flat_ref != null) {
                                                    doc_flat_ref.update("flats_available", FieldValue.arrayRemove(flat));
                                                    doc_flat_ref.update("flats_unavailable", FieldValue.arrayUnion(flat));
                                                }

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG, "An error occurred : " + e.getMessage());
                                            }
                                        });


                                        //now for realtime (messaging)
                                        reference = FirebaseDatabase.getInstance().getReference(society_id).child(firebaseUser.getUid());
                                        String email = firebaseUser.getEmail();

                                        HashMap<String, String> hashMap = new HashMap<>();
                                        hashMap.put("id", firebaseUser.getUid());
                                        hashMap.put("email", email);
                                        hashMap.put("imageURL", "");
                                        hashMap.put("flatno", flat);
                                        hashMap.put("phoneno", phone_number);
                                        hashMap.put("name", name);


                                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    Log.i(TAG, "saveRealtimeDatabaseData task done");
                                                } else {
                                                    Log.i(TAG, "unable to add data in realtime database");
                                                }
                                            }
                                        });


                                    }


                                }
                            });
                        }


                    }
                }
            }
        });

    }


    public void showMessageOptions(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(ChangeSocietyID.this);
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

        final Dialog dialog = new Dialog(ChangeSocietyID.this);
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
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (listenerRegistration!= null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }

    }

}

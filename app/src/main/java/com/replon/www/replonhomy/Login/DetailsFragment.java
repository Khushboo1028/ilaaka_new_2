package com.replon.www.replonhomy.Login;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.replon.www.replonhomy.Home.Home;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static java.lang.Boolean.FALSE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    public static final String SOCIETY_NAME_KEY = "society_name";
    public static final String EMAIL_KEY = "username";
    public static final String FLAT_NO_KEY = "flat_no";
    public static final String TAG = "DetailsFragment";
    public static final String DATE_CREATED_KEY = "date_created";
    public static final String SOCIETY_ID_KEY = "society_id";
    public static final String ADMIN = "admin";
    public static final String NAME_KEY = "name";
    public static final String UNIQUE_ID_KEY = "unique_id";
    public static final String PHONE_NUMBER_KEY = "phone_number";

    View v;
    Fragment fragment;
    RelativeLayout enter_flat_rel;
    TextView tv_soc_name;
    EditText et_name,et_phone_number;
    String flat,wing,data;
    Button register,btn_contactus;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    DocumentReference soc_ref,mDocRef;

    String society_id,user_id;
    String dummy_soc_id="EsKxhoz7zIsMb5vlLX5I";
    String name,phone_number;
    ProgressBar progressBar;
    String doc_id,flat_doc_id;
    ListenerRegistration listenerRegistration;

    DatabaseReference reference;
    DocumentReference doc_flat_ref;
    List<String> categories,fb_flats;
    String dummy_doc_id,dummy_soc_name;

    public DetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_details, container, false);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());

        final Boolean isNewUser=getArguments().getBoolean("isNewUser");
        Log.i(TAG,"isNewUSer? "+isNewUser);
        final String extraValue = getArguments().getString("skip");
        final String society_name=getArguments().getString("society_name");
        society_id=getArguments().getString("society_id");
        doc_id=getArguments().getString("document_id");
        Log.i(TAG,"doc id is "+doc_id);
        Log.i(TAG,"society_Id is "+society_id);

        enter_flat_rel = (RelativeLayout) v.findViewById(R.id.enter_flatno_rel);
        tv_soc_name=(TextView)v.findViewById(R.id.soc_name);

        tv_soc_name.setText(society_name);

        et_name=(EditText) v.findViewById(R.id.name);
        et_phone_number=(EditText) v.findViewById(R.id.phone_number);
        register=(Button)v.findViewById(R.id.btn_register);
        progressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        btn_contactus=(Button) v.findViewById(R.id.btn_contactus);




        fb_flats=new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        user_id=firebaseUser.getUid();
        if (extraValue.equals("skip")){
            enter_flat_rel.setVisibility(View.GONE);
            tv_soc_name.setText("My Society");
        }

        final Spinner spinner = v.findViewById(R.id.spinner);


        // Spinner Drop down elements
        categories = new ArrayList<String>();

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




        listenerRegistration=flat_Ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        if (snapshot.getDocuments().isEmpty()) {
                           Log.i(TAG,"no societies");

                        } else {


                            if(snapshot.getDocuments().get(0).get("flats_available")!=null ) {

                                categories = (ArrayList) ((ArrayList) snapshot.getDocuments().get(0).get("flats_available"));
                                Collections.sort(categories);

                                flat_doc_id=snapshot.getDocuments().get(0).getId();

                                // Creating adapter for spinner
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, categories);

                                // Drop down layout style - list view with radio button
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                // attaching data adapter to spinner
                                spinner.setAdapter(dataAdapter);

                            }else{
                                spinner.setVisibility(View.GONE);
                            }


                            Log.i(TAG, "categories is " + categories);

                            if(categories.isEmpty()){
                                categories.add("No flats available");
                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(dataAdapter);

                                register.setVisibility(View.GONE);
                                et_name.setEnabled(false);
                                et_phone_number.setEnabled(false);
                                btn_contactus.setVisibility(View.VISIBLE);

                                btn_contactus.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent=new Intent(getActivity(), ContactUsActivity.class);
                                        startActivity(intent);
                                    }
                                });




                            }



                        }


                    }
                });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=et_name.getText().toString().trim();
                phone_number=et_phone_number.getText().toString().trim();
                if(name.isEmpty()||phone_number.isEmpty()){
                    showMessage("Error","All fields must be filled",R.drawable.ic_error_dialog);
                }else{
                    addUserData();
                }

            }

            private void addUserData(){
                progressBar.setVisibility(View.VISIBLE);
                String societies=getString(R.string.SOCIETIES);
                String user=getResources().getString(R.string.USER);
                et_name.setEnabled(false);
                et_phone_number.setEnabled(false);
                spinner.setEnabled(false);
                register.setEnabled(false);


                if(extraValue.equals("noskip")){



                   Log.i(TAG,"in no skip");

                    //adding data to firestore
                    soc_ref=db.collection(societies).document(doc_id);
                    Map<String, Object> dataToSave = new HashMap<String, Object>();

                    dataToSave.put(DATE_CREATED_KEY, new Timestamp(new Date()));
                    dataToSave.put(FLAT_NO_KEY, flat);
                    dataToSave.put(SOCIETY_ID_KEY,soc_ref);
                    dataToSave.put(SOCIETY_NAME_KEY, society_name);
                    dataToSave.put(EMAIL_KEY, firebaseUser.getEmail());
                    dataToSave.put(ADMIN,FALSE);
                    dataToSave.put(NAME_KEY,name);
                    dataToSave.put(UNIQUE_ID_KEY,society_id);
                    dataToSave.put(PHONE_NUMBER_KEY,phone_number);

                    mDocRef=FirebaseFirestore.getInstance().collection(user).document(user_id);
                    // mDocRef.collection(user).document(user_id).update(dataToSave);

                    mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG, "Data input success");
                            progressBar.setVisibility(View.GONE);

                            Intent login_intent = new Intent(getActivity(), Home.class);
                            login_intent.putExtra("isNewUser",isNewUser);
                            login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(login_intent);
                            getActivity().overridePendingTransition(0,0);
                            getActivity().finish();
                        }


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Data not saved"+ e.getMessage());
                        }
                    });

                    //adding data to realtime database for messaging
                    reference= FirebaseDatabase.getInstance().getReference(society_id).child(user_id);
                    String email=firebaseUser.getEmail();
                    String username=email.substring(0,email.indexOf("@"));

                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("id" ,user_id);
                    hashMap.put("email",email);
                    hashMap.put("imageURL","");
                    hashMap.put("flatno",flat);
                    hashMap.put("phoneno",phone_number);
                    hashMap.put("name",name);


                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                Log.i(TAG,"saveRealtimeDatabaseData task done");
                            }else{
                                Log.i(TAG,"unable to add data in realtime database");
                            }
                        }
                    });

                    firebaseUser.sendEmailVerification()
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





                }else{
                    soc_ref=db.collection(societies).document();
                    final Query flat_ref_dummy= db.collection(getString(R.string.SOCIETIES)).whereEqualTo("unique_id","DUMMY");
                    listenerRegistration=flat_ref_dummy.addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                            if (e != null) {
                                Log.w(TAG, "Listen failed.", e);
                                return;
                            }

                            if (snapshot.getDocuments().isEmpty()) {
                                Log.i(TAG,"no societies");

                            } else {

                                if(snapshot.getDocuments().get(0).get("unique_id")!=null) {

                                    dummy_doc_id = snapshot.getDocuments().get(0).getId();
                                    dummy_soc_name=snapshot.getDocuments().get(0).get("society_name").toString();
                                    Log.i(TAG,"dummy doc_id is "+dummy_doc_id);

                                    DocumentReference dummy_doc_ref=db.collection(getString(R.string.SOCIETIES)).document(dummy_doc_id);
                                    Log.i(TAG,"dummy doc ref is "+dummy_doc_ref);

                                    Map<String, Object> dataToSave = new HashMap<String, Object>();

                                    dataToSave.put(DATE_CREATED_KEY, new Timestamp(new Date()));
                                    dataToSave.put(FLAT_NO_KEY, "A - 000");
                                    dataToSave.put(SOCIETY_ID_KEY,dummy_doc_ref);
                                    dataToSave.put(SOCIETY_NAME_KEY, dummy_soc_name);
                                    dataToSave.put(EMAIL_KEY, firebaseUser.getEmail() );
                                    dataToSave.put(ADMIN,FALSE);
                                    dataToSave.put("name",name);
                                    dataToSave.put(PHONE_NUMBER_KEY,phone_number);

                                    mDocRef=FirebaseFirestore.getInstance().collection(getString(R.string.USER)).document(user_id);
                                    // mDocRef.collection(user).document(user_id).update(dataToSave);

                                    mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG, "Data input success");
                                            firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    showMessage("Success!","A verification email has been sent to you. Please follow the instructions in the email to verify your email.",R.drawable.ic_success_dialog);
                                                }
                                            });
                                            Intent login_intent = new Intent(getActivity(), Home.class);
                                            login_intent.putExtra("isNewUser",isNewUser);
                                            login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(login_intent);
                                            getActivity().overridePendingTransition(0,0);
                                            getActivity().finish();
                                            progressBar.setVisibility(View.GONE);
                                        }


                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE);
                                            Log.w(TAG, "Data not saved"+ e.getMessage());
                                            et_name.setEnabled(true);
                                            et_phone_number.setEnabled(true);
                                            spinner.setEnabled(true);
                                            register.setEnabled(true);
                                            Toast.makeText(getContext(),"An error occurred",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }

                            }


                        }
                    });



//
                    firebaseUser.sendEmailVerification()
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
                }

                if(flat_doc_id!=null){
                    doc_flat_ref=db.collection(societies).document(flat_doc_id);
                }


                if(doc_flat_ref!=null){
                    doc_flat_ref.update("flats_available", FieldValue.arrayRemove(flat));
                    doc_flat_ref.update("flats_unavailable", FieldValue.arrayUnion(flat));
                }





            }
        });

        return v;


    }

//    public void showMessage(String title, String message){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.show();
//
//    }


    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(getActivity());
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


}

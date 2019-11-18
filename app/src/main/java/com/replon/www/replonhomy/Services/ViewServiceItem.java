package com.replon.www.replonhomy.Services;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewServiceItem extends AppCompatActivity {

    EditText name,contact_no,aadhar_no;
    TextView aadhar_text,service_title,contact_text,edit_service;
    Button btn_edit_service;
    ImageView back;
    CircleImageView service_img;
    ProgressBar mProgressBar;
    ListenerRegistration registration;
    String soc_name;

    final String TAG = "ViewServiceItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewServiceItem.this);

        setContentView(R.layout.activity_view_service_item);

        name = findViewById(R.id.service_provider_name);
        contact_no = findViewById(R.id.contact_number);
        aadhar_text = findViewById(R.id.aadhar_text);
        aadhar_no = findViewById(R.id.aadhar_number);
        mProgressBar = findViewById(R.id.progressBar);

        service_title = (TextView) findViewById(R.id.service_title);
        service_title.setText(getIntent().getExtras().getString("service_title"));
        service_img = findViewById(R.id.service_provider_img);
        contact_text = findViewById(R.id.contact_text);

        String imageURL = getIntent().getExtras().getString("imageURL");

        if(!imageURL.equals("")) {
            Glide.with(getApplicationContext()).load(imageURL).placeholder(R.drawable.ic_service_default).into(service_img);
        }
        else
        {
            service_img.setImageResource(R.drawable.ic_service_default);
        }



        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_edit_service = findViewById(R.id.btn_edit_service);
        btn_edit_service.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);


        soc_name=getIntent().getStringExtra("soc_name");
        if(soc_name.equals(getString(R.string.DUMMY_SOC))){
            btn_edit_service.setEnabled(false);
        }
        name.setText(getIntent().getExtras().getString("name"));


        name.setEnabled(false);
        contact_no.setText(getIntent().getExtras().getString("contact_no"));
        contact_text.setText("Call");
        contact_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = contact_no.getText().toString();
                tel = "tel:"+tel;
                Intent acCall = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                startActivity(acCall);
            }
        });
        contact_no.setEnabled(false);

        edit_service = findViewById(R.id.edit_service);



        if(!getIntent().getExtras().getString("aadhar_no").equals("")){

            aadhar_no.setText(getIntent().getExtras().getString("aadhar_no"));
            aadhar_no.setEnabled(false);
        }
        else {
            aadhar_no.setVisibility(View.GONE);
            aadhar_text.setVisibility(View.GONE);

        }
        edit_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contact_no.setEnabled(true);
                aadhar_no.setEnabled(true);
                contact_text.setText("Contact");
                edit_service.setVisibility(View.GONE);
                btn_edit_service.setVisibility(View.VISIBLE);
                btn_edit_service.setText("Update " + getIntent().getExtras().getString("service_title"));

                name.setEnabled(true);


                btn_edit_service.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String doc_ref_intent = getIntent().getExtras().getString("doc_id");

                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        final String user = getString(R.string.USER);
                        final String user_id = currentFirebaseUser.getUid();
                        Log.i(TAG,"DOC REF FROM INTENT IS: "+doc_ref_intent);

                        DocumentReference doc_ref = db.collection(user).document(user_id);
                        Log.i(TAG,"DOC REF FOR UPDATE IS: "+doc_ref.toString());
                        mProgressBar.setVisibility(View.VISIBLE);

                        name.setEnabled(false);
                        contact_no.setEnabled(false);
                        aadhar_no.setEnabled(false);


                         registration = doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                                if(e!=null){
                                    Log.i("","Request failed");
                                }

                                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                        ? "Local" : "Server";
//                                Log.i(TAG,"SNAPSHOT = "+source);

                                if(snapshot!=null&&snapshot.exists()){
                                    DocumentReference soc_id_ref = (DocumentReference) snapshot.get("society_id");


                                    String st = service_title.getText().toString();
                                    st = st.toLowerCase();
                                    st = st.replace(" ", "_");
                                    DocumentReference doc_id =  soc_id_ref.collection(st).document(doc_ref_intent);

                                    Log.i(TAG,"ic_document ID UPDATE IS "+doc_id.toString());

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("date_added", new Timestamp(new Date()));
                                    data.put("contact", contact_no.getText().toString());
                                    data.put("document_id", doc_id);
                                    data.put("name", name.getText().toString());
                                    if(!getIntent().getExtras().getString("aadhar_no").equals("")){
                                        data.put("aadhar",aadhar_no.getText().toString());
                                    }

                                    doc_id.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mProgressBar.setVisibility(View.GONE);
                                            finish();

                                        }
                                    });
                                }else {
                                    Log.d(TAG, source + " data: null");
                                }
                            }
                        });




                    }
                });

                if(!getIntent().getExtras().getString("aadhar_no").equals("")){

                    aadhar_no.setText(getIntent().getExtras().getString("aadhar_no"));
                    aadhar_no.setEnabled(true);
                }
                else {
                    aadhar_no.setVisibility(View.GONE);
                    aadhar_text.setVisibility(View.GONE);

                }


            }
        });




    }

    @Override
    protected void onStop() {
        super.onStop();

        if (registration!= null) {
            registration.remove();
            registration = null;
        }
    }
}

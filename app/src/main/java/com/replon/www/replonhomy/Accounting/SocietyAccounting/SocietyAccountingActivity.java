package com.replon.www.replonhomy.Accounting.SocietyAccounting;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;



public class SocietyAccountingActivity extends AppCompatActivity {

    public static final String TAG = "SocietyAccountingActivity";
    String soc_name, flat_no,name,username,unique_id,admin;
    ImageView back;
    private RecyclerView recyclerView;
    private SocietyAdapter mAdapter;
    private ArrayList<ContentsAccounts> accountsList;

    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;

    String user_id;
    DocumentReference docRef;
    ListenerRegistration getDataListener;
    TextView add_record;

    //filters
    ImageView filter_icon;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_society_accounting);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        username = getIntent().getExtras().getString("username");
        unique_id=getIntent().getStringExtra("unique_id");
        admin=getIntent().getStringExtra("admin");

        add_record=(TextView)findViewById(R.id.generate);
        add_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getApplicationContext(),AddSocietyAccountActivity.class);
                intent.putExtra("society_name",soc_name);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("username",username);
                intent.putExtra("name",name);
                intent.putExtra("admin",admin);
                intent.putExtra("unique_id",unique_id);
                startActivity(intent);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical

        accountsList=new ArrayList<>();

        db=FirebaseFirestore.getInstance();
        currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        user_id=currentFirebaseUser.getUid();
        docRef=db.collection(getString(R.string.USER)).document(user_id);

        getData();

        mAdapter=new SocietyAdapter(getApplicationContext(),SocietyAccountingActivity.this, accountsList,soc_name,flat_no,username,name,admin,unique_id);
        recyclerView.setAdapter(mAdapter);

        filter_icon=(ImageView)findViewById(R.id.filter_icon);
        filter_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Filter button pressed");
                showFilterDialogue();
            }
        });
    }

    private void showFilterDialogue() {

        final Boolean[] boolean_approved = {false};
        final Boolean[] boolean_rejected = {false};
        final Boolean[] boolean_pending = {true};
        final Boolean[] boolean_all = {true};


        LayoutInflater factory = LayoutInflater.from(this);
        final View filterDialogView = factory.inflate(R.layout.accounts_filters, null);
        final AlertDialog filterDialog = new AlertDialog.Builder(this).create();
        filterDialog.setView(filterDialogView);

        final Button btn_pending = (Button) filterDialogView.findViewById(R.id.btn_pending);
        final Button btn_rejected = (Button) filterDialogView.findViewById(R.id.btn_rejected);
        final Button btn_all = (Button) filterDialogView.findViewById(R.id.btn_all);
        final Button btn_apply_filter = (Button) filterDialogView.findViewById(R.id.btn_apply_filter);
        final Button btn_approved=(Button)filterDialogView.findViewById(R.id.btn_approved);

        btn_approved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( boolean_rejected[0]){
                    boolean_rejected[0] =false;
                    btn_rejected.setTextColor(getColor(R.color.incorrect_red));
                    btn_rejected.setBackgroundColor(Color.parseColor("#1A707070"));
                }
                if( boolean_pending[0]){
                    boolean_pending[0] =false;
                    btn_pending.setTextColor(getColor(R.color.pendingYellow));
                    btn_pending.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                if( boolean_all[0]){
                    boolean_all[0] =false;
                    btn_all.setTextColor(getResources().getColor(R.color.black));
                    btn_all.setBackgroundColor(Color.parseColor("#1A707070"));
                }
                boolean_approved[0] =true;
                Log.i(TAG,"Button pressed");
                btn_approved.setBackgroundColor(getResources().getColor(R.color.greenSolved));
                btn_approved.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });

        btn_pending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( boolean_approved[0]){
                    boolean_approved[0] =false;
                    btn_approved.setTextColor(getResources().getColor(R.color.greenSolved));
                    btn_approved.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                if( boolean_rejected[0]){
                    boolean_rejected[0] =false;
                    btn_rejected.setTextColor(getResources().getColor(R.color.incorrect_red));
                    btn_rejected.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                if( boolean_all[0]){
                    boolean_all[0] =false;
                    btn_all.setTextColor(Color.parseColor("#000000"));
                    btn_all.setBackgroundColor(Color.parseColor("#1A707070"));
                }
                boolean_pending[0] =true;
                Log.i(TAG,"Button pressed");
                btn_pending.setBackgroundColor(getResources().getColor(R.color.pendingYellow));
                btn_pending.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });

        btn_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( boolean_approved[0]){
                    boolean_approved[0] =false;
                    btn_approved.setTextColor(getColor(R.color.greenSolved));
                    btn_approved.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                if( boolean_rejected[0]){
                    boolean_rejected[0] =false;
                    btn_rejected.setTextColor(getColor(R.color.black));
                    btn_rejected.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                if( boolean_pending[0]){
                    boolean_pending[0] =false;
                    btn_pending.setTextColor(getColor(R.color.pendingYellow));
                    btn_pending.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                boolean_all[0] =true;
                Log.i(TAG,"Button pressed");
                btn_all.setBackgroundColor(getResources().getColor(R.color.black));
                btn_all.setTextColor(Color.parseColor("#FFFFFF"));



            }
        });

        btn_rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if( boolean_approved[0]){
                    boolean_approved[0] =false;
                    btn_approved.setTextColor(getColor(R.color.greenSolved));
                    btn_approved.setBackgroundColor(Color.parseColor("#1A707070"));
                }


                if( boolean_pending[0]){
                    boolean_pending[0] =false;
                    btn_pending.setTextColor(getColor(R.color.pendingYellow));
                    btn_pending.setBackgroundColor(Color.parseColor("#1A707070"));
                }


                if( boolean_all[0]){
                    boolean_all[0] =false;
                    btn_all.setTextColor(getColor(R.color.black));
                    btn_all.setBackgroundColor(Color.parseColor("#1A707070"));
                }

                boolean_rejected[0] =true;
                Log.i(TAG,"Button pressed");
                btn_rejected.setBackgroundColor(getResources().getColor(R.color.incorrect_red));
                btn_rejected.setTextColor(Color.parseColor("#FFFFFF"));


            }
        });

        btn_apply_filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(boolean_approved[0]){
                    getFilteredData(3);
                }else if(boolean_pending[0]){
                    getFilteredData(1);

                }else if(boolean_rejected[0]){
                    getFilteredData(2);

                }else{
                    getData();
                }
                filterDialog.dismiss();
            }
        });

        filterDialog.show();
    }

    private void getData() {


        DocumentReference docRef = db.collection(getString(R.string.USER)).document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        DocumentReference soc_id_ref =document.getDocumentReference("society_id");
                        Log.i(TAG, "Society id is " + soc_id_ref);

                        getDataListener=soc_id_ref.collection("accounts")
                                .orderBy("paid_on", Query.Direction.DESCENDING)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.d(TAG, "Error:" + e.getMessage());
                                        } else {


                                            if (snapshots.getDocuments().isEmpty()) {

                                                Log.i(TAG, "No accounts exists");

                                            } else {

                                                accountsList.clear();
                                                mAdapter.notifyDataSetChanged();
                                                for (QueryDocumentSnapshot doc : snapshots) {

                                                    Timestamp date1 = (Timestamp) doc.get("date_created");
                                                    SimpleDateFormat sfd = new SimpleDateFormat("dd/mm/yyyy");
                                                    String date_created=sfd.format(date1.toDate());


                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");


                                                    String paid_on_date="",paid_by="",reference="";
                                                    if(doc.get("paid_on")==null){
                                                        paid_on_date="";
                                                    }else{

                                                        Timestamp paid_on_ts = (Timestamp) doc.get("paid_on");
                                                        paid_on_date= sfd_viewFormat.format(paid_on_ts.toDate());

                                                    }

                                                    if(doc.get("reference_number")==null){
                                                        reference="";
                                                    }else{
                                                        reference=doc.getString("reference_number");
                                                    }

                                                    if(doc.get("paid_by")==null){
                                                        paid_by="";
                                                    }else{
                                                        paid_by=doc.get("paid_by").toString();
                                                    }


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

                                                    DocumentReference document_id=(DocumentReference)doc.get("document_id");



                                                    accountsList.add(
                                                            new ContentsAccounts(
                                                                    doc.getString("amount"),
                                                                    date_created,
                                                                    document_id.getId(),
                                                                    doc.get("flat_no").toString(),
                                                                    fb_imageUrl,
                                                                    doc.get("name").toString(),
                                                                    paid_by,
                                                                    paid_on_date,
                                                                    doc.get("particulars").toString(),
                                                                    fb_pdfUrl,
                                                                    reference,
                                                                    Integer.parseInt(doc.get("status").toString())

                                                            )
                                                    );






                                                }


                                            }


                                        }

                                    }
                                });




                    } else {
                        Log.d(TAG, "No such document");


                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                }
            }
        });




    }

    private void getFilteredData(final int status) {


        DocumentReference docRef = db.collection(getString(R.string.USER)).document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        DocumentReference soc_id_ref =document.getDocumentReference("society_id");
                        Log.i(TAG, "Society id is " + soc_id_ref);

                        getDataListener=soc_id_ref.collection("accounts")
                                .orderBy("paid_on", Query.Direction.DESCENDING)
                                .whereEqualTo("status",status)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.d(TAG, "Error:" + e.getMessage());
                                        } else {


                                            if (snapshots.getDocuments().isEmpty()) {

                                                Log.i(TAG, "No accounts exists");
                                                showMessage("Oops!","No data matches your search!",R.drawable.ic_error_dialog);


                                            } else {

                                                accountsList.clear();
                                                mAdapter.notifyDataSetChanged();
                                                for (QueryDocumentSnapshot doc : snapshots) {

                                                    Timestamp date1 = (Timestamp) doc.get("date_created");
                                                    SimpleDateFormat sfd = new SimpleDateFormat("dd/mm/yyyy");
                                                    String date_created=sfd.format(date1.toDate());


                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");


                                                    String paid_on_date="",paid_by="",reference="";
                                                    if(doc.get("paid_on")==null){
                                                        paid_on_date="";
                                                    }else{

                                                        Timestamp paid_on_ts = (Timestamp) doc.get("paid_on");
                                                        paid_on_date= sfd_viewFormat.format(paid_on_ts.toDate());

                                                    }

                                                    if(doc.get("reference_number")==null){
                                                        reference="";
                                                    }else{
                                                        reference=doc.getString("reference_number");
                                                    }

                                                    if(doc.get("paid_by")==null){
                                                        paid_by="";
                                                    }else{
                                                        paid_by=doc.get("paid_by").toString();
                                                    }


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

                                                    DocumentReference document_id=(DocumentReference)doc.get("document_id");



                                                    accountsList.add(
                                                            new ContentsAccounts(
                                                                    doc.getString("amount"),
                                                                    date_created,
                                                                    document_id.getId(),
                                                                    doc.get("flat_no").toString(),
                                                                    fb_imageUrl,
                                                                    doc.get("name").toString(),
                                                                    paid_by,
                                                                    paid_on_date,
                                                                    doc.get("particulars").toString(),
                                                                    fb_pdfUrl,
                                                                    reference,
                                                                    Integer.parseInt(doc.get("status").toString())

                                                            )
                                                    );






                                                }


                                            }


                                        }

                                    }
                                });




                    } else {
                        Log.d(TAG, "No such document");


                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                }
            }
        });




    }

    public void showMessage(String title, String message, int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(SocietyAccountingActivity.this);
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
}

package com.replon.www.replonhomy.Accounting.MMT;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class MMTMonthActivity extends AppCompatActivity {

    public static final String TAG = "MMTMonthActivity";
    ImageView back;
    TextView generate;
    String soc_name, flat_no,name,username,unique_id,admin;

    private RecyclerView recyclerView;
    private MonthlyMaintenanceAdapter mAdapter;
    List<ContentsMonthlyMaintenance> maintenancelist;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;
    String user_id;
    DocumentReference docRef;
    ListenerRegistration getDataListener;
    ArrayList <ContentsMMT> MMTlist;

    EditText searchView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmtmonth);

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

        MMTlist=new ArrayList<>();

        db= FirebaseFirestore.getInstance();
        currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        user_id=currentFirebaseUser.getUid();
        docRef=db.collection(getString(R.string.USER)).document(user_id);

        generate=(TextView) findViewById(R.id.generate);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),GenerateBillActivity.class);
                intent.putExtra("society_name",soc_name);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("username",username);
                intent.putExtra("name",name);
                intent.putExtra("admin",admin);
                intent.putExtra("unique_id",unique_id);
                startActivity(intent);
            }
        });

        maintenancelist = new ArrayList<>();

        getData();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical

//        maintenancelist.add(new ContentsMonthlyMaintenance("July, 2019","12","69",false));
//        maintenancelist.add(new ContentsMonthlyMaintenance("June, 2019","12","0",true));
//        mAdapter = new MonthlyMaintenanceAdapter(getApplicationContext(),maintenancelist);
//        recyclerView.setAdapter(mAdapter);


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

    private void filter(String text) {
        ArrayList<ContentsMonthlyMaintenance> temp = new ArrayList();
        for (ContentsMonthlyMaintenance d : maintenancelist) {
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if (d.getMonth().toLowerCase().contains(text)) {
                temp.add(d);
            }
        }
        //update recyclerview
        mAdapter.updateList(temp);

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

                        getDataListener=soc_id_ref.collection("mmt")
                                .orderBy("maintenance_month", Query.Direction.DESCENDING)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {

                                        if (e != null) {
                                            Log.d(TAG, "Error:" + e.getMessage());
                                        } else {


                                            if (snapshots.getDocuments().isEmpty()) {
                                                Log.i(TAG, "No MMT exists");

                                            } else {

                                                MMTlist.clear();
//                                                mmtAdapter.notifyDataSetChanged();
                                                for (QueryDocumentSnapshot doc : snapshots) {

                                                    Timestamp date1 = (Timestamp) doc.get("date_created");

                                                    Timestamp due_date_ts = (Timestamp) doc.get("due_date");
                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM d, yyyy");
                                                    String due_date = sfd_viewFormat.format(due_date_ts.toDate());



                                                    int status= Integer.parseInt(doc.get("status").toString());
                                                    String paid_status = "";
                                                    if(status==0){
                                                        paid_status="pay_now";
                                                    }else if(status==1){
                                                        paid_status="pending";
                                                    }else if (status==2){
                                                        paid_status="rejected";
                                                    }else if(status==3){
                                                        paid_status="approved";
                                                    }

                                                    String paid_on_date="",paid_by="",reference="";
                                                    if(doc.get("paid_on")==null){
                                                        paid_on_date="";
                                                    }else{

                                                        Timestamp paid_on_ts = (Timestamp) doc.get("paid_on");
                                                        paid_on_date= sfd_viewFormat.format(paid_on_ts.toDate());

                                                    }

                                                    if(doc.get("reference_no")==null){
                                                        reference="";
                                                    }else{
                                                        reference=doc.getString("reference_no");
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
                                                        fb_imageUrl=(ArrayList)doc.get("pdf_url");
                                                    }

                                                    ArrayList<String> fb_pdfUrl=new ArrayList<>();

                                                    if(doc.get("pdf_url")==null){
                                                        fb_pdfUrl.add("");
                                                    }else{
                                                        fb_pdfUrl=(ArrayList)doc.get("pdf_url");
                                                    }



                                                    MMTlist.add(
                                                            new ContentsMMT(
                                                                    doc.getString("amount"),
                                                                    paid_on_date,
                                                                    paid_by,
                                                                    reference,
                                                                    paid_status,
                                                                    fb_imageUrl,
                                                                    fb_pdfUrl,
                                                                    due_date,
                                                                    doc.getString("maintenance_month"),
                                                                    doc.get("flat_no").toString(),
                                                                    doc.getReference().getId()


                                                            )
                                                    );






                                                }



                                                HashMap<Date,ArrayList<ContentsMMT>> hashMap=new HashMap<>();


                                                int size=MMTlist.size();
                                                Date date_month=new Date();

                                                for(int i=0;i<size;i++){

                                                    Log.i(TAG,"MMT month " + MMTlist.get(i).getMaintenance_month());
                                                    try {
                                                        date_month=new SimpleDateFormat("MMMM, yyyy").parse( MMTlist.get(i).getMaintenance_month());
                                                        Log.i(TAG,"date_month "+date_month);
                                                    } catch (ParseException ex) {
                                                        ex.printStackTrace();
                                                    }

                                                    if(!hashMap.containsKey(date_month)){
                                                        ArrayList<ContentsMMT> newList=new ArrayList<>();
                                                        newList.add(MMTlist.get(i));
                                                        hashMap.put(date_month,newList);
                                                        Log.i(TAG, "PUTTING THE MONTH: "+MMTlist.get(i).getMaintenance_month());
                                                    }else{
                                                        hashMap.get(date_month).add(MMTlist.get(i));
                                                        Log.i(TAG, "ADDING DATA TO MONTH: "+ MMTlist.get(i).getMaintenance_month());

                                                    }
                                                }



                                                Map<Date,ArrayList<ContentsMMT>> sortedMap = new TreeMap<Date,ArrayList<ContentsMMT>>(new Comparator<Date>() {
                                                    @Override
                                                    public int compare(Date o1, Date o2) {
                                                        return o2.compareTo(o1);
                                                    }
                                                });
                                                sortedMap.putAll(hashMap);
                                                Set<Date> monthSet = sortedMap.keySet();


                                                maintenancelist.clear();
                                                for(Date month: monthSet){
                                                    String paid_status;
                                                    int approvedCnt=0;
                                                    int pendingCnt=0;


//                                                    Log.i(TAG, "SIZE OF THIS MONTH: "+ month + ":  " + hashMap.get(month).size());
                                                    for(int i=0;i<sortedMap.get(month).size();i++){
                                                       paid_status=sortedMap.get(month).get(i).getPaid_status();
//                                                       Log.i(TAG,"paid_status is "+paid_status);
                                                       if(paid_status.equals("approved")){
                                                           approvedCnt++;
                                                       }else{
                                                           pendingCnt++;
                                                       }

                                                    }

                                                    SimpleDateFormat sfd_viewFormat = new SimpleDateFormat("MMMM, yyyy");
                                                    String month_string = sfd_viewFormat.format(month);
                                                    Log.i(TAG,"month string is "+month_string);


                                                    Boolean all_transactions_bool;
                                                    if(pendingCnt==0){
                                                        all_transactions_bool=true;
                                                    }else{all_transactions_bool=false;}
                                                    maintenancelist.add(new ContentsMonthlyMaintenance(month_string,String.valueOf(approvedCnt),String.valueOf(pendingCnt),all_transactions_bool));
                                                }

                                                mAdapter = new MonthlyMaintenanceAdapter(getApplicationContext(),maintenancelist,flat_no,admin);
                                                recyclerView.setAdapter(mAdapter);

                                                mAdapter.notifyDataSetChanged();







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

    @Override
    public void onBackPressed() {
        finish();
    }
}

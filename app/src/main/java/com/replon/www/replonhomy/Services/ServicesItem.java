package com.replon.www.replonhomy.Services;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.Settings;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ServicesItem extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_CALL = 54;
    ImageView back;
    TextView service_title, add_service;
    RecyclerView recyclerView;
    ServiceItemAdapter mAdapter;
    List<ContentsServiceItem> serviceItemList;
    RelativeLayout relativeLayout;

    String user;

    String TAG = "ServicesItem";
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;

    String user_id;
    DocumentReference docRef;
    private StorageReference mStorageRef;

    String st = "";
    Boolean dummy=FALSE;
    String soc_name,flat_no,username,name,admin;
    ListenerRegistration getDataListener;

    Context mContext;
    String profile_image_url= "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_item);
        user=getString(R.string.USER);
        back = (ImageView) findViewById(R.id.back);
        service_title = (TextView) findViewById(R.id.service_title);
        add_service = (TextView)findViewById(R.id.add_service);

        mContext=getApplicationContext();


        add_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),AddService.class);
                intent.putExtra("service_title",getIntent().getExtras().getString("service_title"));
                startActivity(intent);
            }
        });

        service_title.setText(getIntent().getExtras().getString("service_title"));


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        serviceItemList=new ArrayList<>();
        relativeLayout=(RelativeLayout)findViewById(R.id.rel_service);
        recyclerView = findViewById(R.id.recycler_view_services);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getData();

        soc_name=getIntent().getStringExtra("society_name");
        flat_no=getIntent().getStringExtra("flat_no");
        username=getIntent().getStringExtra("username");
        name=getIntent().getStringExtra("name");
        admin=getIntent().getStringExtra("admin");

        if(soc_name.equals(getString(R.string.DUMMY_SOC))){
            dummy=TRUE;
        }



//        serviceItemList.add(new ContentsServiceItem("Hello","9004243535","47375637657465","Housekeeper",""));
//        serviceItemList.add(new ContentsServiceItem("Mathur Dubey Ji","9917147714","1714771417417"));

//        mAdapter=new ServiceItemAdapter(this, serviceItemList);
//        recyclerView.setAdapter(mAdapter);


        enableSwipeToCall();

        if(!dummy) {

        enableSwipeToDelete();

        }

    }

    private void getData(){

        serviceItemList=new ArrayList<>();
        db= FirebaseFirestore.getInstance();
        currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        user_id=currentFirebaseUser.getUid();
        docRef=db.collection(user).document(user_id);


        mAdapter=new ServiceItemAdapter(getApplicationContext(), serviceItemList){
            @Override
            public void onBindViewHolder(@NonNull ServiceItemViewHolder serviceItemViewHolder, int i) {


                //so this i is position that will give you the specified item from the product list!


                final ContentsServiceItem contentsServiceItem =serviceItemList.get(i);

                //So now we bind the data using the help of this contentsEmergency object we created
                serviceItemViewHolder.name.setText(String.valueOf(contentsServiceItem.getName()));
                serviceItemViewHolder.contact_number.setText(String.valueOf(contentsServiceItem.getContact_number()));

                if(!contentsServiceItem.getAadhar_number().equals("")) {
                    serviceItemViewHolder.aadhar_number.setText(String.valueOf(contentsServiceItem.getAadhar_number()));
                }
                else
                {
                    serviceItemViewHolder.aadhar_text.setVisibility(View.GONE);
                    serviceItemViewHolder.aadhar_number.setVisibility(View.GONE);
                }

                if(!contentsServiceItem.getImageURL().equals("")){
                    Glide.with(mContext).load(contentsServiceItem.getImageURL()).placeholder(R.drawable.ic_service_default).into(serviceItemViewHolder.service_img);
                }else {
                    serviceItemViewHolder.service_img.setImageDrawable(mContext.getDrawable(R.drawable.ic_service_default));
                }

                serviceItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(mContext,ViewServiceItem.class);

                        intent.putExtra("name",contentsServiceItem.getName());
                        intent.putExtra("contact_no",contentsServiceItem.getContact_number());
                        intent.putExtra("aadhar_no",contentsServiceItem.getAadhar_number());
                        intent.putExtra("service_title",contentsServiceItem.getService_title());
                        intent.putExtra("imageURL",contentsServiceItem.getImageURL());
                        intent.putExtra("doc_ref",contentsServiceItem.getDocument_id().toString());
                        intent.putExtra("doc_id",contentsServiceItem.getDocument_id().getId());
                        intent.putExtra("soc_name",soc_name);



                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        mContext.startActivity(intent);

                    }
                });


                //serviceItemViewHolder.service_img.setImageDrawable(mContext.getResources().getDrawable(contentsServiceItem.get));


                //if image would have been present then
                // commentsViewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsEmergency.getImage()),null);

            }

        };
        recyclerView.setAdapter(mAdapter);

        getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                if(e!=null){
                    Log.i(TAG,"Request failed");
                }

                if(documentSnapshot != null && documentSnapshot.exists()){

                    Log.i(TAG,"Data is here "+documentSnapshot.getData());

                    DocumentReference soc_id_ref = (DocumentReference) documentSnapshot.get("society_id");
                    st = service_title.getText().toString();
                    st = st.toLowerCase();
                    st = st.replace(" ", "_");

                    if(documentSnapshot.get("society_name").toString().equals("My Society")){
                        dummy=TRUE;
                        add_service.setVisibility(View.GONE);

                    }


                    getDataListener=soc_id_ref.collection(st).orderBy("name").addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                            if (e != null) {
                                Log.d(TAG, "Error:" + e.getMessage());
                            }else{

                                if(queryDocumentSnapshots.getDocuments().isEmpty()){
                                    Log.i(TAG,"KAI NATHI");
                                }else{
                                    serviceItemList.clear();
                                    mAdapter.notifyDataSetChanged();

                                    for(QueryDocumentSnapshot doc : queryDocumentSnapshots){
//                                        if (doc.getString("profile_image_url")!= null || doc.getString("profile_image_url")!= ""){
//                                            profile_image_url=doc.getString("profile_image_url");
//                                        }

                                        if(!st.equals("internet_service")){
                                            Log.i(TAG, doc.getString("name"));
                                        serviceItemList.add(new ContentsServiceItem(
                                                    doc.getString("name"),
                                                    doc.getString("contact"),
                                                    doc.getString("aadhar"),
                                                service_title.getText().toString(),
                                                doc.getString("profile_image_url"),
                                                (DocumentReference)doc.get("document_id"))
                                            );
                                            Log.i(TAG,"Data added");
                                        }else {
                                            serviceItemList.add(new ContentsServiceItem(
                                                    doc.get("name").toString(),
                                                    doc.get("contact").toString(),
                                                    "",
                                                    service_title.getText().toString(),
                                                    doc.get("profile_image_url").toString(),
                                                    (DocumentReference)doc.get("document_id"))
                                            );


                                            Log.i(TAG,"Data added");

                                        }



                                    }

                                }
                            }
                            Log.i(TAG,"size is "+serviceItemList.size());
                            mAdapter.notifyDataSetChanged();

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
                relativeLayout = (RelativeLayout)findViewById(R.id.rel_service);

                final DocumentReference ref = serviceItemList.get(position).getDocument_id();

                String image_url_delete= serviceItemList.get(position).getImageURL();
                mAdapter.removeItem(position);

                if(image_url_delete!="") {

                        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(image_url_delete);
                        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "onSuccess: deleted image from storage");
                                ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        Snackbar snackbar = Snackbar
                                                .make(relativeLayout, "Service Item deleted", Snackbar.LENGTH_LONG);
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
                                                .make(relativeLayout, "Unable to delete service", Snackbar.LENGTH_LONG);
                                        snackbar.setActionTextColor(Color.RED);
                                        snackbar.show();
                                    }
                                });

                            }
                        });

                }else{
                    ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Snackbar snackbar = Snackbar
                                    .make(relativeLayout, "Service Deleted", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();
                            //  mAdapter.removeItem(position);


                        }
                    });

                }
                // mAdapter.notifyDataSetChanged();



            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDelete);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    private void enableSwipeToCall() {
        SwipeToCall swipeToCall = new SwipeToCall(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();

                if (ContextCompat.checkSelfPermission(ServicesItem.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ServicesItem.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL);

                    mAdapter.notifyDataSetChanged();

                }else{
                    String tel =serviceItemList.get(position).getContact_number();
                    tel = "tel:"+tel;
                    Intent acCall = new Intent(Intent.ACTION_DIAL);
                    acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    acCall.setData(Uri.parse(tel));
                    startActivity(acCall);

                    mAdapter.notifyDataSetChanged();
                }

                }



        };

        mAdapter.notifyDataSetChanged();

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToCall);
        itemTouchhelper.attachToRecyclerView(recyclerView);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL:

                // If request is cancelled, the result arrays are empty.
                if (grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted,

//                    openCamera();
                    Log.i(TAG,"PERMISSION WAS GRANTED!!!!");
                }


                return;

        }

        // other 'case' lines to check for other
        // permissions this app might request

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

    @Override
    protected void onResume() {
        super.onResume();
        getData();


    }
}

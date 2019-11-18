package com.replon.www.replonhomy.Settings.PersonalInfo.Vehicles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.replon.www.replonhomy.Complaints.ComplaintsActivity;
import com.replon.www.replonhomy.Complaints.ComplaintsFirebaseData;
import com.replon.www.replonhomy.Complaints.ContentsComplaints;
import com.replon.www.replonhomy.Complaints.SwipeToSolve;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Services.ContentsServiceItem;
import com.replon.www.replonhomy.Settings.Settings;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.SwipeToDelete;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import static java.lang.Boolean.TRUE;

public class VehiclesActivity extends AppCompatActivity {

    private static final String TAG="VehiclesActivity";
    ImageView back;
    private RecyclerView recyclerView;
    private  VehicleAdapter mAdapter;
    TextView add_veh;
    RelativeLayout relativeLayout;

    private List<ContentsVehicle> vehicleList;

    DocumentReference docRef;
    private StorageReference mStorageRef;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;

    String user_id;

    ListenerRegistration getDataListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), VehiclesActivity.this);

        setContentView(R.layout.activity_vehicles);

        back=findViewById(R.id.back);
        add_veh=findViewById(R.id.add_vehicles);

        vehicleList =new ArrayList<>();

        currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        mStorageRef= FirebaseStorage.getInstance().getReference();
        db=FirebaseFirestore.getInstance();
        user_id=currentFirebaseUser.getUid();


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical

        add_veh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddVehicleActivity.class);
                startActivity(intent);
            }
        });

        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getData();

//        vehicleList.add(new ContentsVehicle("", "MH 02 KK 6969","4 - Wheeler", "S69","Greenish Blue"));
//        vehicleList.add(new ContentsVehicle("", "MH 01 OK 9696","2 - Wheeler", "S60","Bluish Green"));

        mAdapter = new VehicleAdapter(this, vehicleList);
        recyclerView.setAdapter(mAdapter);

        enableSwipeToDelete();
    }


    private void getData() {

        docRef=db.collection(getString(R.string.USER)).document(user_id);
        db= FirebaseFirestore.getInstance();
        currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        user_id=currentFirebaseUser.getUid();


        docRef.collection("vehicles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        Log.i(TAG,"Inside getData");
                        vehicleList.clear();
                        List<String> cities = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                                vehicleList.add(new ContentsVehicle(
                                        doc.getString("vehicle_image_url"),
                                        doc.getString("vehicle_no"),
                                        doc.getString("vehicle_type"),
                                        doc.getString("vehicle_slot"),
                                        doc.getString("vehicle_color"),
                                        doc.getDocumentReference("document_id")

                                ));

                        }
                        mAdapter.notifyDataSetChanged();
                        Log.d(TAG, "Current cites in CA: " + cities);
                    }
                });

    }


    private void enableSwipeToDelete() {

        SwipeToDelete swipeToDelete = new SwipeToDelete(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();

                    new AlertDialog.Builder(VehiclesActivity.this)
                            .setTitle("")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {



                                    if(!vehicleList.get(position).getImg_url().isEmpty()) {


                                        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(vehicleList.get(position).getImg_url());
                                        Log.i(TAG, "Image URl is " + mStorageRef.toString());
                                        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: deleted image from storage");
                                                vehicleList.get(position).getDocument_ref().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {


                                                        Snackbar snackbar = Snackbar
                                                                .make(relativeLayout, "Vehicle deleted", Snackbar.LENGTH_LONG);
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
                                                                .make(relativeLayout, "An internal error occurred", Snackbar.LENGTH_LONG);
                                                        snackbar.setActionTextColor(Color.RED);
                                                        snackbar.show();
                                                    }
                                                });

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar snackbar = Snackbar
                                                        .make(relativeLayout, "An internal error occurred", Snackbar.LENGTH_LONG);
                                                snackbar.setActionTextColor(Color.RED);
                                                snackbar.show();
                                            }
                                        });
                                    }else{
                                        vehicleList.get(position).getDocument_ref().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                Snackbar snackbar = Snackbar
                                                        .make(relativeLayout, "Vehicle deleted", Snackbar.LENGTH_LONG);
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
                                                        .make(relativeLayout, "Unable to delete complaint", Snackbar.LENGTH_LONG);
                                                snackbar.setActionTextColor(Color.RED);
                                                snackbar.show();
                                            }
                                        });
                                    }


                                }


                            })
                            .setNegativeButton("Cancel", null).show();


                mAdapter.notifyDataSetChanged();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDelete);
        itemTouchhelper.attachToRecyclerView(recyclerView);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }
}

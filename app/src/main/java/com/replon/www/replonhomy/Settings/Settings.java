package com.replon.www.replonhomy.Settings;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.replon.www.replonhomy.Accounting.MMT.AccountingMainActivity;
import com.replon.www.replonhomy.Complaints.ComplaintsActivity;
import com.replon.www.replonhomy.Emergency.Emergency;
import com.replon.www.replonhomy.Login.Login;
import com.replon.www.replonhomy.Messaging.MessagesMainActivity;
import com.replon.www.replonhomy.Nearby.Nearby;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Services.Services;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Settings.HelpCenter.HelpCenterActivity;
import com.replon.www.replonhomy.Settings.PersonalInfo.PersonalInfoActivity;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Settings extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG="Settings";
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 89;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 78;
    public static final int MEDIA_TYPE_IMAGE = 1;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ImageView ham_icon;
    List<ContentsSettings> settingsList;
    DrawerLayout drawerLayout;

    ProgressBar progressBar;
    String soc_name, flat_no,name,username,soc_id,unique_id;
    TextView tv_society_name,tv_flatno,tv_username,tv_name,tv_flatno_profile;
    TextView profile_name;

    int count_vehicles=0;


    RelativeLayout settings_layout;
    CircleImageView user_profile;
    String imageURL="",old_image_url;
    String admin;

    String user,societies;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;
    Bitmap bitmap_photo;
    Uri selectedImageURI,file_camera_uri;
    Uri downloadUri;
    StorageTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    private static final int REQUEST_ACTIVITY = 1000;
    Uri file_camera;
    Boolean dummy=FALSE;
    ListenerRegistration listenerRegistration;
    String phone_number;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), Settings.this);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        user=getString(R.string.USER);
        societies=getString(R.string.SOCIETIES);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        //for camera intent
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        ham_icon = (ImageView) findViewById(R.id.ham_out);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);

        ham_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.START);
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        progressBar=(ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageView backSettings = (ImageView) findViewById(R.id.back_settings);
        backSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(0, 0);
            }
        });

        setupFirebaseAuth();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_home);
        tv_name = headerLayout.findViewById(R.id.name);
        tv_username=headerLayout.findViewById(R.id.username);

        tv_society_name=(TextView)findViewById(R.id.society_name);
        tv_flatno=(TextView)findViewById(R.id.flat_no);

        profile_name=findViewById(R.id.user_name);
        tv_flatno_profile=(TextView)findViewById(R.id.flat_no1);

        settings_layout=(RelativeLayout)findViewById(R.id.settings_layout);
        user_profile=(CircleImageView)findViewById(R.id.user_profile);


        db=FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        getProfile();


        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        admin = getIntent().getExtras().getString("admin");
        unique_id=getIntent().getStringExtra("unique_id");
        username =getIntent().getStringExtra("username");


        tv_society_name.setText(soc_name);
        tv_flatno.setText(flat_no);
        tv_username.setText(username);
        tv_name.setText(name);
        tv_flatno_profile.setText(currentFirebaseUser.getEmail());
        profile_name.setText(name);

        if(soc_name.equals(getString(R.string.DUMMY_SOC))){
            dummy=TRUE;
        }



        Log.i(TAG,"CurrentFirebaseUser is "+currentFirebaseUser);

        settingsList=new ArrayList<>();


        recyclerView = (RecyclerView) findViewById(R.id.settings_list);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical

        settingsList.add(new ContentsSettings(R.drawable.settings_notif,"Notifications"));
        settingsList.add(new ContentsSettings(R.drawable.settings_pass,"Personal Info"));
        settingsList.add(new ContentsSettings(R.drawable.settings_assist,"REPLON Assist"));
        settingsList.add(new ContentsSettings(R.drawable.settings_report,"Help Center"));
        settingsList.add(new ContentsSettings(R.drawable.settings_logut,"Logout"));

        db.collection(societies)
                .whereEqualTo("society_name",soc_name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Inside task.isSuccessful");
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Log.i(TAG, "Society found with is " + document.getId());
                                soc_id = document.get("unique_id").toString();

                            }
                        }else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection(user).document(currentFirebaseUser.getUid())
                .collection("vehicles")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        Log.i(TAG,"Inside getData");

                        for (QueryDocumentSnapshot doc : value) {

                            count_vehicles+=1;
                        }
                        Log.i(TAG,"Number of Vehicles is: "+count_vehicles);
                    }
                });


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            token = task.getResult().getToken();

                            Log.i(TAG, "token is " + token);

                        } else {
                            Log.i(TAG, "An error occurred " + task.getException().getMessage());
                        }
                    }
                });

        mAdapter=new SettingsAdapter(this, settingsList){


            @Override
            public void onBindViewHolder(@NonNull final SettingsAdapter.SettingsViewHolder settingsViewHolder, final int i) {

                ContentsSettings contentsSettings = settingsList.get(i);
                settingsViewHolder.setting.setText(String.valueOf(contentsSettings.getSetting()));

                settingsViewHolder.img_setting.setImageDrawable(getApplicationContext().getDrawable(contentsSettings.getImage()));

                settingsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(i==0){
                            Intent intent = new Intent();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                intent.setAction(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                                intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, getPackageName());
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                                intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                                intent.putExtra("app_package", getPackageName());
                                intent.putExtra("app_uid", getApplicationInfo().uid);
                            } else {
                                intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                            }
                            startActivity(intent);

                        }
                        else if(i==1){
                            Log.i(TAG,"Personal Info is pressed");
                            final Intent intent=new Intent(getApplicationContext(), PersonalInfoActivity.class);
                            intent.putExtra("currentName",name);
                            intent.putExtra("society_name",soc_name);
                            intent.putExtra("admin",admin.toUpperCase());
                            intent.putExtra("email",currentFirebaseUser.getEmail());
                            intent.putExtra("flat",flat_no);
                            intent.putExtra("soc_id",soc_id);
                            intent.putExtra("count_vehicles",String.valueOf(count_vehicles));
                            intent.putExtra("phone_number",phone_number);

                            startActivityForResult(intent,REQUEST_ACTIVITY);
                            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);

                        }
                        else if(i==2){
                            showSoonMessage("Coming Soon",R.drawable.ic_success_dialog);
                        }
                        else if(i==3){
                            Log.i(TAG,"Help center clicked");
                            Log.i(TAG,"admin is "+admin);
                            Intent intent=new Intent(getApplicationContext(), HelpCenterActivity.class);
                            intent.putExtra("currentName",name);
                            intent.putExtra("society_name",soc_name);
                            intent.putExtra("admin",admin.toUpperCase());
                            intent.putExtra("email",currentFirebaseUser.getEmail());
                            intent.putExtra("soc_id",soc_id);
                            startActivityForResult(intent,REQUEST_ACTIVITY);
                            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                        }
                        else if (i==4){
                            Log.i(TAG,"Logout button pressed");

                            new AlertDialog.Builder(Settings.this)
                                    .setTitle("")
                                    .setMessage("Are you sure you want to logout?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {

                                            progressBar.setVisibility(View.VISIBLE);
                                            final DocumentReference doc_id=FirebaseFirestore.getInstance().collection(getString(R.string.USER)).document(currentFirebaseUser.getUid());

                                            doc_id.update("token", FieldValue.arrayRemove(token)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.i(TAG,"Token removed");
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.i(TAG,"An error occurred : "+e.getMessage());
                                                }
                                            });


                                            mAuth.signOut();
                                            finishAffinity();


                                        }
                                    })
                                    .setNegativeButton("No", null).show();





                        }
                    }
                });


            }

        };
        recyclerView.setAdapter(mAdapter);


        user_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPhoto();
            }
        });




    }

    private void getProfile() {

        if(currentFirebaseUser!=null){
            final String user_id = currentFirebaseUser.getUid();
            String user = getResources().getString(R.string.USER);
            DocumentReference docRef = db.collection(user).document(user_id);
            Log.i(TAG,"I am in getProfiile");

            listenerRegistration=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }

                    String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                            ? "Local" : "Server";
                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, source + " Our data is here data: " + snapshot.getData());

//
//                        if(snapshot.get("society_id")!=null){
//                            soc_id = snapshot.get("society_id").toString();
//                            Log.i(TAG,"soc_id is "+soc_id);
//                        }

                        if(snapshot.get("profile_url")!=null){
                            imageURL = snapshot.get("profile_url").toString();
                            Log.i(TAG,"imageURL is "+imageURL);
                        }

                        if(snapshot.get("phone_number")!=null){
                            phone_number = snapshot.get("phone_number").toString();
                            Log.i(TAG,"phone number is "+phone_number);
                        }
                        if(!imageURL.equals("")) {
                            Glide.with(getApplicationContext()).load(imageURL).placeholder(R.drawable.ic_service_default).into(user_profile);
                            old_image_url=imageURL;
                        }
                        else
                        {
                            user_profile.setImageResource(R.drawable.ic_service_default);


                        }
                    }
                }
            });

        }
    }

    public void dialogShowPhoto() {
        String takePhoto = "Take Photo";
        String chooseFromLibrary = "Choose from Gallery";
        final String removePhoto = "Remove Profile Photo";
        final CharSequence[] items = {takePhoto, chooseFromLibrary,removePhoto};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        final String finalTakephoto = takePhoto;
        final String finalChooseFromLibrary = chooseFromLibrary;


        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals(finalChooseFromLibrary)) {

                    int PERMISSION_ALL = 1;
                    String[] PERMISSIONS = {
                            android.Manifest.permission.READ_CONTACTS,
                            android.Manifest.permission.WRITE_CONTACTS,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_SMS,
                            android.Manifest.permission.CAMERA
                    };

                    if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(Settings.this, PERMISSIONS, PERMISSION_ALL);
                    }
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(Settings.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){


//                            final Dialog dialog1 = new Dialog(Settings.this);
//                            dialog1.setContentView(R.layout.dialog_new);
//                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            Log.i(TAG,"NEW DIALOG");
//
//                            Button btn_positive = dialog1.findViewById(R.id.btn_positive);
//                            Button btn_negative = dialog1.findViewById(R.id.btn_negative);
//                            TextView dialog_title = dialog1.findViewById(R.id.dialog_title);
//                            TextView dialog_message = dialog1.findViewById(R.id.dialog_message);
//                            ImageView dialog_icon = dialog1.findViewById(R.id.dialog_img);
//
//                            dialog_title.setText("Permission Denied");
//                            dialog_message.setText("You might have denied the permission for using the Storage. Please go to phone settings and enable the permissions for Home.");
//
//
//                            btn_positive.setText("OK");
//                            btn_negative.setText("Go to Settings");
//                            btn_positive.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    dialog1.dismiss();
//                                }
//                            });
//                            btn_negative.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
////                                    Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
////                                    startActivity(myIntent);
//
//                                    Intent i = new Intent();
//                                    i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    i.addCategory(Intent.CATEGORY_DEFAULT);
//                                    i.setData(Uri.parse("package:" + getPackageName()));
//                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                                    startActivity(i);
//                                }
//                            });
//                            dialog_icon.setImageResource(R.drawable.ic_error_dialog);
//                            dialog1.show();

                        }else {
                            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                        }


                    }else{
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }

                }
                else if(items[item].equals(removePhoto)){
                    removePhoto();
                }
                else if (items[item].equals(finalTakephoto)) {


                    int PERMISSION_ALL = 1;
                    String[] PERMISSIONS = {
                            android.Manifest.permission.READ_CONTACTS,
                            android.Manifest.permission.WRITE_CONTACTS,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_SMS,
                            android.Manifest.permission.CAMERA
                    };

                    if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(Settings.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(Settings.this,
                                Manifest.permission.CAMERA) == true){

//                            final Dialog dialog1 = new Dialog(Settings.this);
//                            dialog1.setContentView(R.layout.dialog_new);
//                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                            Log.i(TAG,"NEW DIALOG");
//
//                            Button btn_positive = dialog1.findViewById(R.id.btn_positive);
//                            Button btn_negative = dialog1.findViewById(R.id.btn_negative);
//                            TextView dialog_title = dialog1.findViewById(R.id.dialog_title);
//                            TextView dialog_message = dialog1.findViewById(R.id.dialog_message);
//                            ImageView dialog_icon = dialog1.findViewById(R.id.dialog_img);
//
//                            dialog_title.setText("Permission Denied");
//                            dialog_message.setText("You might have denied the permission for using the Camera App. Please go to phone settings and enable the permission for Home.");
//                            //        btn_negative.setVisibility(View.GONE);
//                            //        btn_positive.setVisibility(View.GONE);
//
//                            btn_positive.setText("OK");
//                            btn_negative.setText("Go to Settings");
//                            btn_positive.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    dialog1.dismiss();
//                                }
//                            });
//                            btn_negative.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
////                                    Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
////                                    startActivity(myIntent);
//
//                                    Intent i = new Intent();
//                                    i.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    i.addCategory(Intent.CATEGORY_DEFAULT);
//                                    i.setData(Uri.parse("package:" + getPackageName()));
//                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
//                                    startActivity(i);
//                                }
//                            });
//                            dialog_icon.setImageResource(R.drawable.ic_error_dialog);
//                            dialog1.show();

                        }else {
                            ActivityCompat.requestPermissions(Settings.this, new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, REQUEST_CAMERA);
                        }


                    }else{
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file_camera_uri =  getOutputMediaFileUri(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file_camera_uri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }


                }
            }
        });
        builder.show();


    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "ReplonHome");

        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }
    private void removePhoto() {
        Log.i(TAG,"removePhoto: Starting");
        Log.i(TAG,"old image url is "+old_image_url);
        if (old_image_url != null) {


            StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(old_image_url);

            mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "onSuccess: deleted image from storage");
                    user_profile.setImageResource(R.drawable.ic_service_default);

                    String user = getResources().getString(R.string.USER);
                    DocumentReference docRef = db.collection(user).document(currentFirebaseUser.getUid());
                    docRef.update("profile_url", "")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "Profile Url updated");
                                    progressBar.setVisibility(View.GONE);
                                    getProfile();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG, "An error occurred " + e.getMessage());
                            progressBar.setVisibility(View.GONE);
                            Snackbar snackbar = Snackbar.make(drawerLayout, "An error occurred", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();

                        }
                    });

                    //now updating in realtime database
                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference(unique_id).child(currentFirebaseUser.getUid());
                    HashMap<String,Object > map=new HashMap<>();
                    map.put("imageURL","");
                    reference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG,"ImageURL updated in realtime");
                        }
                    });


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG,"An error occurred "+e.getMessage());
                }
            });
        }
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG,"REQ CODE IS" + requestCode);

        if (requestCode == REQUEST_ACTIVITY && resultCode==1000){

            tv_name.setText(data.getStringExtra("name"));
            profile_name.setText(data.getStringExtra("name"));
            Log.i(TAG,"NAME WE GOT IS: " + data.getStringExtra("name"));

            tv_username.setText(data.getStringExtra("email"));
            tv_flatno_profile.setText(data.getStringExtra("email"));
            admin = data.getStringExtra("admin");

        }
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK ) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            }else {

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file_camera_uri);
                    user_profile.setImageURI(file_camera_uri);
                    selectedImageURI=file_camera_uri;

                    Log.i(TAG,"Image set");

                } catch (IOException e) {
                    e.printStackTrace();
                }



              uploadImage(file_camera_uri);

            }




        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK  && data != null && data.getData() != null) {

            selectedImageURI = data.getData();


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);


            }else{
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI);
                    user_profile.setImageBitmap(bitmap);

                    Log.i(TAG,"Image set");
                    uploadImage(selectedImageURI);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }
    }


    private void uploadImage(Uri selectedImageURI) {
        progressBar.setVisibility(View.VISIBLE);
        //delete old image
        Log.i(TAG, "Old_image url is" + old_image_url);
        if (old_image_url != null) {


        StorageReference mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(old_image_url);
        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.i(TAG, "Previous profile_image deleted");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "An error occurred " + e.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

        //upload new image
        final StorageReference ref = storageReference.child("profile_images/"+ UUID.randomUUID().toString());
//        uploadTask = ref.putFile(selectedImageURI);

        try {

            Bitmap bitmap_photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap_photo.compress(Bitmap.CompressFormat.JPEG,50,stream);
            byte[] byteArray = stream.toByteArray();
            uploadTask=ref.putBytes(byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    downloadUri = task.getResult();
                    imageURL= downloadUri.toString();

                    if(currentFirebaseUser!=null) {
                        final String user_id = currentFirebaseUser.getUid();

                        String user = getResources().getString(R.string.USER);
                        DocumentReference docRef = db.collection(user).document(user_id);

                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }

                            }
                        });


                        docRef.update("profile_url", imageURL)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i(TAG, "Profile Url updated");
                                        progressBar.setVisibility(View.GONE);
                                        getProfile();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "An error occurred " + e.getMessage());
                                progressBar.setVisibility(View.GONE);
                                Snackbar snackbar = Snackbar.make(drawerLayout, "An error occurred", Snackbar.LENGTH_LONG);
                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();

                            }
                        });

                        //now updating in realtime database
                        if(unique_id!=null) {

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(unique_id).child(user_id);
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageURL", imageURL);
                            reference.updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "ImageURL updated in realtime");
                                }
                            });
                        }




                    }

                }

            }

        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:

                // If request is cancelled, the result arrays are empty.
                if (grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted,

//                    openCamera();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file_camera_uri =  getOutputMediaFileUri(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, file_camera_uri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }

                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {

                        Log.i(TAG,"PERMISSION NAHI MILLIIIIIIIIII");
//                    ActivityCompat.requestPermissions(this,
//                            new String[]{Manifest.permission.CAMERA},
//                            MY_PERMISSIONS_REQUEST_CAMERA);


                    } else {

                    }
                }

                return;

        }

        // other 'case' lines to check for other
        // permissions this app might request

    }

//    public void showMessage(String title, String message){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.show();
//
//    }


    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Settings.this);
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        switch (id){
            case R.id.home_menu:
                finish();
                overridePendingTransition(0, 0);
                break;
            case R.id.messages_menu:
                if(!dummy) {
                    Intent messages_intent = new Intent(getApplicationContext(), MessagesMainActivity.class);
                    finish();
                    messages_intent.putExtra("society_name", soc_name);
                    messages_intent.putExtra("flat_no", flat_no);
                    messages_intent.putExtra("username", username);
                    messages_intent.putExtra("name", name);
                    messages_intent.putExtra("admin", admin);
                    messages_intent.putExtra("unique_id", unique_id);
                    startActivity(messages_intent);
                    overridePendingTransition(0, 0);
                }else{
                    showMessageOptions("Oops!","You are not able to access Messaging!\n Contact us for more information",R.drawable.ic_error_dialog);
                }
                break;
            case R.id.complaints_menu:
                Intent complaints_intent = new Intent(Settings.this, ComplaintsActivity.class);
                finish();
                complaints_intent.putExtra("society_name",soc_name);
                complaints_intent.putExtra("flat_no",flat_no);
                complaints_intent.putExtra("username",username);
                complaints_intent.putExtra("name",name);
                complaints_intent.putExtra("admin",admin);
                complaints_intent.putExtra("unique_id",unique_id);
                startActivity(complaints_intent);
                overridePendingTransition(0, 0);
                break;
//            case R.id.payments_menu:
//                Intent payments_intent = new Intent(Settings.this, Payments.class);
//                finish();
//                payments_intent.putExtra("society_name",soc_name);
//                payments_intent.putExtra("flat_no",flat_no);
//                payments_intent.putExtra("username",username);
//                payments_intent.putExtra("name",name);
//                payments_intent.putExtra("admin",admin);
//                payments_intent.putExtra("unique_id",unique_id);
//                startActivity(payments_intent);
//                overridePendingTransition(0, 0);
//                break;

            case R.id.accounting_menu:
                Intent acc_intent = new Intent(getApplicationContext(), AccountingMainActivity.class);
                finish();
                acc_intent.putExtra("society_name",soc_name);
                acc_intent.putExtra("flat_no",flat_no);
                acc_intent.putExtra("username",username);
                acc_intent.putExtra("name",name);
                acc_intent.putExtra("admin",  admin);
                acc_intent.putExtra("unique_id", unique_id);
                startActivity(acc_intent);
                overridePendingTransition(0,0);
                break;
            case R.id.services_menu:
                Intent services_intent = new Intent(Settings.this, Services.class);
                finish();
                services_intent.putExtra("society_name",soc_name);
                services_intent.putExtra("flat_no",flat_no);
                services_intent.putExtra("username",username);
                services_intent.putExtra("name",name);
                services_intent.putExtra("admin",admin);
                services_intent.putExtra("unique_id",unique_id);

                startActivity(services_intent);
                overridePendingTransition(0, 0);
                break;
            case R.id.nearby_menu:
                Intent nearby_intent = new Intent(Settings.this, Nearby.class);
                finish();
                nearby_intent.putExtra("society_name",soc_name);
                nearby_intent.putExtra("flat_no",flat_no);
                nearby_intent.putExtra("username",username);
                nearby_intent.putExtra("name",name);
                nearby_intent.putExtra("admin",admin);
                nearby_intent.putExtra("unique_id",unique_id);
                nearby_intent.putExtra("category_home", "");
                startActivity(nearby_intent);
                overridePendingTransition(0,0);
                break;

            case R.id.emergency_menu:
                Intent emergency_intent = new Intent(Settings.this, Emergency.class);
                finish();
                emergency_intent.putExtra("society_name",soc_name);
                emergency_intent.putExtra("flat_no",flat_no);
                emergency_intent.putExtra("username",username);
                emergency_intent.putExtra("name",name);
                emergency_intent.putExtra("admin",admin);
                emergency_intent.putExtra("unique_id",unique_id);
                startActivity(emergency_intent);
                overridePendingTransition(0,0);
                break;
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG,"Checking if user is logged in");
        if(user==null){
            Intent intent=new Intent(getApplicationContext(), Login.class);
            startActivity(intent);

        }
    }

    private void setupFirebaseAuth() {
        Log.d(TAG,"onAuthStateChanged:Setting up firebase auth");

        // Obtain the FirebaseAnalytics instance.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser=mAuth.getCurrentUser();

                //checks if the user is logged in
                checkCurrentUser(currentUser);
                if(currentUser!=null){
                    //user is signed in
                    Log.d(TAG,"onAuthStateChanged:signed_in");
                }
                else{
                    //user is signed out
                    Log.d(TAG,"onAuthStateChanged:signed_out");
                }


            }
        };

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        checkCurrentUser(currentUser);

    }
    @Override
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
        if(listenerRegistration!=null){
            listenerRegistration=null;
        }

    }

    public void showMessageOptions(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Settings.this);
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

    public void showSoonMessage(String title,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(Settings.this);
        dialog.setContentView(R.layout.dialog_new);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.i(TAG,"NEW DIALOG");

        Button btn_positive = dialog.findViewById(R.id.btn_positive);
        Button btn_negative = dialog.findViewById(R.id.btn_negative);
        TextView dialog_title = dialog.findViewById(R.id.dialog_title);
        TextView dialog_message = dialog.findViewById(R.id.dialog_message);
        ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);

        dialog_title.setText(title);
//        dialog_message.setText(message);
        dialog_message.setVisibility(View.GONE);
        btn_negative.setVisibility(View.GONE);
        btn_positive.setVisibility(View.GONE);
        dialog_icon.setImageResource(image);
        dialog.show();

    }
}

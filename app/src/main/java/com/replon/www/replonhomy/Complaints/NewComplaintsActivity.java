package com.replon.www.replonhomy.Complaints;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.UploadsAdapter;
import com.replon.www.replonhomy.Utility.UploadsAttachmentContent;
import com.replon.www.replonhomy.Utility.UserDataFirebase;
import com.replon.www.replonhomy.R;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.Boolean.FALSE;

public class NewComplaintsActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String TAG = "NewComplaintsActivity";
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 89;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 18;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ImageView add;
    private TextView cancel;
    ListenerRegistration getDataListener;

   //for images

    Uri downloadUri, file_camera_uri;
    ArrayList<String> imageURL;
    ArrayList<Uri>imageURIlist;
    StorageTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap_photo;


    ImageView addImage;
    TextView date;
    TextView time;

    TextView generate;
    EditText et_subject,et_description;

    ProgressBar mProgressBar;
    String admin;
    //firebase
    ArrayList<UserDataFirebase> user_arraylist;
    DocumentReference complaints_doc_ref;

    DocumentReference soc_id_ref;
    UserDataFirebase obj = new UserDataFirebase();


    List<UploadsAttachmentContent> uploadList;

    //for uploading image
    int count=0;

    boolean isConnected,monitoringConnectivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), NewComplaintsActivity.this);

        setContentView(R.layout.activity_new_complaint);


        date=(TextView)findViewById(R.id.date);
        addImage=(ImageView)findViewById(R.id.add);
        cancel=(TextView)findViewById(R.id.cancel);
        imageURL=new ArrayList<String>();//fore storing image url
        imageURIlist=new ArrayList<Uri>();

        et_subject=(EditText)findViewById(R.id.subject);
        et_description=(EditText)findViewById(R.id.description);


        uploadList=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));//by default manager is vertical
        admin=getIntent().getStringExtra("admin");
        Log.i(TAG,"admin is "+admin);



        Date today = new Date();
        SimpleDateFormat currentDateTime = new SimpleDateFormat("MMMM d, yyyy");
        String dateToStr = currentDateTime.format(today);
        date.setText(dateToStr);

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String timeToStr = currentTime.format(today);
        time=(TextView) findViewById(R.id.time);
        time.setText(timeToStr);



        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
               NewComplaintsActivity.super.onBackPressed();
            }
        });


        mAdapter=new UploadsAdapter(this, uploadList,imageURIlist);
        recyclerView.setAdapter(mAdapter);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPhoto();

            }
        });

        generate=(TextView)findViewById(R.id.generate);

        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        obj=(UserDataFirebase)getIntent().getSerializableExtra("Soc_ref");
//        Log.i(TAG,"This is soc ref "+obj.getSoc_id().toString());

        // to check if we are connected to Network
         isConnected = true;

        // to check if we are monitoring Network
         monitoringConnectivity = false;

        //for camera intent
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isConnected){
                    final Dialog dialog = new Dialog(NewComplaintsActivity.this);
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
                else if(et_subject.getText().toString().isEmpty()){

                    showMessage("Error!","Please enter the subject",R.drawable.ic_error_dialog);
                }
                else{
                    addData();
                }

            }
        });

        //add image
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();





    }


//    private boolean isNetworkAvailable() {
//        ConnectivityManager connectivityManager
//                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
//        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
//    }

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
            final Dialog dialog = new Dialog(NewComplaintsActivity.this);
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

    // Method to check network connectivity in Main Activity
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

            final Dialog dialog = new Dialog(NewComplaintsActivity.this);
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

        @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        // if network is being moniterd then we will unregister the network callback
        if (monitoringConnectivity) {
            final ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.unregisterNetworkCallback(connectivityCallback);
            monitoringConnectivity = false;
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);


            }else {

                imageURIlist.add(file_camera_uri);

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), file_camera_uri);
                    Bitmap photo = Bitmap.createScaledBitmap(bitmap,50,67,false);

                    uploadList.add(new UploadsAttachmentContent(photo));
                    mAdapter.notifyDataSetChanged();


                } catch (IOException e) {
                    e.printStackTrace();
                }

                mAdapter.notifyDataSetChanged();
                Log.i(TAG,"imageURL is "+imageURIlist);

            }




        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);


            }else{
                imageURIlist.add(data.getData());
                Uri temp=data.getData();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), temp);
                    Bitmap photo = Bitmap.createScaledBitmap(bitmap,50,67,false);

                    uploadList.add(new UploadsAttachmentContent(photo));
                    mAdapter.notifyDataSetChanged();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:

                // If request is cancelled, the result arrays are empty.
                if (grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted,

                   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file_camera_uri =  getOutputMediaFileUri(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file_camera_uri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    Log.i(TAG,"PERMISSION WAS GRANTED!!!!");
                }

                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            (this, Manifest.permission.CAMERA)) {

                        Log.i(TAG,"PERMISSION NAHI MILLIIIIIIIIII");

                    } else {

                    }
                }

                return;

        }

        // other 'case' lines to check for other
        // permissions this app might request

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

    public void dialogShowPhoto() {
        String takePhoto = "Take Photo";
        String chooseFromLibrary = "Choose from Gallery";
        String cancel = "Cancel";
        String addPhoto = "Add an Image";
        final CharSequence[] items = {takePhoto, chooseFromLibrary};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        //builder.setTitle(addPhoto);
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
                        ActivityCompat.requestPermissions(NewComplaintsActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(NewComplaintsActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){




                        }else {
                            ActivityCompat.requestPermissions(NewComplaintsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                        }


                    }else{
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }

                }
                else if (items[item].equals(finalTakephoto)) {
                    int PERMISSION_ALL = 1;
                    String[] PERMISSIONS = {
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_CONTACTS,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA
                    };

                    if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(NewComplaintsActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(NewComplaintsActivity.this,
                                Manifest.permission.CAMERA) == true){



                        }else {
                            ActivityCompat.requestPermissions(NewComplaintsActivity.this, new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
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


    private void addData(){

        if(imageURIlist.size()!=0){
            addImageData();
        }else{
            addNoImageData();
        }

    }


    private void addNoImageData() {

            et_subject.setEnabled(false);
            et_description.setEnabled(false);
            generate.setEnabled(false);
            final FirebaseFirestore db = FirebaseFirestore.getInstance();
            final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            final String societies = getString(R.string.SOCIETIES);
            final String user = getString(R.string.USER);

            final String user_id = currentFirebaseUser.getUid();

            user_arraylist = new ArrayList<>();

            DocumentReference docRef = db.collection(user).document(user_id);
            mProgressBar.setVisibility(View.VISIBLE);
            getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot,
                                    @javax.annotation.Nullable FirebaseFirestoreException e) {

                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                            ? "Local" : "Server";

                    if (snapshot != null && snapshot.exists()) {
                        Log.d(TAG, source + " Our data is here data: " + snapshot.getData());

                        String username_fb = snapshot.get("username").toString();
                        Log.i(TAG, "Username is " + username_fb);

                        DocumentReference soc_id_ref = (DocumentReference) snapshot.get("society_id");
                        String flat = snapshot.get("flat_no").toString();


                        Log.i(TAG, "Society id is " + soc_id_ref);

                        String subject = et_subject.getText().toString();
                        String description = et_description.getText().toString();

                        DocumentReference doc_id=soc_id_ref.collection("complaints").document();
                        Map<String, Object> data = new HashMap<>();
                        data.put("date", new Timestamp(new Date()));
                        data.put("description", description);
                        data.put("flat", flat);
                        data.put("image_url", imageURL);
                        data.put("solved", FALSE);
                        data.put("subject", subject);
                        data.put("user_id", user_id);
                        data.put("document_id",doc_id);

                        doc_id.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Intent intent=new Intent();
                                intent.putExtra("admin",admin);
//                                            startActivity(intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                mProgressBar.setVisibility(View.GONE);
                                showMessage("Error", "unable to generate complaint",R.drawable.ic_error_dialog);
                            }
                        });


                    } else {
                        Log.d(TAG, source + " data: null");
                    }

                }
            });

    }

    private void addImageData(){


        for(int image_pos=0;image_pos<imageURIlist.size();image_pos++){

            mProgressBar.setVisibility(View.VISIBLE);
            uploadImage(imageURIlist.get(image_pos));
            Log.i(TAG,"Images list is "+imageURIlist.get(image_pos).toString());

        }
        Log.i(TAG,"ImageURL string list size is "+imageURL.size());

        if(imageURL.size()==imageURIlist.size()){
            addNoImageData();
        }
        Log.i(TAG,"Image Url outside " +imageURL);



    }


    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(NewComplaintsActivity.this);
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


    private void uploadImage(Uri selectedImageURI){

        et_subject.setEnabled(false);
        et_description.setEnabled(false);
        generate.setEnabled(false);
        final StorageReference ref = storageReference.child("complaints_images/"+ UUID.randomUUID().toString());

//        String stringUri=selectedImageURI.toString();
//        String filePath = SiliCompressor.with(getApplicationContext()).compress(stringUri,null);
//        Uri uri_image_final = Uri.parse(filePath);
//
//        uploadTask = ref.putFile(uri_image_final);

        try {

            Bitmap bitmap_photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap_photo.compress(Bitmap.CompressFormat.JPEG,70,stream);
            byte[] byteArray = stream.toByteArray();
            bitmap_photo.recycle();

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
                if (task.isSuccessful()) {
                    count++;
                    downloadUri = task.getResult();
                    imageURL.add(downloadUri.toString());
                    Log.i(TAG,"The URL for these images is "+imageURL);

                    Log.i(TAG,"imageURIlist size inside upload data :"+imageURIlist.size());

                    if(imageURL.size()==imageURIlist.size()){


                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        final String societies = getString(R.string.SOCIETIES);
                        final String user = getString(R.string.USER);

                        final String user_id = currentFirebaseUser.getUid();

                        user_arraylist = new ArrayList<UserDataFirebase>();

                        DocumentReference docRef = db.collection(user).document(user_id);
                        mProgressBar.setVisibility(View.VISIBLE);
                        getDataListener=docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot,
                                                @javax.annotation.Nullable FirebaseFirestoreException e) {

                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e);
                                    return;
                                }
                                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                        ? "Local" : "Server";

                                if (snapshot != null && snapshot.exists()) {
                                    Log.d(TAG, source + " Our data is here data: " + snapshot.getData());

                                    String username_fb = snapshot.get("username").toString();
                                    Log.i(TAG, "Username is " + username_fb);

                                    DocumentReference soc_id_ref = (DocumentReference) snapshot.get("society_id");
                                    String flat = snapshot.get("flat_no").toString();


                                    Log.i(TAG, "Society id is " + soc_id_ref);

                                    String subject = et_subject.getText().toString();
                                    String description = et_description.getText().toString();

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("date", new Timestamp(new Date()));
                                    data.put("description", description);
                                    data.put("flat", flat);
                                    data.put("image_url", imageURL);
                                    data.put("solved", FALSE);
                                    data.put("subject", subject);
                                    data.put("user_id", user_id);


                                    Log.i(TAG,"Image url in add data is "+imageURL);
                                    DocumentReference complaint_ref=soc_id_ref.collection("complaints").document();
                                    data.put("document_id", complaint_ref);


                                    complaint_ref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.i(TAG,"admin is "+admin);
                                            Log.i(TAG,"Inside soc_id_ref Successlistener");
                                            Intent intent=new Intent();
                                            intent.putExtra("admin",admin);
//                                            startActivity(intent);
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG,"Failed");
                                        }
                                    });

                                } else {
                                    Log.d(TAG, source + " data: null");
                                }

                            }
                        });



                    }


                } else {

                    // Handle failures
                   Log.i(TAG,"Something happened");
                }


            }
        });
        Log.i(TAG,"The imageUrl array is "+imageURL);


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

package com.replon.www.replonhomy.NoticeBoard;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
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
import com.replon.www.replonhomy.Utility.UploadsAttachmentContent;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.UploadAllFilesAdapter;
import com.replon.www.replonhomy.Utility.UserDataFirebase;
import com.replon.www.replonhomy.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class NewNoticeActivity extends AppCompatActivity {

    public static final int REQUEST_LOAD_PDF = 250;
    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    public static final int MEDIA_TYPE_IMAGE = 1;

    public static final String TAG = "NewNoticeActivity";
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 89;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 67;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ImageView add;
    private TextView cancel;

    //for images

    Uri downloadUri,file_camera_uri;
    ArrayList<String> imageURL;
    ArrayList<String> pdfURL;
    ArrayList<Uri> pdfURIlist;
    ArrayList<Uri>imageURIlist;
    ArrayList<String>fileList;
    ArrayList<StorageReference>files_ref_list;
    StorageTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap_photo;

    ImageView addImage;
    TextView date,tv_time;

    TextView generate;
    EditText et_subject,et_description;

    ProgressBar mProgressBar;
    String admin;

    //firebase
    private DocumentReference mDocRef,mDocRef_society;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ArrayList<UserDataFirebase> user_arraylist;
    DocumentReference notice_doc_ref;

    DocumentReference soc_id_ref;
    UserDataFirebase obj = new UserDataFirebase();

    Boolean isConnected,monitoringConnectivity;
    ListenerRegistration getDataListener;


    List<UploadsAttachmentContent> uploadList; //using the same file from complaints for notices

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(),  NewNoticeActivity.this);

        setContentView(R.layout.activity_new_notice);

        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();

        date=(TextView)findViewById(R.id.date);
        tv_time=(TextView)findViewById(R.id.time);
        addImage=(ImageView)findViewById(R.id.add);
        cancel=(TextView)findViewById(R.id.cancel);
        imageURL=new ArrayList<String>();//for storing image url
        pdfURL=new ArrayList<String>();//for storing pdf url
        imageURIlist=new ArrayList<Uri>();
        pdfURIlist=new ArrayList<Uri>();
        fileList=new ArrayList<String>();

        et_subject=(EditText)findViewById(R.id.subject);
        et_description=(EditText)findViewById(R.id.description);


        uploadList=new ArrayList<>();
        files_ref_list=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));//by default manager is vertical
        admin=getIntent().getStringExtra("admin");
        Log.i(TAG,"admin is "+admin);

        Date today = new Date();
        SimpleDateFormat currentDateTime = new SimpleDateFormat("MMMM d, yyyy");
        String dateToStr = currentDateTime.format(today);
        date.setText(dateToStr);

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm a");
        String timeToStr = currentTime.format(today);
        tv_time.setText(timeToStr);


        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                NewNoticeActivity.super.onBackPressed();
            }
        });

        mAdapter=new UploadAllFilesAdapter(this, uploadList,imageURIlist,fileList);
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

        //for camera intent
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isConnected){
                    final Dialog dialog = new Dialog(NewNoticeActivity.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
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


                fileList.add("image");
                mAdapter.notifyDataSetChanged();
                Log.i(TAG,"imageURL is "+imageURIlist);

            }



        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
            imageURIlist.add(data.getData());
            Uri temp = data.getData();

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);


            }else{
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), temp);
                    Bitmap photo = Bitmap.createScaledBitmap(bitmap, 50, 67, false);

                    String filename = getFilename();
                    try {
                        FileOutputStream out = new FileOutputStream(filename);

                        //          write the compressed bitmap at the destination specified by filename.
                        photo.compress(Bitmap.CompressFormat.JPEG,75,out);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    uploadList.add(new UploadsAttachmentContent(photo));
                    fileList.add("image");
                    mAdapter.notifyDataSetChanged();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                // uploadList.add(new UploadsAttachmentContent(BitmapFactory.decodeFile(picturePath)));
            }



        }
        if (requestCode == REQUEST_LOAD_PDF && resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);


            }else{
               // pdfURIlist.add(data.getData());
                imageURIlist.add(data.getData());
                fileList.add("pdf");
                Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.drawable.ic_pdf_load);
                uploadList.add(new UploadsAttachmentContent(icon));

                mAdapter.notifyDataSetChanged();
            }

        }
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Replon/Notices_images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }





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
            final Dialog dialog = new Dialog(NewNoticeActivity.this);
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

            final Dialog dialog = new Dialog(NewNoticeActivity.this);
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
        String attachDocument="Choose Document";
        String cancel = "Cancel";
        String addPhoto = "Add an Image";
        final CharSequence[] items = {takePhoto, chooseFromLibrary,attachDocument};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        //builder.setTitle(addPhoto);
        final String finalTakephoto = takePhoto;
        final String finalChooseFromLibrary = chooseFromLibrary;
        final String finalAttachDocument = attachDocument;


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
                        ActivityCompat.requestPermissions(NewNoticeActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {



                        if (ActivityCompat.shouldShowRequestPermissionRationale(NewNoticeActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true) {


                        } else {
                            ActivityCompat.requestPermissions(NewNoticeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                        }


                    } else {
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }
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
                        ActivityCompat.requestPermissions(NewNoticeActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(NewNoticeActivity.this,
                                Manifest.permission.CAMERA) == true){



                        }else {
                            ActivityCompat.requestPermissions(NewNoticeActivity.this, new String[]{Manifest.permission.CAMERA},
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
                else if(items[item].equals(finalAttachDocument)){

                    int PERMISSION_ALL = 1;
                    String[] PERMISSIONS = {
                            android.Manifest.permission.READ_CONTACTS,
                            android.Manifest.permission.WRITE_CONTACTS,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_SMS,
                            android.Manifest.permission.CAMERA
                    };

                    if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(NewNoticeActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }


                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(NewNoticeActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){


                        }else {
                            ActivityCompat.requestPermissions(NewNoticeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                        }


                    }else{
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("application/pdf");
                        startActivityForResult(intent,REQUEST_LOAD_PDF);
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA:
                // If request is cancelled, the result arrays are empty.
                if (grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted,

//                    openCamera();
                    Log.i(TAG,"PERMISSION WAS GRANTED!!!!");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    file_camera_uri =  getOutputMediaFileUri(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, file_camera_uri);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }

                else {


                    showMessage("Oops!","Seems like you have denied permission",R.drawable.ic_error_dialog);
                }

                return;

        }

        // other 'case' lines to check for other
        // permissions this app might request

    }
    private void addData(){

        if(imageURIlist.size()!=0 || pdfURIlist.size()!=0){
            addImageData();
        }else{
            addNoImageData();
        }

    }

    private void addNoImageData() {


        et_subject.setEnabled(false);
        generate.setEnabled(false);
        et_description.setEnabled(false);
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

                    DocumentReference doc_id=soc_id_ref.collection("notices").document();

                    Map<String, Object> data = new HashMap<>();
                    data.put("date", new Timestamp(new Date()));
                    data.put("image_url", imageURL);
                    data.put("subject", subject);
                    data.put("description",et_description.getText().toString());
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
                            mProgressBar.setVisibility(View.VISIBLE);
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

        if(imageURIlist.size()!=0) {

            for (int image_pos = 0; image_pos < imageURIlist.size(); image_pos++) {
                Log.i(TAG, "Images list is " + imageURIlist.get(image_pos).toString());

                mProgressBar.setVisibility(View.VISIBLE);

                uploadImage(imageURIlist.get(image_pos),fileList.get(image_pos));
                Log.i(TAG, "Images list is " + imageURIlist.get(image_pos).toString());

            }

            Log.i(TAG,"In addImageData image part");
        }

//        if(pdfURIlist.size()!=0) {
//
//            for (int pdf_pos = 0; pdf_pos < pdfURIlist.size(); pdf_pos++) {
//                Log.i(TAG, "Images list is " + pdfURIlist.get(pdf_pos).toString());
//                mProgressBar.setVisibility(View.VISIBLE);
//                uploadImage(pdfURIlist.get(pdf_pos),"pdf");
//                Log.i(TAG, "Images list is " + pdfURIlist.get(pdf_pos).toString());
//
//            }
//            Log.i(TAG,"In addImageData pdf part");
//        }
    }



    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(NewNoticeActivity.this);
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


    private void uploadImage(Uri selectedImageURI, final String fileType){

        et_subject.setEnabled(false);
        generate.setEnabled(false);
        et_description.setEnabled(false);
        final StorageReference ref = storageReference.child("notices_files/"+ UUID.randomUUID().toString());


        if(fileType.equals("image")) {

            try {

                Bitmap bitmap_photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap_photo.compress(Bitmap.CompressFormat.JPEG, 70, stream);
                byte[] byteArray = stream.toByteArray();
                bitmap_photo.recycle();

                uploadTask = ref.putBytes(byteArray);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            uploadTask=ref.putFile(selectedImageURI);
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

                    downloadUri = task.getResult();

                    if(fileType.equals("pdf")){
                        pdfURL.add(downloadUri.toString());
                        Log.i(TAG,"in fileType.equals(pdf)");
                    }
                    if(fileType.equals("image")){
                        imageURL.add(downloadUri.toString());
                        Log.i(TAG,"in fileType.equals(image)");
                    }



                    Log.i(TAG, "The URL for these images is " + imageURL);
                    Log.i(TAG, "imageURIlist size inside upload data :" + imageURIlist.size());
                    Log.i(TAG,"fileList size "+fileList.size());
                    Log.i(TAG,"image_url size is "+imageURL.size());



                    if (fileList.size() == imageURL.size() +pdfURL.size()) {

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
//                                        String flat = snapshot.get("flat_no").toString();
//                                        String wing=snapshot.get("wing").toString();

                                    Log.i(TAG, "Society id is " + soc_id_ref);

                                    String subject = et_subject.getText().toString();

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("date", new Timestamp(new Date()));
                                    data.put("subject", subject);
                                    data.put("description",et_description.getText().toString());
                                    data.put("image_url", imageURL);
                                    data.put("pdf_url", pdfURL);


                                    Log.i(TAG,"Pdf url in add data is "+pdfURL);
                                    DocumentReference notice_ref=soc_id_ref.collection("notices").document();
                                    data.put("document_id", notice_ref);

                                    notice_ref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
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
    protected void onResume() {
        super.onResume();
        checkConnectivity();
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

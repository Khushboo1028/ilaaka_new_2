package com.replon.www.replonhomy.Settings.HelpCenter;

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
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.replon.www.replonhomy.Utility.UploadsAttachmentContent;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.UploadsAdapter;
import com.replon.www.replonhomy.Utility.UserDataFirebase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;


public class SomethingNotWorking extends AppCompatActivity {

    public static final String TAG = "SomethingNotWorking";
    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 89;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 18;

    ProgressBar progressBar;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String email;
    TextView send,cancel;
    EditText et_body;
    Boolean isConnected,monitoringConnectivity;
    String attachmentFile;
    Uri URI = null;
    private static final int PICK_FROM_GALLERY = 101;
    int columnIndex;
    ImageView add_attachment;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    ArrayList<UserDataFirebase> user_arraylist;
    String feedback_type;

    //for images

    Uri downloadUri;
    ArrayList<String> imageURL;
    ArrayList<Uri>imageURIlist;
    StorageTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap_photo;
    List<UploadsAttachmentContent> uploadList;
    int count=0;

    TextView subject,heading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), SomethingNotWorking.this);

        setContentView(R.layout.activity_something_not_working);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        send=(TextView)findViewById(R.id.send);
        cancel=(TextView)findViewById(R.id.cancel);
        add_attachment=(ImageView)findViewById(R.id.add_attachment);
        subject=(TextView)findViewById(R.id.subject);
        heading=(TextView)findViewById(R.id.heading);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        feedback_type=getIntent().getStringExtra("feedback_type");
        Log.i(TAG,"feedback_type is "+feedback_type);



        //add image
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        imageURIlist=new ArrayList<>();
        user_arraylist=new ArrayList<>();
        imageURL=new ArrayList<>();//fore storing image url
        imageURIlist=new ArrayList<>();

        email=firebaseUser.getEmail();
        et_body=(EditText)findViewById(R.id.description);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        uploadList=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));


        if(feedback_type!=null) {
            if (feedback_type.equals("error")) {
                subject.setText("Briefly Explain what happened? How do you reproduce this issue?");
            } else if (feedback_type.equals("Spam")) {
                subject.setText("Please explain the problem with proper details. We will take maximum efforts to make your experience better.");
                heading.setText("Spam or Abuse");

            } else if (feedback_type.equals("general")) {
                recyclerView.setVisibility(View.GONE);
                subject.setText("Briefly explain what you love, or what we could improve!");
                heading.setText("General Feedback");
                add_attachment.setVisibility(View.GONE);
            }
        }

        mAdapter=new UploadsAdapter(this, uploadList,imageURIlist);
        recyclerView.setAdapter(mAdapter);
        // to check if we are connected to Network
        isConnected = true;

        // to check if we are monitoring Network
        monitoringConnectivity = false;

        add_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPhoto();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected){
                    final Dialog dialog = new Dialog(SomethingNotWorking.this);
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
                else if(et_body.getText().toString().isEmpty()){
                    showMessage("Error","Fields cannot be empty.",R.drawable.ic_error_dialog);
                }else{
                    addData();
                }
            }
        });



    }




//adding image
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);


        }else {

            bitmap_photo=(Bitmap)data.getExtras().get("data");
            String filename = getFilename();
            try {
                FileOutputStream out = new FileOutputStream(filename);

                //          write the compressed bitmap at the destination specified by filename.
                bitmap_photo.compress(Bitmap.CompressFormat.JPEG,75,out);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            uploadList.add(new UploadsAttachmentContent(bitmap_photo));
            imageURIlist.add(bitmapToUriConverter(bitmap_photo));
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


            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            // uploadList.add(new UploadsAttachmentContent(BitmapFactory.decodeFile(picturePath)));
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

//                    openCamera();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                    Log.i(TAG,"PERMISSION WAS GRANTED!!!!");
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

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Replon/Complaints_images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }
    //to convert bitmap to image uri
    public Uri bitmapToUriConverter(Bitmap mBitmap) {
        Uri uri = null;
        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 100, 100);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap newBitmap = Bitmap.createScaledBitmap(mBitmap, 200, 200,
                    true);
            File file = new File(getApplicationContext().getFilesDir(), "Image"
                    + new Random().nextInt() + ".jpeg");
            FileOutputStream out = getApplicationContext().openFileOutput(file.getName(),
                    Context.MODE_PRIVATE);
            newBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            //get absolute path
            String realPath = file.getAbsolutePath();
            File f = new File(realPath);
            uri = Uri.fromFile(f);

        } catch (Exception e) {
            Log.e("Your Error Message", e.getMessage());
        }
        return uri;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(SomethingNotWorking.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){


                            final Dialog dialog1 = new Dialog(SomethingNotWorking.this);
                            dialog1.setContentView(R.layout.dialog_new);
                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Log.i(TAG,"NEW DIALOG");

                            Button btn_positive = dialog1.findViewById(R.id.btn_positive);
                            Button btn_negative = dialog1.findViewById(R.id.btn_negative);
                            TextView dialog_title = dialog1.findViewById(R.id.dialog_title);
                            TextView dialog_message = dialog1.findViewById(R.id.dialog_message);
                            ImageView dialog_icon = dialog1.findViewById(R.id.dialog_img);

                            dialog_title.setText("Permission Denied");
                            dialog_message.setText("You might have denied the permission for using the Storage. Please go to phone settings and enable the permissions for Home.");


                            btn_positive.setText("OK");
                            btn_negative.setText("Go to Settings");
                            btn_positive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog1.dismiss();
                                }
                            });
                            btn_negative.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    startActivity(myIntent);

                                    Intent i = new Intent();
                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    i.addCategory(Intent.CATEGORY_DEFAULT);
                                    i.setData(Uri.parse("package:" + getPackageName()));
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(i);
                                }
                            });
                            dialog_icon.setImageResource(R.drawable.ic_error_dialog);
                            dialog1.show();

                        }else {
                            ActivityCompat.requestPermissions(SomethingNotWorking.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                        }


                    }else{
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }

                }
                else if (items[item].equals(finalTakephoto)) {

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(SomethingNotWorking.this,
                                Manifest.permission.CAMERA) == true){


                            final Dialog dialog1 = new Dialog(SomethingNotWorking.this);
                            dialog1.setContentView(R.layout.dialog_new);
                            dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            Log.i(TAG,"NEW DIALOG");

                            Button btn_positive = dialog1.findViewById(R.id.btn_positive);
                            Button btn_negative = dialog1.findViewById(R.id.btn_negative);
                            TextView dialog_title = dialog1.findViewById(R.id.dialog_title);
                            TextView dialog_message = dialog1.findViewById(R.id.dialog_message);
                            ImageView dialog_icon = dialog1.findViewById(R.id.dialog_img);

                            dialog_title.setText("Permission Denied");
                            dialog_message.setText("You might have denied the permission for using the Camera App. Please go to phone settings and enable the permission for Home.");
                            //        btn_negative.setVisibility(View.GONE);
                            //        btn_positive.setVisibility(View.GONE);

                            btn_positive.setText("OK");
                            btn_negative.setText("Go to Settings");
                            btn_positive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog1.dismiss();
                                }
                            });
                            btn_negative.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Intent myIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                                    startActivity(myIntent);

                                    Intent i = new Intent();
                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    i.addCategory(Intent.CATEGORY_DEFAULT);
                                    i.setData(Uri.parse("package:" + getPackageName()));
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                    startActivity(i);
                                }
                            });
                            dialog_icon.setImageResource(R.drawable.ic_error_dialog);
                            dialog1.show();

                        }else {
                            ActivityCompat.requestPermissions(SomethingNotWorking.this, new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }


                    }else{
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }

                }
            }
        });
        builder.show();


    }

    private void addData(){

        if(imageURIlist.size()!=0){
            addImageData();
        }else{
            addNoImageData();
        }

    }


    private void addNoImageData() {
        et_body.setEnabled(false);
        send.setEnabled(false);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String societies = getString(R.string.SOCIETIES);
        final String user = getString(R.string.USER);

        final String user_id = currentFirebaseUser.getUid();

        user_arraylist = new ArrayList<UserDataFirebase>();

        DocumentReference docRef = db.collection(user).document(user_id);
        progressBar.setVisibility(View.VISIBLE);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

                    Map<String, Object> data = new HashMap<>();
                    data.put("date", new Timestamp(new Date()));
                    data.put("description", et_body.getText().toString());
                    data.put("image_url", imageURL);
                    data.put("user_id", user_id);
                    data.put("feedback_type","error");
                    data.put("society_ref",soc_id_ref);


                    Log.i(TAG,"Image url in add data is "+imageURL);
                    DocumentReference ref=db.collection("feedback").document();


                    ref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG,"Inside soc_id_ref Successlistener");
                            showMessage("Thanks","Your feedback helps us serve you better!",R.drawable.ic_success_dialog);
                            progressBar.setVisibility(View.GONE);
                            et_body.setEnabled(true);
                            et_body.setText("");
                            send.setEnabled(true);
                            uploadList.clear();
                            mAdapter.notifyDataSetChanged();

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

    private void addImageData(){


        for(int image_pos=0;image_pos<imageURIlist.size();image_pos++){

            progressBar.setVisibility(View.VISIBLE);
            uploadImage(imageURIlist.get(image_pos));
            Log.i(TAG,"Images list is "+imageURIlist.get(image_pos).toString());

        }
        Log.i(TAG,"ImageURL string list size is "+imageURL.size());

        if(imageURL.size()==imageURIlist.size()){
            addNoImageData();
        }
        Log.i(TAG,"Image Url outside " +imageURL);



    }

    private void uploadImage(Uri selectedImageURI){
        et_body.setEnabled(false);
        send.setEnabled(false);
        final StorageReference ref = storageReference.child("feedback_images/"+ UUID.randomUUID().toString());
        uploadTask = ref.putFile(selectedImageURI);


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

                        final String user = getString(R.string.USER);

                        final String user_id = currentFirebaseUser.getUid();

                        user_arraylist = new ArrayList<UserDataFirebase>();

                        DocumentReference docRef = db.collection(user).document(user_id);
                        progressBar.setVisibility(View.VISIBLE);

                        Map<String, Object> data = new HashMap<>();
                        data.put("date", new Timestamp(new Date()));
                        data.put("description", et_body.getText().toString());
                        data.put("image_url", imageURL);
                        data.put("user_id", user_id);
                        data.put("feedback_type","error");



                        Log.i(TAG,"Image url in add data is "+imageURL);
                        DocumentReference ref=db.collection("feedback").document();


                        ref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG,"Inside soc_id_ref Successlistener");
                                showMessage("Thanks","Your feedback helps us serve you better!",R.drawable.ic_success_dialog);
                                progressBar.setVisibility(View.GONE);
                                et_body.setEnabled(true);
                                et_body.setText("");
                                send.setEnabled(true);
                                uploadList.clear();
                                mAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG,"Failed");
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

    /*----------------------------------standard code to copy everywhere---------------------*/
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
//            final Dialog dialog = new Dialog(SomethingNotWorking.this);
//            dialog.setContentView(R.layout.dialog_new);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//            Button btn_positive = dialog.findViewById(R.id.btn_positive);
//            Button btn_negative = dialog.findViewById(R.id.btn_negative);
//            TextView dialog_title = dialog.findViewById(R.id.dialog_title);
//            TextView dialog_message = dialog.findViewById(R.id.dialog_message);
//            ImageView dialog_icon = dialog.findViewById(R.id.dialog_img);
//
//            dialog_title.setText("Internet Unavailable");
//            dialog_message.setText("Poor network connectivity detected! Please check your internet connection");
//            //        btn_negative.setVisibility(View.GONE);
//            //        btn_positive.setVisibility(View.GONE);
//
//            btn_positive.setText("OK");
//            btn_negative.setText("Go to Settings");
//            btn_positive.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//            btn_negative.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent myIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
//                    startActivity(myIntent);
//                }
//            });
//            dialog_icon.setImageResource(R.drawable.ic_no_internet);
//            dialog.show();
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

            final Dialog dialog = new Dialog(SomethingNotWorking.this);
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

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(SomethingNotWorking.this);
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
//        overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
    }
}

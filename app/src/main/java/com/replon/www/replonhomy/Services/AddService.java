package com.replon.www.replonhomy.Services;

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
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.Settings;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddService extends AppCompatActivity {

    public static final String TAG = "AddService";
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 45;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 43;
    public static final int MEDIA_TYPE_IMAGE = 1;
    ImageView back;
    TextView service_title,aadhar_text;
    Button add_service;
    EditText name, contact_no, aadhar_no;
    String st = "";
    FirebaseStorage storage;
    StorageReference storageReference;

    String imageURL="";

    CircleImageView service_provider_img;
    Bitmap bitmap_photo;
    Uri selectedImageURI,file_camera_uri;
    Uri downloadUri;
    StorageTask uploadTask;
    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;

    ListenerRegistration getDataListener;
    ProgressBar mProgressBar;
    String user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AddService.this);

        setContentView(R.layout.activity_add_service);

        user=getString(R.string.USER);
        back = (ImageView) findViewById(R.id.back);
        service_title = (TextView) findViewById(R.id.service_title);

        st = getIntent().getExtras().getString("service_title");

        service_title.setText("Add "+st);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //for camera intent
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        add_service = (Button) findViewById(R.id.btn_add_service);
        add_service.setText("Add "+st);

        st = st.toLowerCase();
        st = st.replace(" ", "_");

        mProgressBar=(ProgressBar)findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);

        aadhar_no = (EditText)findViewById(R.id.aadhar_number);
        aadhar_text = (TextView)findViewById(R.id.aadhar_text);

        if(st.equals("internet_service")){
            aadhar_no.setVisibility(View.GONE);
            aadhar_text.setVisibility(View.GONE);
        }

        //add Image
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        service_provider_img = findViewById(R.id.service_provider_img);

        service_provider_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPhoto();
            }
        });

        add_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = (EditText)findViewById(R.id.service_provider_name);
                contact_no = (EditText)findViewById(R.id.contact_number);

                if(name.getText().toString().trim().isEmpty()||contact_no.getText().toString().trim().isEmpty()){
                    showMessage("Error","Fields cannot be empty",R.drawable.ic_error_dialog);
                }
                else if(!isValid(contact_no.getText().toString().trim())){
                    showMessage("Error","Contact number not valid",R.drawable.ic_error_dialog);
                }
                else {

                    if (selectedImageURI != null) {
                        mProgressBar.setVisibility(View.VISIBLE);
                        aadhar_no.setEnabled(false);
                        name.setEnabled(false);
                        contact_no.setEnabled(false);
                        add_service.setEnabled(false);
                        uploadImage();
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);
                        aadhar_no.setEnabled(false);
                        name.setEnabled(false);
                        contact_no.setEnabled(false);
                        add_service.setEnabled(false);
                        addNoImageData();
                    }
                }

            }
        });

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

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
                    Log.i(TAG,"PERMISSION WAS GRANTED!!!!");
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

    private void addNoImageData(){
        name = (EditText)findViewById(R.id.service_provider_name);
        contact_no = (EditText)findViewById(R.id.contact_number);
        add_service.setEnabled(false);


        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String user_id = currentFirebaseUser.getUid();

        final DocumentReference docRef = db.collection(user).document(user_id);

       getDataListener= docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                DocumentReference soc_ref = (DocumentReference) documentSnapshot.get("society_id");

                DocumentReference doc_id =  soc_ref.collection(st).document();
                Map<String, Object> data = new HashMap<>();
                data.put("date_added", new Timestamp(new Date()));
                data.put("contact", contact_no.getText().toString());
                data.put("document_id", doc_id);
                data.put("name", name.getText().toString());
                data.put("profile_image_url", imageURL);

                if(!st.equals("internet_service")) {
                    data.put("aadhar", aadhar_no.getText().toString());
                }


                doc_id.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG,"ADDDEDDD");
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG,"NAHI HUA");
                        showMessage("Error!","Could not add this service",R.drawable.ic_error_dialog);
                        aadhar_no.setEnabled(true);
                        name.setEnabled(true);
                        contact_no.setEnabled(true);
                        add_service.setEnabled(true);

                    }
                });
            }
        });
    }

    private void uploadImage(){
        add_service.setEnabled(false);
        final StorageReference ref = storageReference.child("services_images/"+ UUID.randomUUID().toString());

        try {

            Bitmap bitmap_photo = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageURI);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap_photo.compress(Bitmap.CompressFormat.JPEG,30,stream);
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
                if (task.isSuccessful()){
                    downloadUri = task.getResult();
                    imageURL= downloadUri.toString();

                    addNoImageData();
                }
            }
        });
    }

    public void dialogShowPhoto() {
        String takePhoto = "Take Photo";
        String chooseFromLibrary = "Choose from Gallery";
        final String removePhoto = "Remove Profile Photo";
        final CharSequence[] items = {takePhoto, chooseFromLibrary};
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
                        ActivityCompat.requestPermissions(AddService.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddService.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){

//
//                            final Dialog dialog1 = new Dialog(AddService.this);
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
                            ActivityCompat.requestPermissions(AddService.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                        }


                    }else{
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }

                }
//                else if(items[item].equals(removePhoto)){
//                    removePhoto();
//                }
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
                        ActivityCompat.requestPermissions(AddService.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddService.this,
                                Manifest.permission.CAMERA) == true){

//                            final Dialog dialog1 = new Dialog(AddService.this);
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
                            ActivityCompat.requestPermissions(AddService.this, new String[]{Manifest.permission.CAMERA},
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);

            }else{

                service_provider_img.setImageURI(file_camera_uri);
                selectedImageURI=file_camera_uri;
                //uploadImage();

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
                    // bitmap_photo = Bitmap.createScaledBitmap(bitmap,50,67,false);
                    service_provider_img.setImageBitmap(bitmap);

                    Log.i(TAG,"Image set");
                    //uploadImage();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }




        }
    }


    public static boolean isValid(String s)
    {
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");

        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }


    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(AddService.this);
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
    protected void onStop() {
        super.onStop();

        if (getDataListener!= null) {
            getDataListener.remove();
            getDataListener = null;
        }
    }

}

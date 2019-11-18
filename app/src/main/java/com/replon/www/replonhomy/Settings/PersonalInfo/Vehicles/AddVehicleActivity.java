package com.replon.www.replonhomy.Settings.PersonalInfo.Vehicles;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import com.replon.www.replonhomy.Services.AddService;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
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

import javax.annotation.Nullable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class AddVehicleActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    public static final String TAG = "AddVehicleActivity";

    ImageView back, vehicle_image;
    EditText et_veh_num, et_veh_color,et_park_slot;
    Spinner spinner;
    Button add_vehicle;

    //for images
    Uri downloadUri;
    String imageURL="";
    Uri imageURI;
    StorageTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap_photo;
    Boolean imagePresent=FALSE;
    ProgressBar progressBar;
    ListenerRegistration getDataListener;




    //firebase
    ArrayList<UserDataFirebase> user_arraylist;
    DocumentReference complaints_doc_ref;

    String vehicle_type,vehicle_color,vehicle_no,vehicle_slot,vehicle_image_url;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AddVehicleActivity.this);

        setContentView(R.layout.activity_add_vehicle);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });


        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        back=(ImageView) findViewById(R.id.back);
        vehicle_image =(ImageView) findViewById(R.id.vehicle_image);
        et_park_slot=(EditText) findViewById(R.id.parking_slot);
        et_veh_color =(EditText) findViewById(R.id.vehicle_color);
        et_veh_num=(EditText) findViewById(R.id.vehicle_number);
        add_vehicle =(Button)findViewById(R.id.add_vehicle);
        spinner = (Spinner) findViewById(R.id.spinner);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        vehicle_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPhoto();

            }
        });


        List<String> categories = new ArrayList<String>();
        //categories.add("");
        categories.add("4 - Wheeler");
        categories.add("2 - Wheeler");



        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vehicle_type = String.valueOf(spinner.getSelectedItem());

//                int index=data.indexOf("-");
//                wing=data.substring(index-2,index-1);
//                flat=data.substring(index+2);
//                Log.i(TAG,"index is "+index);
//                Log.i(TAG,"wing is "+wing);
//                Log.i(TAG,"flat  is "+flat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        add_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imagePresent){
                    vehicle_no=et_veh_num.getText().toString().toUpperCase().trim();
                    vehicle_color=et_veh_color.getText().toString().trim();
                    vehicle_slot=et_park_slot.getText().toString().trim();
                    if(vehicle_color.isEmpty()||vehicle_slot.isEmpty()||vehicle_no.isEmpty()){
                        showMessage("Error!","All fields must be filled",R.drawable.ic_error_dialog);
                    }else{
                        progressBar.setVisibility(View.VISIBLE);
                        uploadImage();
                    }

                }else{

                    vehicle_no=et_veh_num.getText().toString().toUpperCase().trim();
                    vehicle_color=et_veh_color.getText().toString().trim();
                    vehicle_slot=et_park_slot.getText().toString().trim();

                    if(vehicle_color.isEmpty()||vehicle_slot.isEmpty()||vehicle_no.isEmpty()){
                        showMessage("Error!","All fields must be filled",R.drawable.ic_error_dialog);
                    }else{
                        progressBar.setVisibility(View.VISIBLE);
                        addNoImageData();
                    }
                }
            }
        });
    }

    private void uploadImage(){
        final StorageReference ref = storageReference.child("your_vehicle_K/"+ UUID.randomUUID().toString());
        uploadTask = ref.putFile(imageURI);
        vehicle_image.setEnabled(false);

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

    private void addNoImageData(){


        et_park_slot.setEnabled(false);
        et_veh_color.setEnabled(false);
        et_veh_num.setEnabled(false);
        spinner.setEnabled(false);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        String user_id = currentFirebaseUser.getUid();


        final DocumentReference docRef = db.collection(getString(R.string.USER)).document(user_id);

            DocumentReference doc_id =  docRef.collection("vehicles").document();

            Map<String, Object> data = new HashMap<>();
            data.put("date_created", new Timestamp(new Date()));
            data.put("vehicle_no",vehicle_no);
            data.put("document_id", doc_id);
            data.put("vehicle_color", vehicle_color);
            data.put("vehicle_image_url", imageURL);
            data.put("vehicle_slot",vehicle_slot);
            data.put("vehicle_type",vehicle_type);



            doc_id.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG,"Vehicle data added");
                    finish();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG,"NAHI HUA");
                    showMessage("Error!","Could not add this service",R.drawable.ic_error_dialog);
                    et_park_slot.setEnabled(true);
                    et_veh_color.setEnabled(true);
                    et_veh_num.setEnabled(true);
                    spinner.setEnabled(true);

                }
            });

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
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, RESULT_LOAD_IMAGE);
                }
                else if (items[item].equals(finalTakephoto)) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }
        });
        builder.show();


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {

            String filename = getFilename();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            bitmap_photo=(Bitmap)data.getExtras().get("data");
           // bitmap_photo.compress(Bitmap.CompressFormat.PNG, 75, out);
            vehicle_image.setImageBitmap(bitmap_photo);
            imageURI=bitmapToUriConverter(bitmap_photo);
            imagePresent=TRUE;
            //uploadImage();

        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK  && data != null && data.getData() != null) {
            imageURI = data.getData();

            String filename = getFilename();
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(filename);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageURI);
                // bitmap_photo = Bitmap.createScaledBitmap(bitmap,50,67,false);
//                bitmap.compress(Bitmap.CompressFormat.JPEG,75,out);
                vehicle_image.setImageBitmap(bitmap);

                Log.i(TAG,"Image set");
                imageURI=bitmapToUriConverter(bitmap);
                imagePresent=TRUE;
                //uploadImage();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        Log.i(TAG,"imageURL is "+imageURI);
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

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(AddVehicleActivity.this);
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
    }
}

package com.replon.www.replonhomy.Accounting.MMT;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.replon.www.replonhomy.Utility.UploadsAttachmentContent;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.MultiSpinner;
import com.replon.www.replonhomy.Utility.UploadAllFilesAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GenerateBillActivity extends AppCompatActivity {

    public static final String TAG = "GenerateBillActivity";
    public static final int REQUEST_LOAD_PDF = 250;
    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 89;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 67;
    ProgressBar progressBar;
    ImageView back;
    EditText et_amount,et_reference;
    TextView tv_save,tv_date;
    RelativeLayout date_layout;
    String transaction_date;
    DatePickerDialog picker;

    Spinner spinner;
    //for images
    Uri downloadUri,file_camera_uri;
    ArrayList<String> imageURL;
    ArrayList<String> pdfURL;
    ArrayList<Uri>imageURIlist;
    ArrayList<String>fileList;
    ArrayList<StorageReference>files_ref_list;
    StorageTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap_photo;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    ImageView addImage;

    List<UploadsAttachmentContent> uploadList; //using the same file from complaints for notices

    //internet
    Boolean isConnected,monitoringConnectivity;

    TextView transaction;

    List<String> categories,multi_flats;
    String maintenance_month;

    //addData
    ListenerRegistration getDataListener;
    Date due_date;

    MultiSpinner multiSpinner;
    String soc_name, flat_no,name,username,unique_id,admin;

    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    ListenerRegistration listenerRegistration;
    List<String> flat_list;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_bill);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_amount=(EditText)findViewById(R.id.amount);


        tv_save=(TextView)findViewById(R.id.save);
        tv_date=(TextView)findViewById(R.id.tv_date);
        date_layout=(RelativeLayout)findViewById(R.id.date_layout_rel);

        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        soc_name = getIntent().getExtras().getString("society_name");
        flat_no = getIntent().getExtras().getString("flat_no");
        name = getIntent().getExtras().getString("name");
        username = getIntent().getExtras().getString("username");
        unique_id=getIntent().getStringExtra("unique_id");
        admin=getIntent().getStringExtra("admin");

        //for camera intent
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        addImage=(ImageView)findViewById(R.id.add);
        imageURL=new ArrayList<String>();//for storing image url
        pdfURL=new ArrayList<String>();//for storing pdf url
        imageURIlist=new ArrayList<Uri>();
        fileList=new ArrayList<String>();

        uploadList=new ArrayList<>();
        files_ref_list=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));//by default manager is vertical
        mAdapter=new UploadAllFilesAdapter(this, uploadList,imageURIlist,fileList);
        recyclerView.setAdapter(mAdapter);

        //add image
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPhoto();

            }
        });

        tv_save=(TextView)findViewById(R.id.save);

        tv_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected){
                    final Dialog dialog = new Dialog(GenerateBillActivity.this);
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
                }else{
                    if(flat_list==null||flat_list.isEmpty()||et_amount.getText().toString().isEmpty()||due_date==null){
                        showMessage("Error","Please enter all data",R.drawable.ic_error_dialog);
                    }else{
                        addData();
                    }

                }
            }
        });


        spinner = findViewById(R.id.spinner);
        categories=new ArrayList<>();


        //finding date
        for(int i=-3;i<10;i++){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, i);

            String month= Month.of(cal.get(Calendar.MONTH)+1).toString();
            String monthAndYear= month.substring(0,1).toUpperCase()+month.substring(1).toLowerCase() +", "+ cal.get(Calendar.YEAR);
            Log.i(TAG,"month and year is "+monthAndYear);
            categories.add(monthAndYear);
        }



        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maintenance_month = String.valueOf(spinner.getSelectedItem());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //for flats
        multiSpinner =  (MultiSpinner) findViewById(R.id.mySpinner);
        db=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        Log.i(TAG,"unique id is "+unique_id);
        final Query flat_Ref= db.collection(getString(R.string.SOCIETIES)).whereEqualTo("unique_id",unique_id);

        multi_flats = new ArrayList<>();
        listenerRegistration=flat_Ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {


                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                if (snapshot.getDocuments().isEmpty()) {
                    Log.i(TAG,"no societies");

                } else {

                    if(snapshot.getDocuments().get(0).get("flats_unavailable")!=null ) {

                        multi_flats = (ArrayList) (snapshot.getDocuments().get(0).get("flats_unavailable"));
                        Collections.sort(multi_flats);
                        Log.i(TAG,"flats are "+multi_flats);

                       // flat_doc_id=snapshot.getDocuments().get(0).getId();
                        multiSpinner.setItems(multi_flats);
                        multiSpinner.setSelection(new int[]{});
                        multiSpinner.setListener(new MultiSpinner.OnMultipleItemsSelectedListener() {
                            @Override
                            public void selectedIndices(List<Integer> indices) {
                                Log.i(TAG,"The selected indices are "+indices);

                                if(indices.isEmpty()){
                                    showMessage("Error!","Please select a flat",R.drawable.ic_error_dialog);
                                }
                            }

                            @Override
                            public void selectedStrings(List<String> flats) {
                                Log.i(TAG, "Selected flats are " + flats);
                                flat_list=flats;
                            }
                        });


                    }

                    Log.i(TAG, "multi flats are " + multi_flats);

                    if(multi_flats.isEmpty()){
                        multi_flats.add("No flats available");
                    }

                }


            }
        });





    }

    private void addData() {
        if(imageURIlist.size()!=0 ){
            addImageData();
        }else{
            addNoImageData();
        }

    }

    private void addImageData(){
        if(imageURIlist.size()!=0) {

            for (int image_pos = 0; image_pos < imageURIlist.size(); image_pos++) {
                Log.i(TAG, "Images list is " + imageURIlist.get(image_pos).toString());

                progressBar.setVisibility(View.VISIBLE);
                uploadImage(imageURIlist.get(image_pos),fileList.get(image_pos));
                Log.i(TAG, "Images list is " + imageURIlist.get(image_pos).toString());

            }

            Log.i(TAG,"In addImageData image part");
        }


    }

    private void uploadImage(Uri selectedImageURI, final String fileType){

        et_amount.setEnabled(false);
        spinner.setEnabled(false);
        tv_date.setEnabled(false);
        multiSpinner.setEnabled(false);
        tv_save.setEnabled(false);



        final StorageReference ref = storageReference.child("mmt_files/"+ UUID.randomUUID().toString());


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

                    Log.i(TAG, "The URL for these pdf is " + pdfURL);

                    Log.i(TAG, "The URL for these images is " + imageURL);
                    Log.i(TAG, "imageURIlist size inside upload data :" + imageURIlist.size());


                    if (fileList.size() == pdfURL.size()+imageURL.size()) {

                        final FirebaseFirestore db = FirebaseFirestore.getInstance();
                        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                        final String user = getString(R.string.USER);

                        final String user_id = currentFirebaseUser.getUid();


                        DocumentReference docRef = db.collection(user).document(user_id);
                        progressBar.setVisibility(View.VISIBLE);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        DocumentReference soc_id_ref =document.getDocumentReference("society_id");
                                        Log.i(TAG, "Society id is " + soc_id_ref);




                                        final int[] count = {0};
                                        for(int i=0;i<flat_list.size();i++){


                                            DocumentReference doc_id=soc_id_ref.collection("mmt").document();
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("date_created", new Timestamp(new Date()));
                                            data.put("amount", et_amount.getText().toString().trim());
                                            data.put("due_date",due_date);
                                            data.put("document_id",doc_id);
                                            data.put("maintenance_month",maintenance_month);
                                            data.put("status",0);
                                            data.put("flat_no",flat_list.get(i));
                                            data.put("image_url", imageURL);
                                            data.put("pdf_url", pdfURL);

                                            doc_id.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    count[0]++;
                                                    if(count[0]==flat_list.size()){
                                                        showMessage("Success","Bills Successfully generated ",R.drawable.ic_success_dialog);
                                                        progressBar.setVisibility(View.GONE);
                                                        et_amount.setEnabled(true);
                                                        spinner.setEnabled(true);
                                                        tv_date.setEnabled(true);
                                                        multiSpinner.setEnabled(true);
                                                        tv_save.setEnabled(true);

                                                        et_amount.setText("");
                                                        tv_date.setText("");
                                                        due_date=null;
                                                        multiSpinner.setSelection(new int[]{});
                                                        clearData(uploadList);
                                                    }




                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding document", e);
                                                    progressBar.setVisibility(View.GONE);
                                                    et_amount.setEnabled(true);
                                                    spinner.setEnabled(true);
                                                    tv_date.setEnabled(true);
                                                    multiSpinner.setEnabled(true);
                                                    tv_save.setEnabled(true);

                                                    et_amount.setText("");
                                                    tv_date.setText("");
                                                    due_date=null;
                                                    multiSpinner.setSelection(new int[]{});
                                                    clearData(uploadList);
                                                    showMessage("Error", "unable to generate invoice",R.drawable.ic_error_dialog);
                                                }
                                            });

                                        }



                                    } else {
                                        Log.d(TAG, "No such document");
                                        progressBar.setVisibility(View.GONE);
                                        showMessage("Error", "An internal error occurred",R.drawable.ic_error_dialog);
                                        et_amount.setEnabled(true);
                                        spinner.setEnabled(true);
                                        tv_date.setEnabled(true);
                                        multiSpinner.setEnabled(true);
                                        tv_save.setEnabled(true);
                                        et_amount.setText("");
                                        tv_date.setText("");
                                        due_date=null;
                                        multiSpinner.setSelection(new int[]{});
                                        clearData(uploadList);


                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                    progressBar.setVisibility(View.GONE);
                                    showMessage("Error", "An internal error occurred",R.drawable.ic_error_dialog);
                                    et_amount.setEnabled(true);
                                    spinner.setEnabled(true);
                                    tv_date.setEnabled(true);
                                    multiSpinner.setEnabled(true);
                                    tv_save.setEnabled(true);
                                    et_amount.setText("");
                                    tv_date.setText("");
                                    due_date=null;
                                    multiSpinner.setSelection(new int[]{});
                                    clearData(uploadList);

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

    public void clearData(List myList) {
        myList.clear(); // clear list
        mAdapter.notifyDataSetChanged(); // let your adapter know about the changes and reload view.
    }
    private void addNoImageData() {

        et_amount.setEnabled(false);
        spinner.setEnabled(false);
        tv_date.setEnabled(false);
        multiSpinner.setEnabled(false);
        tv_save.setEnabled(false);

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String societies = getString(R.string.SOCIETIES);
        final String user = getString(R.string.USER);

        final String user_id = currentFirebaseUser.getUid();


        DocumentReference docRef = db.collection(user).document(user_id);
        progressBar.setVisibility(View.VISIBLE);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        DocumentReference soc_id_ref =document.getDocumentReference("society_id");
                        Log.i(TAG, "Society id is " + soc_id_ref);


                        final int[] count = {0};
                        for(int i=0;i<flat_list.size();i++){

                            DocumentReference doc_id=soc_id_ref.collection("mmt").document();

                            Map<String, Object> data = new HashMap<>();
                            data.put("date_created", new Timestamp(new Date()));
                            data.put("amount", et_amount.getText().toString().trim());
                            data.put("due_date",due_date);
                            data.put("document_id",doc_id);
                            data.put("maintenance_month",maintenance_month);
                            data.put("status",0);
                            data.put("flat_no",flat_list.get(i));

                            doc_id.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    count[0]++;

                                    if(count[0]==flat_list.size()){
                                        showMessage("Success","Bills Successfully generated ",R.drawable.ic_success_dialog);
                                        progressBar.setVisibility(View.GONE);
                                        et_amount.setEnabled(true);
                                        spinner.setEnabled(true);
                                        tv_date.setEnabled(true);
                                        multiSpinner.setEnabled(true);
                                        tv_save.setEnabled(true);

                                        et_amount.setText("");
                                        tv_date.setText("");
                                        due_date=null;
                                        multiSpinner.setSelection(new int[]{});
                                        clearData(uploadList);
                                    }


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                    progressBar.setVisibility(View.GONE);
                                    et_amount.setEnabled(true);
                                    spinner.setEnabled(true);
                                    tv_date.setEnabled(true);
                                    multiSpinner.setEnabled(true);
                                    tv_save.setEnabled(true);
                                    et_amount.setText("");
                                    tv_date.setText("");
                                    due_date=null;
                                    multiSpinner.setSelection(new int[]{});
                                    clearData(uploadList);

                                    showMessage("Error", "unable to generate invoice",R.drawable.ic_error_dialog);
                                }
                            });

                        }



                    } else {
                        Log.d(TAG, "No such document");
                        progressBar.setVisibility(View.GONE);
                        showMessage("Error", "An internal error occurred",R.drawable.ic_error_dialog);
                        et_amount.setEnabled(true);
                        spinner.setEnabled(true);
                        tv_date.setEnabled(true);
                        multiSpinner.setEnabled(true);
                        tv_save.setEnabled(true);
                        et_amount.setText("");
                        tv_date.setText("");
                        due_date=null;
                        multiSpinner.setSelection(new int[]{});
                        clearData(uploadList);


                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    progressBar.setVisibility(View.GONE);
                    showMessage("Error", "An internal error occurred",R.drawable.ic_error_dialog);
                    et_amount.setEnabled(true);
                    spinner.setEnabled(true);
                    tv_date.setEnabled(true);
                    multiSpinner.setEnabled(true);
                    tv_save.setEnabled(true);
                    et_amount.setText("");
                    tv_date.setText("");
                    due_date=null;
                    multiSpinner.setSelection(new int[]{});
                    clearData(uploadList);

                }
            }
        });



    }

    private void showDatePicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(GenerateBillActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tv_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        String formatedDate = sdf.format(cldr.getTime());
                        try {

                            String sDate1=dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            due_date =new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);

                            Log.i(TAG,"due_date is "+due_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, year, month, day);
        picker.show();
    }

    //for attachments
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
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".png");
        return uriSting;

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
        final String finalTakephoto = takePhoto;
        final String finalChooseFromLibrary = chooseFromLibrary;
        final String finalAttachDocument = attachDocument;


        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals(finalChooseFromLibrary)) {

                    int PERMISSION_ALL = 1;
                    String[] PERMISSIONS = {
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.CAMERA
                    };

                    if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(GenerateBillActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {



                        if (ActivityCompat.shouldShowRequestPermissionRationale(GenerateBillActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true) {


                        } else {
                            ActivityCompat.requestPermissions(GenerateBillActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.CAMERA
                    };

                    if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(GenerateBillActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(GenerateBillActivity.this,
                                Manifest.permission.CAMERA) == true){

                        }else {
                            ActivityCompat.requestPermissions(GenerateBillActivity.this, new String[]{Manifest.permission.CAMERA},
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
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.CAMERA
                    };

                    if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(GenerateBillActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }


                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(GenerateBillActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){



                        }else {
                            ActivityCompat.requestPermissions(GenerateBillActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(GenerateBillActivity.this);
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

    //check for internet
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
            final Dialog dialog = new Dialog(GenerateBillActivity.this);
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

            final Dialog dialog = new Dialog(GenerateBillActivity.this);
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
        finish();
    }

}

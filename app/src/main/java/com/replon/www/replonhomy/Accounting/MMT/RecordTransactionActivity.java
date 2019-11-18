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

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.replon.www.replonhomy.Utility.UploadsAttachmentContent;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.UploadsAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordTransactionActivity extends AppCompatActivity {

    public static final String TAG = "RecordTransactionActivi";
    public static final int REQUEST_LOAD_PDF = 250;
    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 89;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 67;
    ProgressBar progressBar;
    ImageView back;
    EditText et_particular,et_amount,et_reference;
    TextView tv_save,tv_date;
    RelativeLayout date_layout;
    String transaction_date;
    DatePickerDialog picker;

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
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    ImageView addImage;
    List<UploadsAttachmentContent> uploadList; //using the same file from complaints for notices

    //internet
    Boolean isConnected,monitoringConnectivity;

    //dropdown
    List<String> categories;

    String paid_by;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_transaction);
        Log.i(TAG,"in this page");

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
        et_particular=(EditText)findViewById(R.id.particular);
        et_reference=(EditText)findViewById(R.id.reference_no);
        tv_save=(TextView)findViewById(R.id.save);
        tv_date=(TextView)findViewById(R.id.tv_date);
        date_layout=(RelativeLayout)findViewById(R.id.date_layout_rel);


        tv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        //for camera intent
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        addImage=(ImageView)findViewById(R.id.add);
        imageURL=new ArrayList<String>();//for storing image url
        pdfURL=new ArrayList<String>();//for storing pdf url
        imageURIlist=new ArrayList<Uri>();
        pdfURIlist=new ArrayList<Uri>();
        fileList=new ArrayList<String>();

        uploadList=new ArrayList<>();
        files_ref_list=new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));//by default manager is vertical
        mAdapter=new UploadsAdapter(this, uploadList,imageURIlist);
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
                    final Dialog dialog = new Dialog(RecordTransactionActivity.this);
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

                }
            }
        });



        final Spinner spinner = findViewById(R.id.spinner);
        categories=new ArrayList<>();
        categories.add("Cheque");
        categories.add("NEFT/IMPS");
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                paid_by = String.valueOf(spinner.getSelectedItem());


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });






    }

    private void showDatePicker() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        picker = new DatePickerDialog(RecordTransactionActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tv_date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
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
                pdfURIlist.add(data.getData());
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
                            android.Manifest.permission.READ_CONTACTS,
                            android.Manifest.permission.WRITE_CONTACTS,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_SMS,
                            android.Manifest.permission.CAMERA
                    };

                    if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                        ActivityCompat.requestPermissions(RecordTransactionActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {



                        if (ActivityCompat.shouldShowRequestPermissionRationale(RecordTransactionActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true) {


                        } else {
                            ActivityCompat.requestPermissions(RecordTransactionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
                        ActivityCompat.requestPermissions(RecordTransactionActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(RecordTransactionActivity.this,
                                Manifest.permission.CAMERA) == true){

                        }else {
                            ActivityCompat.requestPermissions(RecordTransactionActivity.this, new String[]{Manifest.permission.CAMERA},
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
                        ActivityCompat.requestPermissions(RecordTransactionActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }


                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(RecordTransactionActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){



                        }else {
                            ActivityCompat.requestPermissions(RecordTransactionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

        final Dialog dialog = new Dialog(RecordTransactionActivity.this);
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
            final Dialog dialog = new Dialog(RecordTransactionActivity.this);
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
                    Intent myIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
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

            final Dialog dialog = new Dialog(RecordTransactionActivity.this);
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
        overridePendingTransition(0,0);
    }

}

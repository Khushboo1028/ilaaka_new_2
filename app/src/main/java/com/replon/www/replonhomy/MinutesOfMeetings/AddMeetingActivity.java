package com.replon.www.replonhomy.MinutesOfMeetings;

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
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.replon.www.replonhomy.Utility.UploadsAttachmentContent;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.SwipeToDelete;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.UploadAllFilesAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AddMeetingActivity extends AppCompatActivity {

    public static final String TAG = "AddMeetingActivity";
    public static final int REQUEST_LOAD_PDF = 250;
    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE = 89;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 67;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ImageView back,add_subject;
    ListenerRegistration getDataListener;

    EditText meeting_subject, meeting_desc;
    private TextView generate;

    List<ContentsAddMoM> addmomList;
    List<String>subjectList;
    List<String>descriptionList;

    ProgressBar progressBar;
    EditText meetingName;

    RelativeLayout relativeLayout;
    Boolean isConnected,monitoringConnectivity;

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
    private RecyclerView recyclerViewImages;
    private RecyclerView.Adapter mAdapterImages;
    ImageView addImage;

    List<UploadsAttachmentContent> uploadList; //using the same file from complaints for notices




    //firebase

    private DocumentReference mDocRef,mDocRef_society;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseFirestore db;
    private FirebaseUser currentFirebaseUser;
    DocumentReference meeting_doc_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AddMeetingActivity.this);
        setContentView(R.layout.activity_add_meeting);

        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        currentFirebaseUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        add_subject=(ImageView)findViewById(R.id.new_subject);
        back=(ImageView)findViewById(R.id.back);
        addmomList=new ArrayList<>();
        subjectList=new ArrayList<>();
        descriptionList=new ArrayList<>();
        generate = findViewById(R.id.generate);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.meeting_recycler_view);
        meetingName=(EditText)findViewById(R.id.meeting_name);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));//by default manager is vertical


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isConnected){
                    final Dialog dialog = new Dialog(AddMeetingActivity.this);
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
               else{
                    addData();
                }

            }
        });

        addmomList.add(0,new ContentsAddMoM("",""));


        add_subject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // addmomList.add(new ContentsAddMoM(true));
                RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForLayoutPosition(0);

                meeting_subject = viewHolder.itemView.findViewById(R.id.meeting_subject);

                String s1 = meeting_subject.getText().toString();

                if (!s1.equals("")){
                    addmomList.add(0,new ContentsAddMoM("",""));
                    mAdapter.notifyItemInserted(0);

                    recyclerView.smoothScrollToPosition(0);
                }
                else{
                    showMessage("Error!", "Subject cannot be empty !",R.drawable.ic_error_dialog);
                }

                mAdapter.notifyDataSetChanged();
            }
        });

        mAdapter=new AddMoMAdapter(getApplicationContext(),addmomList);
        recyclerView.setAdapter(mAdapter);


        //for images
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
        recyclerViewImages = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerViewImages.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));//by default manager is vertical
        mAdapterImages=new UploadAllFilesAdapter(this, uploadList,imageURIlist,fileList);
        recyclerViewImages.setAdapter(mAdapterImages);

        //add image
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogShowPhoto();

            }
        });

        enableSwipeToDelete();
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
    private void uploadImage(Uri selectedImageURI, final String fileType) {

        final StorageReference ref = storageReference.child("mom_files/"+ UUID.randomUUID().toString());

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

                        if(meetingName.getText().toString().equals("")){
                            showMessage("Error!","Your meeting must have a name",R.drawable.ic_error_dialog);
                        }else {


                            final String user_id = currentFirebaseUser.getUid();
                            final String societies = getString(R.string.SOCIETIES);
                            final String user = getString(R.string.USER);

                            int size = mAdapter.getItemCount();

                            Log.i(TAG, "size is " + size);

                            for (int i = 0; i < size; i++) {

                                if(!addmomList.get(i).getSubject().equals("")){
                                    subjectList.add(addmomList.get(i).getSubject());
                                    descriptionList.add(addmomList.get(i).getDescription());
                                }

                            }

                            Collections.reverse(subjectList);
                            Collections.reverse(descriptionList);
                            Log.i(TAG, "Subject list is" + subjectList);
                            Log.i(TAG, "Description list is " + descriptionList);


                            if ( subjectList.isEmpty()||subjectList.get(0).equals("") ) {
                                Log.i(TAG, "Subject list is empty");
                                showMessage("Error!", "There must be at least one subject", R.drawable.ic_error_dialog);
                            } else {
                                progressBar.setVisibility(View.VISIBLE);
                                DocumentReference docRef = db.collection(user).document(user_id);

                                getDataListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                                            Log.i(TAG, "Society id is " + soc_id_ref);


                                            DocumentReference doc_id = soc_id_ref.collection("minutes_of_meeting").document();
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("date_created", new Timestamp(new Date()));
                                            data.put("subjects", subjectList);
                                            data.put("descriptions", descriptionList);
                                            data.put("meeting_name", meetingName.getText().toString());
                                            data.put("document_id", doc_id);
                                            data.put("image_url",imageURL);
                                            data.put("pdf_url",pdfURL);

                                            doc_id.set(data)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            progressBar.setVisibility(View.GONE);

                                                            finish();
                                                            overridePendingTransition(0, 0);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w(TAG, "Error adding document", e);
                                                            progressBar.setVisibility(View.GONE);
                                                            showMessage("Error", "unable to generate MOM", R.drawable.ic_error_dialog);
                                                        }
                                                    });


                                        } else {
                                            Log.d(TAG, source + " data: null");
                                        }

                                    }
                                });
                            }

                        }






                    }



                } else {

                    // Handle failures
                    Log.i(TAG,"Something happened");
                }


            }
        });
        Log.i(TAG,"The imageUrl array is "+imageURL);


    }
    private void addNoImageData() {

        if(meetingName.getText().toString().equals("")){
            showMessage("Error!","Your meeting must have a name",R.drawable.ic_error_dialog);
        }else {


            final String user_id = currentFirebaseUser.getUid();
            final String societies = getString(R.string.SOCIETIES);
            final String user = getString(R.string.USER);

            int size = mAdapter.getItemCount();

            Log.i(TAG, "size is " + size);

            for (int i = 0; i < size; i++) {

                if(!addmomList.get(i).getSubject().equals("")){
                    subjectList.add(addmomList.get(i).getSubject());
                    descriptionList.add(addmomList.get(i).getDescription());
                }



            }

            Collections.reverse(subjectList);
            Collections.reverse(descriptionList);
            Log.i(TAG, "Subject list is" + subjectList);
            Log.i(TAG, "Description list is " + descriptionList);


                if ( subjectList.isEmpty()||subjectList.get(0).equals("") ) {
                    Log.i(TAG, "Subject list is empty");
                    showMessage("Error!", "There must be at least one subject", R.drawable.ic_error_dialog);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    DocumentReference docRef = db.collection(user).document(user_id);

                    getDataListener = docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                                Log.i(TAG, "Society id is " + soc_id_ref);


                                DocumentReference doc_id = soc_id_ref.collection("minutes_of_meeting").document();
                                Map<String, Object> data = new HashMap<>();
                                data.put("date_created", new Timestamp(new Date()));
                                data.put("subjects", subjectList);
                                data.put("descriptions", descriptionList);
                                data.put("meeting_name", meetingName.getText().toString());
                                data.put("document_id", doc_id);

                                doc_id.set(data)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressBar.setVisibility(View.GONE);

                                                finish();
                                                overridePendingTransition(0, 0);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error adding document", e);
                                                progressBar.setVisibility(View.GONE);
                                                showMessage("Error", "unable to generate MOM", R.drawable.ic_error_dialog);
                                            }
                                        });


                            } else {
                                Log.d(TAG, source + " data: null");
                            }

                        }
                    });
                }

        }

    }

    private void enableSwipeToDelete() {

        SwipeToDelete swipeToDelete = new SwipeToDelete(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final ContentsAddMoM item = addmomList.get(position);

                if(addmomList.size()==1){
                    Snackbar snackbar=Snackbar.make(relativeLayout,"Meeting must have at least one subject",Snackbar.LENGTH_LONG );
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                    mAdapter.notifyDataSetChanged();
                }else{
                    addmomList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyDataSetChanged();

                    //undo code below
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addmomList.add(position, item);
                            mAdapter.notifyItemInserted(position);
                            recyclerView.scrollToPosition(position);
                        }
                    });
                    snackbar.show();
                }

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDelete);
        itemTouchhelper.attachToRecyclerView(recyclerView);
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
                    mAdapterImages.notifyDataSetChanged();


                } catch (IOException e) {
                    e.printStackTrace();
                }


                fileList.add("image");
                mAdapterImages.notifyDataSetChanged();
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
                    mAdapterImages.notifyDataSetChanged();

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

                mAdapterImages.notifyDataSetChanged();
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
                        ActivityCompat.requestPermissions(AddMeetingActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {



                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddMeetingActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true) {


                        } else {
                            ActivityCompat.requestPermissions(AddMeetingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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
                        ActivityCompat.requestPermissions(AddMeetingActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddMeetingActivity.this,
                                Manifest.permission.CAMERA) == true){

                        }else {
                            ActivityCompat.requestPermissions(AddMeetingActivity.this, new String[]{Manifest.permission.CAMERA},
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
                        ActivityCompat.requestPermissions(AddMeetingActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }


                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(AddMeetingActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){



                        }else {
                            ActivityCompat.requestPermissions(AddMeetingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

        final Dialog dialog = new Dialog(AddMeetingActivity.this);
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
            final Dialog dialog = new Dialog(AddMeetingActivity.this);
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

            final Dialog dialog = new Dialog(AddMeetingActivity.this);
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
    protected void onStop() {
        super.onStop();

        if (getDataListener!= null) {
            getDataListener.remove();
            getDataListener = null;
        }
    }
}

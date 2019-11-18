package com.replon.www.replonhomy.Messaging.Group;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.replon.www.replonhomy.Messaging.Adapter.MessageAdapter;
import com.replon.www.replonhomy.Messaging.Model.Chat;
import com.replon.www.replonhomy.Messaging.Model.GroupChat;
import com.replon.www.replonhomy.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class GroupMessagesActivity extends AppCompatActivity {

    public static final String TAG = "GroupMessagesActivity";
    FirebaseUser firebaseUser;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    ImageView info;
    String groupName;
    ImageButton btn_send;
    EditText text_send;
    RecyclerView recyclerView;
    List<Chat> mChat;
    List<GroupChat> mGroupChat;
    String chats,unique_id,flat_no;
    TextView tv_username;
    GroupMessageAdapter messageAdapter;
    ImageView back;

    ImageButton btn_attachments;
    Uri selectedImageURI,file_camera_uri;
    Uri downloadUri;
    StorageTask uploadTask;
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int REQUEST_CAMERA = 100;
    private static final int RESULT_LOAD_IMAGE = 69;
    public static final int REQUEST_LOAD_PDF = 250;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA=50;
    private static final int MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE=90;
    public static final int MEDIA_TYPE_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        progressBar=(ProgressBar)findViewById(R.id.progress_bar) ;
        progressBar.setVisibility(View.GONE);

        info=(ImageView)findViewById(R.id.info);
        info.setVisibility(View.GONE);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        groupName=getIntent().getStringExtra("groupName");
        unique_id=getIntent().getStringExtra("unique_id");
        flat_no=getIntent().getStringExtra("flat_no");
        Log.i(TAG,"group name is "+groupName);

        tv_username=(TextView)findViewById(R.id.username);
        tv_username.setText(groupName);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

        chats="Chats_Group_"+unique_id;
        btn_send=(ImageButton)findViewById(R.id.btn_send);
        text_send=findViewById(R.id.txt_send);

        mChat=new ArrayList<>();
        mGroupChat=new ArrayList<>();
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        Parcelable recyclerViewState;
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg=text_send.getText().toString().trim();
//

                if(!msg.trim().equals("")){
                    msg=msg.replaceAll("\\\\n", "\n");
                    recyclerView.scrollToPosition(mGroupChat.size() -1);
                    sendMessage(firebaseUser.getUid(),msg,"","");
                    recyclerView.scrollToPosition(mGroupChat.size() -1);
                   // messageAdapter.notifyDataSetChanged();

                }else{
                    Toast.makeText(getApplicationContext(),"Cannot send empty Message",Toast.LENGTH_SHORT).show();
                }
                text_send.getText().clear();
            }
        });





        readMessages();
        checkForSeen();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        btn_attachments=(ImageButton)findViewById(R.id.btn_attachments);
        btn_attachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPhoto();
            }
        });


    }

    private void readMessages() {
        final DatabaseReference read_ref=FirebaseDatabase.getInstance().getReference(chats);

        mGroupChat.clear();

        read_ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mGroupChat.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    GroupChat groupChat=snapshot.getValue(GroupChat.class);

                    mGroupChat.add(groupChat);

                    Log.i(TAG,"Group chat message is "+groupChat.getMessage());




                    messageAdapter=new GroupMessageAdapter(GroupMessagesActivity.this,mGroupChat,groupChat.getImage_url());
                    recyclerView.setAdapter(messageAdapter);

                    Parcelable recyclerViewState;
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    recyclerView.scrollToPosition(mGroupChat.size() -1);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkForSeen() {

        final DatabaseReference seen_ref=FirebaseDatabase.getInstance().getReference(unique_id).child(firebaseUser.getUid());

        final HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isSeen", true);
        seen_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if(snapshot.getKey().equals("isSeen")){
                        if(snapshot.getValue().equals(FALSE)){
                            seen_ref.updateChildren(hashMap);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String sender, final String message, String image_url, String pdf_url) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        mGroupChat.clear();
        messageAdapter.notifyDataSetChanged();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("time_sent", ServerValue.TIMESTAMP);
        hashMap.put("image_url", image_url);
        hashMap.put("pdf_url", pdf_url);
        hashMap.put("sender_flat",flat_no);

        reference.child(chats).push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // final String userid = intent.getStringExtra("UserID");//userId of message receiver


                final HashMap<String, Object> update_Data = new HashMap<>();
//                update_Data.put("id",firebaseUser.getUid());
                update_Data.put("last_message", message);
                update_Data.put("last_message_time", ServerValue.TIMESTAMP);
                update_Data.put("isSeen",TRUE);


                final HashMap<String, Object> update_Data1 = new HashMap<>();
                update_Data1.put("last_message", message);
                update_Data1.put("last_message_time", ServerValue.TIMESTAMP);
                update_Data1.put("isSeen",FALSE);

                if(task.isSuccessful()) {


                    final DatabaseReference chatRefUsers = FirebaseDatabase.getInstance().getReference(unique_id);

                    chatRefUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot:dataSnapshot.getChildren()){


                                if(!snapshot.getKey().equals(firebaseUser.getUid())) {
                                    //Log.i(TAG,snapshot.getKey());
                                    final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference(unique_id).child(snapshot.getKey());
                                    chatRef.updateChildren(update_Data1);
                                } else {
                                    Log.i(TAG,snapshot.getKey());
                                    final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference(unique_id).child(snapshot.getKey());
                                    chatRef.updateChildren(update_Data);
                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    Parcelable recyclerViewState;
                    recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
                    recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                    recyclerView.scrollToPosition(mGroupChat.size() -1);

                }
                else{
                    Log.i(TAG,"An error occurred "+task.getException());
                }
            }
        });





    }


    //for images and attachments from here

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG,"REQ CODE IS" + requestCode);

        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);


            }else {


                selectedImageURI=file_camera_uri;
                Log.i(TAG,"selectedImageURi from camera is "+selectedImageURI);
                uploadImage(selectedImageURI);

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
                    String filename = getFilename();

                    // bitmap_photo = Bitmap.createScaledBitmap(bitmap,50,67,false);
                    //  user_profile.setImageBitmap(bitmap);

                    //selectedImageURI=bitmapToUriConverter(bitmap);

                    Log.i(TAG,"Image set");
                    uploadImage(selectedImageURI);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }

        if (requestCode == REQUEST_LOAD_PDF && resultCode == Activity.RESULT_OK) {
            selectedImageURI=data.getData();
            uploadPDF();

        }
    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "Replon/Profile_images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
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

    public void dialogShowPhoto() {
        String takePhoto = "Take Photo";
        String chooseFromLibrary = "Choose from Gallery";
        final String uploadDocument = "Send Document";
        final CharSequence[] items = {takePhoto, chooseFromLibrary,uploadDocument};
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
                        ActivityCompat.requestPermissions(GroupMessagesActivity.this, PERMISSIONS, PERMISSION_ALL);
                    }

                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        if (ActivityCompat.shouldShowRequestPermissionRationale(GroupMessagesActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){


//                            final Dialog dialog1 = new Dialog(MessagesActivity.this);
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
//                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
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
                            ActivityCompat.requestPermissions(GroupMessagesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_EXTERNAL_STORAGE);
                        }


                    }else{
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                        startActivityForResult(intent, RESULT_LOAD_IMAGE);
                    }

                }
                else if (items[item].equals(finalTakephoto)) {

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {


                        int PERMISSION_ALL = 1;
                        String[] PERMISSIONS = {
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_CONTACTS,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.CAMERA
                        };

                        if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                            ActivityCompat.requestPermissions(GroupMessagesActivity.this, PERMISSIONS, PERMISSION_ALL);
                        }


                        if (ActivityCompat.shouldShowRequestPermissionRationale(GroupMessagesActivity.this,
                                Manifest.permission.CAMERA) == true){


//                            final Dialog dialog1 = new Dialog(MessagesActivity.this);
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
//                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
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
                            ActivityCompat.requestPermissions(GroupMessagesActivity.this, new String[]{Manifest.permission.CAMERA},
                                    MY_PERMISSIONS_REQUEST_CAMERA);
                        }


                    }  else {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        file_camera_uri =  getOutputMediaFileUri(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, file_camera_uri);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }


                }
                else if(items[item].equals(uploadDocument)){

                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {


                        int PERMISSION_ALL = 1;
                        String[] PERMISSIONS = {
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_CONTACTS,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.CAMERA
                        };

                        if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
                            ActivityCompat.requestPermissions(GroupMessagesActivity.this, PERMISSIONS, PERMISSION_ALL);
                        }

                        if (ActivityCompat.shouldShowRequestPermissionRationale(GroupMessagesActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == true){


//                            final Dialog dialog1 = new Dialog(MessagesActivity.this);
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
//                                    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
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
                            ActivityCompat.requestPermissions(GroupMessagesActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
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

    private void uploadImage(Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);

        //upload new image
        final StorageReference ref = storageReference.child("messages_files/"+ UUID.randomUUID().toString());

        try {

            Bitmap bitmap_photo = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

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
                if (task.isSuccessful()){
                    downloadUri = task.getResult();
                    String text_image_url= downloadUri.toString();

                    if(firebaseUser!=null) {

                        progressBar.setVisibility(View.GONE);
                        recyclerView.scrollToPosition(mChat.size() -1);
                        sendMessage(firebaseUser.getUid(),"",text_image_url,"");
                        messageAdapter.notifyDataSetChanged();
                        recyclerView.scrollToPosition(mChat.size() -1);

                    }

                }

            }

        });


    }

    private void uploadPDF() {
        progressBar.setVisibility(View.VISIBLE);

        //upload new image
        final StorageReference ref = storageReference.child("messages_files/"+ UUID.randomUUID().toString());
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
                if (task.isSuccessful()){
                    downloadUri = task.getResult();
                    String text_pdf_url= downloadUri.toString();

                    if(firebaseUser!=null) {

                        progressBar.setVisibility(View.GONE);
                        recyclerView.scrollToPosition(mChat.size() -1);
                        sendMessage(firebaseUser.getUid(),"","",text_pdf_url);
                        recyclerView.scrollToPosition(mChat.size() -1);

                    }

                }

            }

        });


    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

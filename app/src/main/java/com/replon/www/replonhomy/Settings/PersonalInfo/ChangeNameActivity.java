package com.replon.www.replonhomy.Settings.PersonalInfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.util.HashMap;


public class ChangeNameActivity extends AppCompatActivity {

    public static final String TAG = "ChangeNameActivity";
    public static final int PERSONAL_ACTIVITY_REQUEST_CODE = 9;
    TextView tv_currentName;
    Button done;
    EditText et_new_name;
    String name,new_name,soc_name,flat_no,username,admin,email,soc_id;
    RelativeLayout relativeLayout;
    ProgressBar progressBar;

    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;
    ImageView back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ChangeNameActivity.this);
        setContentView(R.layout.activity_change_name);
        init();
        progressBar.setVisibility(View.GONE);

        db=FirebaseFirestore.getInstance();
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;


        admin = getIntent().getExtras().getString("admin");
        name=getIntent().getStringExtra("currentName");
        email = getIntent().getExtras().getString("email");
        soc_id=getIntent().getStringExtra("soc_id");

        tv_currentName.setText(name);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_name=et_new_name.getText().toString();
                if(new_name.trim().isEmpty()){
                    showMessage("Error","Name cannot be empty!",R.drawable.ic_error_dialog);
                }else{
                    updateName();
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });





    }
    private void init(){
        tv_currentName=(TextView)findViewById(R.id.current_name);
        et_new_name=(EditText)findViewById(R.id.new_name);
        done=findViewById(R.id.done);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        back=(ImageView)findViewById(R.id.back);


    }

    private void updateName() {

        et_new_name.setEnabled(false);

        if(currentFirebaseUser!=null){
            final String user_id = currentFirebaseUser.getUid();

            String user = getString(R.string.USER);
            DocumentReference docRef = db.collection(user).document(user_id);

            progressBar.setVisibility(View.VISIBLE);
            docRef.update("name", new_name)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Log.i(TAG, "Data updated");
                            new_name=et_new_name.getText().toString().trim();
                            if(soc_id!=null){
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference(soc_id).child(user_id);

                                reference.child("name").setValue(name).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressBar.setVisibility(View.GONE);
                                            Intent intent=new Intent(getApplicationContext(),PersonalInfoActivity.class);
                                            intent.putExtra("currentName",new_name);
                                            Log.i(TAG,"New name is "+new_name);
                                            intent.putExtra("admin",admin);
                                            intent.putExtra("email",email);
                                            setResult(1000,intent);
                                            finish();
                                            Log.i(TAG,"saveRealtimeDatabaseData update name task done");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i(TAG,"unable to add data in realtime database");
                                            progressBar.setVisibility(View.GONE);
                                            Intent intent=new Intent(getApplicationContext(),PersonalInfoActivity.class);
                                            intent.putExtra("currentName",new_name);
                                            Log.i(TAG,"New name is "+new_name);
                                            intent.putExtra("admin",admin);
                                            intent.putExtra("email",email);
                                            setResult(1000,intent);
                                            finish();
                                    }
                                });


                            }else{
                                Log.i(TAG,"soc_id is "+soc_id);
                                progressBar.setVisibility(View.GONE);
                                Intent intent=new Intent(getApplicationContext(),PersonalInfoActivity.class);
                                intent.putExtra("currentName",new_name);
                                Log.i(TAG,"New name is "+new_name);
                                intent.putExtra("admin",admin);
                                intent.putExtra("email",email);
                                setResult(1000,intent);
                                finish();
                            }




                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "An error occurred " + e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    Snackbar snackbar = Snackbar.make(relativeLayout, "An error occurred", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.YELLOW);
                    snackbar.show();
                    et_new_name.setEnabled(true);


                }
            });

        }

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

        final Dialog dialog = new Dialog(ChangeNameActivity.this);
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
        Intent intent=new Intent(getApplicationContext(),PersonalInfoActivity.class);
        intent.putExtra("currentName",name);
        intent.putExtra("admin",admin);
        intent.putExtra("email",email);
//                startActivity(intent);
        setResult(1000,intent);
        overridePendingTransition(0,0);
        finish();
    }
}

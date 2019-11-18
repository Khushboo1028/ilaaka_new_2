package com.replon.www.replonhomy.Settings.PersonalInfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class ChangeEmailActivity extends AppCompatActivity {
    public static final String TAG = "ChangeEmailActivity";
    private TextView tv_currentEmail;
    private Button done;
    private EditText et_new_email,et_password;
    ImageView back;
    ProgressBar progressBar;
    RelativeLayout relativeLayout;
    String old_email,new_email,name,admin,soc_name,email,soc_id;
    TextView email_not_verified;

    FirebaseUser user;
    String user_id,password;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    Boolean email_verified;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ChangeEmailActivity.this);

        setContentView(R.layout.activity_change_email);
        init();

        progressBar.setVisibility(View.GONE);
        old_email=getIntent().getStringExtra("email");
        tv_currentEmail.setText(old_email);

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        user.reload();
        email_verified=user.isEmailVerified();

        Log.i(TAG,"email_verified: "+email_verified);

        if(!user.isEmailVerified()){
            tv_currentEmail.setTextColor(Color.RED);
            email_not_verified.setVisibility(View.VISIBLE);
        }

        email = getIntent().getExtras().getString("email");
        admin = getIntent().getExtras().getString("admin");
        name = getIntent().getExtras().getString("currentName");
        soc_id=getIntent().getExtras().getString("soc_id");

        Log.i(TAG,"name is "+name);
        Log.i(TAG,"soc_name is "+soc_name);


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_email=et_new_email.getText().toString().trim().toLowerCase();
                password=et_password.getText().toString();
                if(new_email.isEmpty()||password.isEmpty()){
                    showMessage("Error","Fields cannot be empty!",R.drawable.ic_error_dialog);
                }else if(!new_email.contains("@")){
                    showMessage("Error","Please enter a correct Email!",R.drawable.ic_error_dialog);
                    et_password.setText("");
                }else if(new_email.equals(old_email)){
                    showMessage("Error","New Email cannot be same as the current one !",R.drawable.ic_error_dialog);
                    et_password.setText("");
                }else{
                    updateEmail();
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
        tv_currentEmail=findViewById(R.id.current_email);
        et_new_email=(EditText)findViewById(R.id.new_email);
        done=findViewById(R.id.done);
        relativeLayout=(RelativeLayout)findViewById(R.id.relativeLayout);
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        back=(ImageView)findViewById(R.id.back);
        et_password=(EditText) findViewById(R.id.password);
        email_not_verified = findViewById(R.id.email_not_verified);
        email_not_verified.setVisibility(View.GONE);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth=FirebaseAuth.getInstance();

        db= FirebaseFirestore.getInstance();
        user_id =user.getUid();


    }

    private void updateEmail(){

        progressBar.setVisibility(View.VISIBLE);
        et_new_email.setEnabled(false);
        et_password.setEnabled(false);

        //first login again
        mAuth.signInWithEmailAndPassword(old_email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.i(TAG,"com.replon.www.replonhomy.Login.Login successful");

                        //now update email after successful login
                        user.updateEmail(new_email)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Log.i(TAG,"Email updated");
                                        //send verification email
                                        user.sendEmailVerification()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.i(TAG,"Verification email sent");
                                                    progressBar.setVisibility(View.GONE);


                                                }
                                            });


                                        //now update email in database
                                        String user = getResources().getString(R.string.USER);
                                        DocumentReference docRef = db.collection(user).document(user_id);

                                        docRef.update("username",new_email)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.i(TAG,"Email updated in database");

                                                        if(soc_id!=null){
                                                            DatabaseReference reference= FirebaseDatabase.getInstance().getReference(soc_id).child(user_id);

                                                            reference.child("email").setValue(new_email).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    progressBar.setVisibility(View.GONE);
                                                                    Intent intent=new Intent(getApplicationContext(),PersonalInfoActivity.class);
                                                                    intent.putExtra("currentName",name);
                                                                    intent.putExtra("admin",admin);
                                                                    intent.putExtra("email",new_email);
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
                                                                    intent.putExtra("currentName",name);
                                                                    intent.putExtra("admin",admin);
                                                                    intent.putExtra("email",new_email);
                                                                    setResult(1000,intent);
                                                                    finish();
                                                                }
                                                            });


                                                        }else{
                                                            Log.i(TAG,"soc_id is "+soc_id);
                                                            progressBar.setVisibility(View.GONE);
                                                            Intent intent=new Intent(getApplicationContext(),PersonalInfoActivity.class);
                                                            intent.putExtra("currentName",name);
                                                            intent.putExtra("admin",admin);
                                                            intent.putExtra("email",new_email);
                                                            setResult(1000,intent);
                                                            finish();
                                                        }

                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.i(TAG,"An error occurred");

                                                        Snackbar snackbar = Snackbar.make(relativeLayout, "An error occurred", Snackbar.LENGTH_LONG);
                                                        snackbar.setActionTextColor(Color.YELLOW);
                                                        snackbar.show();
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
//                                        progressBar.setVisibility(View.GONE);
//                                        Log.i(TAG,"An error occurred"+e.getMessage());
//                                        Snackbar snackbar = Snackbar.make(relativeLayout, "An error occurred", Snackbar.LENGTH_LONG);
//                                        snackbar.setActionTextColor(Color.YELLOW);
//                                        snackbar.show();

                                        Log.i(TAG,"An error occurred" +e.getMessage());
                                        String exception=e.getMessage();
                                        int index=exception.indexOf(":");
                                        String data=exception.substring(index+1).trim();
                                        showMessage("Error",data,R.drawable.ic_error_dialog);
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);

                                    }
                                });



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG,"An error occurred" +e.getMessage());
                        String exception=e.getMessage();
                        int index=exception.indexOf(":");
                        String data=exception.substring(index+1).trim();
                        showMessage("Error",data,R.drawable.ic_error_dialog);
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);






                    }
                });




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

        final Dialog dialog = new Dialog(ChangeEmailActivity.this);
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
        finish();
    }
}


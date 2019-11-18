package com.replon.www.replonhomy.Login;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class ForgotPassword extends AppCompatActivity {


    private static final String TAG = "ForgotPasswordActivit";
    EditText et_email;
    Button reset_pass;
    ProgressBar mProgressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ForgotPassword.this);
        setContentView(R.layout.activity_forgot_password);


        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        mAuth = FirebaseAuth.getInstance();


        et_email = findViewById(R.id.email);
        reset_pass = findViewById(R.id.reset_pass);

        mProgressBar=findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);


        reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email=et_email.getText().toString().toLowerCase().trim();

                if(TextUtils.isEmpty(email)){
                    showMessage("Empty Field","Please enter your email to reset password.",R.drawable.ic_error_dialog);

                }
                else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    reset_pass.setEnabled(false);
                    et_email.setEnabled(false);

                    mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showMessage("Success","A link has been sent on your provided email to reset the password.",R.drawable.ic_success_dialog);
                            et_email.setEnabled(true);
                            et_email.setText("");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.w(TAG, "ForgotPassword:failure"+ e.getMessage());

                            String exception=e.getMessage();
                            int index=exception.indexOf(":");
                            String data=exception.substring(index+1).trim();
                            showMessage("Error",data,R.drawable.ic_error_dialog);

                           // showMessage("Error","Could not reset the password.",R.drawable.ic_error_dialog);
                            et_email.setEnabled(true);
                        }
                    });
                    mProgressBar.setVisibility(View.GONE);
                }

            }
        });





    }


    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(ForgotPassword.this);
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
        overridePendingTransition(0,0);
    }
}

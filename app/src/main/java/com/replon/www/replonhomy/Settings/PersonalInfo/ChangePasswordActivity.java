package com.replon.www.replonhomy.Settings.PersonalInfo;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class ChangePasswordActivity extends AppCompatActivity {

    private static final String TAG = "ChangePasswordActivity";
    TextView curr_email,forgot_password;
    EditText et_current_password, et_new_password,et_reenter_new_password;
    String email;
    Button reset_pass;

    ImageView back;

    private FirebaseAuth mAuth;
    Button btn_change_password;
    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ChangePasswordActivity.this);

        setContentView(R.layout.activity_change_password);

        email = getIntent().getExtras().getString("email");

        mAuth = FirebaseAuth.getInstance();

        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });

        curr_email = findViewById(R.id.current_email);
        back = findViewById(R.id.back);
        et_current_password =(EditText) findViewById(R.id.current_password);
        et_new_password =(EditText) findViewById(R.id.new_password);
        btn_change_password=(Button)findViewById(R.id.btn_new_password);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        et_reenter_new_password=(EditText)findViewById(R.id.re_enter_new_password);
        forgot_password=(TextView)findViewById(R.id.forgot_password);


        curr_email.setText(email);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ForgotPasswordSettings.class);
                startActivity(intent);
            }
        });


        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String current_password = et_current_password.getText().toString();
                final String new_password = et_new_password.getText().toString();
                String re_entered_password = et_reenter_new_password.getText().toString();

                if (current_password.isEmpty() || new_password.isEmpty() || re_entered_password.isEmpty()) {
                    showMessage("Oops!", "PLease enter all fields", R.drawable.ic_error_dialog);
                } else {



                if (re_entered_password.equals(new_password)) {
                    progressBar.setVisibility(View.VISIBLE);
                    et_current_password.setEnabled(false);
                    et_new_password.setEnabled(false);
                    et_reenter_new_password.setEnabled(false);

                    mAuth.signInWithEmailAndPassword(email, current_password)
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                    if (new_password.length() > 8) {
                                        user.updatePassword(new_password)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d(TAG, "User password updated.");
                                                            progressBar.setVisibility(View.GONE);
                                                            showMessage("Success", "Password has been updated", R.drawable.ic_success_dialog);
                                                            et_current_password.setText("");
                                                            et_new_password.setText("");
                                                            et_reenter_new_password.setText("");
                                                            et_current_password.setEnabled(true);
                                                            et_new_password.setEnabled(true);
                                                            et_reenter_new_password.setEnabled(true);
                                                        } else {
                                                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                                            progressBar.setVisibility(View.GONE);
                                                            String exception = task.getException().toString();
                                                            int index = exception.indexOf(":");
                                                            String data = exception.substring(index + 1).trim();
                                                            showMessage("Error", data, R.drawable.ic_error_dialog);
                                                            et_current_password.setEnabled(true);
                                                            et_new_password.setEnabled(true);
                                                            et_reenter_new_password.setEnabled(true);
                                                        }
                                                    }
                                                });
                                    } else {
                                        progressBar.setVisibility(View.GONE);
                                        showMessage("Oops!", "Password length must be greater than 8", R.drawable.ic_error_dialog);
                                        et_current_password.setEnabled(true);
                                        et_new_password.setEnabled(true);
                                        et_reenter_new_password.setEnabled(true);
                                        et_new_password.setText("");
                                        et_reenter_new_password.setText("");
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "createUserWithEmail:failure" + e.getMessage());
                            progressBar.setVisibility(View.GONE);
                            String exception = e.getMessage();
                            int index = exception.indexOf(":");
                            String data = exception.substring(index + 1).trim();
                            showMessage("Error", data, R.drawable.ic_error_dialog);
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            btn_change_password.setEnabled(true);

                            et_current_password.setEnabled(true);
                            et_new_password.setEnabled(true);
                            et_reenter_new_password.setEnabled(true);

                            et_current_password.setText("");
                            et_new_password.setText("");
                            et_reenter_new_password.setText("");


                        }
                    });
                } else {
                    showMessage("Error!", "Passwords do not match", R.drawable.ic_error_dialog);
                    et_reenter_new_password.setText("");

                }
            }
            }
        });




    }


    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(ChangePasswordActivity.this);
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

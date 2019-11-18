package com.replon.www.replonhomy.Login;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.HelpCenter.TermsAndConditions;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    public static final String TAG = "RegisterFragment";
    Button register;
    EditText et_email,et_password,et_confirm_password;
    Context mContext;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    String userID;
    ProgressBar progressBar;
    TextView terms_text;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());


        //init
        et_email=(EditText)view.findViewById(R.id.email);
        et_password=(EditText)view.findViewById(R.id.password);
        et_confirm_password=(EditText)view.findViewById(R.id.confirm_password);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

        progressBar.setVisibility(View.GONE);


        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });


        terms_text=view.findViewById(R.id.terms_text);

        terms_text.setMovementMethod(LinkMovementMethod.getInstance());

        String text="By continuing you indicate that you have read and agreed to our Terms of Service and Privacy Policy.";
        SpannableString ss = new SpannableString(text);
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent=new Intent(getContext(), TermsAndConditions.class);
                intent.putExtra("type","T&C");
                getContext().startActivity(intent);

            }
        };

        ClickableSpan span2 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                Intent intent=new Intent(getContext(), TermsAndConditions.class);
                intent.putExtra("type","privacy_policy");
                getContext().startActivity(intent);
            }
        };


        ss.setSpan(span1, text.indexOf("Terms of"), text.indexOf(" and Privacy"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(span2, text.indexOf("Privacy Po"), text.length() -1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        terms_text.setText(ss);
        terms_text.setMovementMethod(LinkMovementMethod.getInstance());


        register = view.findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email=et_email.getText().toString().toLowerCase().trim();
                String password=et_password.getText().toString().trim();
                String confirm_password=et_confirm_password.getText().toString().trim();



               if(email.isEmpty()||password.isEmpty()||confirm_password.isEmpty()){
                   showMessage("Error","All fields are required",R.drawable.ic_error_dialog);

               }
               else if(!emailIsValid(email)){
                   showMessage("Error","Please enter valid email",R.drawable.ic_error_dialog);
               }
               else if(password.length()<8){
                   showMessage("Error!","Password must be at least 8 characters",R.drawable.ic_error_dialog);

               }
               else if(!password.equals(confirm_password)){
                   showMessage("Error","Passwords do not match",R.drawable.ic_error_dialog);

               }
               else{
                   registerUser();
               }

            }

            private void registerUser() {
                progressBar.setVisibility(View.VISIBLE);
                et_email.setEnabled(false);
                et_password.setEnabled(false);
                et_confirm_password.setEnabled(false);

                String email=et_email.getText().toString().toLowerCase().trim();
                String password=et_password.getText().toString().trim();

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                mContext=getContext();
                                Log.d(TAG,"createUserwithEmail:onComplete"+task.isSuccessful());

                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    progressBar.setVisibility(View.GONE);
                                    userID=mAuth.getCurrentUser().getUid();
                                    final String isNewUser=Boolean.toString(task.getResult().getAdditionalUserInfo().isNewUser());
                                    Log.i(TAG,"isNewUser? :"+isNewUser);

                                    Intent intent = new Intent(getActivity(),RegisterActivity.class);
                                    getActivity().finish();
                                    startActivity(intent);
                                    getActivity().overridePendingTransition(0,0);


                                } else {
                                    // If sign in fails, display a message to the user.
                                    progressBar.setVisibility(View.GONE);
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                    String exception=task.getException().toString();
                                    int index=exception.indexOf(":");
                                    String data=exception.substring(index+1).trim();
                                    showMessage("Error",data,R.drawable.ic_error_dialog);
                                    Toast.makeText(mContext, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    Log.d(TAG,"onComplete: Auth changed"+userID);
                                    et_password.setText("");
                                    et_confirm_password.setText("");
                                    et_email.setEnabled(true);
                                    et_password.setEnabled(true);
                                    et_confirm_password.setEnabled(true);

                                }


                            }
                        });
            }


        });



        return view;
    }



    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(getActivity());
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


    public static boolean emailIsValid(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }



}

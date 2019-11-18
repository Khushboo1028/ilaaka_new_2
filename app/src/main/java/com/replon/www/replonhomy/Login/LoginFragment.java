package com.replon.www.replonhomy.Login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.replonhomy.Home.Home;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.HelpCenter.TermsAndConditions;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    public static final String TAG = "LoginFragment";
    private FirebaseAuth mAuth;

    EditText et_email;
    EditText et_password;
    Button btn_login;
    TextView invalid_text,forgot_pass;

    ProgressBar mProgressBar;
    Fragment fragment;

    String societies;
    String user;
    TextView terms_text;
    Spanned Text;



    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());

        View v = inflater.inflate(R.layout.fragment_login, container, false);


        user=getString(R.string.USER);
        societies=getString(R.string.SOCIETIES);
        FirebaseApp.initializeApp(getContext());
        mAuth = FirebaseAuth.getInstance();
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.hide();

        et_email=v.findViewById(R.id.username);
        et_password=v.findViewById(R.id.password);
        btn_login=v.findViewById(R.id.login);
        invalid_text = v.findViewById(R.id.invalid_text);
        forgot_pass = v.findViewById(R.id.forgot_pass);
        terms_text=v.findViewById(R.id.terms_text);

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


        mProgressBar=(ProgressBar)v.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);


        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),ForgotPassword.class);
                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });


        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        et_email.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_email.setBackgroundResource(R.drawable.edittext_borders);
                et_password.setBackgroundResource(R.drawable.edittext_borders);
                return false;
            }
        });

        et_password.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                et_email.setBackgroundResource(R.drawable.edittext_borders);
                et_password.setBackgroundResource(R.drawable.edittext_borders);
                return false;
            }
        });



        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=et_email.getText().toString().toLowerCase().trim();
                String password=et_password.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    invalid_text.setText("Please Enter all the Details");
                    changeUI();

                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    v.getBackground().setAlpha(100);
//                    Log.i("LoginFragment","Email is"+email+"Password:"+password);
                    mAuth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.i("LoginFragment","I AM INSIDE::");
                                    if(task.isSuccessful()){

                                        mProgressBar.setVisibility(View.GONE);
                                        FirebaseFirestore db=FirebaseFirestore.getInstance();
                                        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        String user_id=currentFirebaseUser.getUid();
                                        final boolean isNewUser=task.getResult().getAdditionalUserInfo().isNewUser();

                                        if(currentFirebaseUser!=null){
                                            DocumentReference doc_ref=db.collection(user).document(user_id);
                                            doc_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    if(documentSnapshot.exists()){

                                                        Log.i(TAG,"User is present");
                                                        Intent login_intent = new Intent(getActivity(), Home.class);
                                                        login_intent.putExtra("isNewUSer",isNewUser);
                                                        login_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        startActivity(login_intent);
                                                        getActivity().overridePendingTransition(0,0);
                                                        getActivity().finish();

                                                    }else{
                                                        Log.i(TAG,"User is not present");

                                                        Intent intent = new Intent(getActivity(),RegisterActivity.class);
                                                        intent.putExtra("isNewUSer",isNewUser);
                                                        getActivity().finish();
                                                        startActivity(intent);
                                                        getActivity().overridePendingTransition(0,0);
                                                    }
                                                }
                                            });
                                        }



                                    }
                                    else{
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());

                                        String exception=task.getException().toString();
                                        int index=exception.indexOf(":");
                                        String data=exception.substring(index+1).trim();
                                        showMessage("Error",data,R.drawable.ic_error_dialog);
                                        Toast.makeText(getContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        btn_login.setEnabled(true);

                                        changeUI();



                                    }

                                }
                            });

                }




            }
//            public void showMessage(String title, String message){
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setCancelable(true);
//                builder.setTitle(title);
//                builder.setMessage(message);
//                builder.show();
//
//            }

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

            private void changeUI() {

                Vibrator vibrator = (Vibrator) getActivity().getSystemService(getContext().VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(500);
                }
                et_email.setBackgroundResource(R.drawable.edittext_borders_red);
                et_password.setBackgroundResource(R.drawable.edittext_borders_red);
            }
        });

        return v;
    }




}

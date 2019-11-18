package com.replon.www.replonhomy.Login;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SocIdFragment extends Fragment {
    public static final String TAG = "SocIdFragment";
    View v;

    Button next_btn;
    TextView skip;
    Fragment fragment;
    ViewGroup viewGroup;
    InputMethodManager imm;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    String user_email,society_name,soc_id;

    ProgressBar progressBar;


    TextView tv_email;
    EditText et_soc_id;

    String isNewUSer,doc_id;

    public SocIdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v =inflater.inflate(R.layout.fragment_soc_id, container, false);

//        RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.mainrel);
//        relativeLayout.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                InputMethodManager imm =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//
//            }
//        });

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), getActivity());

        isNewUSer = getArguments().getString("isNewUser");
        Log.i(TAG,"isNEwUser? :"+isNewUSer);

        viewGroup = container;

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        user_email=firebaseUser.getEmail();
        Log.i("SocIdFragment","Current email is "+user_email);
        tv_email=(TextView) v.findViewById(R.id.email);
        tv_email.setText(user_email);
        et_soc_id=(EditText)v.findViewById(R.id.soc_id);

        progressBar = v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        //firebase
        db=FirebaseFirestore.getInstance();

        //get the input method manager service
        imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        next_btn = v.findViewById(R.id.btn_next);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkID();
            }

            private void checkID(){

                progressBar.setVisibility(View.VISIBLE);
                et_soc_id.setEnabled(false);
                soc_id=et_soc_id.getText().toString().trim();
                String societies=getString(R.string.SOCIETIES);
                CollectionReference doc_ref=db.collection(societies);
                final Boolean[] FOUND = {FALSE};

                doc_ref.whereEqualTo("unique_id",soc_id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.i(TAG,"Inside onComplete checkID");
                                if (task.isSuccessful()) {
                                    Log.i(TAG,"Inside task.isSuccessful");
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Log.i(TAG,"Society found with is "+document.getId());
                                        society_name=document.get("society_name").toString();
                                        soc_id=document.getString("unique_id");
                                        doc_id=document.getId();

                                        FOUND[0] =TRUE;
                                        progressBar.setVisibility(View.GONE);
                                    }

                                    if(!FOUND[0]){
                                        showMessage("Error","Society not found with this ID",R.drawable.ic_error_dialog);
                                        progressBar.setVisibility(View.GONE);
                                        et_soc_id.setEnabled(true);
                                    }

                                    if(FOUND[0]){
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        fragment =new DetailsFragment();
                                        Bundle args = new Bundle();
                                        args.putString("skip", "noskip");
                                        args.putString("society_name",society_name);
                                        args.putString("society_id",soc_id);
                                        args.putString("document_id",doc_id);
                                        fragment.setArguments(args);
                                        ft.replace(R.id.fragment_register,fragment);
                                        ft.commit();
                                        progressBar.setVisibility(View.GONE);
                                        et_soc_id.setEnabled(true);
                                    }
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });

            }
        });

        skip = v.findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                fragment =new DetailsFragment();
                Bundle args = new Bundle();
                args.putString("skip", "skip");
                args.putString("isNewUSer",isNewUSer);
                fragment.setArguments(args);
                ft.replace(R.id.fragment_register,fragment);
                ft.commit();

            }
        });





        return v;
    }


//    public void showMessage(String title, String message){
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setCancelable(true);
//        builder.setTitle(title);
//        builder.setMessage(message);
//        builder.show();
//
//    }

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


    @Override
    public void onStart() {
        super.onStart();
        imm.hideSoftInputFromWindow(viewGroup.getWindowToken(), 0);
    }
}

package com.replon.www.replonhomy.Voting;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.SwipeToDelete;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class AddPollActivity extends AppCompatActivity {


    public static final String TAG = "AddPollActivity";
    ImageView back;
    EditText poll_ques;
    private RecyclerView recyclerView;
    private ChoicesAdapter mAdapter;
    TextView tv_poll_end_time, add_choices;
    TextView generate;
    String poll_question;

    List<String>choices;
    List<ContentsChoices> choicesList;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    Calendar date;
    Timestamp poll_end_time;
    ProgressBar progressBar;
    FirebaseFirestore db;
    ListenerRegistration getDataListener;
    RelativeLayout relativeLayout;
    LinearLayoutManager linearLayoutManager;
    Boolean isConnected,monitoringConnectivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), AddPollActivity.this);

        setContentView(R.layout.activity_voting_add_poll);


        back=(ImageView)findViewById(R.id.back);
        choicesList=new ArrayList<>();
        poll_ques = findViewById(R.id.poll_question);
        add_choices = findViewById(R.id.add_choices);
        generate=(TextView)findViewById(R.id.generate_poll);
        tv_poll_end_time =(TextView)findViewById(R.id.poll_ends_time);
        progressBar=(ProgressBar)findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        relativeLayout=(RelativeLayout)findViewById(R.id.mainrel);

        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        db=FirebaseFirestore.getInstance();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.mainrel);
        relativeLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                InputMethodManager imm =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.choices_recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);//by default manager is vertical
        choices=new ArrayList<String>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        choicesList.add(new ContentsChoices(""));
        choicesList.add(new ContentsChoices(""));


        mAdapter = new ChoicesAdapter(getApplicationContext(), choicesList);
        recyclerView.setAdapter(mAdapter);

        if(choicesList.size()>=5){
            add_choices.setVisibility(View.GONE);
        }
        add_choices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = mAdapter.getItemCount();
                Log.i(TAG, "size is " + size);
                Boolean FLAG=FALSE;
                for (int i = 0; i < size; i++) {
                    Log.i(TAG,"Choice list at pos "+i+"is"+choicesList.get(i).getChoice());
                    if(choicesList.get(i).getChoice().equals("")){

                        FLAG=TRUE;
                    }
                }
                if(!FLAG){
                    choicesList.add(new ContentsChoices(""));
                    mAdapter.notifyDataSetChanged();
//                    recyclerView.findViewHolderForAdapterPosition(choicesList.size()-1).itemView.requestFocus();

                    final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null && inputMethodManager.isActive()) {
                        if (getCurrentFocus() != null) {
                            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                    }

                }else{
                    showMessage("Error","Choice cannot be empty",R.drawable.ic_error_dialog);
                }


                if(choicesList.size()>=5){
                    add_choices.setVisibility(View.GONE);
                }


            }
        });


        tv_poll_end_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              showDateAndTimePicker();
            }
        });



        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                poll_question=poll_ques.getText().toString().trim();
                Log.i(TAG,"choice size is "+choicesList.size());
                Log.i(TAG,"choice 0 is "+choicesList.get(0).getChoice());
                Log.i(TAG,"choice 1 is "+choicesList.get(1).getChoice());
                if(!isConnected){
                    final Dialog dialog = new Dialog(AddPollActivity.this);
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
                else if(poll_question.isEmpty()){
                    showMessage("Error!","Poll must have a question",R.drawable.ic_error_dialog);
                }
                else if(choicesList.size()==2 && (choicesList.get(0).getChoice().equals("") || choicesList.get(1).getChoice().equals(""))){
                    showMessage("Error!","Poll must have at least two choices",R.drawable.ic_error_dialog);
                }
                else if(poll_end_time ==null){
                    //showMessage("Error!","Poll must have an end time",R.drawable.ic_error_dialog);
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Poll must have an end time", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    showDateAndTimePicker();

                }
                else{
                    addData();
                }
            }
        });

        enableSwipeToDelete();

    }

    private void enableSwipeToDelete() {

        SwipeToDelete swipeToDelete = new SwipeToDelete(this) {

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int position = viewHolder.getAdapterPosition();
                final ContentsChoices item = choicesList.get(position);

                if (choicesList.size() == 2) {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Poll must have at least two choices", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                    mAdapter.notifyDataSetChanged();
                } else {
                    choicesList.remove(position);
                    mAdapter.notifyItemRemoved(position);
                    mAdapter.notifyDataSetChanged();

                    //undo code below
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Choice was removed from the list.", Snackbar.LENGTH_LONG);
                    snackbar.setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            choicesList.add(position, item);
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

    private void showDateAndTimePicker(){
        final Calendar currentDate = Calendar.getInstance();
        date = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(AddPollActivity.this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {

                date.set(year, monthOfYear, dayOfMonth);
                new TimePickerDialog(AddPollActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        date.set(Calendar.MINUTE, minute);
                        // Log.v(TAG, "The choosen one " + date.getTime());
                        // Toast.makeText(getContext(),"The choosen one " + date.getTime(),Toast.LENGTH_SHORT).show();
                        tv_poll_end_time.setText(new SimpleDateFormat("h:mm a d MMM, yyyy").format(date.getTime()));
                        Log.i(TAG,"Set date is "+date);

                        poll_end_time =new Timestamp(date.getTime());
                        Log.i(TAG,"Poll end date is "+ poll_end_time);
                    }

                },currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), false).show();

            }
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.getDatePicker().setMinDate(currentDate.getTimeInMillis());
        datePickerDialog.show();
    }
    private void addData(){
        generate.setEnabled(FALSE);
        poll_ques.setEnabled(FALSE);
        progressBar.setVisibility(View.VISIBLE);

        final String user_id = firebaseUser.getUid();
        final String societies = getString(R.string.SOCIETIES);
        final String user = getString(R.string.USER);

        Boolean emptyChoices=FALSE;
        int size = mAdapter.getItemCount();

        Log.i(TAG, "size is " + size);

        for (int i = 0; i < size; i++) {
            if(choicesList.get(i).getChoice().equals("")){
                //showMessage("Error","Choice list cannot be empty",R.drawable.ic_error_dialog);
                emptyChoices=TRUE;
            }else{
                choices.add(choicesList.get(i).getChoice());

            }

        }
        Log.i(TAG,"Choices are "+choices);


        if(!isConnected){
            final Dialog dialog = new Dialog(AddPollActivity.this);
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
            //internet is available
            //let's add data to firebase

            final DocumentReference docRef = db.collection(user).document(user_id);

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


                        DocumentReference doc_id=soc_id_ref.collection("voting_poll").document();
                        Map<String, Object> data = new HashMap<>();
                        data.put("date_created", new Timestamp(new Date()));
                        data.put("choices", choices);
                        data.put("question", poll_question);
                        data.put("time_poll_ends", poll_end_time);
                        data.put("document_id",doc_id);

                        doc_id.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                progressBar.setVisibility(View.GONE);

                                finish();
                                overridePendingTransition(0, 0);

                            }
                        }) .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                progressBar.setVisibility(View.GONE);
                                showMessage("Error", "unable to generate Poll",R.drawable.ic_error_dialog);
                            }
                        });


                    } else {
                        Log.d(TAG, source + " data: null");
                    }

                }
            });
        }



    }
    public void showMessage(String title, String message,int image){

        final Dialog dialog = new Dialog(AddPollActivity.this);
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

//        btn_positive.setText("OK");
//        btn_negative.setText("Go to Settings");
//        btn_positive.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        btn_negative.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(myIntent);
//            }
//        });
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
            final Dialog dialog = new Dialog(AddPollActivity.this);
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

            final Dialog dialog = new Dialog(AddPollActivity.this);
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

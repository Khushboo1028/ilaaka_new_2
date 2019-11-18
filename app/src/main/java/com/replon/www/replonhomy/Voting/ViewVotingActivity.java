package com.replon.www.replonhomy.Voting;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ViewVotingActivity extends AppCompatActivity {

    public static final String TAG = "ViewVotingActivity";
    String question,time_ends,time_created;
    TextView tv_poll_question,tv_time_ends,poll_ends_text;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    List<String> choiceList;
    List<ContentsChoices> choices;
    Boolean timePassed;
    String doc_id;
    ImageView back;
    String selected_choice;
    ListenerRegistration registration;
    String flat_no;
    String vote_flat;
    Boolean shouldWeUpdate=TRUE;
    DocumentReference soc_id_ref;
    Collection<String> hashmap_values;
    String choiceOfFlat;

    RelativeLayout relativeLayout;
    private PieChart chart;
    private float[] yData;
    public String[] xData;

    Typeface typeface;
    Boolean isConnected,monitoringConnectivity;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewVotingActivity.this);

        setContentView(R.layout.activity_voting_view_poll);

        choiceList=new ArrayList<>();

         typeface = ResourcesCompat.getFont(this, R.font.avnxt_medium);

        chart=findViewById(R.id.pie_chart);


        //Chart Setting
        chart.setUsePercentValues(true);

        chart.setExtraOffsets(5, 0, 0, 10);
        chart.setCenterText("Poll Results");
        chart.setDrawCenterText(true);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setCenterTextSize(18);
        chart.setHoleRadius(60);
        chart.setRotationEnabled(true);
        chart.setCenterTextColor(Color.WHITE);
        chart.setEntryLabelColor(Color.WHITE);
        chart.setHighlightPerTapEnabled(true);
        chart.animateY(1400, Easing.EaseInOutQuad);
        chart.setDrawEntryLabels(true);
        chart.setTransparentCircleAlpha(0);
        chart.setUsePercentValues(true);
        chart.setCenterTextTypeface(typeface);
        chart.setEntryLabelTypeface(typeface);
        chart.getDescription().setEnabled(false);
        chart.setNoDataText("No votes have been recorded for this poll");
        chart.setNoDataTextColor(Color.WHITE);
//        chart.getDescription().setText("Note: The values displayed on the chart are in percentage (%)");
//        chart.getDescription().setYOffset(-12);
//        chart.getDescription().setTypeface(typeface);
//        chart.getDescription().setTextSize(12);

        question=getIntent().getStringExtra("poll_name");
        time_ends=getIntent().getStringExtra("poll_end_time");
        time_created=getIntent().getStringExtra("poll_time_created");
        choiceList=getIntent().getStringArrayListExtra("choiceList");
        timePassed=getIntent().getBooleanExtra("timePassed",FALSE);
        flat_no=getIntent().getStringExtra("flat_no");


        doc_id=getIntent().getStringExtra("doc_id");
        relativeLayout=(RelativeLayout)findViewById(R.id.mainrel);

        Log.i(TAG,"choiceList is "+choiceList);

        choices=new ArrayList<>();

        hashmap_values=new Collection<String>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean contains(Object o) {
                return false;
            }

            @NonNull
            @Override
            public Iterator<String> iterator() {
                return null;
            }

            @NonNull
            @Override
            public Object[] toArray() {
                return new Object[0];
            }

            @NonNull
            @Override
            public <T> T[] toArray(@NonNull T[] a) {
                return null;
            }

            @Override
            public boolean add(String s) {
                return false;
            }

            @Override
            public boolean remove(Object o) {
                return false;
            }

            @Override
            public boolean containsAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean addAll(@NonNull Collection<? extends String> c) {
                return false;
            }

            @Override
            public boolean removeAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public boolean retainAll(@NonNull Collection<?> c) {
                return false;
            }

            @Override
            public void clear() {

            }
        };


        tv_poll_question=(TextView)findViewById(R.id.poll_question);
        tv_time_ends=(TextView)findViewById(R.id.poll_ends_time);
        poll_ends_text=(TextView)findViewById(R.id.poll_ends_text);
        back=(ImageView)findViewById(R.id.back);

        tv_time_ends.setText(time_ends);
        tv_poll_question.setText(question);


        recyclerView = (RecyclerView) findViewById(R.id.choices_recycler_view);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        for(int j=0;j<choiceList.size();j++){

            if (j==0){
                xData = new String[choiceList.size()];
            }

            Log.i(TAG,"Choice list at pos "+j+" is"+choiceList.get(j));
            choices.add(j,new ContentsChoices(choiceList.get(j)));
            xData[j] = choiceList.get(j);

        }

       // getData();




        mAdapter=new ChoicesViewAdapter(this,choices){
            @Override
            public void onBindViewHolder(@NonNull final ChoicesViewHolder holder, int i) {

                final ContentsChoices contentsChoices = choices.get(i);
                holder.choice.setText(String.valueOf(contentsChoices.getChoice()));
                holder.choice.setInputType(0);


                if (timePassed) {
                    Log.i(TAG,"timepassed equals TRUE");
                    holder.choice.setEnabled(FALSE);

                    if (choiceList.size() == 2) {
                        Log.i(TAG,"In two choices");
                        showResultsOfTwoChoices();

                    } else if (choiceList.size() == 3) {
                        Log.i(TAG,"In three choices");
                        showResultsOfThreeChoices();

                    }else if(choiceList.size()==4){
                        Log.i(TAG,"In 4 choices");
                        showResultsOfFourChoices();

                    }else if(choiceList.size()==5){
                        Log.i(TAG,"In 5 choices");
                        showResultsOfFiveChoices();

                    }


                }



                final FirebaseFirestore db = FirebaseFirestore.getInstance();
                final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                final String user = getString(R.string.USER);
                final String user_id = currentFirebaseUser.getUid();

                final DocumentReference doc_ref = db.collection(user).document(user_id);

                registration = doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        if(e!=null){
                            Log.i("","Request failed");
                        }

                        String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                                ? "Local" : "Server";
//                                Log.i(TAG,"SNAPSHOT = "+source);

                        if(snapshot!=null&&snapshot.exists()){
                            soc_id_ref = (DocumentReference) snapshot.get("society_id");
                            DocumentReference doc_ref =  soc_id_ref.collection("voting_poll").document(doc_id);

                            doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                    if(e!=null){
                                        Log.i(TAG,"Request failed");
                                    }

                                    if(documentSnapshot!=null&&documentSnapshot.exists()) {
                                        HashMap votedDict = (HashMap) documentSnapshot.get("voted");
//                                Log.i(TAG,"Dict is "+votedDict.containsKey(flat_no));

                                        if(votedDict==null){
                                            shouldWeUpdate=TRUE;
                                            chart.setVisibility(View.GONE);
                                        }else {

                                            for (int i = 0; i < votedDict.size(); i++) {
                                                Log.i(TAG, "Flat no is" + votedDict.containsKey(flat_no));
                                                if (votedDict.containsKey(flat_no)) {
                                                    shouldWeUpdate = FALSE;
                                                    chart.setVisibility(View.VISIBLE);
                                                    recyclerView.setVisibility(View.GONE);


                                                    choiceOfFlat=String.valueOf(votedDict.get(flat_no));
                                                    Log.i(TAG,"choice of flat is "+choiceOfFlat);

                                                }
                                            }

                                            if(choiceOfFlat!=null){
                                                Log.i(TAG,"choice of current flat is "+choiceOfFlat);
                                                if (choiceList.size() == 2) {
                                                    Log.i(TAG,"In two choices");
                                                    showResultsOfTwoChoices();
                                                } else if (choiceList.size() == 3) {
                                                    Log.i(TAG,"In three choices");
                                                    showResultsOfThreeChoices();
                                                }else if(choiceList.size()==4){
                                                    Log.i(TAG,"In 4 choices");
                                                    showResultsOfFourChoices();
                                                }else if(choiceList.size()==5){
                                                    Log.i(TAG,"In 5 choices");
                                                    showResultsOfFiveChoices();
                                                }
                                            }
                                        }



                                    }
                                }
                            });
                        }else {
                            Log.d(TAG, source + " data: null");
                        }
                    }
                });

                holder.choice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.i(TAG, "Choice clicked");

                        //   holder.choice.setBackgroundColor(Color.parseColor("#000000"));

                        selected_choice = contentsChoices.getChoice();

                        Log.i(TAG,"should we update? "+shouldWeUpdate);

//                        if(!shouldWeUpdate) {
//
//                            if (choiceList.size() == 2) {
//                                Log.i(TAG,"In two choices");
//                                showResultsOfTwoChoices();
//                            } else if (choiceList.size() == 3) {
//                                Log.i(TAG,"In three choices");
//                                showResultsOfThreeChoices();
//                            }else if(choiceList.size()==4){
//                                Log.i(TAG,"In 4 choices");
//                                showResultsOfFourChoices();
//                            }else if(choiceList.size()==5){
//                                Log.i(TAG,"In 5 choices");
//                                showResultsOfFiveChoices();
//                            }
//                        }


                        if(!isConnected){
                            final Dialog dialog = new Dialog(ViewVotingActivity.this);
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
                        else if (shouldWeUpdate && !timePassed) {
                            setData();
                            chart.setVisibility(View.GONE);


                        } else {
                            showMessage("Oops!", "Your response has already been recorded", R.drawable.ic_error_dialog);


                        }

                    }
                });






            }
        };
        recyclerView.setAdapter(mAdapter);


        vote_flat="voted."+flat_no;



        if(timePassed){
//            tv_time_ends.setTextColor(Color.RED);
//            poll_ends_text.setTextColor(Color.RED);
            poll_ends_text.setText("Poll ended on: ");
            chart.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            Log.i(TAG,"chart visible");
            if (choiceList.size() == 2) {
                Log.i(TAG,"In two choices");
                showResultsOfTwoChoices();
            } else if (choiceList.size() == 3) {
                Log.i(TAG,"In three choices");
                showResultsOfThreeChoices();
            }else if(choiceList.size()==4){
                Log.i(TAG,"In 4 choices");
                showResultsOfFourChoices();
            }else if(choiceList.size()==5){
                Log.i(TAG,"In 5 choices");
                showResultsOfFiveChoices();
            }

        }else{
            chart.setVisibility(View.GONE);
        }



    }


    private void setChartData(){

        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();

        for (int i = 0;i<yData.length;i++){
            yEntries.add(new PieEntry(yData[i],i));
        }

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(10f);
        l.setYOffset(10f);
        l.setTextColor(Color.WHITE);

        l.setWordWrapEnabled(true);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.YELLOW);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);


        l.setTypeface(typeface);
        l.setTextSize(14);


//        LegendEntry l1 = new LegendEntry(xData[0], Legend.LegendForm.CIRCLE,10,2,null,colors.get(0));
//        l.setCustom(Arrays.asList(l1));

//        LegendEntry[] legendEntries = new LegendEntry[xData.length];
        ArrayList<LegendEntry> legendEntries= new ArrayList<>();

        for (int i = 0;i<xData.length;i++){
            xEntries.add(choiceList.get(i));

//            legendEntries[i-1] = new LegendEntry(xData[i], Legend.LegendForm.CIRCLE,10,2,null,colors.get(i-1));
            legendEntries.add(new LegendEntry(choiceList.get(i), Legend.LegendForm.SQUARE,10,2,null,colors.get(i)));

        }
        l.setCustom(legendEntries);
        l.setEnabled(true);

        PieDataSet dataSet = new PieDataSet(yEntries,"Poll Results");
        dataSet.setSliceSpace(8);
        dataSet.setValueTextSize(16);
        dataSet.setColors(colors);




        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        chart.setData(data);
        chart.invalidate();

    }



    private void showResultsOfThreeChoices() {
        yData = new float[3];

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String user = getString(R.string.USER);
        final String user_id = currentFirebaseUser.getUid();
        final DocumentReference doc_ref = db.collection(user).document(user_id);

        final List<String> choice1=new ArrayList<>();
        final List<String> choice2=new ArrayList<>();
        final List<String> choice3=new ArrayList<>();

        registration = doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i("","Request failed");
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
//                                Log.i(TAG,"SNAPSHOT = "+source);

                if(snapshot!=null&&snapshot.exists()){
                    soc_id_ref = (DocumentReference) snapshot.get("society_id");
                    DocumentReference doc_ref =  soc_id_ref.collection("voting_poll").document(doc_id);

                    doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.i(TAG,"Request failed");
                            }

                            if(documentSnapshot!=null&&documentSnapshot.exists()) {
                                HashMap votedDict = (HashMap) documentSnapshot.get("voted");
//                                Log.i(TAG,"Dict is "+votedDict.containsKey(flat_no));


                                if(votedDict==null){
                                    Log.i(TAG,"Voted dictionary does not exist ");
                                }else {

                                    Log.i(TAG,"Values are "+ votedDict.values());
                                    hashmap_values=votedDict.values();
                                    Log.i(TAG,"hashmap_values are "+hashmap_values);


                                    List both_choice=new ArrayList(hashmap_values);

                                    String first_value=String.valueOf(both_choice.get(0));
                                    String second_value;

                                    Log.i(TAG,"first value is "+first_value);

                                    for(int i=0;i<both_choice.size();i++){

//                                        if(String.valueOf(both_choice.get(i)).equals(first_value)){
//                                            choice1.add(String.valueOf(both_choice.get(i)));
//
//                                            Log.i(TAG,"here");
//                                        }else{
//                                            choice2.add(String.valueOf(both_choice.get(i)));
//                                            Log.i(TAG,"Here in else");
//                                        }
                                        if(choiceList.get(0).equals(String.valueOf(both_choice.get(i)))){
                                            choice1.add(String.valueOf(both_choice.get(i)));
                                        }else if(choiceList.get(1).equals(String.valueOf(both_choice.get(i)))){
                                            choice2.add(String.valueOf(both_choice.get(i)));
                                        }else{
                                            choice3.add(String.valueOf(both_choice.get(i)));
                                        }
                                    }

                                    Log.i(TAG,"choice1 array is "+choice1);
                                    Log.i(TAG,"choice2 array is "+choice2);
                                    Log.i(TAG,"choice3 array is "+choice3);




                                    //     now we calculate percentage

                                    float size1=Float.valueOf(choice1.size());
                                    float size2=Float.valueOf(choice2.size());
                                    float size3=Float.valueOf(choice3.size());


                                    Log.i(TAG,"choice1 size is "+size1);
                                    Log.i(TAG,"choice2 size is "+size2);
                                    Log.i(TAG,"choice3 size is "+size3);

                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                    float oneDigit;

                                    float percentage_choice1=(size1/(size1+size2+size3))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice1));
                                    Log.i(TAG,"percentage choice1 is "+oneDigit);
                                    yData[0] = oneDigit;

                                    float percentage_choice2=(size2/(size1+size2+size3))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice2));
                                    Log.i(TAG,"percentage choice2 is "+oneDigit);
                                    yData[1] = oneDigit;

                                    float percentage_choice3=(size3/(size1+size2+size3))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice3));
                                    Log.i(TAG,"percentage choice3 is "+oneDigit);
                                    yData[2] = oneDigit;

                                    setChartData();



                                }


                            }
                        }
                    });
                }else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });



    }


    private void showResultsOfFourChoices() {
        yData = new float[4];

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String user = getString(R.string.USER);
        final String user_id = currentFirebaseUser.getUid();
        final DocumentReference doc_ref = db.collection(user).document(user_id);

        final List<String> choice1=new ArrayList<>();
        final List<String> choice2=new ArrayList<>();
        final List<String> choice3=new ArrayList<>();
        final List<String> choice4=new ArrayList<>();

        registration = doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i("","Request failed");
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
//                                Log.i(TAG,"SNAPSHOT = "+source);

                if(snapshot!=null&&snapshot.exists()){
                    soc_id_ref = (DocumentReference) snapshot.get("society_id");
                    DocumentReference doc_ref =  soc_id_ref.collection("voting_poll").document(doc_id);

                    doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.i(TAG,"Request failed");
                            }

                            if(documentSnapshot!=null&&documentSnapshot.exists()) {
                                HashMap votedDict = (HashMap) documentSnapshot.get("voted");
//                                Log.i(TAG,"Dict is "+votedDict.containsKey(flat_no));


                                if(votedDict==null){
                                    Log.i(TAG,"Voted dictionary does not exist ");
                                }else {

                                    Log.i(TAG,"Values are "+ votedDict.values());
                                    hashmap_values=votedDict.values();
                                    Log.i(TAG,"hashmap_values are "+hashmap_values);


                                    List both_choice=new ArrayList(hashmap_values);

                                    String first_value=String.valueOf(both_choice.get(0));
                                    String second_value;

                                    Log.i(TAG,"first value is "+first_value);

                                    for(int i=0;i<both_choice.size();i++){

                                        if(choiceList.get(0).equals(String.valueOf(both_choice.get(i)))){
                                            choice1.add(String.valueOf(both_choice.get(i)));
                                        }else if(choiceList.get(1).equals(String.valueOf(both_choice.get(i)))){
                                            choice2.add(String.valueOf(both_choice.get(i)));
                                        }else if(choiceList.get(2).equals(String.valueOf(both_choice.get(i)))) {
                                            choice3.add(String.valueOf(both_choice.get(i)));
                                        }else{
                                            choice4.add(String.valueOf(both_choice.get(i)));
                                        }
                                    }

                                    Log.i(TAG,"choice1 array is "+choice1);
                                    Log.i(TAG,"choice2 array is "+choice2);
                                    Log.i(TAG,"choice3 array is "+choice3);
                                    Log.i(TAG,"choice4 array is "+choice4);


                                    //     now we calculate percentage

                                    float size1=Float.valueOf(choice1.size());
                                    float size2=Float.valueOf(choice2.size());
                                    float size3=Float.valueOf(choice3.size());
                                    float size4=Float.valueOf(choice4.size());


                                    Log.i(TAG,"choice1 size is "+size1);
                                    Log.i(TAG,"choice2 size is "+size2);
                                    Log.i(TAG,"choice3 size is "+size3);
                                    Log.i(TAG,"choice4 size is "+size4);

                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                    float oneDigit;

                                    float percentage_choice1=(size1/(size1+size2+size3+size4))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice1));
                                    Log.i(TAG,"percentage choice1 is "+oneDigit);
                                    yData[0] = oneDigit;

                                    float percentage_choice2=(size2/(size1+size2+size3+size4))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice2));
                                    Log.i(TAG,"percentage choice2 is "+oneDigit);
                                    yData[1] = oneDigit;

                                    float percentage_choice3=(size3/(size1+size2+size3+size4))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice3));
                                    Log.i(TAG,"percentage choice3 is "+oneDigit);
                                    yData[2] = oneDigit;

                                    float percentage_choice4=(size4/(size1+size2+size3+size4))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice4));
                                    Log.i(TAG,"percentage choice4 is "+oneDigit);
                                    yData[3] = oneDigit;

                                    setChartData();






                                }


                            }
                        }
                    });
                }else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });



    }


    private void showResultsOfFiveChoices() {
        yData = new float[5];

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String user = getString(R.string.USER);
        final String user_id = currentFirebaseUser.getUid();
        final DocumentReference doc_ref = db.collection(user).document(user_id);

        final List<String> choice1=new ArrayList<>();
        final List<String> choice2=new ArrayList<>();
        final List<String> choice3=new ArrayList<>();
        final List<String> choice4=new ArrayList<>();
        final List<String> choice5=new ArrayList<>();

        registration = doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i("","Request failed");
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
//                                Log.i(TAG,"SNAPSHOT = "+source);

                if(snapshot!=null&&snapshot.exists()){
                    soc_id_ref = (DocumentReference) snapshot.get("society_id");
                    DocumentReference doc_ref =  soc_id_ref.collection("voting_poll").document(doc_id);

                    doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.i(TAG,"Request failed");
                            }

                            if(documentSnapshot!=null&&documentSnapshot.exists()) {
                                HashMap votedDict = (HashMap) documentSnapshot.get("voted");
//                                Log.i(TAG,"Dict is "+votedDict.containsKey(flat_no));


                                if(votedDict==null){
                                    Log.i(TAG,"Voted dictionary does not exist ");
                                }else {

                                    Log.i(TAG,"Values are "+ votedDict.values());
                                    hashmap_values=votedDict.values();
                                    Log.i(TAG,"hashmap_values are "+hashmap_values);


                                    List both_choice=new ArrayList(hashmap_values);

                                    String first_value=String.valueOf(both_choice.get(0));
                                    String second_value;

                                    Log.i(TAG,"first value is "+first_value);

                                    for(int i=0;i<both_choice.size();i++){

                                        if(choiceList.get(0).equals(String.valueOf(both_choice.get(i)))){
                                            choice1.add(String.valueOf(both_choice.get(i)));
                                        }else if(choiceList.get(1).equals(String.valueOf(both_choice.get(i)))){
                                            choice2.add(String.valueOf(both_choice.get(i)));
                                        }else if(choiceList.get(2).equals(String.valueOf(both_choice.get(i)))) {
                                            choice3.add(String.valueOf(both_choice.get(i)));
                                        }else if(choiceList.get(3).equals(String.valueOf(both_choice.get(i)))){
                                            choice4.add(String.valueOf(both_choice.get(i)));
                                        }else{
                                            choice5.add(String.valueOf(both_choice.get(i)));
                                        }
                                    }

                                    Log.i(TAG,"choice1 array is "+choice1);
                                    Log.i(TAG,"choice2 array is "+choice2);
                                    Log.i(TAG,"choice3 array is "+choice3);
                                    Log.i(TAG,"choice4 array is "+choice4);
                                    Log.i(TAG,"choice4 array is "+choice5);


                                    //     now we calculate percentage

                                    float size1=Float.valueOf(choice1.size());
                                    float size2=Float.valueOf(choice2.size());
                                    float size3=Float.valueOf(choice3.size());
                                    float size4=Float.valueOf(choice4.size());
                                    float size5=Float.valueOf(choice5.size());


                                    Log.i(TAG,"choice1 size is "+size1);
                                    Log.i(TAG,"choice2 size is "+size2);
                                    Log.i(TAG,"choice3 size is "+size3);
                                    Log.i(TAG,"choice4 size is "+size4);
                                    Log.i(TAG,"choice5 size is "+size4);

                                    float sum=size1+size2+size3+size4+size5;

                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                    float oneDigit;

                                    float percentage_choice1=(size1/sum)*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice1));
                                    Log.i(TAG,"percentage choice1 is "+oneDigit);
                                    yData[0] = oneDigit;

                                    float percentage_choice2=(size2/sum)*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice2));
                                    Log.i(TAG,"percentage choice2 is "+oneDigit);
                                    yData[1] = oneDigit;

                                    float percentage_choice3=(size3/sum)*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice3));
                                    Log.i(TAG,"percentage choice3 is "+oneDigit);
                                    yData[2] = oneDigit;

                                    float percentage_choice4=(size4/sum)*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice4));
                                    Log.i(TAG,"percentage choice4 is "+oneDigit);
                                    yData[3] = oneDigit;

                                    float percentage_choice5=(size5/sum)*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice5));
                                    Log.i(TAG,"percentage choice5 is "+oneDigit);
                                    yData[4] = oneDigit;


                                    setChartData();




                                }


                            }
                        }
                    });
                }else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });



    }




    private void showResultsOfTwoChoices() {

        yData = new float[2];

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String user = getString(R.string.USER);
        final String user_id = currentFirebaseUser.getUid();
        final DocumentReference doc_ref = db.collection(user).document(user_id);

        final List<String> choice1=new ArrayList<>();
        final List<String> choice2=new ArrayList<>();

        registration = doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i("","Request failed");
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
//                                Log.i(TAG,"SNAPSHOT = "+source);

                if(snapshot!=null&&snapshot.exists()){
                    soc_id_ref = (DocumentReference) snapshot.get("society_id");
                    DocumentReference doc_ref =  soc_id_ref.collection("voting_poll").document(doc_id);

                    doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@javax.annotation.Nullable DocumentSnapshot documentSnapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                            if(e!=null){
                                Log.i(TAG,"Request failed");
                            }

                            if(documentSnapshot!=null&&documentSnapshot.exists()) {
                                HashMap votedDict = (HashMap) documentSnapshot.get("voted");
//                                Log.i(TAG,"Dict is "+votedDict.containsKey(flat_no));


                                if(votedDict==null){
                                    Log.i(TAG,"Voted dictionary does not exist ");
                                }else {

                                    Log.i(TAG,"Values are "+ votedDict.values());
                                    hashmap_values=votedDict.values();
                                    Log.i(TAG,"hashmap_values are "+hashmap_values);


                                    List both_choice=new ArrayList(hashmap_values);

//                                    String first_value=String.valueOf(both_choice.get(0));
//                                    Log.i(TAG,"first value is "+first_value);

                                    for(int i=0;i<both_choice.size();i++){

                                        if(choiceList.get(0).equals(String.valueOf(both_choice.get(i)))){
                                            choice1.add(String.valueOf(both_choice.get(i)));
                                            Log.i(TAG,"here");
                                        }else{
                                            choice2.add(String.valueOf(both_choice.get(i)));
                                            Log.i(TAG,"Here in else");
                                        }
                                    }

                                    Log.i(TAG,"choice1 array is "+choice1);
                                    Log.i(TAG,"choice2 array is "+choice2);


                                    //     now we calculate percentage

                                    float size1=Float.valueOf(choice1.size());
                                    float size2=Float.valueOf(choice2.size());


                                    Log.i(TAG,"choice1 size is "+size1);
                                    Log.i(TAG,"choice2 size is "+size2);

                                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                                    float oneDigit;

                                    float percentage_choice1=(size1/(size1+size2))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice1));
                                    Log.i(TAG,"percentage choice1 is "+oneDigit);
                                    yData[0] = oneDigit;

                                    float percentage_choice2=(size2/(size1+size2))*100;
                                    oneDigit = Float.valueOf(decimalFormat.format(percentage_choice2));
                                    Log.i(TAG,"percentage choice2 is "+oneDigit);
                                    yData[1] = oneDigit;

                                    setChartData();



                                }


                            }
                        }
                    });
                }else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });



    }



    private void setData() {

        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        final String user = getString(R.string.USER);
        final String user_id = currentFirebaseUser.getUid();

        final DocumentReference doc_ref = db.collection(user).document(user_id);

        registration = doc_ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable DocumentSnapshot snapshot, @javax.annotation.Nullable FirebaseFirestoreException e) {
                if(e!=null){
                    Log.i("","Request failed");
                }

                String source = snapshot != null && snapshot.getMetadata().hasPendingWrites()
                        ? "Local" : "Server";
//                                Log.i(TAG,"SNAPSHOT = "+source);

                if(snapshot!=null&&snapshot.exists()){
                    DocumentReference soc_id_ref = (DocumentReference) snapshot.get("society_id");


                    DocumentReference doc_ref =  soc_id_ref.collection("voting_poll").document(doc_id);



                    Map<String, Object> data = new HashMap<>();
                    data.put(vote_flat, selected_choice);

                    doc_ref.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.i(TAG,"updated");
                          //  showMessage("Success", "Your vote has been recorded",R.drawable.ic_success_dialog);

                            Snackbar snackbar = Snackbar
                                    .make(relativeLayout, "Success! Vote recorded", Snackbar.LENGTH_LONG);
                            snackbar.setActionTextColor(Color.YELLOW);
                            snackbar.show();
                            chart.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);

                            if (choiceList.size() == 2) {
                                Log.i(TAG,"In two choices");
                                showResultsOfTwoChoices();
                            } else if (choiceList.size() == 3) {
                                Log.i(TAG,"In three choices");
                                showResultsOfThreeChoices();
                            }else if(choiceList.size()==4){
                                Log.i(TAG,"In 4 choices");
                                showResultsOfFourChoices();
                            }else if(choiceList.size()==5){
                                Log.i(TAG,"In 5 choices");
                                showResultsOfFiveChoices();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.i(TAG,"Could not update "+e.getMessage());
                        }
                    });
                }else {
                    Log.d(TAG, source + " data: null");
                }
            }
        });

    }



    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(ViewVotingActivity.this);
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
            final Dialog dialog = new Dialog(ViewVotingActivity.this);
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
                    Intent myIntent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
                    startActivity(myIntent);
                }
            });
            dialog_icon.setImageResource(R.drawable.ic_no_internet);
            dialog.show();
        }
    };

    // Method to check internet connectivity in Main Activity
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

            final Dialog dialog = new Dialog(ViewVotingActivity.this);
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
}

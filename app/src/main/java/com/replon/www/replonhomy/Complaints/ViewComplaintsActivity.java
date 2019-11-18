package com.replon.www.replonhomy.Complaints;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.DownloadAdapter;
import com.replon.www.replonhomy.Utility.DownloadsAttachmentContent;

import java.util.ArrayList;
import java.util.List;

public class ViewComplaintsActivity extends AppCompatActivity {

    public static final String TAG = "ViewComplaintsActivity";
    TextView cancel,tv_date;
    TextView tv_subject,tv_description;
    TextView tv_time;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;


    private List<DownloadsAttachmentContent> downloadList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewComplaintsActivity.this);

        setContentView(R.layout.activity_view_complaint);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        cancel=(TextView)findViewById(R.id.cancel);
        tv_subject=(TextView) findViewById(R.id.subject);
        tv_subject.setTextIsSelectable(true);
        tv_description=(TextView) findViewById(R.id.description);
        tv_description.setMovementMethod(new ScrollingMovementMethod());
        tv_description.setTextIsSelectable(true);
        tv_date=(TextView)findViewById(R.id.date);
        tv_time=(TextView)findViewById(R.id.time);

        String date=getIntent().getStringExtra("date");
        String subject=getIntent().getStringExtra("subject");
        String description=getIntent().getStringExtra("description");
        ArrayList<String> image_url=getIntent().getStringArrayListExtra("image_url");
        String time=getIntent().getStringExtra("time");

        Log.i(TAG,"Date is " +date);
        Log.i(TAG,"subject is " +description);
        Log.i(TAG,"description is " +subject);
        Log.i(TAG,"Image URL array is "+image_url);


        tv_subject.setText(subject);
        tv_description.setText(description);
        tv_date.setText(date);
        tv_time.setText(time);

        downloadList =new ArrayList<DownloadsAttachmentContent>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));



        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(image_url.size()!=0) {
            for (int i = 0; i < image_url.size(); i++) {

                downloadList.add(new DownloadsAttachmentContent(image_url.get(i),"image",""));

            }
        }

        mAdapter=new DownloadAdapter(this, downloadList);
        recyclerView.setAdapter(mAdapter);


    }



}


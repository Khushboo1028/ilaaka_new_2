package com.replon.www.replonhomy.MinutesOfMeetings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.replon.www.replonhomy.Utility.DownloadsAttachmentContent;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.DownloadAdapter;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.FALSE;

public  class ViewMOMActivity extends AppCompatActivity {

    public static final String TAG = "ViewMOMActivity";
    ArrayList<String> subject,description,image_url,pdf_url;
    List<ContentsAddMoM> addMomList;
    String meeting_name;
    ProgressBar progressBar;
    EditText et_meetingName;
    private ImageView back,add_subject;
    private TextView generate;

    ImageView add_image;


    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    private RecyclerView recyclerViewImages;
    private RecyclerView.Adapter mAdapterImages;
    private List<DownloadsAttachmentContent> downloadList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewMOMActivity.this);

        setContentView(R.layout.activity_add_meeting);

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        et_meetingName=(EditText)findViewById(R.id.meeting_name);
        et_meetingName.setEnabled(FALSE);
        progressBar.setVisibility(View.GONE);
        recyclerView = (RecyclerView) findViewById(R.id.meeting_recycler_view);
        add_subject=(ImageView)findViewById(R.id.new_subject);
        add_subject.setVisibility(View.GONE);
        generate = findViewById(R.id.generate);
        generate.setVisibility(View.GONE);

        add_image = (ImageView) findViewById(R.id.add);
        add_image.setImageDrawable(getDrawable(R.drawable.ic_view_attachment));

        et_meetingName.setBackground(null);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));//by default manager is vertical

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
                overridePendingTransition(0,0);
            }
        });

        subject=new ArrayList<>();
        description=new ArrayList<>();
        addMomList=new ArrayList<>();

        meeting_name=getIntent().getStringExtra("meeting_name");
        subject=getIntent().getStringArrayListExtra("subjects");
        description=getIntent().getStringArrayListExtra("descriptions");


        Log.i(TAG,"meeting_name is"+meeting_name);
        Log.i(TAG,"Subjects "+subject);
        Log.i(TAG,"descriptions "+description);

        et_meetingName.setText(meeting_name);

        for(int i=0;i<subject.size();i++){

            addMomList.add(i,new ContentsAddMoM(subject.get(i),description.get(i)));


        }

        mAdapter=new ViewMoMAdapter(this,addMomList);
        recyclerView.setAdapter(mAdapter);


        downloadList =new ArrayList<>();
        image_url=new ArrayList<>();
        pdf_url=new ArrayList<>();

        image_url=getIntent().getStringArrayListExtra("image_url");
        pdf_url=getIntent().getStringArrayListExtra("pdf_url");

        Log.i(TAG,"image_url is "+image_url);
        Log.i(TAG,"Pdf_url is "+pdf_url);

        recyclerViewImages = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerViewImages.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerViewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

        if(image_url!=null) {
            if (!image_url.isEmpty()) {
                for (int i = 0; i < image_url.size(); i++) {

                    Log.i(TAG,"in image_download");
                    //image_url.get(i);
                    Log.i(TAG,"image_url is"+image_url.get(i));
                    if (!image_url.get(i).equals("")) {
                        downloadList.add(new DownloadsAttachmentContent(image_url.get(i), "image", ""));

                    }


                }
            }
        }
        Log.i(TAG,"pdf_url : "+pdf_url);


        if(pdf_url!=null) {

            if (!pdf_url.isEmpty()) {
                for (int i = 0; i < pdf_url.size(); i++) {

                    //image_url.get(i);
                    Log.i(TAG,"I am here");
                    if(!pdf_url.get(i).equals("")){
                        downloadList.add(new DownloadsAttachmentContent("", "pdf", pdf_url.get(i)));

                    }
                    Log.i(TAG,pdf_url.get(i));

                }
            }
        }


        mAdapterImages=new DownloadAdapter(this, downloadList);
        recyclerViewImages.setAdapter(mAdapterImages);
    }
}

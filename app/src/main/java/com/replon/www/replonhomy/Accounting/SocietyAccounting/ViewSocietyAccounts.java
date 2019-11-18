package com.replon.www.replonhomy.Accounting.SocietyAccounting;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.replon.www.replonhomy.Utility.DownloadsAttachmentContent;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DownloadAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewSocietyAccounts extends AppCompatActivity {

    public static final String TAG = "ViewSocietyAccounts";
    TextView tv_title,tv_paid_on,tv_save;
    EditText et_perticulars,et_Reference,et_amount;

    ArrayList<ContentsAccounts> accountsArrayList;
    int pos;
    Spinner spinner;
    List<String> categories,pdf_url,image_url;
    private List<DownloadsAttachmentContent> downloadList;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private ProgressBar progressBar;
    private ImageView addImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_society_account);

        tv_title=(TextView)findViewById(R.id.title);
        et_perticulars=(EditText)findViewById(R.id.particular);
        et_amount=(EditText)findViewById(R.id.amount);
        et_Reference=(EditText)findViewById(R.id.reference_no);
        tv_paid_on=(TextView)findViewById(R.id.tv_date);
        tv_title.setText("Accounts");
        tv_save=(TextView)findViewById(R.id.save);
        tv_save.setVisibility(View.GONE);

        addImage=(ImageView)findViewById(R.id.add);
        addImage.setImageDrawable(getDrawable(R.drawable.ic_view_attachment));

        accountsArrayList=getIntent().getParcelableArrayListExtra("accountList");
        pos=getIntent().getIntExtra("position",0);

        et_perticulars.setText(accountsArrayList.get(pos).getParticulars());
        et_perticulars.setInputType(0);

        et_Reference.setText(accountsArrayList.get(pos).getReference_no());
        et_Reference.setInputType(0);

        et_amount.setText(accountsArrayList.get(pos).getAmount());
        et_amount.setInputType(0);

        tv_paid_on.setText(accountsArrayList.get(pos).getPaid_on());
        Log.i(TAG,"date paid on is "+accountsArrayList.get(pos).getPaid_on());

        spinner = findViewById(R.id.spinner);
        categories=new ArrayList<>();

        categories.add(accountsArrayList.get(pos).getPaid_by());
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        downloadList =new ArrayList<>();
        image_url=new ArrayList<>();
        pdf_url=new ArrayList<>();

        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        image_url=accountsArrayList.get(pos).getImage_url();
        pdf_url=accountsArrayList.get(pos).getPdf_url();

        Log.i(TAG,"image_url is "+image_url +" and image_url size is "+image_url.size());
        Log.i(TAG,"pdf_url is " +pdf_url +" and pdf_url size is "+pdf_url.size());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));

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


        mAdapter=new DownloadAdapter(this, downloadList);
        recyclerView.setAdapter(mAdapter);





    }
}

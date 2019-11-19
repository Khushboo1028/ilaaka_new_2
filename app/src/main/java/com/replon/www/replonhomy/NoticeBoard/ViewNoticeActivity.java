package com.replon.www.replonhomy.NoticeBoard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Table;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.replon.www.replonhomy.Utility.DownloadsAttachmentContent;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;
import com.replon.www.replonhomy.Utility.DownloadAdapter;
import com.replon.www.replonhomy.R;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewNoticeActivity extends AppCompatActivity {

    public static final int PERMISSION_ALL = 1;
    public static final String TAG = "ViewNoticeActivity";
    TextView cancel,tv_date,tv_time,generate,share;
    TextView tv_subject,tv_description;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    ArrayList<String> pdf_url, image_url;
    String date,subject,description,time;

    Document document;

    private List<DownloadsAttachmentContent> downloadList;

    private static String FILE = "c:/temp/FirstPdf.pdf";
    private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
            Font.BOLD);
    private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.NORMAL, BaseColor.RED);
    private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
            Font.BOLD);
    private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
            Font.BOLD);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ViewNoticeActivity.this);

        setContentView(R.layout.activity_view_notice);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        cancel=(TextView)findViewById(R.id.cancel);
        generate=findViewById(R.id.generate);
        tv_subject=(TextView) findViewById(R.id.subject);
        tv_subject.setTextIsSelectable(true);
        tv_description=(TextView) findViewById(R.id.description);
        tv_description.setMovementMethod(new ScrollingMovementMethod());
        tv_description.setTextIsSelectable(true);
        tv_date=(TextView)findViewById(R.id.date);
        tv_time=(TextView)findViewById(R.id.time);
        share=(TextView)findViewById(R.id.share);

        date=getIntent().getStringExtra("date");
        subject=getIntent().getStringExtra("subject");
        description=getIntent().getStringExtra("description");
        image_url=getIntent().getStringArrayListExtra("image_url");
        pdf_url=getIntent().getStringArrayListExtra("pdf_url");
        time=getIntent().getStringExtra("time");


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        tv_subject.setText(subject);
        tv_description.setText(description);
        tv_date.setText(date);
        tv_time.setText(time);
        downloadList =new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_images);
        recyclerView.setHasFixedSize(true); //so it doesn't matter if element size increases or decreases
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        if(image_url!=null && image_url.size()!=0) {

            for (int i = 0; i < image_url.size(); i++) {

                 downloadList.add(new DownloadsAttachmentContent(image_url.get(i), "image",""));

            }
        }
        Log.i(TAG,"pdf_url : "+pdf_url);


        if(pdf_url!=null) {

            if (pdf_url.size() != 0) {
                for (int i = 0; i < pdf_url.size(); i++) {


                    downloadList.add(new DownloadsAttachmentContent("", "pdf", pdf_url.get(i)));
                    Log.i(TAG,pdf_url.get(i));

                }
            }
        }


        mAdapter=new DownloadAdapter(this, downloadList);
        recyclerView.setAdapter(mAdapter);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePdf();
            }
        });

    }

    private void generatePdf(){


        String[] PERMISSIONS = {

                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,

        };

        if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(ViewNoticeActivity.this, PERMISSIONS, PERMISSION_ALL);
        }else {

            String fname = "ilaaka_notice_"+date+"_"+time+".pdf";


            File file = new File(Environment.getExternalStorageDirectory().getPath(), fname);
            File file2=new File(Environment.getExternalStorageDirectory().getPath());
            file2.mkdirs();
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.i(TAG, "new file created");



            document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
                Log.i(TAG, "File created");
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.i(TAG, "An error occurred in creating file 2 " + e.getMessage());
            }
            document.open();

            try {

                addMetaData(document,subject);
                addTitlePage(document,subject,description,date);

                if(image_url!=null && image_url.size()!=0) {
                    for (int i = 0; i < image_url.size(); i++) {
                        addImageInPdf(document,image_url.get(i));
                    }
                }

                Log.i(TAG, "Data added");
                document.close();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        }





    }

    private static void addMetaData(Document document, String title) {

        document.addTitle(title);
        document.addSubject("Notice");


    }

    private static void addTitlePage(Document document,String title,String description,String date)
            throws DocumentException {
        Paragraph preface = new Paragraph();
        // We add one empty line
        addEmptyLine(preface, 1);
        // Lets write a big header
        preface.add(new Paragraph(title, catFont));

        addEmptyLine(preface, 1);
        // Will create: Report generated by: _name, _date
        preface.add(new Paragraph("Notice generated by: admin"  + ", " + date, smallBold));
        addEmptyLine(preface, 3);
        preface.add(new Paragraph(description, smallBold));

        addEmptyLine(preface, 8);


        document.add(preface);




        // Start a new page
       // document.newPage();
    }

    private void addImageInPdf(Document document,String url){
        Image image;

        try {
            image = Image.getInstance(url);
            image.setScaleToFitLineWhenOverflow(true);
            image.setAlignment(10);
            document.add(image);
//            PdfPTable table = new PdfPTable(1);
//            table.setWidthPercentage((float) 50.0);
//            table.setLockedWidth(true);
//            table.addCell(image);
//            document.add(table);

        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }


    }


    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:
                if(grantResults!=null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG,"Permissions are granted");


                    String fname = "ilaaka_notice";
                    String fpath = android.os.Environment.getExternalStorageDirectory().getPath() + fname + ".pdf";

                    File file = new File(fpath);
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                            Log.i(TAG, "new file created");
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i(TAG, "An error occurred in creating file " + e.getMessage());
                        }
                    }

                    Font bfBold12 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, new BaseColor(0, 0, 0));

                    Document document = new Document();
                    try {
                        PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
                        Log.i(TAG, "File created");
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Log.i(TAG, "An error occurred in creating file 2 " + e.getMessage());
                    }
                    document.open();

                    try {
                        document.add(new Paragraph("My First Pdf !"));
                        document.add(new Paragraph("Hello World"));
                        Log.i(TAG, "Data added");
                        document.close();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.i(TAG,"Permissions are denied");
                }
                break;
        }
    }



    @Override
    public void onBackPressed() {
        finish();
    }
}

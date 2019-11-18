package com.replon.www.replonhomy.Settings.HelpCenter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.PersonalInfo.ContentsPersonalInfo;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.util.ArrayList;
import java.util.List;

public class ReportAproblemActivity extends AppCompatActivity {

    public static final String TAG = "ReportAproblemActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    List<ContentsPersonalInfo> infoList;
    ImageView back;
    private String admin,email,name,soc_name,soc_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), ReportAproblemActivity.this);
        setContentView(R.layout.activity_report_aproblem);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        infoList=new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical

        admin = getIntent().getExtras().getString("admin");
        email = getIntent().getExtras().getString("email");
        name = getIntent().getExtras().getString("currentName");
        soc_name= getIntent().getExtras().getString("society_name");
        soc_id = getIntent().getExtras().getString("soc_id");

        infoList.add(new ContentsPersonalInfo("Something isn't working","", true));
        infoList.add(new ContentsPersonalInfo("Spam or Abuse","", true));
        infoList.add(new ContentsPersonalInfo("General Feedback","", true));

        mAdapter=new HelpCenterAdapter(getApplicationContext(),infoList){

            @Override
            public void onBindViewHolder(@NonNull HelpCenterViewHolder holder, final int i) {

                ContentsPersonalInfo contentsPersonalInfo = infoList.get(i);
                holder.title.setText(String.valueOf(contentsPersonalInfo.getTitle()));
                holder.info.setText(String.valueOf(contentsPersonalInfo.getInfo()));

                if(!contentsPersonalInfo.getNxt_show()){
                    holder.arrow_next.setVisibility(View.GONE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(i==0){
                            Log.i(TAG,"Something isn't working pressed");
                            Intent intent=new Intent(getApplicationContext(), SomethingNotWorking.class);
                            intent.putExtra("currentName",name);
                            intent.putExtra("society_name",soc_name);
                            intent.putExtra("admin",admin);
                            intent.putExtra("email",email);
                            intent.putExtra("soc_id",soc_id);
                            intent.putExtra("feedback_type","error");
                            startActivity(intent);

                        }

                        if(i==1){
                            Intent intent=new Intent(getApplicationContext(), SomethingNotWorking.class);
                            intent.putExtra("currentName",name);
                            intent.putExtra("society_name",soc_name);
                            intent.putExtra("admin",admin);
                            intent.putExtra("email",email);
                            intent.putExtra("soc_id",soc_id);
                            intent.putExtra("feedback_type","spam");
                            startActivity(intent);

                        }

                        if(i==2)
                        {
                            Intent intent=new Intent(getApplicationContext(), SomethingNotWorking.class);
                            intent.putExtra("currentName",name);
                            intent.putExtra("society_name",soc_name);
                            intent.putExtra("admin",admin);
                            intent.putExtra("email",email);
                            intent.putExtra("soc_id",soc_id);
                            intent.putExtra("feedback_type","general");
                            startActivity(intent);

                        }

                    }
                });

            }
        };

        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);

    }
}

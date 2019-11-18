package com.replon.www.replonhomy.Settings.PersonalInfo;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Settings.HelpCenter.ContactUsActivity;
import com.replon.www.replonhomy.Settings.PersonalInfo.Vehicles.VehiclesActivity;
import com.replon.www.replonhomy.Settings.Settings;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.util.ArrayList;
import java.util.List;

public class PersonalInfoActivity extends AppCompatActivity {

    private static final String TAG="PersonalInfoActivity";
    public static final int CHANGE_NAME_ACTIVITY_REQUEST_CODE = 1000;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;

    List<ContentsPersonalInfo> infoList;
    ImageView back;
    private String admin,email,name,soc_name,soc_id,flat_no;
    String count_vehicles,phone_number;
//    FirebaseAuth mAuth;
//    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), PersonalInfoActivity.this);
        setContentView(R.layout.activity_personal_info);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });

        infoList=new ArrayList<>();

//        mAuth = FirebaseAuth.getInstance();
//        firebaseUser = mAuth.getCurrentUser();


        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));//by default manager is vertical

        admin = getIntent().getExtras().getString("admin");
        email = getIntent().getExtras().getString("email");
        name = getIntent().getExtras().getString("currentName");
        soc_name= getIntent().getExtras().getString("society_name");
        soc_id = getIntent().getExtras().getString("soc_id");
        count_vehicles = getIntent().getStringExtra("count_vehicles");
        phone_number = getIntent().getStringExtra("phone_number");
        flat_no=getIntent().getStringExtra("flat_no");

        Log.i(TAG,"society unique id is "+soc_id);

        infoList.add(new ContentsPersonalInfo("Name",name, true));
        infoList.add(new ContentsPersonalInfo("Society Name",soc_name,false));
        infoList.add(new ContentsPersonalInfo("Email",email,true));
        infoList.add(new ContentsPersonalInfo("Change Password","",true));
        infoList.add(new ContentsPersonalInfo("Change SocietyID","",true));
        infoList.add(new ContentsPersonalInfo("Admin Privileges",admin,false));
        infoList.add(new ContentsPersonalInfo("Your Vehicles",count_vehicles,true));


        mAdapter = new PersonalInfoAdapter(this,infoList){
            @Override
            public void onBindViewHolder(@NonNull final PersonalInfoViewHolder holder,final int i) {
                ContentsPersonalInfo contentsPersonalInfo = infoList.get(i);
                holder.title.setText(String.valueOf(contentsPersonalInfo.getTitle()));
                holder.info.setText(String.valueOf(contentsPersonalInfo.getInfo()));

                if(!contentsPersonalInfo.getNxt_show()){
                    holder.arrow_next.setVisibility(View.GONE);
                }
                if(contentsPersonalInfo.getTitle().equals("Change SocietyID")){
                    holder.title.setTextColor(getResources().getColor(R.color.incorrect_red));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(i==0){
                            Log.i(TAG,"Change name is pressed");
                            Intent intent=new Intent(getApplicationContext(),ChangeNameActivity.class);
                            intent.putExtra("currentName",name);
                            intent.putExtra("admin",admin);
                            intent.putExtra("email",email);
                            intent.putExtra("soc_id",soc_id);

                            startActivityForResult(intent,1000);
                            holder.arrow_next.setVisibility(View.VISIBLE);

                        }

                        if(i==2){
                            Log.i(TAG,"Change email pressed");
                            Intent intent=new Intent(getApplicationContext(),ChangeEmailActivity.class);
                            intent.putExtra("currentName",name);
                            intent.putExtra("admin",admin);
                            intent.putExtra("email",email);
                            intent.putExtra("soc_id",soc_id);
                            startActivityForResult(intent,1000);
                            holder.arrow_next.setVisibility(View.VISIBLE);
                        }
                        if (i==3){

                            Log.i(TAG,"Password pressed");
                            Intent intent=new Intent(getApplicationContext(), ChangePasswordActivity.class);
                            intent.putExtra("email",email);
                            startActivity(intent);
                            holder.arrow_next.setVisibility(View.VISIBLE);

                        }
                        if (i==4){

                            Log.i(TAG,"Change societyID pressed");

                            if(soc_id.equals(getString(R.string.DUMMY_SOC_ID))){
                                Intent intent=new Intent(getApplicationContext(), ChangeSocietyID.class);
                                intent.putExtra("email",email);
                                intent.putExtra("currentName",name);
                                intent.putExtra("phone_number",phone_number);
                                intent.putExtra("soc_id",soc_id);
                                startActivity(intent);
                                holder.arrow_next.setVisibility(View.VISIBLE);
                            }else{
                                showMessageOptions("Oops!","You are already logged into a society!",R.drawable.ic_success_dialog);
                            }




                        }
                        if (i==6){

                            Log.i(TAG,"Your Vehicles pressed");
                            Intent intent=new Intent(getApplicationContext(), VehiclesActivity.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_start_activity,R.anim.exit_start_activity);
                            holder.arrow_next.setVisibility(View.VISIBLE);

                        }


                    }
                });

            }
        };

        recyclerView.setAdapter(mAdapter);
    }


    public void showMessage(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(PersonalInfoActivity.this);
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

    public void showMessageOptions(String title, String message,int image){

//        ,R.drawable.ic_error_dialog

        final Dialog dialog = new Dialog(PersonalInfoActivity.this);
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
        btn_negative.setVisibility(View.VISIBLE);
        btn_positive.setVisibility(View.VISIBLE);

        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings_intent=new Intent(getApplicationContext(), ContactUsActivity.class);
                settings_intent.putExtra("society_name",soc_name);
                settings_intent.putExtra("flat_no",flat_no);
                settings_intent.putExtra("username",email);
                settings_intent.putExtra("name",name);
                settings_intent.putExtra("admin",admin);
                startActivity(settings_intent);
                overridePendingTransition(0, 0);

            }
        });
        btn_positive.setText("Contact Us");

        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        btn_negative.setText("Cancel");
        dialog_icon.setImageResource(image);
        dialog.show();

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        name = data.getStringExtra("currentName");
        admin = data.getStringExtra("admin");
        email = data.getStringExtra("email");

        infoList.clear();
        infoList.add(new ContentsPersonalInfo("Name",name, true));
        infoList.add(new ContentsPersonalInfo("Society Name",soc_name,false));
        infoList.add(new ContentsPersonalInfo("Email",email,true));
        infoList.add(new ContentsPersonalInfo("Change Password","",true));
        infoList.add(new ContentsPersonalInfo("Change SocietyID","",true));
        infoList.add(new ContentsPersonalInfo("Admin Privileges",admin,false));
        infoList.add(new ContentsPersonalInfo("Your Vehicles",count_vehicles,true));

        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(getApplicationContext(), Settings.class);
        intent.putExtra("name",name);
        intent.putExtra("admin",admin);
        intent.putExtra("email",email);

//                startActivity(intent);
        setResult(1000,intent);
        finish();
        overridePendingTransition(R.anim.enter_finish_activity,R.anim.exit_finish_activity);
    }
}

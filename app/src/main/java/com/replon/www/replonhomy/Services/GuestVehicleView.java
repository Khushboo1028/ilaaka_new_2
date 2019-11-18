package com.replon.www.replonhomy.Services;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class GuestVehicleView extends AppCompatActivity {

    ImageView back,veh_img;
    TextView veh_num,veh_type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
        defaultTextConfig.adjustFontScale(getResources().getConfiguration(), GuestVehicleView.this);

        setContentView(R.layout.activity_guest_vehicle_view);


        back = findViewById(R.id.back);
        veh_img = findViewById(R.id.vehicle_image);
        veh_num = findViewById(R.id.vehicle_number);
        veh_type = findViewById(R.id.vehicle_type);



        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {
        finish();

    }
}

package com.replon.www.replonhomy.Emergency;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.List;


public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.EmergencyViewHolder>{



    private Context mContext;
    private List<ContentsEmergency> emergencyList;

    public EmergencyAdapter(Context mContext, List<ContentsEmergency> emergencyList) {
        this.mContext = mContext;
        this.emergencyList = emergencyList;
    }

    @NonNull
    @Override
    public EmergencyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_emergency_number_list_layout,null);
        EmergencyViewHolder holder=new EmergencyViewHolder(view);
        return holder;
    }

    public void updateList(List<ContentsEmergency> list){
        emergencyList = list;
        notifyDataSetChanged();
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull EmergencyViewHolder emergencyViewHolder, int i) {


        //so this i is position that will give you the specified item from the product list!


        final ContentsEmergency contentsEmergency =emergencyList.get(i);

        //So now we bind the data using the help of this contentsEmergency object we created
        emergencyViewHolder.emergency.setText(String.valueOf(contentsEmergency.getEmergency()));
        emergencyViewHolder.emergency_number.setText(String.valueOf(contentsEmergency.getEmergency_number()));

        emergencyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = contentsEmergency.getEmergency_number();
                tel = "tel:"+tel;
                //Intent acCall = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));

                Intent acCall = new Intent(Intent.ACTION_DIAL);
                acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                acCall.setData(Uri.parse(tel));
                v.getContext().startActivity(acCall);
            }
        });

        //if image would have been present then
        // commentsViewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsEmergency.getImage()),null);


    }


    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return emergencyList.size();
    }

    class EmergencyViewHolder extends RecyclerView.ViewHolder {


        TextView emergency,emergency_number;
        ImageView emg_call;


        public EmergencyViewHolder(@NonNull View itemView) {
            super(itemView);

            emergency=(TextView)itemView.findViewById(R.id.emergency_name);
            emergency_number=(TextView)itemView.findViewById(R.id.emergency_number);
            emg_call = (ImageView)itemView.findViewById(R.id.emergency_call);



        }
    }




}

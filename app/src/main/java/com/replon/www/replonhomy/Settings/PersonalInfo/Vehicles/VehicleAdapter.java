package com.replon.www.replonhomy.Settings.PersonalInfo.Vehicles;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.replon.www.replonhomy.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>{

    public static final String TAG = "VehicleAdapter";
    private Context mContext;
    private List<ContentsVehicle> veh_list;

    public VehicleAdapter(Context mContext, List<ContentsVehicle> veh_list) {
        this.mContext = mContext;
        this.veh_list = veh_list;

    }

    @NonNull
    @Override
    public VehicleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.vehicles_row_layout,null);
        VehicleViewHolder holder=new VehicleViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull VehicleViewHolder holder,final int i) {

        ContentsVehicle contentsVehicle = veh_list.get(i);

        holder.veh_num.setText(String.valueOf(contentsVehicle.getVehicle_num()));
        holder.veh_type.setText(String.valueOf(contentsVehicle.getVehicle_type()));
        holder.park_slot.setText(String.valueOf(contentsVehicle.getParking_slot()));
        holder.veh_color.setText(String.valueOf(contentsVehicle.getVehicle_color()));

        if(contentsVehicle.getImg_url()!=""){
            Glide.with(mContext).load(contentsVehicle.getImg_url()).placeholder(R.drawable.ic_img_loading).into(holder.veh_img);
        }






    }

    @Override
    public int getItemCount() {
        return veh_list.size();
    }


    class VehicleViewHolder extends RecyclerView.ViewHolder {

        CircleImageView veh_img;
        TextView veh_type,veh_num,veh_color,park_slot;


        public VehicleViewHolder(@NonNull View itemView) {
            super(itemView);

            veh_img = itemView.findViewById(R.id.veh_img);
            veh_type=itemView.findViewById(R.id.veh_type);
            veh_num=itemView.findViewById(R.id.veh_num);
            veh_color=itemView.findViewById(R.id.veh_color);
            park_slot=itemView.findViewById(R.id.park_slot);


        }


    }
}

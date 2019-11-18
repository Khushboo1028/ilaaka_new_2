package com.replon.www.replonhomy.Nearby;

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

public class NearbyAdapter extends RecyclerView.Adapter<NearbyAdapter.NearbyViewHolder> {


    public static final String TAG = "NearbyAdapter";
    private Context mContext;
    List<ContentsNearbyPlaces> nearbyList;
    private View.OnClickListener mClickListener;

    public NearbyAdapter(Context mContext, List<ContentsNearbyPlaces> nearbyList) {
        this.mContext = mContext;
        this.nearbyList = nearbyList;
    }

    @NonNull
    @Override
    public NearbyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.nearby_places_list,null);
        NearbyViewHolder holder=new NearbyViewHolder (view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull NearbyViewHolder holder, int i) {

        final ContentsNearbyPlaces contentsNearbyPlaces = nearbyList.get(i);

        holder.place_name.setText(String.valueOf(contentsNearbyPlaces.getPlace_name()));
        holder.place_address.setText(String.valueOf(contentsNearbyPlaces.getPlace_address()));
        holder.place_rating.setText(String.valueOf(contentsNearbyPlaces.getPlace_rating()));

        holder.place_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = contentsNearbyPlaces.getPlace_phno();
                tel = "tel:"+tel;
                //Intent acCall = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));

                Intent acCall = new Intent(Intent.ACTION_DIAL);
                acCall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                acCall.setData(Uri.parse(tel));
                // acCall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                Intent chooserIntent = Intent.createChooser(acCall, "Open With");
//                chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(acCall);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.google.com/search?q="+contentsNearbyPlaces.getPlace_name());
                Intent gSearchIntent = new Intent(Intent.ACTION_VIEW, uri);
                gSearchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(gSearchIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return nearbyList.size();
    }

    public class NearbyViewHolder extends RecyclerView.ViewHolder {


        TextView place_name, place_address, place_rating;
        ImageView place_phone;



        public NearbyViewHolder(@NonNull View itemView) {
            super(itemView);

            place_name = itemView.findViewById(R.id.place_name);
            place_address = itemView.findViewById(R.id.place_address);
            place_rating = itemView.findViewById(R.id.place_rating);
            place_phone = itemView.findViewById(R.id.place_phone);


        }
    }

}


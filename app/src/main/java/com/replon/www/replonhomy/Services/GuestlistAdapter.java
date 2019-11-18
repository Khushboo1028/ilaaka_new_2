package com.replon.www.replonhomy.Services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.List;

public class GuestlistAdapter extends RecyclerView.Adapter<GuestlistAdapter.GuestViewHolder> {

    private Context mContext;
    private List<ContentsGuestlist> guestList;

    public GuestlistAdapter(Context mContext, List <ContentsGuestlist>guestList) {
        this.mContext=mContext;
        this.guestList=guestList;
    }

    @NonNull
    @Override
    public GuestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(viewGroup.getContext());
        View view=layoutInflater.inflate(R.layout.guestlist_row_layout,null);
        GuestViewHolder holder=new GuestViewHolder(view);
        return holder;
    }

    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull GuestViewHolder holder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        ContentsGuestlist contentsGuestlist =guestList.get(i);
        holder.checkedin_text.setVisibility(View.GONE);
        holder.guest_name.setText(String.valueOf(contentsGuestlist.getName()));
        holder.guest_purpose.setText(String.valueOf(contentsGuestlist.getPurpose()));
        holder.dateAndTime.setText(String.valueOf(contentsGuestlist.getDate()));
        holder.dateAndTime_out.setText(String.valueOf(contentsGuestlist.getOut_date()));

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext,GuestVehicleView.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//
//            }
//        });

        if(contentsGuestlist.getOut_date().equals("---NA---")){
            holder.checkedin_text.setVisibility(View.VISIBLE);
        }else{
            holder.checkedin_image.setVisibility(View.VISIBLE);
        }

        //if Image would have been present then
        // commentsViewHolder.Image.setImageDrawable(mContext. getResources().getDrawable(contentsComments.getImage()),null);


    }


    public void updateList(List<ContentsGuestlist> list){
        guestList = list;
        notifyDataSetChanged();
    }


    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return guestList.size();
    }







    class GuestViewHolder extends RecyclerView.ViewHolder {


        TextView guest_name,guest_purpose,dateAndTime, dateAndTime_out,checkedin_text;
        RelativeLayout row;
        ImageView checkedin_image;


        public GuestViewHolder(@NonNull View itemView) {
            super(itemView);

            guest_name = itemView.findViewById(R.id.guest_name);
            guest_purpose = itemView.findViewById(R.id.guest_purpose);
            dateAndTime = itemView.findViewById(R.id.dateAndTime);
            dateAndTime_out = itemView.findViewById(R.id.dateAndTime_out);
            row = (RelativeLayout)itemView.findViewById(R.id.row);
            checkedin_text=(TextView)itemView.findViewById(R.id.checkedin_text);
            checkedin_image=(ImageView)itemView.findViewById(R.id.checkedin_image);


        }
    }

}

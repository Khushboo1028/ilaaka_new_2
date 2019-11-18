package com.replon.www.replonhomy.Services;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.replon.www.replonhomy.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ServiceItemAdapter extends RecyclerView.Adapter<ServiceItemAdapter.ServiceItemViewHolder>{


    public static final String TAG = "ServiceItemAdapter";
    private Context mContext;
    private List<ContentsServiceItem> serviceItemsList;

    public ServiceItemAdapter(Context mContext, List<ContentsServiceItem> serviceItemsList) {
        this.mContext = mContext;
        this.serviceItemsList = serviceItemsList;
    }

    @NonNull
    @Override
    public ServiceItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_service_item_layout,null);
        ServiceItemViewHolder holder=new ServiceItemViewHolder(view);
        return holder;
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull ServiceItemViewHolder serviceItemViewHolder, int i) {


//        //so this i is position that will give you the specified item from the product list!
//
//
//        final ContentsServiceItem contentsServiceItem =serviceItemsList.get(i);
//
//        //So now we bind the data using the help of this contentsEmergency object we created
//        serviceItemViewHolder.name.setText(String.valueOf(contentsServiceItem.getName()));
//        serviceItemViewHolder.contact_number.setText(String.valueOf(contentsServiceItem.getContact_number()));
//
//        if(!contentsServiceItem.getAadhar_number().equals("")) {
//            serviceItemViewHolder.aadhar_number.setText(String.valueOf(contentsServiceItem.getAadhar_number()));
//        }
//        else
//        {
//            serviceItemViewHolder.aadhar_text.setVisibility(View.GONE);
//            serviceItemViewHolder.aadhar_number.setVisibility(View.GONE);
//        }
//
//        if(!contentsServiceItem.getImageURL().equals("")){
//            Glide.with(mContext).load(contentsServiceItem.getImageURL()).into(serviceItemViewHolder.service_img);
//        }else {
//            serviceItemViewHolder.service_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_service_default));
//        }
//
//        serviceItemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                Intent intent = new Intent(mContext,ViewServiceItem.class);
//
//                intent.putExtra("name",contentsServiceItem.getName());
//                intent.putExtra("contact_no",contentsServiceItem.getContact_number());
//                intent.putExtra("aadhar_no",contentsServiceItem.getAadhar_number());
//                intent.putExtra("service_title",contentsServiceItem.getService_title());
//                intent.putExtra("imageURL",contentsServiceItem.getImageURL());
//                intent.putExtra("doc_ref",contentsServiceItem.getDocument_id().toString());
//                intent.putExtra("doc_id",contentsServiceItem.getDocument_id().getId());
//
//
//
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                mContext.startActivity(intent);
//
//            }
//        });
//
//
//        //serviceItemViewHolder.service_img.setImageDrawable(mContext.getResources().getDrawable(contentsServiceItem.get));
//
//
//        //if image would have been present then
//        // commentsViewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsEmergency.getImage()),null);

    }


    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        Log.i(TAG,"Size is "+serviceItemsList.size());
        return serviceItemsList.size();

    }

    public void removeItem(int position) {
        serviceItemsList.remove(position);
        notifyItemRemoved(position);
    }

    class ServiceItemViewHolder extends RecyclerView.ViewHolder {


        TextView name,contact_number,aadhar_number,aadhar_text,service_title;

        CircleImageView service_img;


        public ServiceItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name=(TextView)itemView.findViewById(R.id.service_provider_name);
            contact_number=(TextView)itemView.findViewById(R.id.contact_number);
            aadhar_number = (TextView) itemView.findViewById(R.id.aadhar_number);
            aadhar_text = (TextView)itemView.findViewById(R.id.aadhar_text);
            service_img = (CircleImageView) itemView .findViewById(R.id.service_provider_img);



        }
    }




}

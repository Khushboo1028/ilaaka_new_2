package com.replon.www.replonhomy.Accounting.MMT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.ArrayList;

public class MMTAdapter extends RecyclerView.Adapter<MMTAdapter.ViewHolder>{


    public static final String TAG = "MMTAdapter";
    private Context mContext;
    private ArrayList<ContentsMMT> mmtList;
    private View.OnClickListener mClickListener;
    private String soc_name,flat_no,username,admin,unique_id,name,status;


    public MMTAdapter(Context mContext, ArrayList<ContentsMMT> mmtList,String soc_name,String flat_no,String username,String name,String admin,String unique_id) {
        this.mContext = mContext;
        this.mmtList = mmtList;
        this.soc_name=soc_name;
        this.username=username;
        this.admin=admin;
        this.flat_no=flat_no;
        this.unique_id=unique_id;
        this.name=name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_mmt_layout,null);
        ViewHolder holder=new ViewHolder(view);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mClickListener.onClick(view);
//            }
//        });


        return holder;
    }

    public void updateList(ArrayList<ContentsMMT> list){
        mmtList = list;
        notifyDataSetChanged();
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        final ContentsMMT contentsMMT = mmtList.get(i);

        //So now we bind the data using the help of this contentsComplaints object we created
        viewHolder.tv_amount.setText("Rs. "+ contentsMMT.getAmount());
        viewHolder.tv_paid_via.setText(contentsMMT.getPaid_by());
        viewHolder.tv_paid_on.setText(contentsMMT.getPaid_on_date());
        viewHolder.tv_due_date.setText(contentsMMT.getDue_date());
        viewHolder.tv_month.setText(contentsMMT.getMaintenance_month());
        viewHolder.tv_reference.setText(contentsMMT.getReference());

        //if image would have been present then
       // viewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsComplaints.getImage()),null);

        Log.i(TAG,"status is "+contentsMMT.getPaid_status());
        if(contentsMMT.getPaid_status().equals("approved")){
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_approved);
            viewHolder.tv_status.setText("Approved");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.greenSolved));

            status="approved";

        }else if(contentsMMT.getPaid_status().equals("pending")) {
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_pending_approval);
            viewHolder.tv_status.setText("Pending\nApproval");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.pendingYellow));

            status="pending";


        }else if(contentsMMT.getPaid_status().equals("rejected")){
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_rejected);
            viewHolder.tv_status.setText("Rejected");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.incorrect_red));

            status="rejected";
        }
        else if(contentsMMT.getPaid_status().equals("pay_now")){

            viewHolder.pay_now.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setVisibility(View.GONE);
            viewHolder.tv_status.setVisibility(View.GONE);

            status="pay_now";

        }

        viewHolder.pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"findList is "+mmtList.get(0).getPdf_url());
                Intent intent = new Intent(mContext, PayNowActivity.class);
                intent.putExtra("society_name",soc_name);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("username",username);
                intent.putExtra("name",name);
                intent.putExtra("admin",admin);
                intent.putExtra("unique_id",unique_id);
                intent.putExtra("status","pay_now");
                intent.putExtra("position",i);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("findList", mmtList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"findList is "+mmtList.get(0).getPdf_url());
                Intent intent = new Intent(mContext,PayNowActivity.class);
                intent.putExtra("society_name",soc_name);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("username",username);
                intent.putExtra("name",name);
                intent.putExtra("admin",admin);
                intent.putExtra("unique_id",unique_id);
                intent.putExtra("status",contentsMMT.getPaid_status());
                intent.putExtra("position",i);
                intent.putExtra("image_url_array",contentsMMT.getImage_url());
                intent.putExtra("pdf_url_array",contentsMMT.getPdf_url());

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("findList", mmtList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }





    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return mmtList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


       TextView tv_paid_on,tv_paid_via,tv_amount, tv_reference, tv_month, tv_due_date, tv_status;
       ImageView image_paid_check;
       Button pay_now;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_paid_on=(TextView)itemView.findViewById(R.id.paid_on_date);
            tv_paid_via=(TextView)itemView.findViewById(R.id.paid_via);
            tv_amount=(TextView)itemView.findViewById(R.id.amount);
            tv_reference=(TextView) itemView.findViewById(R.id.reference_no);
            tv_month = (TextView) itemView.findViewById(R.id.month_text);
            tv_due_date = (TextView) itemView.findViewById(R.id.due_date);
            image_paid_check=(ImageView)itemView.findViewById(R.id.check_image);
            tv_status = (TextView) itemView.findViewById(R.id.status_text);
            pay_now = (Button) itemView.findViewById(R.id.pay_now_btn);

        }
    }




}

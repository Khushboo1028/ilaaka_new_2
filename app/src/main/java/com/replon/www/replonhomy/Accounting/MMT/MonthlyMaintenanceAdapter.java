package com.replon.www.replonhomy.Accounting.MMT;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.List;

public class MonthlyMaintenanceAdapter extends RecyclerView.Adapter<MonthlyMaintenanceAdapter.ViewHolder> {


    public static final String TAG = "MonthlyMaintenanceAdapter";
    private Context mContext;
    private List<ContentsMonthlyMaintenance> maintenanceList;
    private  String flat_no,admin;


    public MonthlyMaintenanceAdapter(Context mContext, List<ContentsMonthlyMaintenance> mmtList,String flat_no,String admin) {
        this.mContext = mContext;
        this.maintenanceList = mmtList;
        this.flat_no=flat_no;
        this.admin=admin;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_monthly_maintenance,null);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    public void updateList(List<ContentsMonthlyMaintenance> list){
        maintenanceList = list;
        notifyDataSetChanged();
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        final ContentsMonthlyMaintenance contentsMonthlyMaintenance = maintenanceList.get(i);

        //So now we bind the data using the help of this contentsComplaints object we created
        viewHolder.tv_approved.setText(String.valueOf(contentsMonthlyMaintenance.getApproved_transactions()));
        viewHolder.tv_rejected.setText(String.valueOf(contentsMonthlyMaintenance.getRejected_transactions()));
        viewHolder.tv_month.setText(String.valueOf(contentsMonthlyMaintenance.getMonth()));

        if (contentsMonthlyMaintenance.getAll_transactions_bool()){
            viewHolder.image_trans_check.setVisibility(View.VISIBLE);
        }else {
            viewHolder.image_trans_check.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext, ViewMonthlyTransaction.class);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("admin",admin);
                intent.putExtra("title",contentsMonthlyMaintenance.getMonth());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);


            }
        });

    }





    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return maintenanceList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView tv_approved, tv_month, tv_rejected;
        ImageView image_trans_check;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_approved = (TextView) itemView.findViewById(R.id.approved_transactions);
            tv_month = (TextView) itemView.findViewById(R.id.month_text);
            tv_rejected = (TextView) itemView.findViewById(R.id.rejected_transactions);
            image_trans_check = (ImageView) itemView.findViewById(R.id.image_all_trans_check);

        }
    }
}

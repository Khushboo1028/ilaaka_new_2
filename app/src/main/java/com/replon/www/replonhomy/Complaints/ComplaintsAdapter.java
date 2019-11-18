package com.replon.www.replonhomy.Complaints;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.List;

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplaintsViewHolder>{


    public static final String TAG = "ComplaintsAdapter";
    private Context mContext;
    private List<ContentsComplaints> complaintsList;
    private View.OnClickListener mClickListener;


    public ComplaintsAdapter(Context mContext, List<ContentsComplaints> complaintsList) {
        this.mContext = mContext;
        this.complaintsList = complaintsList;
    }

    @NonNull
    @Override
    public ComplaintsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.complaints_row_layout,null);
        ComplaintsViewHolder holder=new ComplaintsViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onClick(view);
            }
        });


        return holder;
    }

    public void updateList(List<ContentsComplaints> list){
        complaintsList = list;
        notifyDataSetChanged();
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull ComplaintsViewHolder complaintsViewHolder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        final ContentsComplaints contentsComplaints =complaintsList.get(i);

        //So now we bind the data using the help of this contentsComplaints object we created
        complaintsViewHolder.solved_unsolved.setText(String.valueOf(contentsComplaints.getSolved_unsolved()));
        complaintsViewHolder.date.setText(String.valueOf(contentsComplaints.getDate()));
        complaintsViewHolder.month.setText(String.valueOf(contentsComplaints.getMonth()));
        complaintsViewHolder.year.setText(String.valueOf(contentsComplaints.getYear()));
        complaintsViewHolder.description.setText(String.valueOf(contentsComplaints.getSubject()));
        complaintsViewHolder.flat_no_users.setText(String.valueOf(contentsComplaints.getFlat_no()));
        complaintsViewHolder.time_created.setText(String.valueOf(contentsComplaints.getTime()));


        //if image would have been present then
       // complaintsViewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsComplaints.getImage()),null);


        if(contentsComplaints.getAdmin().equals("false")){
            complaintsViewHolder.flat_no_users.setVisibility(View.GONE);
            complaintsViewHolder.view_solved.setVisibility(View.GONE);

        }

        if(contentsComplaints.getSolved_unsolved()==""){
            complaintsViewHolder.solved_unsolved.setBackgroundResource(0);
            complaintsViewHolder.description.setTextSize(30);
            complaintsViewHolder.description.setTypeface(Typeface.DEFAULT_BOLD);
            complaintsViewHolder.description.setTextColor(Color.BLACK);


        }

        else if(contentsComplaints.getSolved_unsolved()=="Solved"){

            complaintsViewHolder.solved_unsolved.setBackgroundResource(R.drawable.solved_border);
            complaintsViewHolder.solved_unsolved.setTextColor(mContext.getResources().getColor(R.color.greenSolved));

        }
        else {
            complaintsViewHolder.solved_unsolved.setBackgroundResource(R.drawable.unsolved_border);
            complaintsViewHolder.solved_unsolved.setTextColor(Color.parseColor("#DD2012"));
            complaintsViewHolder.solved_unsolved.setText("Unsolved");
        }

        complaintsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent =new Intent(mContext, ViewComplaintsActivity.class);
                intent.putExtra("solved",contentsComplaints.getSolved_unsolved());
                intent.putExtra("subject",contentsComplaints.getSubject());
                intent.putExtra("description",contentsComplaints.getDescription());
                intent.putExtra("date",contentsComplaints.getDate_in_ViewFormat());
                intent.putExtra("image_url",contentsComplaints.getFb_imageURl());
                intent.putExtra("time",contentsComplaints.getTime());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);

            }
        });



    }





    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return complaintsList.size();
    }


    public void removeItem(int position) {
        complaintsList.remove(position);
        notifyItemRemoved(position);
    }


    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }

    public class ComplaintsViewHolder extends RecyclerView.ViewHolder {


        TextView solved_unsolved,date,month,year,description,subject,flat_no_users,time_created;
        RelativeLayout row;
        View view_solved;



        public ComplaintsViewHolder(@NonNull View itemView) {
            super(itemView);

            solved_unsolved=(TextView)itemView.findViewById(R.id.solved_unsolved);
            date=(TextView)itemView.findViewById(R.id.date);
            month=(TextView)itemView.findViewById(R.id.month);
            year=(TextView)itemView.findViewById(R.id.year);
            subject=(TextView) itemView.findViewById(R.id.subject);
            description=(TextView) itemView.findViewById(R.id.description);
            row=(RelativeLayout) itemView.findViewById(R.id.row);
            flat_no_users=(TextView)itemView.findViewById(R.id.flat_no_users);
            view_solved=(View)itemView.findViewById(R.id.view_solved);
            time_created = itemView.findViewById(R.id.time_created);

        }
    }




}

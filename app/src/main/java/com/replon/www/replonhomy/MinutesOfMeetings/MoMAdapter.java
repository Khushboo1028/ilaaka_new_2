package com.replon.www.replonhomy.MinutesOfMeetings;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.replon.www.replonhomy.Complaints.ViewComplaintsActivity;
import com.replon.www.replonhomy.R;

import java.util.Arrays;
import java.util.List;

public class MoMAdapter extends RecyclerView.Adapter<MoMAdapter.MoMViewHolder>{



    private Context mContext;
    private List<ContentsMoM> momList;

    public MoMAdapter(Context mContext, List<ContentsMoM> momList) {
        this.mContext = mContext;
        this.momList = momList;
    }

    @NonNull
    @Override
    public MoMViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_mom_list_layout,null);
        MoMViewHolder holder=new MoMViewHolder(view);
        return holder;
    }

    public void updateList(List<ContentsMoM> list){
        momList = list;
        notifyDataSetChanged();
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull MoMViewHolder momViewHolder, int i) {


        //so this i is position that will give you the specified item from the product list!


        final ContentsMoM contentsMoM =momList.get(i);

        //So now we bind the data using the help of this contentsComplaints object we created
        momViewHolder.meeting_name.setText(String.valueOf(contentsMoM.getMeeting_name()));
        momViewHolder.date.setText(String.valueOf(contentsMoM.getDate()));
        momViewHolder.month.setText(String.valueOf(contentsMoM.getMonth()));
        momViewHolder.year.setText(String.valueOf(contentsMoM.getYear()));
        momViewHolder.time_created.setText(String.valueOf(contentsMoM.getTime()));
        String subject[] = new String[30];
        Arrays.fill(subject,null);
        String temp = contentsMoM.getMeeting_subjects().toString();
        temp = temp.substring(1,temp.length()-1);

        subject = temp.split(",");
        String print_subject = "";
        for (String item : subject) {
            if (item==null){
                break;
            }
            item = item.trim();
            print_subject+=item;
            print_subject+="\n";
        }
        momViewHolder.meeting_subject.setText(print_subject);
       // momViewHolder.meeting_subject.setText(String.valueOf(contentsMoM.getMeeting_subjects()));

        //if image would have been present then
        // commentsViewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsComplaints.getImage()),null);

        momViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(mContext, ViewMOMActivity.class);
                intent.putExtra("date",contentsMoM.getDate_inViewFormat());
                intent.putExtra("meeting_name",contentsMoM.getMeeting_name());
                intent.putExtra("subjects",contentsMoM.getMeeting_subjects());
                intent.putExtra("descriptions",contentsMoM.getMeeting_description());
                intent.putExtra("image_url",contentsMoM.getImage_url());
                intent.putExtra("pdf_url",contentsMoM.getPdf_url());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });



    }


    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return momList.size();
    }



    class MoMViewHolder extends RecyclerView.ViewHolder {


        TextView meeting_subject,date,month,year,meeting_name,time_created;


        public MoMViewHolder(@NonNull View itemView) {
            super(itemView);

            meeting_name=(TextView)itemView.findViewById(R.id.meeting_name);
            date=(TextView)itemView.findViewById(R.id.date);
            month=(TextView)itemView.findViewById(R.id.month);
            year=(TextView)itemView.findViewById(R.id.year);
            meeting_subject=(TextView)itemView.findViewById(R.id.meeting_subject);
            time_created=itemView.findViewById(R.id.time_created);



        }
    }




}


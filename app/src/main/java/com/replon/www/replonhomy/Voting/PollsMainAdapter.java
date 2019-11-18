package com.replon.www.replonhomy.Voting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.List;

public class PollsMainAdapter extends RecyclerView.Adapter<PollsMainAdapter.PollsViewHolder> {


    private Context mContext;
    private List<ContentsPolls> pollsList;

    public PollsMainAdapter(Context mContext, List<ContentsPolls> pollsList) {
        this.mContext = mContext;
        this.pollsList = pollsList;
    }


    @NonNull
    @Override
    public PollsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_voting_main,null);
        PollsViewHolder holder=new PollsViewHolder(view);
        return holder;
    }


    public void updateList(List<ContentsPolls> list){
        pollsList = list;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull PollsViewHolder holder, int i) {

        final ContentsPolls contentsPolls = pollsList.get(i);
        holder.poll_name.setText(String.valueOf(contentsPolls.getPoll_name()));
        holder.date.setText(String.valueOf(contentsPolls.getDate()));
        holder.month.setText(String.valueOf(contentsPolls.getMonth()));
        holder.year.setText(String.valueOf(contentsPolls.getYear()));
        holder.poll_end_time.setText(String.valueOf(contentsPolls.getTime_poll_ends()));
        holder.poll_time_created.setText(String.valueOf(contentsPolls.getTime_created()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext,ViewVotingActivity.class);
                intent.putExtra("poll_name",contentsPolls.getPoll_name());
                intent.putExtra("poll_end_time",contentsPolls.getTime_poll_ends());
                intent.putExtra("poll_time_created",contentsPolls.getTime_created());
                intent.putExtra("choiceList",contentsPolls.getChoiceList());
                intent.putExtra("timePassed",contentsPolls.getTimePassed());
                intent.putExtra("doc_id",contentsPolls.getDoc_ref().getId());
                intent.putExtra("flat_no",contentsPolls.getFlat_no());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        if(contentsPolls.getTimePassed()){
            holder.poll_end_time.setTextColor(Color.RED);
            holder.poll_ends_text.setTextColor(Color.RED);
            holder.poll_ends_text.setText("Poll ended on:");
        }
    }

    @Override
    public int getItemCount() {
        return pollsList.size();
    }

    class PollsViewHolder extends RecyclerView.ViewHolder {


        TextView poll_name,date,month,year,poll_end_time,poll_time_created,poll_ends_text;


        public PollsViewHolder(@NonNull View itemView) {
            super(itemView);

            poll_name=(TextView)itemView.findViewById(R.id.poll_question);
            date=(TextView)itemView.findViewById(R.id.date);
            month=(TextView)itemView.findViewById(R.id.month);
            year=(TextView)itemView.findViewById(R.id.year);
            poll_end_time=(TextView)itemView.findViewById(R.id.poll_end_time);
            poll_time_created=itemView.findViewById(R.id.time_created);
            poll_ends_text=(TextView)itemView.findViewById(R.id.poll_ends_text);



        }
    }
}

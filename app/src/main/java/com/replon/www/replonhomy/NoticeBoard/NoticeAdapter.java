package com.replon.www.replonhomy.NoticeBoard;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.replon.www.replonhomy.Complaints.ContentsComplaints;
import com.replon.www.replonhomy.Complaints.ViewComplaintsActivity;
import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

import java.util.List;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>{


    public static final String TAG = "NoticeAdapter";
    private Context mContext;
    private List<ContentsNotice> noticeList;
    private View.OnClickListener mClickListener;


    public NoticeAdapter(Context mContext, List<ContentsNotice> noticeList) {
        this.mContext = mContext;
        this.noticeList = noticeList;
    }

    public void updateList(List<ContentsNotice> list){
        noticeList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_notice_board,null);

//        DefaultTextConfig defaultTextConfig = new DefaultTextConfig();
//        defaultTextConfig.adjustFontScale(view.getResources().getConfiguration(), view);

//        adjustFontScale(view.getResources().getConfiguration());

       NoticeViewHolder holder=new NoticeViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onClick(view);
            }
        });

        return holder;
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull NoticeViewHolder noticeViewHolder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        final ContentsNotice notice =noticeList.get(i);

        //So now we bind the data using the help of this contentsComplaints object we created

        noticeViewHolder.day.setText(String.valueOf(notice.getDay()));
        noticeViewHolder.month.setText(String.valueOf(notice.getMonth()));
        noticeViewHolder.year.setText(String.valueOf(notice.getYear()));
        noticeViewHolder.subject.setText(String.valueOf(notice.getSubject()));
        noticeViewHolder.time.setText(String.valueOf(notice.getTime()));





        noticeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,ViewNoticeActivity.class);

                if (notice.getFb_imageURl().equals(null)){
                    intent.putExtra("image_url","0");

                }else {
                    intent.putExtra("image_url",notice.getFb_imageURl());

                }


                intent.putExtra("subject",notice.getSubject());
                intent.putExtra("description",notice.getDescription());
                intent.putExtra("date",notice.getDate_in_view_format());
                intent.putExtra("time",notice.getTime());


//                intent.putExtra("image_url",notice.getFb_imageURl());
                intent.putExtra("pdf_url",notice.getFb_pdfURL());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);


            }
        });

    }

    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return noticeList.size();
    }


    public void removeItem(int position) {
        noticeList.remove(position);
        notifyItemRemoved(position);
    }


    public void setClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {


        TextView subject,day,month,year,time;



        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);

            day=(TextView)itemView.findViewById(R.id.date);
            month=(TextView)itemView.findViewById(R.id.month);
            year=(TextView)itemView.findViewById(R.id.year);
            time=(TextView)itemView.findViewById(R.id.time);
            subject=(TextView) itemView.findViewById(R.id.subject);


        }
    }


    public void adjustFontScale(Configuration configuration)
    {
        configuration.fontScale = (float) 1; //0.85 small size, 1 normal size, 1,15 big etc
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        configuration.densityDpi = (int) mContext.getResources().getDisplayMetrics().xdpi;
        ((Activity)mContext).getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }




}

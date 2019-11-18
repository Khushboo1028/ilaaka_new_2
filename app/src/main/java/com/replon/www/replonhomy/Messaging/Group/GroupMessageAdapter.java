package com.replon.www.replonhomy.Messaging.Group;

import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.replon.www.replonhomy.Utility.ViewImageActivity;
import com.replon.www.replonhomy.Messaging.Model.GroupChat;
import com.replon.www.replonhomy.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;
    public static final String TAG = "MessageAdapter";


    private Context mContext;
    private List<GroupChat> mChat;
    private String imageUrl;

    FirebaseUser firebaseUser;
    ViewHolder prevholder;


    public GroupMessageAdapter(Context mContext, List <GroupChat>mChat, String imageUrl){
        this.mChat=mChat;
        this.mContext=mContext;
        this.imageUrl=imageUrl;
    }

    @NonNull
    @Override
    public GroupMessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        onAttachedToRecyclerView((RecyclerView)parent);

        if(viewType==MSG_TYPE_RIGHT){
            View view=LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return new GroupMessageAdapter.ViewHolder(view);

        }else{
            View view=LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return new GroupMessageAdapter.ViewHolder(view);

        }

    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        final GroupChat chat=mChat.get(i);
        holder.show_message.setText(chat.getMessage());

//        if(imageUrl!= null && imageUrl.equals("default")){
//            holder.profile_image.setImageResource(R.drawable.ic_service_default);
//
//        }else{
//            Glide.with(mContext).load(imageUrl).into(holder.profile_image);
//
//        }

        Log.i(TAG,"Timestamp is "+chat.getTime_sent());

        Date netDate = (new Date(chat.getTime_sent()));
        SimpleDateFormat sfd_date = new SimpleDateFormat("dd/MM/yyyy");
        String date = sfd_date.format(netDate);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df.format(c);
        if (date.equals(formattedDate)){
            holder.date.setText("Today");
        }else{
            holder.date.setText(date);
        }
        if(i!=0) {
            GroupChat lastchat = mChat.get(i - 1);
            Date lastnetDate = (new Date(lastchat.getTime_sent()));
            SimpleDateFormat lastsfd_date = new SimpleDateFormat("dd/MM/yyyy");
            String lastdate = lastsfd_date.format(lastnetDate);
            if (lastdate.equals(date)) {

                holder.date.setVisibility(View.GONE);
            }

        }



        SimpleDateFormat sfd_time = new SimpleDateFormat("HH:mm");
        String time = sfd_time.format(netDate);

        Date ct = Calendar.getInstance().getTime();
        SimpleDateFormat dft = new SimpleDateFormat("HH:mm");
        String formattedTime = dft.format(ct);

//        if (time.equals(formattedTime)){
//            holder.txt_time.setText("now");
//        }else{
            holder.txt_time.setText(time);
//        }


        holder.txt_seen.setVisibility(View.GONE);
        holder.flat_no.setText(String.valueOf(chat.getSender_flat()));


        holder.text_image.setVisibility(View.GONE);
//        holder.image_card.setVisibility(View.GONE);


        if(chat.getImage_url()!=null){

            if(chat.getImage_url().equals("")){
                holder.text_image.setVisibility(View.GONE);
                holder.image_card.setVisibility(View.GONE);
            }else{
                holder.text_image.setVisibility(View.VISIBLE);
                holder.image_card.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(chat.getImage_url()).into(holder.text_image);

                holder.text_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext, ViewImageActivity.class);
                        intent.putExtra("clickedImageURl",chat.getImage_url());
                        intent.putExtra("fileType","image");
                        mContext.startActivity(intent);

                    }
                });

            }
        }

        if(chat.getPdf_url()!=null){
            if(!chat.getPdf_url().equals("")){
                holder.text_image.setVisibility(View.VISIBLE);
                holder.image_card.setVisibility(View.VISIBLE);
                holder.text_image.setImageResource(R.drawable.pdf_view);

                holder.text_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri path=Uri.parse(chat.getPdf_url());
                        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                        pdfIntent.setDataAndType(path , "application/pdf");
                        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        try
                        {
                            mContext.startActivity(pdfIntent);
                        }
                        catch (ActivityNotFoundException e)
                        {
                            Toast.makeText(mContext,"No Application available to view PDF",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }

        holder.show_message.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(holder.show_message.getText().toString());
                Toast.makeText(mContext, "Message copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });


    }



    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView show_message;
        public ImageView profile_image;
        public TextView txt_seen;
        public TextView txt_time,date;
        public ImageView text_image;
        public CardView image_card;
        public TextView flat_no;

        public ViewHolder(View itemView){
            super(itemView);

            show_message=itemView.findViewById(R.id.show_message);
            profile_image=itemView.findViewById(R.id.profile_image);
            txt_seen=itemView.findViewById(R.id.txt_seen);
            txt_time=itemView.findViewById(R.id.timestamp);
            date=itemView.findViewById(R.id.date);
            text_image=itemView.findViewById(R.id.text_image);
            image_card=itemView.findViewById(R.id.image_card);
            flat_no=itemView.findViewById(R.id.flat_no);


        }
    }


    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else{
            return MSG_TYPE_LEFT;
        }
    }
}

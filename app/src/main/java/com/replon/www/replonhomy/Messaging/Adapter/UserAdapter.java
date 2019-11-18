package com.replon.www.replonhomy.Messaging.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.replon.www.replonhomy.Messaging.MessagesActivity;
import com.replon.www.replonhomy.Messaging.Model.Chat;
import com.replon.www.replonhomy.Messaging.Model.User;
import com.replon.www.replonhomy.R;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private static final String TAG="UserAdapter";

    private Context mContext;
    private List<User> mUsers;
    private boolean ischat;
    private String soc_name,name,flat_no,admin,unique_id;

    String theLastMessage,time,date,lastDate;
    Long last_message_time;
    Date netDateFormat,lastDateFormat;

    private FirebaseAuth mAuth;
    FirebaseUser firebaseUser;


    public UserAdapter(Context mContext,List<User> mUsers,boolean ischat,String soc_name,String name,String flat_no,String admin,
                       String unique_id){
        this.mUsers=mUsers;
        this.mContext=mContext;
        this.ischat=ischat;
        this.soc_name=soc_name;
        this.name=name;
        this.admin=admin;
        this.flat_no=flat_no;
        this.unique_id=unique_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(mContext).inflate(R.layout.row_messages_user_item,parent,false);

        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int i) {

        final User user=mUsers.get(i);
        holder.username.setText(user.getFlatno() +"  "+ user.getName());
        if(user.getImageURL()!= null && user.getImageURL().equals("")){
            holder.profile_image.setImageResource(R.drawable.ic_service_default);

        }else{
            Glide.with(mContext).load(user.getImageURL()).placeholder(R.drawable.ic_service_default).into(holder.profile_image);

        }

        if(ischat){
            last_message(user.getId(),holder.last_msg,holder.tv_last_message_time,holder.tv_last_message_date);
        }else {
            holder.last_msg.setVisibility(View.GONE);
            holder.tv_last_message_date.setVisibility(View.GONE);
            holder.tv_last_message_time.setVisibility(View.GONE);
        }

        //unseenMessage(holder.text_unread);
//
//        if(ischat){
//            if(user.getStatus().equals("Online")){
//                Log.i(TAG,"Online");
//                holder.img_on.setVisibility(View.GONE);
//                holder.img_off.setVisibility(View.GONE);
//            }else{
//                holder.img_off.setVisibility(View.GONE);
//                holder.img_on.setVisibility(View.GONE);
//            }
//        }else{
//            holder.img_off.setVisibility(View.GONE);
//            holder.img_on.setVisibility(View.GONE);
//        }

        if(user.getSeen()!=null){
            if(!user.getSeen()){
                holder.username.setTypeface(null,Typeface.BOLD);
                holder.read_indicator.setVisibility(View.VISIBLE);
                holder.last_msg.setTypeface(null, Typeface.BOLD);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MessagesActivity.class);
                Log.i(TAG,"The selected user id is "+user.getId());
                intent.putExtra("UserID",user.getId());
                intent.putExtra("society_name",soc_name);
                intent.putExtra("name",name);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("admin",admin);
                intent.putExtra("unique_id",unique_id);
                mContext.startActivity(intent);



            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public void updateList(List<User> list){
        mUsers = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_image;
        private ImageView img_on;
        private ImageView img_off;
        private TextView last_msg,tv_last_message_time,tv_last_message_date,text_unread;
        private View read_indicator;



        public ViewHolder(View itemView){
            super(itemView);
            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            last_msg=itemView.findViewById(R.id.last_message);
            tv_last_message_time=itemView.findViewById(R.id.last_message_time);
            tv_last_message_date=itemView.findViewById(R.id.last_message_date);
            text_unread=itemView.findViewById(R.id.text_unread);
            read_indicator=(View)itemView.findViewById(R.id.read_indicator);

        }
    }

    //check for last message
    private void last_message(final String userid, final TextView last_msg,final TextView message_time,final TextView message_date){
        theLastMessage="default";
        time="";
        date="";

        String chats="Chats_"+unique_id;
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference(chats);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid)
                            || chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())){

//                       if(!chat.getImage_url().equals("")){
//                           theLastMessage="Photo";
//                       }else if(!chat.getPdf_url().equals("")){
//                           theLastMessage="PDF";
//                       }

                        theLastMessage = chat.getMessage();

                        if (theLastMessage.equals("")){

                            if(chat.getImage_url()!=null){
                                if( !chat.getImage_url().equals("")){
                                    theLastMessage="Photo";
                                }
                            }

                            else if(chat.getPdf_url()!=null){
                                if (!chat.getPdf_url().equals("")){
                                    theLastMessage="PDF";
                                }
                            }

                        }


                        last_message_time=chat.getTime_sent();

                        Date netDate = (new Date(last_message_time));
                        SimpleDateFormat sfd_time = new SimpleDateFormat("HH:mm");
                        time = sfd_time.format(netDate);

                        SimpleDateFormat sfd_date = new SimpleDateFormat("dd/MM/yyyy");
                        date = sfd_date.format(netDate);
                        try {
                            netDateFormat = sfd_date.parse(date);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, -1);
                            lastDate = sfd_date.format(calendar.getTime());
                            lastDateFormat = sfd_date.parse(lastDate);
                            Log.i(TAG,"Now date is "+lastDate);
                            if (netDateFormat.compareTo(lastDateFormat)==0){
                                time="Yesterday";
                                message_time.setText("Yesterday");
                                message_date.setVisibility(View.GONE);
                            }
                            else if (netDateFormat.compareTo(lastDateFormat)==1){
                                message_date.setVisibility(View.GONE);

                            }
                            else {
                                time = sfd_date.format(netDate);
                                message_time.setText(date);
                                message_date.setVisibility(View.GONE);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        Log.i(TAG,"date is "+date);




                    }
                }

                switch (theLastMessage){
                    case "default":
                        last_msg.setText("");
                        message_time.setText("");
                        message_date.setText("");
                        break;
                    default:

                        last_msg.setText(theLastMessage);
                        message_time.setText(time);
//
//
//                        SimpleDateFormat sfd_date = new SimpleDateFormat("dd/MM/yyyy");
//
//                        Log.i(TAG,String.valueOf("01/01/2019".compareTo("31/12/2018")));
//                        message_date.setText(date);

                }

                theLastMessage="default";
                time="";
                date="";

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void unseenMessage(final TextView tv_unread){
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();

        String chats="Chats_"+unique_id;
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference(chats);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int unread = 0;
                Log.i(TAG,"unread is "+unread);
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Chat chat=snapshot.getValue(Chat.class);
                    Log.i(TAG,"Isseen is "+chat.isSeen());
                    if(chat.getReceiver().equals(firebaseUser.getUid()) && !chat.isSeen()){
                        unread++;
                        Log.i(TAG,"unread messages are "+unread);
                    }
                }

                if(unread!=0){
//                  tv_unread.setText(value);
                    Log.i(TAG,"unread messages are "+unread);
                }else{
                    tv_unread.setVisibility(View.GONE);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

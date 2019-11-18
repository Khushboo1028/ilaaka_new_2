package com.replon.www.replonhomy.Messaging.Fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.replon.www.replonhomy.Messaging.Adapter.UserAdapter;
import com.replon.www.replonhomy.Messaging.Group.GroupMessagesActivity;
import com.replon.www.replonhomy.Messaging.Model.Chat;
import com.replon.www.replonhomy.Messaging.Model.Chatlist;
import com.replon.www.replonhomy.Messaging.Model.User;
import com.replon.www.replonhomy.Messaging.SortingClass;
import com.replon.www.replonhomy.R;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


public class ChatsFragment extends Fragment {
    private final String TAG="ChatsFragment";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser firebaseUser;
    DatabaseReference reference,reference_received;
    RelativeLayout group_message_rel;

    private List<Chatlist> usersList;
    String soc_name,name,flat_no,admin,received_id,unique_id;
    Boolean FOUND=FALSE;
    ProgressBar progressBar;
    List<User> sortedList;
    EditText searchView;
    TextView tv_groupName,message_date;
    String groupName;
    TextView last_message,tv_last_message_time;

    String theLastMessage,time,date,lastDate;
    Long last_message_time;
    Date netDateFormat,lastDateFormat;

    View read_indicator;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chats, container, false);

        group_message_rel=(RelativeLayout)view.findViewById(R.id.group_message_rel);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        progressBar=(ProgressBar)view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        soc_name=getArguments().getString("society_name");
        name=getArguments().getString("name");
        flat_no=getArguments().getString("flat_no");
        admin=getArguments().getString("admin");
        unique_id=getArguments().getString("unique_id");

        last_message=(TextView)view.findViewById(R.id.last_message);
        tv_last_message_time=(TextView)view.findViewById(R.id.last_message_time);
        message_date=(TextView)view.findViewById(R.id.last_message_date);
        read_indicator=(View)view.findViewById(R.id.read_indicator);

        Log.i(TAG,"unique name is "+unique_id);

        Log.i(TAG,"society name is "+soc_name);

        tv_groupName=(TextView)view.findViewById(R.id.groupname);
        groupName=soc_name + " Group";
        tv_groupName.setText(groupName);

        usersList=new ArrayList<>();
        sortedList=new ArrayList<>();
        final String chatlist="Chatlist_"+unique_id;

        group_message_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(getContext(), GroupMessagesActivity.class);
                intent.putExtra("groupName",groupName);
                intent.putExtra("unique_id",unique_id);
                intent.putExtra("flat_no",flat_no);
                getContext().startActivity(intent);


            }
        });

        reference= FirebaseDatabase.getInstance().getReference(chatlist).child(firebaseUser.getUid());
        reference.keepSynced(true);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.VISIBLE);
                usersList.clear();
                Chatlist chatlist;
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    chatlist=snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }

                chatList();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        searchView=(EditText)view.findViewById(R.id.search_field);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filter(s.toString().toLowerCase());

            }
        });

        getGroupLastMessage();

        return view;
    }


    private void filter(String text){
        List<User> temp = new ArrayList();
        for(User d: sortedList){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getFlatno().toLowerCase().contains(text) || d.getName().toLowerCase().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        userAdapter.updateList(temp);

    }


    private void chatList() {

        mUsers=new ArrayList<>();

        reference= FirebaseDatabase.getInstance().getReference(unique_id);

        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUsers.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user=snapshot.getValue(User.class);


                    for(Chatlist chatlist:usersList){

                        if(user.getId()!=null ) {
                            if (user.getId().equals(chatlist.getId())) {
//                                mUsers.add(user);
                                mUsers.add(new User(user.getId(),user.getEmail(),user.getImageURL(),user.getStatus(),user.getWing(),user.getFlatno(),
                                        user.getName(),user.getPhoneno(),chatlist.getLast_message_time(),chatlist.getSeen()));
                            }
                        }

                        if(received_id!=null){
                            if(received_id.equals(user.getId())){
                                mUsers.add(user);
                            }
                        }


                    }
                }

                SortingClass sortingClass=new SortingClass(mUsers);
                sortedList=sortingClass.getSortedByTime();

                userAdapter=new UserAdapter(getContext(),sortedList,true,soc_name,name,flat_no,admin,unique_id);
                recyclerView.setAdapter(userAdapter);
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getGroupLastMessage(){
        DatabaseReference groupChatRef=FirebaseDatabase.getInstance().getReference(unique_id).child(firebaseUser.getUid());

        groupChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){

                    if(snapshot.getKey().equals("isSeen")){
                        if(snapshot.getValue().equals(FALSE)){
                            tv_groupName.setTypeface(null, Typeface.BOLD);
                            read_indicator.setVisibility(View.VISIBLE);
                            last_message.setTypeface(null, Typeface.BOLD);
                        }else{
                            tv_groupName.setTypeface(null, Typeface.NORMAL);
                            read_indicator.setVisibility(View.GONE);
                            last_message.setTypeface(null, Typeface.NORMAL);
                        }
                    }
                    if(snapshot.getKey().equals("last_message")) {
                        last_message.setText(snapshot.getValue().toString());

                    }
                    if(snapshot.getKey().equals("last_message_time")) {

                        last_message_time = Long.parseLong(snapshot.getValue().toString());
                        Date netDate = (new Date(last_message_time));
                        SimpleDateFormat sfd_time = new SimpleDateFormat("HH:mm");
                        time = sfd_time.format(netDate);

                        Log.i(TAG,"time is "+time);

                        SimpleDateFormat sfd_date = new SimpleDateFormat("dd/MM/yyyy");
                        date = sfd_date.format(netDate);

                        Log.i(TAG,"date is "+date);

                        try {
                            netDateFormat = sfd_date.parse(date);
                            Calendar calendar = Calendar.getInstance();
                            calendar.add(Calendar.DATE, -1);
                            lastDate = sfd_date.format(calendar.getTime());
                            lastDateFormat = sfd_date.parse(lastDate);

                            Log.i(TAG,"Now date is "+lastDate);

                            if (netDateFormat.compareTo(lastDateFormat)==0){
                                time="Yesterday";
                                tv_last_message_time.setText("Yesterday");
                                Log.i(TAG,"time is "+time);
                                message_date.setVisibility(View.GONE);
                            }
                            else if (netDateFormat.compareTo(lastDateFormat)==1){
                                tv_last_message_time.setText(time);
                                message_date.setVisibility(View.GONE);

                            }
                            else {
                                time = sfd_date.format(netDate);
                                Log.i(TAG,"time is "+time);
                                tv_last_message_time.setText(date);

                                message_date.setVisibility(View.GONE);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}


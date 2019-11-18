package com.replon.www.replonhomy.Messaging.Fragments;

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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.replon.www.replonhomy.Messaging.Adapter.UserAdapter;
import com.replon.www.replonhomy.Messaging.Model.User;
import com.replon.www.replonhomy.Messaging.SortingClass;
import com.replon.www.replonhomy.R;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersFragment extends Fragment {

    private final String TAG="UsersFragment";

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUser;

    EditText search_users;
    String soc_name,name,flat_no,admin,unique_id;
    List<User> sortedList;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        soc_name=getArguments().getString("society_name");
        name=getArguments().getString("name");
        flat_no=getArguments().getString("flat_no");
        admin=getArguments().getString("admin");
        unique_id=getArguments().getString("unique_id");

        Log.i(TAG,"society name is "+soc_name);
        Log.i(TAG,"unique_id is "+unique_id);
        sortedList=new ArrayList<>();

        View view= inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUser=new ArrayList<>();
        readUsers();




        search_users=(EditText)view.findViewById(R.id.search_users);
        search_users.addTextChangedListener(new TextWatcher() {
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

        return view;

    }

    private void filter(String text){
        List<User> temp = new ArrayList();
        for(User d: mUser){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getFlatno().toLowerCase().contains(text) || d.getName().toLowerCase().contains(text)){
                temp.add(d);
            }

        }

        //update recyclerview
        userAdapter.updateList(temp);

    }

    private void readUsers(){
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        Log.i(TAG,"unique id is "+unique_id);

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference(unique_id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(search_users.getText().toString().equals("")) {

                    mUser.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        User user = snapshot.getValue(User.class);
                        assert user != null;
                        assert firebaseUser != null;


                        if(user.getId()!=null){

                        if (user.getId().equals(firebaseUser.getUid())) {
                            //mUser.add(user);
                            Log.d(TAG, "Same user");
                        } else {
                            mUser.add(user);
                        }
                        }
                    }

                    SortingClass sortingClass=new SortingClass(mUser);
                    Collections.reverse(sortingClass.getSortedByFlat());


                    userAdapter = new UserAdapter(getContext(), mUser, false,soc_name,name,flat_no,admin,unique_id);
                    recyclerView.setAdapter(userAdapter);

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}

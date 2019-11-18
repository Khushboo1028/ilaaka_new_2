package com.replon.www.replonhomy.MinutesOfMeetings;



import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.replon.www.replonhomy.R;

import java.util.List;

public class AddMoMAdapter extends RecyclerView.Adapter<AddMoMAdapter.AddMoMViewHolder>{


    public static final String TAG = "AddMOMAdapter";
    private Context mContext;
    private List<ContentsAddMoM> addmomList;

    public AddMoMAdapter(Context mContext, List<ContentsAddMoM> addmomList) {
        this.mContext = mContext;
        this.addmomList = addmomList;
    }

    @NonNull
    @Override
    public AddMoMViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_mom_meetings_subject_layout,null);
        AddMoMViewHolder holder=new AddMoMViewHolder(view);
        return holder;
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull AddMoMViewHolder addmomViewHolder, int i) {


        //so this i is position that will give you the specified item from the product list!


        ContentsAddMoM contentsAddMoM =addmomList.get(i);

        //So now we bind the data using the help of this contentsComplaints object we create

        addmomViewHolder.meeting_subject.setText(String.valueOf(contentsAddMoM.getSubject()));
        addmomViewHolder.meeting_desc.setText(String.valueOf(contentsAddMoM.getDescription()));
        //if image would have been present then
        // commentsViewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsComplaints.getImage()),null);


        Log.i(TAG,"meeting_subject is"+ addmomViewHolder.meeting_subject.getText());
        Log.i(TAG,"meeting_desc is"+ addmomViewHolder.meeting_desc.getText());



    }



    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return addmomList.size();
    }



    class AddMoMViewHolder extends RecyclerView.ViewHolder {


        EditText meeting_subject,meeting_desc;


        public AddMoMViewHolder(@NonNull View itemView) {
            super(itemView);

            meeting_desc=(EditText) itemView.findViewById(R.id.meeting_desc);
            meeting_subject=(EditText) itemView.findViewById(R.id.meeting_subject);

            meeting_subject.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    addmomList.get(getAdapterPosition()).setSubject(meeting_subject.getText().toString());
                  //  addmomList.get(getAdapterPosition()).setSubject(meeting_desc.getText().toString());
                    Log.i(TAG,"Inside onTextChanged ");
                    Log.i(TAG,"onTextChanged subject "+meeting_subject.getText().toString());

                }

                @Override
                public void afterTextChanged(Editable s) {

                    addmomList.get(getAdapterPosition()).setSubject(meeting_subject.getText().toString());
                 //   addmomList.get(getAdapterPosition()).setSubject(meeting_desc.getText().toString());
                    Log.i(TAG,"Inside afterTextChanged ");
                    Log.i(TAG,"onTextChanged subject "+meeting_subject.getText().toString());


                }
            });

            meeting_desc.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                  //  addmomList.get(getAdapterPosition()).setSubject(meeting_subject.getText().toString());
                    addmomList.get(getAdapterPosition()).setDescription(meeting_desc.getText().toString());
                    Log.i(TAG,"Inside onTextChanged ");
                    Log.i(TAG,"onTextChanged desc "+meeting_desc.getText().toString());




                }

                @Override
                public void afterTextChanged(Editable s) {

                   // addmomList.get(getAdapterPosition()).setSubject(meeting_subject.getText().toString());
                    addmomList.get(getAdapterPosition()).setDescription(meeting_desc.getText().toString());
                    Log.i(TAG,"Inside afterTextChanged ");
                    Log.i(TAG,"afterTextChanged desc "+meeting_desc.getText().toString());

                }
            });



        }
    }




}



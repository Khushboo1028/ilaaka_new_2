package com.replon.www.replonhomy.MinutesOfMeetings;



import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import org.w3c.dom.Text;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class ViewMoMAdapter extends RecyclerView.Adapter<ViewMoMAdapter.AddMoMViewHolder>{


    public static final String TAG = "AddMOMAdapter";
    private Context mContext;
    private List<ContentsAddMoM> addmomList;

    public ViewMoMAdapter(Context mContext, List<ContentsAddMoM> addmomList) {
        this.mContext = mContext;
        this.addmomList = addmomList;
    }

    @NonNull
    @Override
    public AddMoMViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_mom_meetings_subject_layout_view,null);
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

        addmomViewHolder.meeting_subject.setTextIsSelectable(true);
        addmomViewHolder.meeting_desc.setTextIsSelectable(true);

       // addmomViewHolder.meeting_desc.setMovementMethod(new ScrollingMovementMethod());
        //if image would have been present then
        // commentsViewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsComplaints.getImage()),null);



    }


    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return addmomList.size();
    }

    class AddMoMViewHolder extends RecyclerView.ViewHolder {
        TextView meeting_subject,meeting_desc;

        public AddMoMViewHolder(@NonNull View itemView) {
            super(itemView);

            meeting_desc=(TextView) itemView.findViewById(R.id.meeting_desc);
            meeting_subject=(TextView) itemView.findViewById(R.id.meeting_subject);
        }
    }




}



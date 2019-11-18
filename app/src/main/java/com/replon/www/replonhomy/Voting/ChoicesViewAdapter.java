package com.replon.www.replonhomy.Voting;

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
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.List;

import static java.lang.Boolean.FALSE;

public class ChoicesViewAdapter extends RecyclerView.Adapter<ChoicesViewAdapter.ChoicesViewHolder> {

    private Context mContext;
    private List<ContentsChoices> choicesList;
    private static final String TAG="ChociesAdapter";

    public ChoicesViewAdapter(Context mContext, List<ContentsChoices> choicesList) {
        this.mContext = mContext;
        this.choicesList = choicesList;
    }


    @NonNull
    @Override
    public ChoicesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_poll_view_choices,null);
        ChoicesViewHolder holder=new ChoicesViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChoicesViewHolder holder, int i) {

        final ContentsChoices contentsChoices = choicesList.get(i);
        holder.choice.setText(String.valueOf(contentsChoices.getChoice()));



    }

    @Override
    public int getItemCount() {
        return choicesList.size();
    }

    class ChoicesViewHolder extends RecyclerView.ViewHolder {

        TextView choice;

        public ChoicesViewHolder(@NonNull View itemView) {
            super(itemView);

            choice = (TextView) itemView.findViewById(R.id.choices);
//            choice.setFocusable(false);
//            choice.setClickable(false);

        }
    }

}

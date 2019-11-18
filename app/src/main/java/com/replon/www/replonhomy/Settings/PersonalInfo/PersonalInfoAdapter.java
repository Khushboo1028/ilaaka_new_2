package com.replon.www.replonhomy.Settings.PersonalInfo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.List;

public class PersonalInfoAdapter extends RecyclerView.Adapter<PersonalInfoAdapter.PersonalInfoViewHolder> {

    public static final String TAG = "PersonalInfoAdapter";
    private Context mContext;
    private List<ContentsPersonalInfo> info_list;

    public PersonalInfoAdapter(Context mContext, List<ContentsPersonalInfo> info_list) {
        this.mContext = mContext;
        this.info_list = info_list;

    }

    @NonNull
    @Override
    public PersonalInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_personal_info,null);
        PersonalInfoViewHolder holder=new PersonalInfoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalInfoViewHolder holder,final int i) {

        ContentsPersonalInfo contentsPersonalInfo = info_list.get(i);
        holder.title.setText(String.valueOf(contentsPersonalInfo.getTitle()));
        holder.info.setText(String.valueOf(contentsPersonalInfo.getInfo()));

        if(!contentsPersonalInfo.getNxt_show()){
            holder.arrow_next.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return info_list.size();
    }

    class PersonalInfoViewHolder extends RecyclerView.ViewHolder {

        ImageView  arrow_next;
        TextView title, info;


        public PersonalInfoViewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.title);
            info = itemView.findViewById(R.id.info);
            arrow_next = itemView.findViewById(R.id.nxt);


        }


    }
}

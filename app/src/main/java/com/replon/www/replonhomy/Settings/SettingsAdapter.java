package com.replon.www.replonhomy.Settings;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.replon.www.replonhomy.R;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder> {

    public static final String TAG = "SettingsAdapter";
    private Context mContext;
    private List<ContentsSettings> settingsList;
    private View.OnClickListener mClickListener;


    public SettingsAdapter(Context mContext, List<ContentsSettings> settingsList) {
        this.mContext = mContext;
        this.settingsList = settingsList;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_settings_list_layout,null);
        SettingsViewHolder holder=new SettingsViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(v);
            }
        });
        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder settingsViewHolder, final int i) {

        ContentsSettings contentsSettings = settingsList.get(i);
        settingsViewHolder.setting.setText(String.valueOf(contentsSettings.getSetting()));

        settingsViewHolder.img_setting.setImageDrawable(mContext. getDrawable(contentsSettings.getImage()));



    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }


    class SettingsViewHolder extends RecyclerView.ViewHolder {

        ImageView img_setting,arrow_next;
        TextView setting;


        public SettingsViewHolder(@NonNull View itemView) {
            super(itemView);
            setting=(TextView)itemView.findViewById(R.id.settings_list_text);
            img_setting = (ImageView)itemView.findViewById(R.id.settings_list_img);


        }


    }



}

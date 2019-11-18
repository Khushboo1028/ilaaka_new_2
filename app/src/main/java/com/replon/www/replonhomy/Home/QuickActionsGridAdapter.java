package com.replon.www.replonhomy.Home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.replon.www.replonhomy.R;
import com.replon.www.replonhomy.Utility.DefaultTextConfig;

public class QuickActionsGridAdapter extends BaseAdapter {

    private int icons[];
    private String letters[];
    private Context context;

    private LayoutInflater inflater;


    public QuickActionsGridAdapter(Context context, int icons[], String letters[] ){

        this.context=context;
        this.icons=icons;
        this.letters=letters;
    }

    @Override
    public int getCount() {
        return letters.length;
    }

    @Override
    public Object getItem(int position) {
        return letters[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View gridView=convertView;

        if(convertView==null){

            inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView=inflater.inflate(R.layout.grid_view_snippet_nobg,null);
        }

        ImageView icon=(ImageView) gridView.findViewById(R.id.icons);
        TextView letter=(TextView)gridView.findViewById(R.id.icon_names);

        icon.setImageResource(icons[position]);

        letter.setText(letters[position]);

        return gridView;
    }
}

package com.replon.www.replonhomy.Utility;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.replon.www.replonhomy.R;

import java.util.ArrayList;
import java.util.List;

public class UploadsAdapter extends RecyclerView.Adapter<UploadsAdapter.UploadsViewHolder>{


    private Context mContext;
    private List<UploadsAttachmentContent> uploadsList;
    ArrayList<Uri> imageURIlist;



    public UploadsAdapter(Context mContext, List<UploadsAttachmentContent> uploadsList, ArrayList<Uri> imageURIlist) {
        this.mContext = mContext;
        this.uploadsList = uploadsList;
        this.imageURIlist=imageURIlist;
    }


    @NonNull
    @Override
    public UploadsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_image_layout,null);
        UploadsViewHolder holder=new UploadsViewHolder(view);
        return holder;
    }



    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull UploadsViewHolder holder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        UploadsAttachmentContent uploadsAttachmentContent =uploadsList.get(i);

        //So now we bind the data using the help of this contentsComments object we created

        //if image would have been present then
       //holder.images.setImageDrawable(mContext.getResources().getDrawable(uploadsAttachmentContent.getImage(),null));

        holder.images.setImageBitmap(uploadsAttachmentContent.getImage());
        //Glide.with(mContext).load(uploadsAttachmentContent.getImage().into(holder.images);

        //  holder.images.setVisibility(View.VISIBLE);

        holder.images.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showAddDeleteDialog(i,imageURIlist);
                return true;
            }
        });


    }



    public void showAddDeleteDialog(final int pos,final ArrayList<Uri>imageURIlist){


        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setCancelable(false);
        dialog.setTitle("Are you sure you want to remove attachment?");
        // dialog.setMessage("Are you sure you want to delete this entry?" );
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                //Action for "Add".
                uploadsList.remove(pos);
                imageURIlist.remove(pos);


                notifyDataSetChanged();
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }

    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return uploadsList.size();
    }

    public class UploadsViewHolder extends RecyclerView.ViewHolder {


       private ImageView images;


        public UploadsViewHolder(@NonNull View itemView) {
            super(itemView);

            images=(ImageView)itemView.findViewById(R.id.images);




        }
    }






}


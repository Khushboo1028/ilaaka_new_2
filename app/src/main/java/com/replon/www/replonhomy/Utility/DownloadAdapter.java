package com.replon.www.replonhomy.Utility;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.replon.www.replonhomy.R;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadsViewHolder>{


    public static final String TAG = "ComplaintsDownloadAdapt";
    private Context mContext;
    private List<DownloadsAttachmentContent> downloadList;


    public DownloadAdapter(Context mContext, List<DownloadsAttachmentContent> downloadList) {
        this.mContext = mContext;
        this.downloadList = downloadList;
    }


    @NonNull
    @Override
    public DownloadsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_image_layout,null);
        DownloadsViewHolder holder=new DownloadsViewHolder(view);
        return holder;
    }



    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull final DownloadsViewHolder holder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        final DownloadsAttachmentContent downloadsAttachmentContent =downloadList.get(i);

        //So now we bind the data using the help of this contentsComments object we created

        //if image would have been present then
        //holder.images.setImageDrawable(mContext.getResources().getDrawable(compaintsUploadsContent.getImage(),null));

        //holder.images.setImageBitmap(downloadsAttachmentContent.getImageURL());

        if(downloadList.get(i).getFileType().equals("image")){
            try {
                Glide.with(mContext).load(downloadsAttachmentContent.getImageURL()).placeholder(R.drawable.ic_img_loading).into(holder.images);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if(downloadList.get(i).getFileType().equals("pdf")){
            holder.images.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_pdf_load,null));
            Log.i(TAG,"I am here ");
        }





        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Item Clicked");
                Intent intent=new Intent(mContext, ViewImageActivity.class);
                intent.putExtra("clickedImageURl",downloadList.get(i).getImageURL());
                intent.putExtra("fileType",downloadList.get(i).getFileType());
                if(downloadList.get(i).getFileType().equals("image")) {
                    mContext.startActivity(intent);
                }

                if(downloadList.get(i).getFileType().equals("pdf")) {

                    Uri path=Uri.parse(downloadList.get(i).getPdfURL());
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path , "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    try
                    {
                        mContext.startActivity(pdfIntent);
                    }
                    catch (ActivityNotFoundException e)
                    {
                        Toast.makeText(mContext,"No Application available to viewPDF",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });


    }



    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    public class DownloadsViewHolder extends RecyclerView.ViewHolder {


        private ImageView images;
        private ViewPager viewPager;



        public DownloadsViewHolder(@NonNull View itemView) {
            super(itemView);

            images=(ImageView)itemView.findViewById(R.id.images);
            viewPager=(ViewPager)itemView.findViewById(R.id.view_pager);

        }
    }






}


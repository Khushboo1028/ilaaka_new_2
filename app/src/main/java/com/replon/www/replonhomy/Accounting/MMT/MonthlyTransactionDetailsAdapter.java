package com.replon.www.replonhomy.Accounting.MMT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.replon.www.replonhomy.R;

import java.util.ArrayList;

public class MonthlyTransactionDetailsAdapter extends RecyclerView.Adapter<MonthlyTransactionDetailsAdapter.ViewHolder>{


    public static final String TAG = "MonthlyTransactionDeta";
    private Context mContext;
    private ArrayList<ContentsMMT> mmtList;
    private String soc_name,flat_no,username,admin,unique_id,name,status;
    private Activity mActivity;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;
    String user_id;
    DocumentReference docRef;

    public MonthlyTransactionDetailsAdapter(Context mContext,Activity mActivity,ArrayList<ContentsMMT> mmtList, String soc_name, String flat_no, String username, String name, String admin, String unique_id) {
        this.mContext = mContext;
        this.mActivity=mActivity;
        this.mmtList = mmtList;
        this.soc_name=soc_name;
        this.username=username;
        this.admin=admin;
        this.flat_no=flat_no;
        this.unique_id=unique_id;
        this.name=name;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        View view=layoutInflater.inflate(R.layout.row_monthly_transaction_detail,null);
        ViewHolder holder=new ViewHolder(view);


        return holder;
    }

    public void updateList(ArrayList<ContentsMMT> list){
        mmtList = list;
        notifyDataSetChanged();
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        final ContentsMMT contentsMMT = mmtList.get(i);

        //So now we bind the data using the help of this contentsComplaints object we created
        viewHolder.tv_amount.setText("Rs. "+ contentsMMT.getAmount());
        viewHolder.tv_paid_via.setText(contentsMMT.getPaid_by());
        viewHolder.tv_paid_on.setText(contentsMMT.getPaid_on_date());
        viewHolder.tv_due_date.setText(contentsMMT.getDue_date());
        viewHolder.tv_flat.setText(contentsMMT.getFlat_no());
        viewHolder.tv_reference.setText(contentsMMT.getReference());

        //if image would have been present then
       // viewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsComplaints.getImage()),null);

        if(contentsMMT.getPaid_status().equals("approved")){
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_approved);
            viewHolder.tv_status.setText("Approved");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.greenSolved));

            status="approved";

        }else if(contentsMMT.getPaid_status().equals("pending")) {
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_pending_approval);
            viewHolder.tv_status.setText("Pending\nApproval");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.pendingYellow));

            status="pending";




        }else if(contentsMMT.getPaid_status().equals("rejected")){
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_rejected);
            viewHolder.tv_status.setText("Rejected");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.incorrect_red));

            status="rejected";
        }
        else{

            viewHolder.pay_now.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setVisibility(View.GONE);
            viewHolder.tv_status.setVisibility(View.GONE);

            status="pay_now";

        }

        viewHolder.pay_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"findList is "+mmtList.get(0).getPdf_url());
                Intent intent = new Intent(mContext,PayNowActivity.class);
                intent.putExtra("society_name",soc_name);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("username",username);
                intent.putExtra("name",name);
                intent.putExtra("admin",admin);
                intent.putExtra("unique_id",unique_id);
                intent.putExtra("status","pay_now");
                intent.putExtra("position",i);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("findList", mmtList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"findList is "+mmtList.get(0).getPdf_url());
                Intent intent = new Intent(mContext,PayNowActivity.class);
                intent.putExtra("society_name",soc_name);
                intent.putExtra("flat_no",flat_no);
                intent.putExtra("username",username);
                intent.putExtra("name",name);
                intent.putExtra("admin",admin);
                intent.putExtra("unique_id",unique_id);
                intent.putExtra("status",contentsMMT.getPaid_status());
                intent.putExtra("position",i);

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("findList", mmtList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

        if(!contentsMMT.getPaid_status().equals("pending")){
            viewHolder.choose_rel.setVisibility(View.GONE);
        }else{
            viewHolder.choose_rel.setVisibility(View.VISIBLE);
        }

        viewHolder.image_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("")
                        .setMessage("Are you sure you want to approve this transaction?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                               updateData(contentsMMT,3);
                               viewHolder.choose_rel.setVisibility(View.GONE);
                               viewHolder.image_paid_check.setImageResource(R.drawable.ic_approved);
                               viewHolder.tv_status.setText("Approved");
                               viewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.greenSolved));
                            }
                        })
                        .setNegativeButton("Cancel", null).show();

            }
        });

        viewHolder.image_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mActivity)
                        .setTitle("")
                        .setMessage("Are you sure you want to reject this transaction?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                updateData(contentsMMT,2);
                                viewHolder.choose_rel.setVisibility(View.GONE);
                                viewHolder.image_paid_check.setImageResource(R.drawable.ic_rejected);
                                viewHolder.tv_status.setText("Rejected");
                                viewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.incorrect_red));
                            }
                        })
                        .setNegativeButton("Cancel", null).show();

            }
        });

    }

    private void updateData(final ContentsMMT contentsMMT, final int status) {
        db= FirebaseFirestore.getInstance();
        currentFirebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        user_id=currentFirebaseUser.getUid();
        docRef=db.collection(mContext.getString(R.string.USER)).document(user_id);

        DocumentReference docRef = db.collection(mContext.getString(R.string.USER)).document(user_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        DocumentReference soc_id_ref = document.getDocumentReference("society_id");
                        Log.i(TAG, "Society id is " + soc_id_ref);

                        soc_id_ref.collection("mmt").document(contentsMMT.getDocument_id())
                                .update("status",status).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG,"Data updated");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG,"An internal error occurred "+e.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }


    //Will return the size of the list  ie the number of elements available inside the list that is 5
    @Override
    public int getItemCount() {
        return mmtList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


       TextView tv_paid_on,tv_paid_via,tv_amount, tv_reference, tv_flat, tv_due_date, tv_status;
       ImageView image_paid_check,image_reject,image_approve;
       Button pay_now;

       LinearLayout choose_rel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_paid_on=(TextView)itemView.findViewById(R.id.paid_on_date);
            tv_paid_via=(TextView)itemView.findViewById(R.id.paid_via);
            tv_amount=(TextView)itemView.findViewById(R.id.amount);
            tv_reference=(TextView) itemView.findViewById(R.id.reference_no);
            tv_flat = (TextView) itemView.findViewById(R.id.flat_no);
            tv_due_date = (TextView) itemView.findViewById(R.id.due_date);
            image_paid_check=(ImageView)itemView.findViewById(R.id.check_image);
            tv_status = (TextView) itemView.findViewById(R.id.status_text);
            pay_now = (Button) itemView.findViewById(R.id.pay_now_btn);
            image_reject=(ImageView)itemView.findViewById(R.id.transaction_rejected_image);
            image_approve=(ImageView)itemView.findViewById(R.id.transaction_approved_image);
            choose_rel=(LinearLayout)itemView.findViewById(R.id.choose_rel);

        }
    }




}

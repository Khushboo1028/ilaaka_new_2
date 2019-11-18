package com.replon.www.replonhomy.Accounting.SocietyAccounting;

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
import com.replon.www.replonhomy.Accounting.MMT.ContentsMMT;
import com.replon.www.replonhomy.Accounting.MMT.PayNowActivity;
import com.replon.www.replonhomy.R;

import java.util.ArrayList;

public class SocietyAdapter extends RecyclerView.Adapter<SocietyAdapter.ViewHolder>{


    public static final String TAG = "SocietyAdapter";
    private Context mContext;
    private ArrayList<ContentsAccounts> accountsArrayList;
    private String soc_name,flat_no,username,admin,unique_id,name;
    private Activity mActivity;
    FirebaseFirestore db;
    FirebaseUser currentFirebaseUser;
    String user_id;
    DocumentReference docRef;

    public SocietyAdapter(Context mContext, Activity mActivity, ArrayList<ContentsAccounts> accountsArrayList, String soc_name, String flat_no, String username, String name, String admin, String unique_id) {
        this.mContext = mContext;
        this.mActivity=mActivity;
        this.accountsArrayList = accountsArrayList;
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
        View view=layoutInflater.inflate(R.layout.row_society_accounting_detail,null);
        ViewHolder holder=new ViewHolder(view);


        return holder;
    }

    public void updateList(ArrayList<ContentsAccounts> list){
        accountsArrayList = list;
        notifyDataSetChanged();
    }


    //Will bind data to ViewHolder(UI elements)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {


        //so this i is position that will give you the specified item from the product list!


        final ContentsAccounts contentsAccounts = accountsArrayList.get(i);

        //So now we bind the data using the help of this contentsComplaints object we created
        viewHolder.tv_amount.setText("Rs. "+ contentsAccounts.getAmount());
        viewHolder.tv_paid_via.setText(contentsAccounts.getPaid_by());
        viewHolder.tv_paid_on.setText(contentsAccounts.getPaid_on());
        viewHolder.tv_flat.setText(contentsAccounts.getFlat_no() + ". "+contentsAccounts.getName());
        viewHolder.tv_reference.setText(contentsAccounts.getReference_no());
        viewHolder.tv_particular.setText(contentsAccounts.getParticulars());


        //if image would have been present then
       // viewHolder.image.setImageDrawable(mContext. getResources().getDrawable(contentsComplaints.getImage()),null);

        if(contentsAccounts.getStatus()==3){
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_approved);
            viewHolder.tv_status.setText("Approved");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.greenSolved));



        }else if(contentsAccounts.getStatus()==1) {
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_pending_approval);
            viewHolder.tv_status.setText("Pending\nApproval");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.pendingYellow));






        }else if(contentsAccounts.getStatus()==2){
            viewHolder.pay_now.setVisibility(View.GONE);
            viewHolder.image_paid_check.setVisibility(View.VISIBLE);
            viewHolder.tv_status.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setImageResource(R.drawable.ic_rejected);
            viewHolder.tv_status.setText("Rejected");
            viewHolder.tv_status.setTextColor(mContext.getColor(R.color.incorrect_red));


        }
        else{

            viewHolder.pay_now.setVisibility(View.VISIBLE);
            viewHolder.image_paid_check.setVisibility(View.GONE);
            viewHolder.tv_status.setVisibility(View.GONE);



        }



//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.i(TAG,"findList is "+ accountsArrayList.get(0).getPdf_url());
//                Intent intent = new Intent(mContext,PayNowActivity.class);
//                intent.putExtra("society_name",soc_name);
//                intent.putExtra("flat_no",flat_no);
//                intent.putExtra("username",username);
//                intent.putExtra("name",name);
//                intent.putExtra("admin",admin);
//                intent.putExtra("unique_id",unique_id);
//                intent.putExtra("status",contentsAccounts.getStatus());
//                intent.putExtra("position",i);
//
//                Bundle bundle = new Bundle();
//                bundle.putParcelableArrayList("findList", accountsArrayList);
//                intent.putExtras(bundle);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                mContext.startActivity(intent);
//            }
//        });

        if(contentsAccounts.getStatus()!=1){
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
                               updateData(contentsAccounts,3);
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
                                updateData(contentsAccounts,2);
                                viewHolder.choose_rel.setVisibility(View.GONE);
                                viewHolder.image_paid_check.setImageResource(R.drawable.ic_rejected);
                                viewHolder.tv_status.setText("Rejected");
                                viewHolder.tv_status.setTextColor(mContext.getResources().getColor(R.color.incorrect_red));
                            }
                        })
                        .setNegativeButton("Cancel", null).show();

            }
        });

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext,ViewSocietyAccounts.class);
                intent.putExtra("position",i);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("accountList", accountsArrayList);
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }

    private void updateData(final ContentsAccounts contentsAccounts, final int status) {
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

                        soc_id_ref.collection("accounts").document(contentsAccounts.getDocument_id())
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
        return accountsArrayList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


       TextView tv_paid_on,tv_paid_via,tv_amount, tv_reference, tv_flat,tv_status,tv_particular;
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
            tv_particular = (TextView) itemView.findViewById(R.id.particular);
            image_paid_check=(ImageView)itemView.findViewById(R.id.check_image);
            tv_status = (TextView) itemView.findViewById(R.id.status_text);
            pay_now = (Button) itemView.findViewById(R.id.pay_now_btn);
            image_reject=(ImageView)itemView.findViewById(R.id.transaction_rejected_image);
            image_approve=(ImageView)itemView.findViewById(R.id.transaction_approved_image);
            choose_rel=(LinearLayout)itemView.findViewById(R.id.choose_rel);

        }
    }




}

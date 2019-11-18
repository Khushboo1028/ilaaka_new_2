package com.replon.www.replonhomy.Accounting.MMT;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ContentsMMT extends ArrayList<Parcelable> implements Parcelable {

    String amount;
    String paid_on_date;
    String paid_by;
    String reference;
    String paid_status;
    String due_date;
    String maintenance_month;
    String document_id;
    ArrayList<String> image_url;
    ArrayList<String> pdf_url;
    String flat_no;


    public ContentsMMT(String amount, String paid_on_date, String paid_by,String reference,String paid_status,ArrayList<String>image_url,ArrayList<String>pdf_url,String due_date,
                         String maintenance_month,String flat_no,String document_id) {
        this.amount = amount;
        this.paid_on_date = paid_on_date;
        this.paid_by = paid_by;
        this.reference=reference;
        this.paid_status=paid_status;
        this.image_url=image_url;
        this.pdf_url=pdf_url;
        this.due_date=due_date;
        this.maintenance_month=maintenance_month;
        this.flat_no =flat_no;
        this.document_id=document_id;
    }
    public ContentsMMT() {

    }

    protected ContentsMMT(Parcel in) {
        amount = in.readString();
        paid_on_date = in.readString();
        paid_by = in.readString();
        reference = in.readString();
        paid_status = in.readString();
        due_date = in.readString();
        maintenance_month = in.readString();
        image_url = in.createStringArrayList();
        pdf_url = in.createStringArrayList();
        flat_no = in.readString();
        document_id=in.readString();

    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public static Creator<ContentsMMT> getCREATOR() {
        return CREATOR;
    }

    public static final Creator<ContentsMMT> CREATOR = new Creator<ContentsMMT>() {
        @Override
        public ContentsMMT createFromParcel(Parcel in) {
            return new ContentsMMT(in);
        }

        @Override
        public ContentsMMT[] newArray(int size) {
            return new ContentsMMT[size];
        }
    };

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    public String getMaintenance_month() {
        return maintenance_month;
    }

    public void setMaintenance_month(String maintenance_month) {
        this.maintenance_month = maintenance_month;
    }

    public String getDue_date() {
        return due_date;
    }

    public void setDue_date(String due_date) {
        this.due_date = due_date;
    }

    public ArrayList<String> getImage_url() {
        return image_url;
    }

    public void setImage_url(ArrayList<String> image_url) {
        this.image_url = image_url;
    }

    public ArrayList<String> getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(ArrayList<String> pdf_url) {
        this.pdf_url = pdf_url;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getPaid_status() {
        return paid_status;
    }

    public void setPaid_status(String paid_status) {
        this.paid_status = paid_status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaid_on_date() {
        return paid_on_date;
    }

    public void setPaid_on_date(String paid_on_date) {
        this.paid_on_date = paid_on_date;
    }

    public String getPaid_by() {
        return paid_by;
    }

    public void setPaid_by(String paid_by) {
        this.paid_by = paid_by;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(amount);
        dest.writeString(paid_on_date);
        dest.writeString(paid_by);
        dest.writeString(reference);
        dest.writeString(paid_status);
        dest.writeString(due_date);
        dest.writeString(maintenance_month);
        dest.writeStringList(image_url);
        dest.writeStringList(pdf_url);
        dest.writeString(flat_no);
        dest.writeString(document_id);
    }

    @Override
    public Stream<Parcelable> stream() {
        return null;
    }

    @Override
    public Stream<Parcelable> parallelStream() {
        return null;
    }
}

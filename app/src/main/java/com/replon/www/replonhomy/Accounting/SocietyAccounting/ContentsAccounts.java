package com.replon.www.replonhomy.Accounting.SocietyAccounting;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.stream.Stream;

public class ContentsAccounts extends ArrayList<Parcelable> implements Parcelable {
    String amount;
    String date_created;
    String document_id;
    String flat_no;
    ArrayList<String> image_url;
    String name;
    String paid_by;
    String paid_on;
    String particulars;
    ArrayList<String> pdf_url;
    String reference_no;
    int status;


    public ContentsAccounts(String amount, String date_created, String document_id, String flat_no, ArrayList<String> image_url, String name, String paid_by, String paid_on, String particulars, ArrayList<String> pdf_url, String reference_no, int status) {
        this.amount = amount;
        this.date_created = date_created;
        this.document_id = document_id;
        this.flat_no = flat_no;
        this.image_url = image_url;
        this.name = name;
        this.paid_by = paid_by;
        this.paid_on = paid_on;
        this.particulars = particulars;
        this.pdf_url = pdf_url;
        this.reference_no = reference_no;
        this.status = status;
    }

    protected ContentsAccounts(Parcel in) {
        amount = in.readString();
        date_created = in.readString();
        document_id = in.readString();
        flat_no = in.readString();
        image_url = in.createStringArrayList();
        name = in.readString();
        paid_by = in.readString();
        paid_on = in.readString();
        particulars = in.readString();
        pdf_url = in.createStringArrayList();
        reference_no = in.readString();
        status = in.readInt();
    }

    public static final Creator<ContentsAccounts> CREATOR = new Creator<ContentsAccounts>() {
        @Override
        public ContentsAccounts createFromParcel(Parcel in) {
            return new ContentsAccounts(in);
        }

        @Override
        public ContentsAccounts[] newArray(int size) {
            return new ContentsAccounts[size];
        }
    };

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    public ArrayList<String> getImage_url() {
        return image_url;
    }

    public void setImage_url(ArrayList<String> image_url) {
        this.image_url = image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaid_by() {
        return paid_by;
    }

    public void setPaid_by(String paid_by) {
        this.paid_by = paid_by;
    }

    public String getPaid_on() {
        return paid_on;
    }

    public void setPaid_on(String paid_on) {
        this.paid_on = paid_on;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public ArrayList<String> getPdf_url() {
        return pdf_url;
    }

    public void setPdf_url(ArrayList<String> pdf_url) {
        this.pdf_url = pdf_url;
    }

    public String getReference_no() {
        return reference_no;
    }

    public void setReference_no(String reference_no) {
        this.reference_no = reference_no;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(date_created);
        dest.writeString(document_id);
        dest.writeString(flat_no);
        dest.writeStringList(image_url);
        dest.writeString(name);
        dest.writeString(paid_by);
        dest.writeString(paid_on);
        dest.writeString(particulars);
        dest.writeStringList(pdf_url);
        dest.writeString(reference_no);
        dest.writeInt(status);
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

package com.replon.www.replonhomy.Accounting.MMT;

public class ContentsMonthlyMaintenance {

    String month;
    String approved_transactions;
    String rejected_transactions;
    Boolean all_transactions_bool;

    public ContentsMonthlyMaintenance(String month, String approved_transactions, String rejected_transactions, Boolean all_transactions_bool) {
        this.month = month;
        this.approved_transactions = approved_transactions;
        this.rejected_transactions = rejected_transactions;
        this.all_transactions_bool = all_transactions_bool;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getApproved_transactions() {
        return approved_transactions;
    }

    public void setApproved_transactions(String approved_transactions) {
        this.approved_transactions = approved_transactions;
    }

    public String getRejected_transactions() {
        return rejected_transactions;
    }

    public void setRejected_transactions(String rejected_transactions) {
        this.rejected_transactions = rejected_transactions;
    }

    public Boolean getAll_transactions_bool() {
        return all_transactions_bool;
    }

    public void setAll_transactions_bool(Boolean all_transactions_bool) {
        this.all_transactions_bool = all_transactions_bool;
    }
}

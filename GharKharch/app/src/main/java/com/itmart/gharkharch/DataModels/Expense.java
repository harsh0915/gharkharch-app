package com.itmart.gharkharch.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Expense implements Parcelable, GenericItem {

    private final int expense_id;
    private final String expense_name;
    private final int under_sub_cat_id;
    private final int expense_amount;
    private final long expense_date;
    private final int expense_mode;
    private String expense_remarks;

    public Expense(int expense_id, String expense_name, int under_sub_cat_id, int expense_amount, long expense_date, int expense_mode, String expense_remarks) {
        this.expense_id = expense_id;
        this.expense_name = expense_name;
        this.under_sub_cat_id = under_sub_cat_id;
        this.expense_amount = expense_amount;
        this.expense_date = expense_date;
        this.expense_mode = expense_mode;
        this.expense_remarks = expense_remarks;
    }

    protected Expense(Parcel in) {
        expense_id = in.readInt();
        expense_name = in.readString();
        under_sub_cat_id = in.readInt();
        expense_amount = in.readInt();
        expense_date = in.readLong();
        expense_mode = in.readInt();
        expense_remarks = in.readString();
    }

    public static final Creator<Expense> CREATOR = new Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };

    public int getExpense_id() {
        return expense_id;
    }

    public String getExpense_name() {
        return expense_name;
    }

    public int getUnder_sub_cat_id() {
        return under_sub_cat_id;
    }

    public int getExpense_amount() {
        return expense_amount;
    }

    public long getExpense_date() {
        return expense_date;
    }

    public int getExpense_mode() {
        return expense_mode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(expense_id);
        dest.writeString(expense_name);
        dest.writeInt(under_sub_cat_id);
        dest.writeInt(expense_amount);
        dest.writeLong(expense_date);
        dest.writeInt(expense_mode);
        dest.writeString(expense_remarks);
    }

    @Override
    public int get_item_id() {
        return this.expense_id;
    }

    @Override
    public String get_item_name() {
        return this.expense_name;
    }

    @Override
    public int get_fk_id() {
        return this.under_sub_cat_id;
    }

    @Override
    public int get_item_amount() {
        return this.expense_amount;
    }

    @Override
    public int get_mode_of_payment() {
        return this.expense_mode;
    }

    @Override
    public long get_date() {
        return this.expense_date;
    }

    @Override
    public String get_remarks() {
        return this.expense_remarks;
    }
}

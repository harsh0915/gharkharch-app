package com.itmart.gharkharch.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Expense implements Parcelable {

    private int expense_id;
    private String expense_name;
    private int sub_category_id;
    private int expense_amount;
    private long expense_date;
    private int expense_mode;

    public Expense() {

    }

    protected Expense(Parcel in) {
        expense_id = in.readInt();
        expense_name = in.readString();
        sub_category_id = in.readInt();
        expense_amount = in.readInt();
        expense_date = in.readLong();
        expense_mode = in.readInt();
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

    public int getSub_category_id() {
        return sub_category_id;
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
        dest.writeInt(sub_category_id);
        dest.writeInt(expense_amount);
        dest.writeLong(expense_date);
        dest.writeInt(expense_mode);
    }
}

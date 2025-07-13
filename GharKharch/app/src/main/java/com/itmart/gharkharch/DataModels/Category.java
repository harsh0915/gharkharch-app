package com.itmart.gharkharch.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.itmart.gharkharch.Adapters.SearchListAdapter;

public class Category implements Parcelable, SearchListAdapter.SearchableItem {

    private int category_id;
    private String category_name;

    public Category(int category_id, String category_name) {
        this.category_id = category_id;
        this.category_name = category_name;
    }

    protected Category(Parcel in) {
        category_id = in.readInt();
        category_name = in.readString();
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    public int getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(category_id);
        dest.writeString(category_name);
    }

    @Override
    public int get_item_id() {
        return category_id;
    }

    @Override
    public String get_item_name() {
        return category_name;
    }
}

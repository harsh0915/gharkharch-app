package com.itmart.gharkharch.DataModels;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.itmart.gharkharch.Adapters.SearchListAdapter;

public class SubCategory implements Parcelable, SubCatItem {

    private final int sub_category_id;
    private final String sub_category_name;
    private final int category_id;
    private final String under_cat_name;


    public SubCategory(int sub_category_id, String sub_category_name, int category_id, String under_cat_name) {
        this.sub_category_id = sub_category_id;
        this.sub_category_name = sub_category_name;
        this.category_id = category_id;
        this.under_cat_name = under_cat_name;
    }

    protected SubCategory(Parcel in) {
        sub_category_id = in.readInt();
        sub_category_name = in.readString();
        category_id = in.readInt();
        under_cat_name = in.readString();
    }

    public static final Creator<SubCategory> CREATOR = new Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel in) {
            return new SubCategory(in);
        }

        @Override
        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(sub_category_id);
        dest.writeString(sub_category_name);
        dest.writeInt(category_id);
        dest.writeString(under_cat_name);
    }

    public int getId() {
        return this.sub_category_id;
    }

    public String getName() {
        return this.sub_category_name;
    }

    public String getParentName() {
        return this.under_cat_name;
    }

    public int getParentId() {
        return this.category_id;
    }

    @Override
    public int get_item_id() {
        return sub_category_id;
    }

    @Override
    public int get_under_id() {
        return category_id;
    }

    @Override
    public String get_under_cat_name() {
        return under_cat_name;
    }

    @Override
    public String get_item_name() {
        return sub_category_name;
    }

}

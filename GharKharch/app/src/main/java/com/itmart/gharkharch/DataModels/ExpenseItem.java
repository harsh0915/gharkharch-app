package com.itmart.gharkharch.DataModels;

public interface ExpenseItem extends GenericItem {

    int get_under_id();

    int get_item_amount();

    int get_mode_of_payment();

    long get_date();

    String get_remarks();

}

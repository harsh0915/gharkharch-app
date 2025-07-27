package com.itmart.gharkharch.DataModels;

/*the generic item class is used to provide a common interface
 to get data from the data model classes
which allows a single adapter (simple adapter, search adapter) to handle different
different data models using a single generic item list i.e. List<GenericItem>*/
public interface GenericItem {
    int get_item_id();

    int get_under_id();

    String get_item_name();

}


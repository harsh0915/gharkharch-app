package com.itmart.gharkharch.DataModels;

import java.util.HashSet;
import java.util.List;

public class DatabaseCallbackObject {
    private List<GenericItem> data_list;
    private HashSet<String> data_fast_lookup;

    public DatabaseCallbackObject(List<GenericItem> data_list, HashSet<String> data_fast_lookup) {
        this.data_list = data_list;
        this.data_fast_lookup = data_fast_lookup;
    }

    public DatabaseCallbackObject(List<GenericItem> data_list) {
        this.data_list = data_list;
    }

    public DatabaseCallbackObject() {
    }

    public List<GenericItem> getDataList() {
        return data_list;
    }

    public HashSet<String> getDataFastLookup() {
        return data_fast_lookup;
    }
}

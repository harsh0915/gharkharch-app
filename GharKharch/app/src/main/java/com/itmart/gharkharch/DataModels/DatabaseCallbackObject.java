package com.itmart.gharkharch.DataModels;

import java.util.HashSet;
import java.util.List;

public class DatabaseCallbackObject{
    private List<GenericItem> filter_list;
    private List<GenericItem> original_list;
    private HashSet<String> original_fast_lookup;

    public DatabaseCallbackObject(List<GenericItem> filter_list, List<GenericItem> original_list, HashSet<String> original_fast_lookup) {
        this.filter_list = filter_list;
        this.original_list = original_list;
        this.original_fast_lookup = original_fast_lookup;
    }

    public DatabaseCallbackObject(List<GenericItem> filter_list, List<GenericItem> original_list) {
        this.filter_list = filter_list;
        this.original_list = original_list;
    }

    public DatabaseCallbackObject(List<GenericItem> filter_list) {
        this.filter_list = filter_list;
    }

    public DatabaseCallbackObject() {
    }

    public List<GenericItem> getFilter_list() {
        return filter_list;
    }

    public List<GenericItem> getOriginal_list() {
        return original_list;
    }

    public HashSet<String> getOriginal_fast_lookup() {
        return original_fast_lookup;
    }
}

package com.itmart.gharkharch.DataModels;

import java.util.List;

public class DBOpsForAnalysisCallbackObject {

    private final List<ItemForAnalysis> data_list;

    public DBOpsForAnalysisCallbackObject(List<ItemForAnalysis> data) {
        this.data_list = data;
    }

    public List<ItemForAnalysis> getData() {
        return this.data_list;
    }
}

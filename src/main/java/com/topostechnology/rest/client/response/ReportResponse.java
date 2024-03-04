package com.topostechnology.rest.client.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class ReportResponse<T> implements Serializable {
    private static final long serialVersionUID = 8676872919284689128L;

    private int recordsTotal;
    private int recordsFiltered;
    private List<T> data;

    public void setData(List<T> data) {
        if (data != null) {
            this.data = new ArrayList<>(data);
            this.recordsTotal = this.data.size();
            this.recordsFiltered = this.recordsTotal;
        }
    }
}

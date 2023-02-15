package com.isflee;

import java.util.List;

public class DzmzggBean {
    private String msg;
    private String total;
    private String code;
    private List<DzmzggDataBean> data;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DzmzggDataBean> getData() {
        return data;
    }

    public void setData(List<DzmzggDataBean> data) {
        this.data = data;
    }
}

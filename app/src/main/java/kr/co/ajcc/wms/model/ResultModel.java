package kr.co.ajcc.wms.model;

import java.io.Serializable;

public class ResultModel implements Serializable {
    public static final String SUCCESS = "0";
    String Flag;
    String MSG;

    public String getFlag() {
        return Flag;
    }

    public void setFlag(String flag) {
        Flag = flag;
    }

    public String getMSG() {
        return MSG;
    }

    public void setMSG(String MSG) {
        this.MSG = MSG;
    }
}
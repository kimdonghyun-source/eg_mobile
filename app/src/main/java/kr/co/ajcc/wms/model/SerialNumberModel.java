package kr.co.ajcc.wms.model;

import java.util.List;

public class SerialNumberModel extends ResultModel{
    String NewSerialNo;

    List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public String getNewSerialNo() {
        return NewSerialNo;
    }

    public void setNewSerialNo(String newSerialNo) {
        NewSerialNo = newSerialNo;
    }

    public class Items extends ResultModel {
        String NewSerialNo;

        public String getNewSerialNo() {
            return NewSerialNo;
        }

        public void setNewSerialNo(String newSerialNo) {
            NewSerialNo = newSerialNo;
        }
    }
}

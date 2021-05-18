package kr.co.bang.wms.model;


import java.util.List;

public class ResultBoxModel extends ResultModel {
    String BOX_SERIAL;

    List<Item>items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getBOX_SERIAL() {
        return BOX_SERIAL;
    }

    public void setBOX_SERIAL(String BOX_SERIAL) {
        this.BOX_SERIAL = BOX_SERIAL;
    }

    public class Item extends ResultModel{
        //박스시리얼
        String BOX_SERIAL;

        public String getBOX_SERIAL() {
            return BOX_SERIAL;
        }

        public void setBOX_SERIAL(String BOX_SERIAL) {
            this.BOX_SERIAL = BOX_SERIAL;
        }
    }
}
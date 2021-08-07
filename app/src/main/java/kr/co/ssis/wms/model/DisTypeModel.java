package kr.co.ssis.wms.model;

import java.util.List;

public class DisTypeModel extends ResultModel {
    List<DisTypeModel.Item>items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //실사종류
        String c_code;
        //실사명
        String c_name;

        public String getC_code() {
            return c_code;
        }

        public void setC_code(String c_code) {
            this.c_code = c_code;
        }

        public String getC_name() {
            return c_name;
        }

        public void setC_name(String c_name) {
            this.c_name = c_name;
        }
    }
}

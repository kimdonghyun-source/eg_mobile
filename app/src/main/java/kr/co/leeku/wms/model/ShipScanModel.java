package kr.co.leeku.wms.model;

import java.util.List;

public class ShipScanModel extends ResultModel {
    //출하등록 스캔 모델
    List<Item>items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{


        //스캔수량
        float scan_qty;
        //바코드
        String msg;
        //PLTNO
        String plt_no;
        //품명
        String fg_name;

        public float getScan_qty() {
            return scan_qty;
        }

        public void setScan_qty(float scan_qty) {
            this.scan_qty = scan_qty;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getPlt_no() {
            return plt_no;
        }

        public void setPlt_no(String plt_no) {
            this.plt_no = plt_no;
        }

        public String getFg_name() {
            return fg_name;
        }

        public void setFg_name(String fg_name) {
            this.fg_name = fg_name;
        }
    }
}

package kr.co.leeku.wms.model;

import java.util.List;

public class RemeltModel extends ResultModel {

    List<RemeltModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //품목명
        String fg_name;
        //스캔수량
        float scan_qty;
        //play_qty
        float plan_qty;
        //바코드번호
        String lbl_id;

        public String getFg_name() {
            return fg_name;
        }

        public void setFg_name(String fg_name) {
            this.fg_name = fg_name;
        }

        public float getScan_qty() {
            return scan_qty;
        }

        public void setScan_qty(float scan_qty) {
            this.scan_qty = scan_qty;
        }

        public float getPlan_qty() {
            return plan_qty;
        }

        public void setPlan_qty(float plan_qty) {
            this.plan_qty = plan_qty;
        }

        public String getLbl_id() {
            return lbl_id;
        }

        public void setLbl_id(String lbl_id) {
            this.lbl_id = lbl_id;
        }
    }
}

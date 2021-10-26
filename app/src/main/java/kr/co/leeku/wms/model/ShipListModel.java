package kr.co.leeku.wms.model;

import java.util.List;

public class ShipListModel extends ResultModel {
    List<Item>items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //품명
        String fg_name;
        //cst_stock_name
        String cst_stock_name;
        //의뢰수량
        float sp_qty;
        //재고수량
        float stock_qty;
        //스캔수량
        float scan_qty;

        public String getFg_name() {
            return fg_name;
        }

        public void setFg_name(String fg_name) {
            this.fg_name = fg_name;
        }

        public String getCst_stock_name() {
            return cst_stock_name;
        }

        public void setCst_stock_name(String cst_stock_name) {
            this.cst_stock_name = cst_stock_name;
        }

        public float getSp_qty() {
            return sp_qty;
        }

        public void setSp_qty(float sp_qty) {
            this.sp_qty = sp_qty;
        }

        public float getStock_qty() {
            return stock_qty;
        }

        public void setStock_qty(float stock_qty) {
            this.stock_qty = stock_qty;
        }

        public void setStock_qty(int stock_qty) {
            this.stock_qty = stock_qty;
        }

        public float getScan_qty() {
            return scan_qty;
        }

        public void setScan_qty(float scan_qty) {
            this.scan_qty = scan_qty;
        }
    }
}

package kr.co.leeku.wms.model;

import java.util.List;

public class ShipListPcodeModel extends ResultModel{
    List<ShipListPcodeModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //바코드
        String barcode;
        //수량
        float qty;

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public float getQty() {
            return qty;
        }

        public void setQty(float qty) {
            this.qty = qty;
        }
    }
}

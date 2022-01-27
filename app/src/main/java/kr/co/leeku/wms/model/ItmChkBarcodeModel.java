package kr.co.leeku.wms.model;

import java.util.List;

public class ItmChkBarcodeModel extends ResultModel {

    List<ItmChkBarcodeModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //바코드번호
        String barcode;

        public String getBarcode() {
            return barcode;
        }

        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }
    }
}

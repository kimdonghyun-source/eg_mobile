package kr.co.ajcc.wms.model;

import java.util.List;

public class PalletSnanModel extends ResultModel{
    List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public class Items extends ResultModel {
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //규격
        String itm_size;
        //수량
        String pallet_qty;

        public String getItm_code() {
            return itm_code;
        }

        public void setItm_code(String itm_code) {
            this.itm_code = itm_code;
        }

        public String getItm_name() {
            return itm_name;
        }

        public void setItm_name(String itm_name) {
            this.itm_name = itm_name;
        }

        public String getItm_size() {
            return itm_size;
        }

        public void setItm_size(String itm_size) {
            this.itm_size = itm_size;
        }

        public String getPallet_qty() {
            return pallet_qty;
        }

        public void setPallet_qty(String pallet_qty) {
            this.pallet_qty = pallet_qty;
        }
    }
}

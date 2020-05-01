package kr.co.ajcc.wms.model;

import java.util.List;

public class LotItemsModel extends ResultModel{
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
        //단위
        String itm_unit;
        //재고수량
        long inv_qty;
        //사용자 입력값
        long input_qty;

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

        public String getItm_unit() {
            return itm_unit;
        }

        public void setItm_unit(String itm_unit) {
            this.itm_unit = itm_unit;
        }

        public long getInv_qty() {
            return inv_qty;
        }

        public void setInv_qty(long inv_qty) {
            this.inv_qty = inv_qty;
        }

        public long getInput_qty() {
            return input_qty;
        }

        public void setInput_qty(long input_qty) {
            this.input_qty = input_qty;
        }
    }
}

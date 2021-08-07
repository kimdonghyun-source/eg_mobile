package kr.co.ssis.wms.model;

import java.util.List;

public class MoveAskModel extends ResultModel {
    List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //입력된 수량(서버에서 받는게 아님)
        int input_qty;

        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //입고처재고
        int inv_qty_in;
        //출고처재고
        int inv_qty_out;


        public int getInput_qty() {
            return input_qty;
        }

        public void setInput_qty(int input_qty) {
            this.input_qty = input_qty;
        }

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

        public int getInv_qty_in() {
            return inv_qty_in;
        }

        public void setInv_qty_in(int inv_qty_in) {
            this.inv_qty_in = inv_qty_in;
        }

        public int getInv_qty_out() {
            return inv_qty_out;
        }

        public void setInv_qty_out(int inv_qty_out) {
            this.inv_qty_out = inv_qty_out;
        }
    }
}

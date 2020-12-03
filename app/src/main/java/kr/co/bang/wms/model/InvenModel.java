package kr.co.bang.wms.model;

import java.util.List;

public class InvenModel extends ResultModel {
    List<Inven> items;

    public List<Inven> getItems() {
        return items;
    }

    public void setItems(List<Inven> items) {
        this.items = items;
    }

    public class Inven extends ResultModel{
        //시리얼번호
        String serial_no;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //창고코드
        String wh_code;
        //창고명
        String wh_name;
        //재고수량
        int stk_qty;
        //Y/N 구분값
        String picking_yn;

        public String getSerial_no() {
            return serial_no;
        }

        public void setSerial_no(String serial_no) {
            this.serial_no = serial_no;
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

        public String getWh_code() {
            return wh_code;
        }

        public void setWh_code(String wh_code) {
            this.wh_code = wh_code;
        }

        public String getWh_name() {
            return wh_name;
        }

        public void setWh_name(String wh_name) {
            this.wh_name = wh_name;
        }

        public int getStk_qty() {
            return stk_qty;
        }

        public void setStk_qty(int stk_qty) {
            this.stk_qty = stk_qty;
        }

        public String getPicking_yn() {
            return picking_yn;
        }

        public void setPicking_yn(String picking_yn) {
            this.picking_yn = picking_yn;
        }
    }

}

package kr.co.ssis.wms.model;

import java.util.List;

public class MorSerialScan extends ResultModel{
    List<Items> items;

    public List<Items> getItems(){
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public class Items extends ResultModel{
        //시리얼번호
        String serial_no;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //출고수량
        int serial_qty;
        //입고처
        String wh_code_in;
        //구분값
        String itm_id;

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

        public int getSerial_qty() {
            return serial_qty;
        }

        public void setSerial_qty(int serial_qty) {
            this.serial_qty = serial_qty;
        }

        public String getWh_code_in() {
            return wh_code_in;
        }

        public void setWh_code_in(String wh_code_in) {
            this.wh_code_in = wh_code_in;
        }

        public String getItm_id() {
            return itm_id;
        }

        public void setItm_id(String itm_id) {
            this.itm_id = itm_id;
        }
    }
}

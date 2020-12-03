package kr.co.bang.wms.model;

import java.util.List;

public class MatMoveModel extends ResultModel{
    List<Item> items;

    public List<Item> getItems(){
        return items;
    }

    public void setItems(List<Item> items){
        this.items = items;
    }

    public class Item extends ResultModel{
        //시리얼구분(품목=1, 박스=2)
        String gbn;
        //시리얼번호
        String serial_no;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //이동수량
        int move_qty;

        public String getGbn() {
            return gbn;
        }

        public void setGbn(String gbn) {
            this.gbn = gbn;
        }

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

        public int getMove_qty() {
            return move_qty;
        }

        public void setMove_qty(int move_qty) {
            this.move_qty = move_qty;
        }
    }

}

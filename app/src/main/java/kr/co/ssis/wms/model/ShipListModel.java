package kr.co.ssis.wms.model;

import java.util.List;

public class ShipListModel extends ResultModel {
    List<ShipItem> items;

    public List<ShipItem> getItems() {
        return items;
    }

    public void setItems(List<ShipItem> items) {
        this.items = items;
    }

    public class ShipItem extends ResultModel {
        //사업장코드
        String corp_code;
        //내수구분
        String sal_id;
        //출하일자
        String ship_date;
        //출하번호1
        int ship_no1;
        //출하번호2
        int ship_no2;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //출하수량
        int ship_qty;
        //규격
        String itm_size;
        //단위
        String c_name;
        //스캔수량
        int scan_qty;
        //스캔넣는수량
        int set_scan_qty;
        //시리얼번호
        String serial_no;
        //창고코드
        String wh_code;

        //출하피킹 스캔 수량
        List<ShipOkModel.Item> items;


        public List<ShipOkModel.Item> getItems() {
            return items;
        }

        public void setItems(List<ShipOkModel.Item> items) {
            this.items = items;
        }

        public String getCorp_code() {
            return corp_code;
        }

        public void setCorp_code(String corp_code) {
            this.corp_code = corp_code;
        }

        public String getSal_id() {
            return sal_id;
        }

        public void setSal_id(String sal_id) {
            this.sal_id = sal_id;
        }

        public String getShip_date() {
            return ship_date;
        }

        public void setShip_date(String ship_date) {
            this.ship_date = ship_date;
        }

        public int getShip_no1() {
            return ship_no1;
        }

        public void setShip_no1(int ship_no1) {
            this.ship_no1 = ship_no1;
        }

        public int getShip_no2() {
            return ship_no2;
        }

        public void setShip_no2(int ship_no2) {
            this.ship_no2 = ship_no2;
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

        public int getShip_qty() {
            return ship_qty;
        }

        public void setShip_qty(int ship_qty) {
            this.ship_qty = ship_qty;
        }

        public String getItm_size() {
            return itm_size;
        }

        public void setItm_size(String itm_size) {
            this.itm_size = itm_size;
        }

        public String getC_name() {
            return c_name;
        }

        public void setC_name(String c_name) {
            this.c_name = c_name;
        }

        public int getScan_qty() {
            return scan_qty;
        }

        public void setScan_qty(int scan_qty) {
            this.scan_qty = scan_qty;
        }

        public int getSet_scan_qty() {
            return set_scan_qty;
        }

        public void setSet_scan_qty(int set_scan_qty) {
            this.set_scan_qty = set_scan_qty;
        }

        public String getSerial_no() {
            return serial_no;
        }

        public void setSerial_no(String serial_no) {
            this.serial_no = serial_no;
        }

        public String getWh_code() {
            return wh_code;
        }

        public void setWh_code(String wh_code) {
            this.wh_code = wh_code;
        }
    }
}

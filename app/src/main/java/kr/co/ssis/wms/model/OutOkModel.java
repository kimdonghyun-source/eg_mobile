package kr.co.ssis.wms.model;

import java.util.List;

public class OutOkModel extends ResultModel {
    List<OutOkModel.Item>items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //발주번호
        String bor_code;
        //입고처
        String wh_code;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //규격
        String itm_size;
        //단위
        String itm_unit;
        //수량
        int tin_qty;
        //사급일자
        String mat_out_date;
        //사급상태
        String mat_status;

        public String getBor_code() {
            return bor_code;
        }

        public void setBor_code(String bor_code) {
            this.bor_code = bor_code;
        }

        public String getWh_code() {
            return wh_code;
        }

        public void setWh_code(String wh_code) {
            this.wh_code = wh_code;
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

        public int getTin_qty() {
            return tin_qty;
        }

        public void setTin_qty(int tin_qty) {
            this.tin_qty = tin_qty;
        }

        public String getMat_out_date() {
            return mat_out_date;
        }

        public void setMat_out_date(String mat_out_date) {
            this.mat_out_date = mat_out_date;
        }

        public String getMat_status() {
            return mat_status;
        }

        public void setMat_status(String mat_status) {
            this.mat_status = mat_status;
        }
    }
}

package kr.co.ssis.wms.model;

import java.util.List;

public class OutInModel extends ResultModel {
    List<OutInModel.Item> items;

    public List<OutInModel.Item> getItems() {
        return items;
    }

    public void setItems(List<OutInModel.Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{

        //거래처코드
        String cst_code;
        //거래처명
        String cst_name;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //규격
        String itm_size;
        //단위
        String c_name;
        //입고량
        int tin_qty;
        //발주번호
        String bor_code;
        //일자
        String bor_date;
        //발주순번1
        String bor_no1;
        //발주순번2
        String bor_no2;
        //LOT수량
        int tin_dtl_qty;
        //LOT번호
        String lot_no;
        //사업자
        String corp_code;
        //내수구분
        String tin_id;
        //SCM일자
        String tin_date;
        //SCM순번1
        int tin_no1;
        //SCM순번2
        int tin_no2;
        //SCM순번3
        int tin_no3;


        public String getCst_code() {
            return cst_code;
        }

        public void setCst_code(String cst_code) {
            this.cst_code = cst_code;
        }

        public String getCst_name() {
            return cst_name;
        }

        public void setCst_name(String cst_name) {
            this.cst_name = cst_name;
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

        public String getC_name() {
            return c_name;
        }

        public void setC_name(String c_name) {
            this.c_name = c_name;
        }

        public int getTin_qty() {
            return tin_qty;
        }

        public void setTin_qty(int tin_qty) {
            this.tin_qty = tin_qty;
        }

        public String getBor_code() {
            return bor_code;
        }

        public void setBor_code(String bor_code) {
            this.bor_code = bor_code;
        }

        public String getBor_no1() {
            return bor_no1;
        }

        public void setBor_no1(String bor_no1) {
            this.bor_no1 = bor_no1;
        }

        public String getBor_no2() {
            return bor_no2;
        }

        public void setBor_no2(String bor_no2) {
            this.bor_no2 = bor_no2;
        }

        public int getTin_dtl_qty() {
            return tin_dtl_qty;
        }

        public void setTin_dtl_qty(int tin_dtl_qty) {
            this.tin_dtl_qty = tin_dtl_qty;
        }

        public String getLot_no() {
            return lot_no;
        }

        public void setLot_no(String lot_no) {
            this.lot_no = lot_no;
        }

        public String getBor_date() {
            return bor_date;
        }

        public void setBor_date(String bor_date) {
            this.bor_date = bor_date;
        }

        public String getCorp_code() {
            return corp_code;
        }

        public void setCorp_code(String corp_code) {
            this.corp_code = corp_code;
        }

        public String getTin_id() {
            return tin_id;
        }

        public void setTin_id(String tin_id) {
            this.tin_id = tin_id;
        }

        public String getTin_date() {
            return tin_date;
        }

        public void setTin_date(String tin_date) {
            this.tin_date = tin_date;
        }

        public int getTin_no1() {
            return tin_no1;
        }

        public void setTin_no1(int tin_no1) {
            this.tin_no1 = tin_no1;
        }

        public int getTin_no2() {
            return tin_no2;
        }

        public void setTin_no2(int tin_no2) {
            this.tin_no2 = tin_no2;
        }

        public int getTin_no3() {
            return tin_no3;
        }

        public void setTin_no3(int tin_no3) {
            this.tin_no3 = tin_no3;
        }
    }
}


package kr.co.ssis.wms.model;

import java.util.List;

public class MatOutDetailModel extends ResultModel {
    List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel {

        //사업장코드
        String corp_code;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //스캔수량
        int scan_qty;
        //요청수량
        int req_mat_qty;

        public String getCorp_code() {
            return corp_code;
        }

        public void setCorp_code(String corp_code) {
            this.corp_code = corp_code;
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

        public int getScan_qty() {
            return scan_qty;
        }

        public void setScan_qty(int scan_qty) {
            this.scan_qty = scan_qty;
        }

        public int getReq_mat_qty() {
            return req_mat_qty;
        }

        public void setReq_mat_qty(int req_mat_qty) {
            this.req_mat_qty = req_mat_qty;
        }

        List<MatOutDetailModel.Item> items;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }
}

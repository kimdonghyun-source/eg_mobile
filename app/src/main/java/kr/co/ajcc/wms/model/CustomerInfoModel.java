package kr.co.ajcc.wms.model;

import java.util.List;

public class CustomerInfoModel extends ResultModel{
    List<CustomerInfo> items;

    public List<CustomerInfo> getItems() {
        return items;
    }

    public void setItems(List<CustomerInfo> items) {
        this.items = items;
    }

    public class CustomerInfo extends ResultModel {
        //품목순번(출하의뢰품목순번)
        String req_car_no;
        //품목코드
        String cst_code;
        //품목명
        String cst_name;
        //출고의뢰수량
        float box_qty;
        //규격
        String po_no;
        //자재불출수량
        float req_qty;

        public String getReq_car_no() {
            return req_car_no;
        }

        public void setReq_car_no(String req_car_no) {
            this.req_car_no = req_car_no;
        }

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

        public float getBox_qty() {
            return box_qty;
        }

        public void setBox_qty(float box_qty) {
            this.box_qty = box_qty;
        }

        public String getPo_no() {
            return po_no;
        }

        public void setPo_no(String po_no) {
            this.po_no = po_no;
        }

        public float getReq_qty() {
            return req_qty;
        }

        public void setReq_qty(float req_qty) {
            this.req_qty = req_qty;
        }
    }
}

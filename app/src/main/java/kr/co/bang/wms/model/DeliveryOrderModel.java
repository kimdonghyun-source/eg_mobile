package kr.co.bang.wms.model;

import java.util.List;

public class DeliveryOrderModel extends ResultModel{
    List<DeliveryOrder> items;

    public List<DeliveryOrder> getItems() {
        return items;
    }

    public void setItems(List<DeliveryOrder> items) {
        this.items = items;
    }

    public class DeliveryOrder extends ResultModel {
        //품목순번(출하의뢰품목순번)
        int req_no2;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //출고의뢰수량
        float box_qty;
        //규격
        String itm_size;

        String cst_code;
        String cst_name;
        String po_no;

        //자재불출수량
        float req_qty;

        List<PalletSnanModel.Items> items;

        public int getReq_no2() {
            return req_no2;
        }

        public void setReq_no2(int req_no2) {
            this.req_no2 = req_no2;
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

        public float getBox_qty() {
            return box_qty;
        }

        public void setBox_qty(float box_qty) {
            this.box_qty = box_qty;
        }

        public String getItm_size() {
            return itm_size;
        }

        public void setItm_size(String itm_size) {
            this.itm_size = itm_size;
        }

        public float getReq_qty() {
            return req_qty;
        }

        public void setReq_qty(float req_qty) {
            this.req_qty = req_qty;
        }

        public List<PalletSnanModel.Items> getItems() {
            return items;
        }

        public void setItems(List<PalletSnanModel.Items> items) {
            this.items = items;
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

        public String getPo_no() {
            return po_no;
        }

        public void setPo_no(String po_no) {
            this.po_no = po_no;
        }
    }
}

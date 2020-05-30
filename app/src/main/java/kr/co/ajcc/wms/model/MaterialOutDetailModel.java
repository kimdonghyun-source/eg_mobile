package kr.co.ajcc.wms.model;

import java.util.List;

public class MaterialOutDetailModel extends ResultModel {
    List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public class Items extends ResultModel {
        //자재불출품목순번
        int out_no2;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //규격
        String itm_size;
        //자재불출수량
        float req_qty;

        public int getOut_no2() {
            return out_no2;
        }

        public void setOut_no2(int out_no2) {
            this.out_no2 = out_no2;
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

        public float getReq_qty() {
            return req_qty;
        }

        public void setReq_qty(float req_qty) {
            this.req_qty = req_qty;
        }
    }
}

package kr.co.bang.wms.model;

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

        //공정코드
        String wh_code_in;
        //공정명
        String wh_name_in;
        //창고코드
        String wh_code_out;
        //창고명
        String wh_name_out;

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

        List<MaterialLocAndLotModel.Items> items;

        public List<MaterialLocAndLotModel.Items> getItems() {
            return items;
        }

        public void setItems(List<MaterialLocAndLotModel.Items> items) {
            this.items = items;
        }

        public String getWh_code_in() {
            return wh_code_in;
        }

        public void setWh_code_in(String wh_code_in) {
            this.wh_code_in = wh_code_in;
        }

        public String getWh_name_in() {
            return wh_name_in;
        }

        public void setWh_name_in(String wh_name_in) {
            this.wh_name_in = wh_name_in;
        }

        public String getWh_code_out() {
            return wh_code_out;
        }

        public void setWh_code_out(String wh_code_out) {
            this.wh_code_out = wh_code_out;
        }

        public String getWh_name_out() {
            return wh_name_out;
        }

        public void setWh_name_out(String wh_name_out) {
            this.wh_name_out = wh_name_out;
        }
    }
}

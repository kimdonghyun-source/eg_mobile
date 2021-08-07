package kr.co.ssis.wms.model;

import java.util.List;

public class MatOutDetailDel extends ResultModel {
    List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //Mac주소
        String p_mad_ad;
        //요청코드
        String p_req_mat_code;

        public String getP_mad_ad() {
            return p_mad_ad;
        }

        public void setP_mad_ad(String p_mad_ad) {
            this.p_mad_ad = p_mad_ad;
        }

        public String getP_req_mat_code() {
            return p_req_mat_code;
        }

        public void setP_req_mat_code(String p_req_mat_code) {
            this.p_req_mat_code = p_req_mat_code;
        }
    }
}

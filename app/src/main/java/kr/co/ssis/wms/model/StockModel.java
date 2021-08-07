package kr.co.ssis.wms.model;

import java.util.List;

public class StockModel extends ResultModel {
    List<stockModel> items;

    public List<stockModel> getItems() {
        return items;
    }

    public void setItems(List<stockModel> items) {
        this.items = items;
    }

    public class stockModel extends ResultModel{
        //사업장
        String corp_code;
        //조사일자
        String stk_date;
        //순번
        int stk_no1;
        //창고
        String wh_code;
        //창고명
        String wh_name;
        //비고
        String remark;

        public String getCorp_code() {
            return corp_code;
        }

        public void setCorp_code(String corp_code) {
            this.corp_code = corp_code;
        }

        public String getStk_date() {
            return stk_date;
        }

        public void setStk_date(String stk_date) {
            this.stk_date = stk_date;
        }

        public int getStk_no1() {
            return stk_no1;
        }

        public void setStk_no1(int stk_no1) {
            this.stk_no1 = stk_no1;
        }

        public String getWh_code() {
            return wh_code;
        }

        public void setWh_code(String wh_code) {
            this.wh_code = wh_code;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getWh_name() {
            return wh_name;
        }

        public void setWh_name(String wh_name) {
            this.wh_name = wh_name;
        }
    }

}

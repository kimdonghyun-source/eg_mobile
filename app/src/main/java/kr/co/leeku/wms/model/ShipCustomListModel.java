package kr.co.leeku.wms.model;

import java.util.List;

public class ShipCustomListModel extends ResultModel{
    List<Item>items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //플랜일자
        String plan_date;
        //거래처코드
        String cst_code;
        //거래처명
        String cst_name;
        //deli_place
        String deli_place;

        public String getPlan_date() {
            return plan_date;
        }

        public void setPlan_date(String plan_date) {
            this.plan_date = plan_date;
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

        public String getDeli_place() {
            return deli_place;
        }

        public void setDeli_place(String deli_place) {
            this.deli_place = deli_place;
        }
    }
}

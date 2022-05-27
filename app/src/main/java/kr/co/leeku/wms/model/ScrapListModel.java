package kr.co.leeku.wms.model;

import java.util.List;

public class ScrapListModel extends ResultModel {

    List<ScrapListModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //스크랩번호
        String scrap_no;
        //입력날짜
        String scrap_dt;
        //품목id
        int cmp_id;
        //품목명
        String cmp_nm;
        //도금코드
        int dogum;
        //도금명
        String dogum_nm;
        //중량
        int cnt;
        //위치
        String location;

        public String getScrap_no() {
            return scrap_no;
        }

        public void setScrap_no(String scrap_no) {
            this.scrap_no = scrap_no;
        }

        public String getScrap_dt() {
            return scrap_dt;
        }

        public void setScrap_dt(String scrap_dt) {
            this.scrap_dt = scrap_dt;
        }

        public int getCmp_id() {
            return cmp_id;
        }

        public void setCmp_id(int cmp_id) {
            this.cmp_id = cmp_id;
        }

        public String getCmp_nm() {
            return cmp_nm;
        }

        public void setCmp_nm(String cmp_nm) {
            this.cmp_nm = cmp_nm;
        }

        public int getDogum() {
            return dogum;
        }

        public void setDogum(int dogum) {
            this.dogum = dogum;
        }

        public String getDogum_nm() {
            return dogum_nm;
        }

        public void setDogum_nm(String dogum_nm) {
            this.dogum_nm = dogum_nm;
        }

        public int getCnt() {
            return cnt;
        }

        public void setCnt(int cnt) {
            this.cnt = cnt;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
}

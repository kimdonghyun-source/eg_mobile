package kr.co.leeku.wms.model;

import java.util.List;

public class LabelComboModel extends ResultModel {

    List<LabelComboModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //저울코드
        int scale_id;
        //중량
        float scale;
        //품명
        String fg_name;
        //전ERP품목코드
        int cmp_id;
        //황동구분
        int cmp_rnk;
        //도금코드
        int dogum;
        //도금명
        String dogum_nm;

        public int getScale_id() {
            return scale_id;
        }

        public void setScale_id(int scale_id) {
            this.scale_id = scale_id;
        }

        public float getScale() {
            return scale;
        }

        public void setScale(float scale) {
            this.scale = scale;
        }

        public String getFg_name() {
            return fg_name;
        }

        public void setFg_name(String fg_name) {
            this.fg_name = fg_name;
        }

        public int getCmp_id() {
            return cmp_id;
        }

        public void setCmp_id(int cmp_id) {
            this.cmp_id = cmp_id;
        }

        public int getCmp_rnk() {
            return cmp_rnk;
        }

        public void setCmp_rnk(int cmp_rnk) {
            this.cmp_rnk = cmp_rnk;
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
    }
}

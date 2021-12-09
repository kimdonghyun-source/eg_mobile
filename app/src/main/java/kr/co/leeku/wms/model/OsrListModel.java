package kr.co.leeku.wms.model;

import java.util.List;

public class OsrListModel extends ResultModel {
    List<OsrListModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //전표번호
        String ood_dmd_no;
        //유형
        String dvr_type;
        //입고처코드
        String ood_code;
        //입고처명
        String ood_name;
        //거래처코드
        String cst_code;
        //거래처명
        String cst_name;
        //품명
        String fg_name;
        //의뢰중량
        float dmd_wht;
        //요구사항
        String ood_dmd_etr;

        public String getOod_dmd_no() {
            return ood_dmd_no;
        }

        public void setOod_dmd_no(String ood_dmd_no) {
            this.ood_dmd_no = ood_dmd_no;
        }

        public String getDvr_type() {
            return dvr_type;
        }

        public void setDvr_type(String dvr_type) {
            this.dvr_type = dvr_type;
        }

        public String getOod_code() {
            return ood_code;
        }

        public void setOod_code(String ood_code) {
            this.ood_code = ood_code;
        }

        public String getOod_name() {
            return ood_name;
        }

        public void setOod_name(String ood_name) {
            this.ood_name = ood_name;
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

        public String getFg_name() {
            return fg_name;
        }

        public void setFg_name(String fg_name) {
            this.fg_name = fg_name;
        }

        public float getDmd_wht() {
            return dmd_wht;
        }

        public void setDmd_wht(float dmd_wht) {
            this.dmd_wht = dmd_wht;
        }

        public String getOod_dmd_etr() {
            return ood_dmd_etr;
        }

        public void setOod_dmd_etr(String ood_dmd_etr) {
            this.ood_dmd_etr = ood_dmd_etr;
        }
    }
}

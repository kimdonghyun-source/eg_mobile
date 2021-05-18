package kr.co.bang.wms.model;

import java.util.List;

public class MatOutDetailGet extends ResultModel {
    List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{

        //이동요청번호
        String req_mat_code;
        //사업장코드
        String corp_code;
        //인수자
        String in_emp_code;
        //출고자
        String emp_code;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //시리얼번호
        String serial_no;
        //이동수량
        int req_mat_qty;

        public String getReq_mat_code() {
            return req_mat_code;
        }

        public void setReq_mat_code(String req_mat_code) {
            this.req_mat_code = req_mat_code;
        }

        public String getCorp_code() {
            return corp_code;
        }

        public void setCorp_code(String corp_code) {
            this.corp_code = corp_code;
        }

        public String getIn_emp_code() {
            return in_emp_code;
        }

        public void setIn_emp_code(String in_emp_code) {
            this.in_emp_code = in_emp_code;
        }

        public String getEmp_code() {
            return emp_code;
        }

        public void setEmp_code(String emp_code) {
            this.emp_code = emp_code;
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

        public String getSerial_no() {
            return serial_no;
        }

        public void setSerial_no(String serial_no) {
            this.serial_no = serial_no;
        }

        public int getReq_mat_qty() {
            return req_mat_qty;
        }

        public void setReq_mat_qty(int req_mat_qty) {
            this.req_mat_qty = req_mat_qty;
        }
    }
}

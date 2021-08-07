package kr.co.ssis.wms.model;

import java.util.List;

public class MatOutListModel extends ResultModel {
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
        //이동요청일자
        String req_mat_date;
        //이동요청부서코드
        String dpt_code;
        //이동요청부서명
        String dpt_name;
        //이동요청자코드
        String emp_code;
        //이동요청자명
        String emp_name;

        public String getReq_mat_code() {
            return req_mat_code;
        }

        public void setReq_mat_code(String req_mat_code) {
            this.req_mat_code = req_mat_code;
        }

        public String getReq_mat_date() {
            return req_mat_date;
        }

        public void setReq_mat_date(String req_mat_date) {
            this.req_mat_date = req_mat_date;
        }

        public String getDpt_code() {
            return dpt_code;
        }

        public void setDpt_code(String dpt_code) {
            this.dpt_code = dpt_code;
        }

        public String getDpt_name() {
            return dpt_name;
        }

        public void setDpt_name(String dpt_name) {
            this.dpt_name = dpt_name;
        }

        public String getEmp_code() {
            return emp_code;
        }

        public void setEmp_code(String emp_code) {
            this.emp_code = emp_code;
        }

        public String getEmp_name() {
            return emp_name;
        }

        public void setEmp_name(String emp_name) {
            this.emp_name = emp_name;
        }
    }
}

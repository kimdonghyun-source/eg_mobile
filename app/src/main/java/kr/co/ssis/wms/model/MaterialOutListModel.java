package kr.co.ssis.wms.model;

import java.util.List;

public class MaterialOutListModel extends ResultModel{
    List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public class Items extends ResultModel {
        //불출지시번호
        String out_slip_no;
        //입고처창고코드
        String wh_code_in;
        //입고처창고명
        String wh_name_in;
        //출고처창고코드
        String wh_code_out;
        //출고처창고명
        String wh_name_out;

        public String getOut_slip_no() {
            return out_slip_no;
        }

        public void setOut_slip_no(String out_slip_no) {
            this.out_slip_no = out_slip_no;
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

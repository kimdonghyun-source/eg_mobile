package kr.co.ssis.wms.model;

import java.util.List;

public class LogSearchModel extends ResultModel{
    List<LogSearchModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //일자
        String itm_date;
        //품목명
        String itm_name;
        //입고
        int in_qty;
        //출고
        int out_qty;

        public String getItm_date() {
            return itm_date;
        }

        public void setItm_date(String itm_date) {
            this.itm_date = itm_date;
        }

        public String getItm_name() {
            return itm_name;
        }

        public void setItm_name(String itm_name) {
            this.itm_name = itm_name;
        }

        public int getIn_qty() {
            return in_qty;
        }

        public void setIn_qty(int in_qty) {
            this.in_qty = in_qty;
        }

        public int getOut_qty() {
            return out_qty;
        }

        public void setOut_qty(int out_qty) {
            this.out_qty = out_qty;
        }
    }
}

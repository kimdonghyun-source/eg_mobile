package kr.co.ssis.wms.model;

import java.util.List;

public class LogQtySearchModel extends ResultModel {
    List<LogQtySearchModel.Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public class Item extends ResultModel{
        //품목코드
        String itm_date;
        //수량
        int inv_qty;

        public String getItm_date() {
            return itm_date;
        }

        public void setItm_date(String itm_date) {
            this.itm_date = itm_date;
        }

        public int getInv_qty() {
            return inv_qty;
        }

        public void setInv_qty(int inv_qty) {
            this.inv_qty = inv_qty;
        }
    }
}

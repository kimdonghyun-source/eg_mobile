package kr.co.ssis.wms.model;

import java.util.List;

public class LocationModel extends ResultModel{
    List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public class Items extends ResultModel {
        //창고코드
        String wh_code;
        //창고명
        String wh_name;
        //로케이션코드
        String location_code;
        //로케이션명
        String location_name;
        //사용유무
        String use_flag;
        //로케이션적재수량
        int loc_stk_cnt;
        //로케이션총적재가능수량
        int loc_tot_cnt;

        public String getWh_code() {
            return wh_code;
        }

        public void setWh_code(String wh_code) {
            this.wh_code = wh_code;
        }

        public String getWh_name() {
            return wh_name;
        }

        public void setWh_name(String wh_name) {
            this.wh_name = wh_name;
        }

        public String getLocation_code() {
            return location_code;
        }

        public void setLocation_code(String location_code) {
            this.location_code = location_code;
        }

        public String getLocation_name() {
            return location_name;
        }

        public void setLocation_name(String location_name) {
            this.location_name = location_name;
        }

        public String getUse_flag() {
            return use_flag;
        }

        public void setUse_flag(String use_flag) {
            this.use_flag = use_flag;
        }

        public int getLoc_stk_cnt() {
            return loc_stk_cnt;
        }

        public void setLoc_stk_cnt(int loc_stk_cnt) {
            this.loc_stk_cnt = loc_stk_cnt;
        }

        public int getLoc_tot_cnt() {
            return loc_tot_cnt;
        }

        public void setLoc_tot_cnt(int loc_tot_cnt) {
            this.loc_tot_cnt = loc_tot_cnt;
        }
    }
}

package kr.co.bang.wms.model;

import java.util.List;

public class MorListModel extends ResultModel {
    List<Items> items;

    public List<Items> getItems() {
        return items;
    }

    public void setItems(List<Items> items) {
        this.items = items;
    }

    public class Items extends ResultModel {
        //사업장번호
        String corp_code;
        //대리점 주문일자
        String ord_date;
        //주문일자
        String mor_date;
        //거래처코드/회원코드
        String cst_code;
        //거래처명/회원명
        String cst_name;
        //주문번호
        String slip_no;
        //전표타입
        String slip_type;
        //주문수량
        int mor_qty;
        //클럽유형
        String gk_club_work;
        //품목코드
        String itm_code;
        //품목명
        String itm_name;
        //규격
        String itm_size;
        //헤드모델코드
        String h_code;
        //헤드모델명
        String h_name;
        //헤드요청수량
        int mor_h_qty;
        //헤드색상코드
        String h_color_code;
        //헤드색상명
        String h_color_name;
        //헤드각도
        String h_loft_code;
        //헤드각도명
        String h_loft_name;
        //헤드방향코드
        String haed_direc_code;
        //헤드방향명
        String haed_direc_name;
        //헤드코드
        String head_code;
        //헤드코드명
        String head_code_name;
        //헤드무게코드
        String h_weight_code;
        //헤드무게명
        String h_weight_name;
        //샤프트코드
        String s_code;
        //샤프트명
        String s_name;
        //샤프트색상코드
        String s_color_code;
        //샤프트색상명
        String s_color_name;
        //샤프트각도코드
        String s_strong_code;
        //샤프트각도명
        String s_strong_name;

        //샤프트요청수량
        int mor_s_qty;
        //주문자재출고일자
        //String mor_date;
        //주문자재출고순번1
        int mor_no1;
        //수주번호
        String ord_code;
        //수주순번
        int ord_no1;


        public String getCorp_code() {
            return corp_code;
        }

        public void setCorp_code(String corp_code) {
            this.corp_code = corp_code;
        }

        public String getOrd_date() {
            return ord_date;
        }

        public void setOrd_date(String ord_date) {
            this.ord_date = ord_date;
        }

        public String getMor_date() {
            return mor_date;
        }

        public void setMor_date(String mor_date) {
            this.mor_date = mor_date;
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

        public String getSlip_no() {
            return slip_no;
        }

        public void setSlip_no(String slip_no) {
            this.slip_no = slip_no;
        }

        public String getSlip_type() {
            return slip_type;
        }

        public void setSlip_type(String slip_type) {
            this.slip_type = slip_type;
        }

        public int getMor_qty() {
            return mor_qty;
        }

        public void setMor_qty(int mor_qty) {
            this.mor_qty = mor_qty;
        }

        public String getGk_club_work() {
            return gk_club_work;
        }

        public void setGk_club_work(String gk_club_work) {
            this.gk_club_work = gk_club_work;
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

        public String getItm_size() {
            return itm_size;
        }

        public void setItm_size(String itm_size) {
            this.itm_size = itm_size;
        }

        public String getH_code() {
            return h_code;
        }

        public void setH_code(String h_code) {
            this.h_code = h_code;
        }

        public String getH_name() {
            return h_name;
        }

        public void setH_name(String h_name) {
            this.h_name = h_name;
        }

        public int getMor_h_qty() {
            return mor_h_qty;
        }

        public void setMor_h_qty(int mor_h_qty) {
            this.mor_h_qty = mor_h_qty;
        }

        public String getS_code() {
            return s_code;
        }

        public void setS_code(String s_code) {
            this.s_code = s_code;
        }

        public String getS_name() {
            return s_name;
        }

        public void setS_name(String s_name) {
            this.s_name = s_name;
        }

        public int getMor_s_qty() {
            return mor_s_qty;
        }

        public void setMor_s_qty(int mor_s_qty) {
            this.mor_s_qty = mor_s_qty;
        }

        public int getMor_no1() {
            return mor_no1;
        }

        public void setMor_no1(int mor_no1) {
            this.mor_no1 = mor_no1;
        }

        public String getOrd_code() {
            return ord_code;
        }

        public void setOrd_code(String ord_code) {
            this.ord_code = ord_code;
        }

        public int getOrd_no1() {
            return ord_no1;
        }

        public void setOrd_no1(int ord_no1) {
            this.ord_no1 = ord_no1;
        }

        public String getH_color_code() {
            return h_color_code;
        }

        public void setH_color_code(String h_color_code) {
            this.h_color_code = h_color_code;
        }

        public String getH_color_name() {
            return h_color_name;
        }

        public void setH_color_name(String h_color_name) {
            this.h_color_name = h_color_name;
        }

        public String getH_loft_code() {
            return h_loft_code;
        }

        public void setH_loft_code(String h_loft_code) {
            this.h_loft_code = h_loft_code;
        }

        public String getH_loft_name() {
            return h_loft_name;
        }

        public void setH_loft_name(String h_loft_name) {
            this.h_loft_name = h_loft_name;
        }

        public String getHaed_direc_code() {
            return haed_direc_code;
        }

        public void setHaed_direc_code(String haed_direc_code) {
            this.haed_direc_code = haed_direc_code;
        }

        public String getHaed_direc_name() {
            return haed_direc_name;
        }

        public void setHaed_direc_name(String haed_direc_name) {
            this.haed_direc_name = haed_direc_name;
        }

        public String getHead_code() {
            return head_code;
        }

        public void setHead_code(String head_code) {
            this.head_code = head_code;
        }

        public String getHead_code_name() {
            return head_code_name;
        }

        public void setHead_code_name(String head_code_name) {
            this.head_code_name = head_code_name;
        }

        public String getH_weight_code() {
            return h_weight_code;
        }

        public void setH_weight_code(String h_weight_code) {
            this.h_weight_code = h_weight_code;
        }

        public String getH_weight_name() {
            return h_weight_name;
        }

        public void setH_weight_name(String h_weight_name) {
            this.h_weight_name = h_weight_name;
        }

        public String getS_color_code() {
            return s_color_code;
        }

        public void setS_color_code(String s_color_code) {
            this.s_color_code = s_color_code;
        }

        public String getS_color_name() {
            return s_color_name;
        }

        public void setS_color_name(String s_color_name) {
            this.s_color_name = s_color_name;
        }

        public String getS_strong_code() {
            return s_strong_code;
        }

        public void setS_strong_code(String s_strong_code) {
            this.s_strong_code = s_strong_code;
        }

        public String getS_strong_name() {
            return s_strong_name;
        }

        public void setS_strong_name(String s_strong_name) {
            this.s_strong_name = s_strong_name;
        }
    }
}

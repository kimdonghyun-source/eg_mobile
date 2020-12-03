package kr.co.bang.wms.model;

/**
 * 입력받은 자재불출 정보를 담고 있는 model(서버에서 내려오는 데이터 아님)
 */
public class MaterialSaveModel extends ResultModel{
    //자재불출번호
    String p_out_slip_no;
    //로그인ID
    String p_user_id;
    Detail detail;

    public String getP_out_slip_no() {
        return p_out_slip_no;
    }

    public void setP_out_slip_no(String p_out_slip_no) {
        this.p_out_slip_no = p_out_slip_no;
    }

    public String getP_user_id() {
        return p_user_id;
    }

    public void setP_user_id(String p_user_id) {
        this.p_user_id = p_user_id;
    }

    public Detail getDetail() {
        return detail;
    }

    public void setDetail(Detail detail) {
        this.detail = detail;
    }

    public class Detail extends ResultModel {
        //시스템로트번호
        String lot_no;
        //로케이션코드
        String location_code;
        //불출수량
        String out_qty;
        //자재불출품목순번
        int out_no2;

        public String getLot_no() {
            return lot_no;
        }

        public void setLot_no(String lot_no) {
            this.lot_no = lot_no;
        }

        public String getLocation_code() {
            return location_code;
        }

        public void setLocation_code(String location_code) {
            this.location_code = location_code;
        }

        public String getOut_qty() {
            return out_qty;
        }

        public void setOut_qty(String out_qty) {
            this.out_qty = out_qty;
        }

        public int getOut_no2() {
            return out_no2;
        }

        public void setOut_no2(int out_no2) {
            this.out_no2 = out_no2;
        }
    }
}

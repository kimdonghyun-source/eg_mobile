package kr.co.ajcc.wms.print;

import android.content.Context;
import android.util.Log;

import java.io.UnsupportedEncodingException;

import kr.co.jesoft.jelib.tsc.printer.TSCPrinter;

public class PrintUtil {
    private static final int LEFT = 1;
    private static final int CENTER = 2;
    private static final int RIGHT = 3;

    private static String makeCommand(int x, int y, int alignment, String str){
        int posX = 0;
        if(x == 0){
            if(alignment == 1)
                posX = 10;
            else if(alignment == 2)
                posX = 280;
            else if(alignment == 3)
                posX = 560;
            else
                posX = 10;
        }else{
            posX = x;
        }

        String text = "TEXT "+posX+","+y+","+"\"K.BF2\",0,1,1,"+alignment+","+"\""+str+"\""+"\r\n";

        return text;
    }
/*
    public static void printPaper(Context mContext, int mMode, PrintModel model , int printCnt) {
        if(model==null || model.getItems() == null || model.getItems().size()<=0)return;
        //버퍼로 한번에 넘김
        StringBuffer buffer = new StringBuffer();
        int posY = 100;
        if(mMode == Define.RESULT_COLLECTION) {
            PrintModel.TransactionRecord record = model.getItems().get(0);

            String Customer_cd = record.getCustomer_cd();      //거래처코드
            String Customer_nm = record.getCustomer_nm();      // 거래처명
            String company_no = record.getCompany_no();       //사업자번호
            String master_nm = record.getMaster_nm();        //대표자
            String address = record.getZip_addr() + " " + record.getDetail_addr(); //주소
            String bizItem = record.getBuzItem_nm();       //업종
            String bizType = record.getBuzType_nm();       //업태
            String remark = record.getRemark();           //비고
            String bill_no = record.getBill_no();          //어음번호
            String billPlace_nm = record.getBillPlace_nm();      //발급기관
            String expired_dt = record.getExpired_dt();       //발급일
            String pub_dt = record.getPub_dt();           //만기일
            String inout_dt = record.getInout_dt();         //수금일
            String tot_amt = Util.makeStringWithComma(record.getTot_amt(), false);          //합계
            String bill_amt = Util.makeStringWithComma(record.getBill_amt(), false);          //어음
            String cash_amt = Util.makeStringWithComma(record.getCash_amt(), false);          //현금

            String customer_comp = record.getAgency_nm();

            String text = "입금표(공급받는자용)";
            buffer.append(makeCommand(0, posY, CENTER, text));

            posY += 80;
            text = customer_comp + " 귀하";
            buffer.append(makeCommand(0, posY, LEFT, text));

            //거래처 정보
            posY += 30;
            text = "공급자";
            buffer.append(makeCommand(0, posY, LEFT, text));

            posY += 35;
            text = "사업자번호:";
            buffer.append(makeCommand(0, posY, LEFT, text));
            ////
            text = company_no;
            buffer.append(makeCommand(143, posY, LEFT, text));

            posY += 35;
            text = "상      호:";
            buffer.append(makeCommand(0, posY, LEFT, text));
            ////
            text = Customer_nm;
            buffer.append(makeCommand(143, posY, LEFT, text));

            text = "대  표  자:";
            buffer.append(makeCommand(310, posY, LEFT, text));
            ////
            text = master_nm;
            buffer.append(makeCommand(440, posY, LEFT, text));

            posY += 35;
            text = "주      소:";
            buffer.append(makeCommand(0, posY, LEFT, text));

            ////
            text = address;
            buffer.append(makeCommand(143, posY, LEFT, text));

            posY += 35;
            text = "업      태:";
            buffer.append(makeCommand(0, posY, LEFT, text));
            ////
            text = bizType;
            buffer.append(makeCommand(143, posY, LEFT, text));

            text = "업      종:";
            buffer.append(makeCommand(310, posY, LEFT, text));
            ////
            text = bizItem;
            buffer.append(makeCommand(440, posY, LEFT, text));

            posY += 100;

            text = "입   금   일 :";
            buffer.append(makeCommand(0, posY, LEFT, text));
            text = inout_dt;
            buffer.append(makeCommand(180, posY, LEFT, text));

            if (Util.stringToInt(bill_amt.replace(",", "")) > 0) {
                String inType = "";
                String inPrice = "";
                if (Util.stringToInt(cash_amt.replace(",", "")) > 0) {
                    inType = "현금+어음";
                    inPrice = String.format("%s원(현금 : %s / 어음 : %s)", tot_amt, cash_amt, bill_amt);
                } else {
                    inType = "어음";
                    inPrice = String.format("%s원", tot_amt);
                }
                posY += 35;
                text = "입 금 방 법 :";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = inType;
                buffer.append(makeCommand(180, posY, LEFT, text));

                posY += 35;
                text = "입   금   액:";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = inPrice;
                buffer.append(makeCommand(180, posY, LEFT, text));

                posY += 35;
                text = "어 음 번 호 :";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = bill_no;
                buffer.append(makeCommand(180, posY, LEFT, text));

                posY += 35;
                text = "발 급 기 관 :";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = billPlace_nm;
                buffer.append(makeCommand(180, posY, LEFT, text));

                posY += 35;
                text = "발   급   일:";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = expired_dt;
                buffer.append(makeCommand(180, posY, LEFT, text));

                posY += 35;
                text = "만   기   일:";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = pub_dt;
                buffer.append(makeCommand(180, posY, LEFT, text));
            } else {
                posY += 35;
                text = "입 금 방 법 :";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = "현금";
                buffer.append(makeCommand(180, posY, LEFT, text));

                posY += 35;
                text = "입   금   액:";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = tot_amt;
                buffer.append(makeCommand(180, posY, LEFT, text));
            }
            posY += 35;
            text = "비        고:";
            buffer.append(makeCommand(0, posY, LEFT, text));
            text = remark;
            buffer.append(makeCommand(180, posY, LEFT, text));

            text = "=====================서명=====================";
            posY += 60;
            buffer.append(makeCommand(0, posY, CENTER, text));
            posY += 270;

            posY += 30;
            text = "==============================================";
            buffer.append(makeCommand(0, posY, CENTER, text));
            if(printCnt >= 2){
                posY += 100;
                text = "----------------------------------------------";
                buffer.append(makeCommand(0, posY, CENTER, text));

                posY += 100;
                ////////////////////////////////////////////////////////////////////////////////////////
                //위와 똑같음
                text = "입금표(공급자용)";
                buffer.append(makeCommand(0, posY, CENTER, text));

                posY += 80;
                text = customer_comp + " 귀하";
                buffer.append(makeCommand(0, posY, LEFT, text));

                //거래처 정보
                posY += 30;
                text = "공급자";
                buffer.append(makeCommand(0, posY, LEFT, text));

                posY += 35;
                text = "사업자번호:";
                buffer.append(makeCommand(0, posY, LEFT, text));
                ////
                text = company_no;
                buffer.append(makeCommand(143, posY, LEFT, text));

                posY += 35;
                text = "상      호:";
                buffer.append(makeCommand(0, posY, LEFT, text));
                ////
                text = Customer_nm;
                buffer.append(makeCommand(143, posY, LEFT, text));

                text = "대  표  자:";
                buffer.append(makeCommand(310, posY, LEFT, text));
                ////
                text = master_nm;
                buffer.append(makeCommand(440, posY, LEFT, text));

                posY += 35;
                text = "주      소:";
                buffer.append(makeCommand(0, posY, LEFT, text));

                ////
                text = address;
                buffer.append(makeCommand(143, posY, LEFT, text));

                posY += 35;
                text = "업      태:";
                buffer.append(makeCommand(0, posY, LEFT, text));
                ////
                text = bizType;
                buffer.append(makeCommand(143, posY, LEFT, text));

                text = "업      종:";
                buffer.append(makeCommand(310, posY, LEFT, text));
                ////
                text = bizItem;
                buffer.append(makeCommand(440, posY, LEFT, text));

                posY += 100;

                text = "입   금   일 :";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = inout_dt;
                buffer.append(makeCommand(180, posY, LEFT, text));

                if (Util.stringToInt(bill_amt.replace(",", "")) > 0) {
                    String inType = "";
                    String inPrice = "";
                    if (Util.stringToInt(cash_amt.replace(",", "")) > 0) {
                        inType = "현금+어음";
                        inPrice = String.format("%s원", tot_amt);
                        posY += 30;
                        buffer.append(makeCommand(180, posY, LEFT, text));
                        inPrice = String.format("(현금 : %s / 어음 : %s)", cash_amt, bill_amt);
                    } else {
                        inType = "어음";
                        inPrice = String.format("%s원", tot_amt);
                    }
                    posY += 35;
                    text = "입 금 방 법 :";
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    text = inType;
                    buffer.append(makeCommand(180, posY, LEFT, text));

                    posY += 35;
                    text = "입   금   액:";
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    text = inPrice;
                    buffer.append(makeCommand(180, posY, LEFT, text));

                    posY += 35;
                    text = "어 음 번 호 :";
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    text = bill_no;
                    buffer.append(makeCommand(180, posY, LEFT, text));

                    posY += 35;
                    text = "발 급 기 관 :";
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    text = billPlace_nm;
                    buffer.append(makeCommand(180, posY, LEFT, text));

                    posY += 35;
                    text = "발   급   일:";
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    text = expired_dt;
                    buffer.append(makeCommand(180, posY, LEFT, text));

                    posY += 35;
                    text = "만   기   일:";
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    text = pub_dt;
                    buffer.append(makeCommand(180, posY, LEFT, text));
                } else {
                    posY += 35;
                    text = "입 금 방 법 :";
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    text = "현금";
                    buffer.append(makeCommand(180, posY, LEFT, text));

                    posY += 35;
                    text = "입   금   액:";
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    text = tot_amt;
                    buffer.append(makeCommand(180, posY, LEFT, text));
                }
                posY += 35;
                text = "비        고:";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = remark;
                buffer.append(makeCommand(180, posY, LEFT, text));

                text = "=====================서명=====================";
                posY += 60;
                buffer.append(makeCommand(0, posY, CENTER, text));
                posY += 270;

                posY += 30;
                text = "==============================================";
                buffer.append(makeCommand(0, posY, CENTER, text));
            }
            posY += 100;
        } else if(mMode == Define.RESULT_REGISTER_ORDER||mMode == Define.RESULT_REGISTER_RETURN) {

            int orderTotCnt     = 0;    //총수량
            int orderSupplyAmt  = 0;    //총공급가
            int orderSupertax   = 0;    //총부가세
            int orderTotalAmt   = 0;    //총금액

            String userName = (String) SharedData.getSharedData(mContext, SharedData.Type.EMP_NM.name());

            PrintModel.TransactionRecord record = model.getItems().get(0);
            String acceptSlip_no = Util.nullString(record.getAcceptSlip_no(), "");
            String agency_nm    = Util.nullString(record.getAgency_nm(), "");
            String slipGbn = Util.nullString(record.getSlip_gbn(), "");
            String timeLimit_dt = Util.nullString(record.getTimeLimit_dt(), "");
            String agemcyCom_no = Util.nullString(record.getAgencyCom_no(), "");
            String agemcyMas_nm = Util.nullString(record.getAgencyMas_nm(), "");
            String custcom_no = Util.nullString(record.getCustCom_no(), "");
            String customer_nm = Util.nullString(record.getCustomer_nm(), "");
            String custMas_nm = Util.nullString(record.getCustMas_nm(), "");
            String cust_Addr = Util.nullString(record.getCust_Addr(), "");
            String custDet_Addr = Util.nullString(record.getCustDet_Addr(), "");
            String custel_no1 = Util.nullString(record.getCustel_no(), "");
            String aegncytel_no = Util.nullString(record.getAgencytel_no(), "");
            String agency_addr = Util.nullString(record.getAgency_Addr(), "");
            String agencydet_addr = Util.nullString(record.getAgencyDet_Addr(), "");

            String plus = "-";
            if(slipGbn.trim().equals("정상")){
                plus = "";
            }
            String text = "거래명세서(공급받는자용)";
            buffer.append(makeCommand(0, posY, CENTER, text));

            posY += 30;

            //거래처 정보
            posY += 30;
            text = "공급받는자";
            buffer.append(makeCommand(0, posY, LEFT, text));

            text = "공급자";
            buffer.append(makeCommand(280, posY, LEFT, text));

            //거래처명
            text = customer_nm;
            posY += 30;
            buffer.append(makeCommand(0, posY, LEFT, text));

            text = agency_nm;
            buffer.append(makeCommand(280, posY, LEFT, text));

            //주소
            text = cust_Addr;
            //if (text.length() > 10)
            //    text = text.substring(0, 10);
            posY += 30;
            buffer.append(makeCommand(0, posY, LEFT, text));

            text = agency_addr;
            buffer.append(makeCommand(280, posY, LEFT, text));

            //상세주소
            text = custDet_Addr;
            posY += 30;
            buffer.append(makeCommand(0, posY, LEFT, text));

            text = agencydet_addr;
            buffer.append(makeCommand(280, posY, LEFT, text));

            //전화번호
            text = custel_no1;
            posY += 30;
            buffer.append(makeCommand(0, posY, LEFT, text));

            text = aegncytel_no;
            buffer.append(makeCommand(280, posY, LEFT, text));
            posY += 50;
            text = "----------------------------------------------";
            buffer.append(makeCommand(0, posY, LEFT, text));
            posY += 30;
            String item_nm = "";
            text = "제품명";
            buffer.append(makeCommand(0, posY, LEFT, text));
            posY += 35;
            text = "수량";
            buffer.append(makeCommand(45, posY, CENTER, text));
            text = "단가";
            buffer.append(makeCommand(150, posY, CENTER, text));
            text = "공급가";
            buffer.append(makeCommand(260, posY, CENTER, text));
            text = "부가세";
            buffer.append(makeCommand(380, posY, CENTER, text));
            //부가세+공급가
            text = "합계";
            buffer.append(makeCommand(490, posY, CENTER, text));
            posY += 30;
            text = "----------------------------------------------";
            buffer.append(makeCommand(0, posY, LEFT, text));
            posY += 30;
            for (int i = 0; i < model.getItems().size(); i++) {
                PrintModel.TransactionRecord detail = model.getItems().get(i);

                int nTotQty = Util.stringToInt(Util.nullString(detail.getTot_qty(),"0"));
                int nPriceAmt = Util.stringToInt(Util.nullString(detail.getPrice_amt(), "0"));
                //단가가 과세 적용된 금액임
                long total      =   nTotQty*nPriceAmt; //합계 = 총수량*단가
                long supply    =   0;
                long tax             =   0;
                //01:과세 1.1 / 02,03:일반
                if(detail.getTransVat_gbn().trim().equals("01")) {
                    supply = (long) Math.round(total/1.1); //공급가 = 합계/1.1
                    tax = total - supply; //부가세 = 합계 - 공급가
                } else {
                    supply = total;
                    tax = total - supply; //부가세 = 합계 - 공급가
                }

                String itemNm   = detail.getItem_nm();      //상품명
                String totQty   = Util.makeStringWithComma(nTotQty+"", false);      //총수량
                String priceAmt = Util.makeStringWithComma(nPriceAmt+"", false);    //단가
                String supplyAmt= Util.makeStringWithComma(supply + "", false);     //공급가
                String supertax = Util.makeStringWithComma(tax + "", false);        //부가세
                String totalAmt = Util.makeStringWithComma(total + "", false);      //합계

                orderTotCnt     += nTotQty;    //총수량
                orderSupplyAmt  += supply;    //총공급가
                orderSupertax   += tax;    //총부가세
                orderTotalAmt   += total;    //총금액

                text = itemNm;
                buffer.append(makeCommand(0, posY, LEFT, text));
                posY += 35;
                text = plus+totQty;
                buffer.append(makeCommand(45, posY, CENTER, text));
                text = plus+priceAmt;
                buffer.append(makeCommand(150, posY, CENTER, text));
                text = plus+supplyAmt;
                buffer.append(makeCommand(260, posY, CENTER, text));
                text = plus+supertax;
                buffer.append(makeCommand(380, posY, CENTER, text));
                //부가세+공급가
                text = plus+totalAmt;
                buffer.append(makeCommand(490, posY, CENTER, text));
                posY += 30;
            }
            //총  계       [총수량]        [총공급가]      [총부가세]       [총금액]
            //posY += 30;
            text = "----------------------------------------------";
            buffer.append(makeCommand(0, posY, LEFT, text));
            posY += 30;
            text = "총  계";
            buffer.append(makeCommand(0, posY, LEFT, text));
            text = "[총수량]";
            buffer.append(makeCommand(150, posY, CENTER, text));
            text = "[총공급가]";
            buffer.append(makeCommand(260, posY, CENTER, text));
            text = "[총부가세]";
            buffer.append(makeCommand(380, posY, CENTER, text));
            text = "[총금액]";
            buffer.append(makeCommand(490, posY, CENTER, text));

            posY += 45;

            text = plus+Util.makeStringWithComma(String.format("%d",orderTotCnt),false);
            buffer.append(makeCommand(150, posY, CENTER, text));
            text = plus+Util.makeStringWithComma(String.format("%d",orderSupplyAmt),false);
            buffer.append(makeCommand(260, posY, CENTER, text));
            text = plus+Util.makeStringWithComma(String.format("%d",orderSupertax),false);
            buffer.append(makeCommand(380, posY, CENTER, text));
            text = plus+Util.makeStringWithComma(String.format("%d",orderTotalAmt),false);
            buffer.append(makeCommand(490, posY, CENTER, text));

            posY += 30;

            text = "담당자:" + SharedData.getSharedData(mContext, SharedData.Type.EMP_NM.name());
            posY += 30;
            buffer.append(makeCommand(0, posY, RIGHT, text));
            if(printCnt >= 2) {
                posY += 200;
                text = "----------------------------------------------";
                buffer.append(makeCommand(0, posY, CENTER, text));

                orderTotCnt = 0;    //총수량
                orderSupplyAmt = 0;    //총공급가
                orderSupertax = 0;    //총부가세
                orderTotalAmt = 0;    //총금액

                posY += 150;
                ////////////////////////////////////////////////////////////////////////////////////////
                //위와 똑같음
                text = "거래명세서(공급자용)";
                buffer.append(makeCommand(0, posY, CENTER, text));

                posY += 30;

                //거래처 정보
                posY += 30;
                text = "공급받는자";
                buffer.append(makeCommand(0, posY, LEFT, text));

                text = "공급자";
                buffer.append(makeCommand(280, posY, LEFT, text));

                //거래처명
                text = customer_nm;
                posY += 30;
                buffer.append(makeCommand(0, posY, LEFT, text));

                text = agency_nm;
                buffer.append(makeCommand(280, posY, LEFT, text));

                //주소
                text = cust_Addr;
                //if (text.length() > 10)
                //    text = text.substring(0, 10);
                posY += 30;
                buffer.append(makeCommand(0, posY, LEFT, text));

                text = agency_addr;
                buffer.append(makeCommand(280, posY, LEFT, text));

                //상세주소
                text = custDet_Addr;
                posY += 30;
                buffer.append(makeCommand(0, posY, LEFT, text));

                text = agencydet_addr;
                buffer.append(makeCommand(280, posY, LEFT, text));

                //전화번호
                text = custel_no1;
                posY += 30;
                buffer.append(makeCommand(0, posY, LEFT, text));

                text = aegncytel_no;
                buffer.append(makeCommand(280, posY, LEFT, text));
                posY += 50;
                text = "----------------------------------------------";
                buffer.append(makeCommand(0, posY, LEFT, text));
                posY += 30;
                text = "제품명";
                buffer.append(makeCommand(0, posY, LEFT, text));
                posY += 35;
                text = "수량";
                buffer.append(makeCommand(45, posY, CENTER, text));
                text = "단가";
                buffer.append(makeCommand(150, posY, CENTER, text));
                text = "공급가";
                buffer.append(makeCommand(260, posY, CENTER, text));
                text = "부가세";
                buffer.append(makeCommand(380, posY, CENTER, text));
                //부가세+공급가
                text = "합계";
                buffer.append(makeCommand(490, posY, CENTER, text));
                posY += 30;
                text = "----------------------------------------------";
                buffer.append(makeCommand(0, posY, LEFT, text));
                posY += 30;
                for (int i = 0; i < model.getItems().size(); i++) {
                    PrintModel.TransactionRecord detail = model.getItems().get(i);

                    int nTotQty = Util.stringToInt(Util.nullString(detail.getTot_qty(),"0"));
                    int nPriceAmt = Util.stringToInt(Util.nullString(detail.getPrice_amt(), "0"));
                    //단가가 과세 적용된 금액임
                    long total      =   nTotQty*nPriceAmt; //합계 = 총수량*단가
                    long supply    =   0;
                    long tax             =   0;
                    //01:과세 1.1 / 02,03:일반
                    if(detail.getTransVat_gbn().trim().equals("01")) {
                        supply = (long) Math.round(total/1.1); //공급가 = 합계/1.1
                        tax = total - supply; //부가세 = 합계 - 공급가
                    } else {
                        supply = total;
                        tax = total - supply; //부가세 = 합계 - 공급가
                    }

                    String itemNm = detail.getItem_nm();      //상품명
                    String totQty = Util.makeStringWithComma(nTotQty + "", false);      //총수량
                    String priceAmt = Util.makeStringWithComma(nPriceAmt + "", false);    //단가
                    String supplyAmt = Util.makeStringWithComma(supply + "", false);     //공급가
                    String supertax = Util.makeStringWithComma(tax + "", false);        //부가세
                    String totalAmt = Util.makeStringWithComma(total + "", false);      //합계

                    orderTotCnt += nTotQty;    //총수량
                    orderSupplyAmt += supply;    //총공급가
                    orderSupertax += tax;    //총부가세
                    orderTotalAmt += total;    //총금액

                    text = itemNm;
                    buffer.append(makeCommand(0, posY, LEFT, text));
                    posY += 35;
                    text = plus + totQty;
                    buffer.append(makeCommand(45, posY, CENTER, text));
                    text = plus + priceAmt;
                    buffer.append(makeCommand(150, posY, CENTER, text));
                    text = plus + supplyAmt;
                    buffer.append(makeCommand(260, posY, CENTER, text));
                    text = plus + supertax;
                    buffer.append(makeCommand(380, posY, CENTER, text));
                    //부가세+공급가
                    text = plus + totalAmt;
                    buffer.append(makeCommand(490, posY, CENTER, text));
                    posY += 30;
                }
                //총  계       [총수량]        [총공급가]      [총부가세]       [총금액]
                //posY += 30;
                text = "----------------------------------------------";
                buffer.append(makeCommand(0, posY, LEFT, text));
                posY += 30;
                text = "총  계";
                buffer.append(makeCommand(0, posY, LEFT, text));
                text = "[총수량]";
                buffer.append(makeCommand(150, posY, CENTER, text));
                text = "[총공급가]";
                buffer.append(makeCommand(260, posY, CENTER, text));
                text = "[총부가세]";
                buffer.append(makeCommand(380, posY, CENTER, text));
                text = "[총금액]";
                buffer.append(makeCommand(490, posY, CENTER, text));

                posY += 45;

                text = plus + Util.makeStringWithComma(String.format("%d", orderTotCnt), false);
                buffer.append(makeCommand(150, posY, CENTER, text));
                text = plus + Util.makeStringWithComma(String.format("%d", orderSupplyAmt), false);
                buffer.append(makeCommand(260, posY, CENTER, text));
                text = plus + Util.makeStringWithComma(String.format("%d", orderSupertax), false);
                buffer.append(makeCommand(380, posY, CENTER, text));
                text = plus + Util.makeStringWithComma(String.format("%d", orderTotalAmt), false);
                buffer.append(makeCommand(490, posY, CENTER, text));

                posY += 30;

                text = "담당자:" + SharedData.getSharedData(mContext, SharedData.Type.EMP_NM.name());
                posY += 30;
                buffer.append(makeCommand(0, posY, RIGHT, text));
            }
            posY += 100;
        }

        TSCPrinter.shared(mContext).sendConmmand("GAP 0,0\r\n");
        TSCPrinter.shared(mContext).sendConmmand("DIRECTION 0\r\n");
        TSCPrinter.shared(mContext).sendConmmand("SET TEAR OFF\r\n");
        TSCPrinter.shared(mContext).sendConmmand("SIZE 72 mm,"+(posY/8)+" mm\r\n");
        TSCPrinter.shared(mContext).sendConmmand("CLS\r\n");

        try {
            TSCPrinter.shared(mContext).sendConmmand(buffer.toString().getBytes("EUC-KR"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            Log.d("JeLib","msg::::::::"+e.getMessage());
        }
        Log.d("JeLib","PRINT");
        TSCPrinter.shared(mContext).sendConmmand(String.format("PRINT %d\r\n",1));
    }*/
}
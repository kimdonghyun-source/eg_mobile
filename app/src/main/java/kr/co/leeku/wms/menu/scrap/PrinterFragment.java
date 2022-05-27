package kr.co.leeku.wms.menu.scrap;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.squareup.otto.Subscribe;

import java.io.UnsupportedEncodingException;

import kr.co.jesoft.jelib.listener.OnJEReaderResponseListener;
import kr.co.jesoft.jelib.tsc.printer.TSCPrinter;
import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.Define;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.BusProvider;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.menu.main.BaseActivity;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;

public class PrinterFragment extends CommonFragment {
    private static final int LEFT = 1;
    private static final int CENTER = 2;
    private static final int RIGHT = 3;

    Context mContext;

    ImageButton btn_next = null;
    ImageButton btn_change = null;
    TextView tv_pallet_sn = null;
    TwoBtnPopup mPopup = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("JeLib", "-------PrinterFragment-------");
        Log.d("JeLib", "-------PrinterFragment------");
        mContext = getActivity();
    }

    private static String makeCommand(int x, int y, int alignment, String str) {
        int posX = 0;
        if (x == 0) {
            if (alignment == 1)
                posX = 10;
            else if (alignment == 2)
                posX = 280;
            else if (alignment == 3)
                posX = 560;
            else
                posX = 10;
        } else {
            posX = x;
        }

        String text = "TEXT " + posX + "," + y + "," + "\"K.BF2\",0,1,1," + alignment + "," + "\"" + str + "\"" + "\r\n";
        //String text = "TEXT " + posX + "," + y + "," + "5," + alignment + "," + "\"" + str + "\"" + "\r\n" + "FONT 5";
        //TEXT 10,360,"5",0,1,1,0,"FONT 5"

        return text;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_printer, container, false);
        btn_change = v.findViewById(R.id.bt_change);
        btn_next = v.findViewById(R.id.bt_next);
        tv_pallet_sn = v.findViewById(R.id.tv_pallet_sn);
        Log.d("JeLib", "--------------1-------------");
        Log.d("JeLib", "--------------1-------------");
        Bundle args = getArguments();
        if (args != null) {

            final String scrap_no = args.getString("SCRAPNO");
            final String scrap_dt = args.getString("SCRAPDT");
            final String cmp_nm = args.getString("CMPNM");
            final String dogum_nm = args.getString("DOGUMNM");
            final String location = args.getString("LOCATION");
            final String cnt = args.getString("CNT");

            final String b_SN_2 = args.getString("B_SN_2");
            final String item_qty_2 = args.getString("QTY_2");
            Utils.Log("scrap_no:" + scrap_no);
            Utils.Log("scrap_dt:" + scrap_dt);
            Utils.Log("cmp_nm:" + cmp_nm);
            Utils.Log("dogum_nm:" + dogum_nm);
            Utils.Log("location:" + location);
            Utils.Log("cnt:" + cnt);

            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btn_change.isSelected()) {
                        print(scrap_no, scrap_dt, cmp_nm, dogum_nm, location, cnt);

                    } else {
                        String printer = (String) SharedData.getSharedData(mContext, "printer_info", "");
                        String arr[] = printer.split(" ");
                        if (arr != null && arr.length >= 2) {

                        } else {
                            mPopup = new TwoBtnPopup(getActivity(), "프린터가 설정되지 않았습니다. 확인을 누르시면 설정화면으로 이동합니다.", R.drawable.popup_title_alert, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.what == 1) {
                                        goConfig();
                                    } else {
                                        getActivity().finish();
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
        btn_change.setSelected(false);

        connectPrinter();

        BusProvider.getInstance().register(this);

        return v;
    }

    public void connectPrinter() {
        final String printer = (String) SharedData.getSharedData(mContext, "printer_info", "");
        String arr[] = printer.split(" ");
        if (arr != null && arr.length >= 2) {
            TSCPrinter.shared(mContext).connect(arr[1], new OnJEReaderResponseListener() {
                @Override
                public void jeReaderDidConnect() {
                    if (!Utils.isEmpty(printer)) {
                        btn_change.setSelected(true);
                    } else {
                        btn_change.setSelected(false);
                    }
                }

                @Override
                public void jeReaderDataReceived(Object o) {

                }
            });
        }
    }

    @Subscribe
    public void getPost(Integer event) {
        connectPrinter();
    }

    @Override
    public void onDestroy() {
        try {
            TSCPrinter.shared(mContext).closeSession();
        } catch (Exception e) {

        }

        BusProvider.getInstance().unregister(this);

        super.onDestroy();
    }

    private void goConfig() {
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_CONFIG);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    private void print(String scrap_no, String scrap_dt, String cmp_nm, String dogum_nm, String location, String cnt) {
        StringBuffer buffer = new StringBuffer();
        //buffer.append("BOX 10,10,575,110,2\r\n").append("BAR 10,65,565,2\r\n").append("BAR 150,10,2,99\r\n").append("BAR 400,65,2,44\r\n");

        buffer.append(makeCommand(13, 98, LEFT, "*"+scrap_no+"*"));
        buffer.append(makeCommand(13, 158, LEFT, scrap_dt));
        buffer.append(makeCommand(13, 218, LEFT, cmp_nm));
        buffer.append(makeCommand(175, 218, CENTER, dogum_nm));
        buffer.append(makeCommand(382, 218, CENTER, "위치: "+location));
        //buffer.append(makeCommand(13, 278, LEFT, cnt + "(Kg)"));
        buffer.append("TEXT " + 13 + "," + 278 + "," + "\"5\",0,1,1,0,"+ cnt +  "");

        //buffer.append("TEXT " + 295 + "," + 540 + "," + "\"3\",0,1,1," + 2 + "," + "\"" + "palletSN.trim()" + "\"" + "\r\n");

        //TSCPrinter.shared(mContext).sendConmmand("SIZE 75 mm,75 mm\r\n");
        TSCPrinter.shared(mContext).sendConmmand("SIZE 75 mm,75 mm\r\n");
        /*TSCPrinter.shared(mContext).sendConmmand("GAP 3 mm,0\r\n");
        TSCPrinter.shared(mContext).sendConmmand("DIRECTION 0\r\n");
        TSCPrinter.shared(mContext).sendConmmand("CLS\r\n");*/

        TSCPrinter.shared(mContext).sendConmmand("GAP 0,0");
        TSCPrinter.shared(mContext).sendConmmand("DIRECTION 1");
        TSCPrinter.shared(mContext).sendConmmand("CLS");


        //TSCPrinter.shared(mContext).sendConmmand(String.format("QRCODE 105,140,H,15,A,0, \"%s\"\r\n", scrap_no.trim()));
        //TSCPrinter.shared(mContext).sendConmmand(String.format("TLC39 10,300,0, \"123456,SN00000001,00601,01501\"\n", scrap_no.trim()));

        TSCPrinter.shared(mContext).sendConmmand(String.format("BARCODE 10,350, \"128\",100,1,0,2,2, " + scrap_no));
        //TSCPrinter.shared(mContext).sendConmmand(String.format("BARCODE 10,10, " +"128"+",100,1,0,2,2, " + scrap_no));
        String a = String.format("BARCODE 10,10, \"128\",100,1,0,2,2, " + scrap_no );
        //String a = String.format("BARCODE 10,10, " +"128"+",100,1,0,2,2, " + scrap_no);
        Log.d("값::", a);
        Log.d("값11::", scrap_no);

        //TSCPrinter.shared(mContext).sendConmmand(String.format("TLC39 10,350,0, \"050505,\"scrap_no.trim(),\"00601,01501\"\n"));


        try {
            TSCPrinter.shared(mContext).sendConmmand(buffer.toString().getBytes("EUC-KR"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
        }
        TSCPrinter.shared(mContext).sendConmmand(String.format("PRINT %d\r\n", 1));
    }
}

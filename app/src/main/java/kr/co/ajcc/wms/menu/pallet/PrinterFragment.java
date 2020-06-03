package kr.co.ajcc.wms.menu.pallet;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Define;
import kr.co.ajcc.wms.common.SharedData;
import kr.co.ajcc.wms.common.Utils;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.menu.main.BaseActivity;
import kr.co.ajcc.wms.menu.popup.TwoBtnPopup;
import kr.co.jesoft.jelib.listener.OnJEReaderResponseListener;
import kr.co.jesoft.jelib.tsc.printer.TSCPrinter;

public class PrinterFragment extends CommonFragment {
    private static final int LEFT = 1;
    private static final int CENTER = 2;
    private static final int RIGHT = 3;

    ImageButton btn_next    =   null;
    ImageButton btn_change  =   null;
    TextView    tv_pallet_sn=   null;
    TwoBtnPopup mPopup      =   null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_printer, container, false);
        btn_change = v.findViewById(R.id.bt_change);
        btn_next = v.findViewById(R.id.bt_next);
        tv_pallet_sn = v.findViewById(R.id.tv_pallet_sn);

        Bundle args = getArguments();
        if(args!=null){
            final String palletSN = args.getString("SN");
            final String item_cd = args.getString("ITEMCD");
            final String item_nm = args.getString("ITEMNM");
            final String item_cnt = args.getString("CNT");
            tv_pallet_sn.setText(palletSN);
            btn_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btn_change.isSelected()){
                        StringBuffer buffer = new StringBuffer();
                        buffer.append("BOX 10,10,575,110,2\r\n").append("BAR 10,65,565,2\r\n").append("BAR 150,10,2,99\r\n").append("BAR 400,65,2,44\r\n");
                        buffer.append(makeCommand(13,18,LEFT,"품       명"));
                        buffer.append(makeCommand(13,78,LEFT,"코드 / 수량"));

                        buffer.append("BLOCK 160,18,415,60, \"K.BF2\",0,1,1,1,\""+item_nm+"\"\r\n");
                        buffer.append(makeCommand(275,78,CENTER,item_cd));
                        buffer.append(makeCommand(482,78,CENTER,item_cnt));
                        buffer.append("TEXT "+295+","+540+","+"\"3\",0,1,1,"+2+","+"\""+palletSN.trim()+"\""+"\r\n");

                        TSCPrinter.shared(mContext).sendConmmand("SIZE 75 mm,75 mm\r\n");
                        TSCPrinter.shared(mContext).sendConmmand("GAP 3 mm,0\r\n");
                        TSCPrinter.shared(mContext).sendConmmand("DIRECTION 0\r\n");
                        //TSCPrinter.shared(mContext).sendConmmand("FORMFEED\r\n");
                        TSCPrinter.shared(mContext).sendConmmand("CLS\r\n");

                        TSCPrinter.shared(mContext).sendConmmand(String.format("QRCODE 105,140,H,15,A,0, \"%s\"\r\n",palletSN.trim()));
                        try {
                            TSCPrinter.shared(mContext).sendConmmand(buffer.toString().getBytes("EUC-KR"));
                        } catch (UnsupportedEncodingException e) {
                            // TODO Auto-generated catch block
                        }
                        TSCPrinter.shared(mContext).sendConmmand(String.format("PRINT %d\r\n",1));
                    } else {
                        String printer = (String) SharedData.getSharedData(mContext, "printer_info","");
                        String arr[] = printer.split(" ");
                        if(arr!=null && arr.length>=2) {

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

        final String printer = (String) SharedData.getSharedData(mContext, "printer_info","");
        String arr[] = printer.split(" ");
        if(arr!=null && arr.length>=2) {
            TSCPrinter.shared(mContext).connect(arr[1], new OnJEReaderResponseListener() {
                @Override
                public void jeReaderDidConnect() {
                    if(!Utils.isEmpty(printer)) {
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

        return v;
    }
    @Override
    public void onDestroy()
    {
        try {
            TSCPrinter.shared(mContext).closeSession();
        } catch (Exception e){

        }
        super.onDestroy();
    }
    private void goConfig(){
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_CONFIG);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
}

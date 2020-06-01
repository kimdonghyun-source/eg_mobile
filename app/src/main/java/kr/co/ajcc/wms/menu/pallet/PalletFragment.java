package kr.co.ajcc.wms.menu.pallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.ajcc.wms.R;
import kr.co.ajcc.wms.common.Define;
import kr.co.ajcc.wms.common.SharedData;
import kr.co.ajcc.wms.custom.CommonFragment;
import kr.co.ajcc.wms.menu.main.BaseActivity;
import kr.co.ajcc.wms.menu.popup.TwoBtnPopup;

public class PalletFragment extends CommonFragment {
    Context mContext;
    TwoBtnPopup mPopup = null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_pallet, container, false);

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

        return v;
    }

    private void goConfig(){
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_CONFIG);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }
}

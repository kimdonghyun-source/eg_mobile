package kr.co.leeku.wms.menu.scrap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.co.leeku.wms.R;
import kr.co.leeku.wms.common.Define;
import kr.co.leeku.wms.common.SharedData;
import kr.co.leeku.wms.common.Utils;
import kr.co.leeku.wms.custom.CommonFragment;
import kr.co.leeku.wms.menu.main.BaseActivity;
import kr.co.leeku.wms.menu.popup.TwoBtnPopup;
import kr.co.leeku.wms.model.ScrapListModel;
import kr.co.leeku.wms.model.ShipListModel;
import kr.co.leeku.wms.model.ShipScanModel;

public class ScrapDetailFragment extends CommonFragment {

    Context mContext;
    TwoBtnPopup mTwoBtnPopup = null;
    ScrapListModel mScrapListModel = null;
    ScrapListModel.Item order = null;
    int mPosition = -1;
    TextView tv_scrap_no, tv_scrap_dt, tv_cmp_nm, tv_dogum_nm, tv_cnt, tv_location;
    ImageButton btn_next;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }//Close onCreate


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.frag_scrap_detail, container, false);

        Bundle arguments = getArguments();
        mScrapListModel = (ScrapListModel) arguments.getSerializable("model");
        mPosition = arguments.getInt("position");
        order = mScrapListModel.getItems().get(mPosition);

        tv_scrap_no = v.findViewById(R.id.tv_scrap_no);
        tv_scrap_dt = v.findViewById(R.id.tv_scrap_dt);
        tv_cmp_nm = v.findViewById(R.id.tv_cmp_nm);
        tv_dogum_nm = v.findViewById(R.id.tv_dogum_nm);
        tv_cnt = v.findViewById(R.id.tv_cnt);
        tv_location = v.findViewById(R.id.tv_location);
        btn_next = v.findViewById(R.id.btn_next);

        tv_scrap_no.setText(order.getScrap_no());
        tv_scrap_dt.setText(order.getScrap_dt());
        tv_cmp_nm.setText(order.getCmp_nm());
        tv_dogum_nm.setText(order.getDogum_nm());
        tv_cnt.setText(Integer.toString(order.getCnt()));
        tv_location.setText(order.getLocation());

        btn_next.setOnClickListener(onClickListener);

        String printer = (String) SharedData.getSharedData(mContext, "printer_info","");
        String arr[] = printer.split(" ");
        if(arr!=null && arr.length>=2) {
            Utils.Toast(mContext, "프린터 연결중입니다.");
        } else {
            mTwoBtnPopup = new TwoBtnPopup(getActivity(), "프린터가 설정되지 않았습니다. 확인을 누르시면 설정화면으로 이동합니다.", R.drawable.popup_title_alert, new Handler() {
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

    }//Close onCreateView

    private void goConfig(){
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_CONFIG);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }//Close

    private void goBarcode(String sn){
        Utils.Log("goBarcode");
        Intent intent = new Intent(mContext, BaseActivity.class);
        intent.putExtra("menu", Define.MENU_SCRAP_PRINTER);
        Bundle args=new Bundle();
        args.putString("SN",sn);
        //바코드??????????????????????????
        args.putString("SCRAPNO", order.getScrap_no());          //스크랩번호
        args.putString("SCRAPDT", order.getScrap_dt());          //입력날짜
        args.putString("CMPNM", order.getCmp_nm());              //품목
        args.putString("DOGUMNM", order.getDogum_nm());          //도금
        args.putString("LOCATION", order.getLocation());         //위치
        args.putString("CNT", String.valueOf(order.getCnt()));   //중량


        intent.putExtra("args",args);
        startActivity(intent);
    }//Close

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){

                case R.id.btn_next:
                    goBarcode(order.getScrap_no());
                    break;

            }
        }
    };

}//Close Fragment